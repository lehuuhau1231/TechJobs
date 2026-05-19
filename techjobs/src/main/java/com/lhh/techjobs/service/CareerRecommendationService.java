package com.lhh.techjobs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lhh.techjobs.dto.request.CareerRequest;
import com.lhh.techjobs.dto.response.CareerRecommendResponse;
import com.lhh.techjobs.dto.response.CareerResponse;
import com.lhh.techjobs.dto.response.GenerateQuestionResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.ChatSession;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.entity.Message;
import com.lhh.techjobs.enums.Sender;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.ChatSessionRepository;
import com.lhh.techjobs.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CareerRecommendationService {
    private final CandidateRepository candidateRepository;
    private final EmbeddingService embeddingService;
    private final JedisPooled jedis;
    private final ChatSessionService chatSessionService;
    private final MessageRepository messageRepository;
    private final GeminiService geminiService;
    private final MessageService messageService;
    private static final String INDEX_CAREER_NAME = "jobIdx";
    private static final int MAX_QUESTIONS = 5;

    public CareerRecommendResponse recommendCareer(CareerRequest careerRequest) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CvProfile cvProfile = candidateRepository.findCvProfileByEmail(email);

        ChatSession chatSession = chatSessionService.createChatSession(careerRequest);

        List<String> recentMessage = messageRepository.findTop5Message(chatSession, PageRequest.of(0, 10));

        GenerateQuestionResponse generateQuestion = geminiService.generateQuestion(
                "",
                careerRequest.getContent(),
                recentMessage,
                chatSession.getSoftSkill(),
                chatSession.getQuestionCount() != null ? chatSession.getQuestionCount() : 0,
                MAX_QUESTIONS
        );
        
        chatSession.setSoftSkill(mergeValue(chatSession.getSoftSkill(), generateQuestion.getSoftSkill()));

        chatSession.setQuestionCount((chatSession.getQuestionCount() != null ? chatSession.getQuestionCount() : 0) + 1);
        chatSessionService.save(chatSession);

        String answerGenerated;

        if(generateQuestion.getIsReady()){
            String finalUserQuery = (cvProfile == null ? generateQuestion.getSummary() : generateQuestion.getSummary().concat(" " + cvProfile.buildCareerVectorContent()));

            // B1: tạo embedding từ CV
            float[] embedding = embeddingService.getEmbedding(finalUserQuery);

            // B2: tìm job gần nhất bằng KNN
            byte[] vecBytes = VectorService.floatArrayToBytes(embedding);

            Query q = new Query("*=>[KNN 2 @vector $vec AS score]")
                    .addParam("vec", vecBytes)
                    .returnFields("id", "title", "skillNames", "jobDetailUrl", "softSkill", "score")
                    .setSortBy("score", true)
                    .dialect(2);

            SearchResult result = jedis.ftSearch(INDEX_CAREER_NAME, q);

            List<CareerResponse> careerResponses = mapSearchResult(result);
            List<String> careerDescription = careerResponses.stream().map(CareerResponse::formatCareerDescription).toList();

            answerGenerated = geminiService.generateAnswer(careerDescription, generateQuestion.getSummary());
        } else {
            answerGenerated = generateQuestion.getQuestion();
        }

        // Save answer of Assistant
        messageService.createAndSaveMessage(chatSession, answerGenerated);

        log.info("softSkill: {}", chatSession.getSoftSkill());

        return CareerRecommendResponse.builder()
                .chatSessionId(chatSession.getId())
                .text(answerGenerated)
                .sender(Sender.ASSISTANT)
                .build();
    }

    private String mergeValue(String oldValue, String newValue) {
        return (newValue != null && !newValue.isBlank()) ? newValue : oldValue;
    }

    private List<CareerResponse> mapSearchResult(SearchResult result) {
        if (result == null || result.getDocuments() == null) {
            return Collections.emptyList();
        }

        return result.getDocuments().stream().map(this::mapToJobResponse).toList();
    }

    private CareerResponse mapToJobResponse(Document doc) {
        return CareerResponse.builder()
                .id(Integer.parseInt(doc.getString("id")))
                .title(doc.getString("title"))
                .skillNames(doc.getString("skillNames"))
                .softSkill(doc.getString("softSkill"))
                .jobDetailUrl(doc.getString("jobDetailUrl"))
                .score(
                        Double.parseDouble(doc.getString("score"))
                )
                .build();
    }

}