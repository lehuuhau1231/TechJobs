package com.lhh.techjobs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.lhh.techjobs.dto.response.CareerResponse;
import com.lhh.techjobs.dto.response.JobExplainResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CareerRecommendationService {
    private final CandidateRepository candidateRepository;
    private final EmbeddingService embeddingService;
    private final JedisPooled jedis;
    private static final String INDEX_CAREER_NAME = "careerIdx";

    private List<JobResponse> recommendCareer(String userQuery) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CvProfile cvProfile = candidateRepository.findCvProfileByEmail(email);
        
        // B1: tạo embedding từ CV
        float[] embedding = embeddingService.getEmbedding(cvProfile.buildVectorContent());

        // B2: tìm job gần nhất bằng KNN
        byte[] vecBytes = VectorService.floatArrayToBytes(embedding);

        Query q = new Query("=>[KNN 2 @vector $vec AS score]")
                .addParam("vec", vecBytes)
                .returnFields("id", "jobTitle", "keySkills", "characterTraits", "score")
                .setSortBy("score", true)
                .dialect(2);

        SearchResult result = jedis.ftSearch(INDEX_CAREER_NAME, q);

        List<CareerResponse> careerResponses = mapSearchResult(result);

        careerResponses.forEach(System.out::println);


//        String prompt = buildExplainPrompt(jobResponses, cvProfile);
//
//        ChatResponse chatResponse = chatModel.call(new Prompt(prompt));
//        try {
//            List<JobExplainResponse> jobExplainResponses = objectMapper.readValue(chatResponse.getResult().getOutput().getText(),
//                    new TypeReference<List<JobExplainResponse>>() {});
//        } catch (JsonProcessingException e) {
//            log.error("Error parsing job explain gemini response: {}", e.getMessage());
//            throw new RuntimeException(e);
//        }

        return null;


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
                .jobTitle(doc.getString("jobTitle"))
                .keySkills(doc.getString("keySkills"))
                .characterTraits(doc.getString("characterTraits"))
                .matchPercentage(
                        (int) (1 - Double.parseDouble(doc.getString("score")) * 100)
                )
                .build();
    }

}