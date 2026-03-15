package com.lhh.techjobs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.genai.Client;
import com.lhh.techjobs.dto.response.JobExplainResponse;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.entity.User;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final JedisPooled jedis;
    private final EmbeddingService embeddingService;
    private final ChatModel chatModel;
    private final CandidateRepository candidateRepository;
    private final ObjectMapper objectMapper;

    private static final String INDEX_NAME = "jobIdx";

    public List<JobResponse> recommendationJobFromCV() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CvProfile cvProfile = candidateRepository.findCvProfileByEmail(email);
        if (cvProfile == null) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        return recommendJobs(cvProfile);
    }

    private List<JobResponse> recommendJobs(CvProfile cvProfile) {
        // B1: tạo embedding từ CV
        float[] embedding = embeddingService.getEmbedding(cvProfile.buildVectorContent());

        // B2: tìm job gần nhất bằng KNN
        byte[] vecBytes = VectorService.floatArrayToBytes(embedding);

        String queryAddition = (cvProfile.getPreferredLocation() != null) ?
                "@city: {" + cvProfile.getPreferredLocation().toLowerCase() + "}"
                : "*";

        Query q = new Query(queryAddition + "=>[KNN 5 @vector $vec AS score]")
                .addParam("vec", vecBytes)
                .returnFields("id", "title", "city", "districtName", "jobLevelName", "skillNames", "salaryMin", "salaryMax", "image", "score")
                .setSortBy("score", true)
                .dialect(2);

        SearchResult result = jedis.ftSearch(INDEX_NAME, q);

        List<JobResponse> jobResponses = mapSearchResult(result);

        String prompt = buildExplainPrompt(jobResponses, cvProfile);

        ChatResponse chatResponse = chatModel.call(new Prompt(prompt));
        try {
            List<JobExplainResponse> jobExplainResponses = objectMapper.readValue(chatResponse.getResult().getOutput().getText(),
                    new TypeReference<List<JobExplainResponse>>() {});
        } catch (JsonProcessingException e) {
            log.error("Error parsing job explain gemini response: {}", e.getMessage());
            throw new RuntimeException(e);
        }

        return jobResponses;


    }

    private List<JobResponse> mapSearchResult(SearchResult result) {
        if(result == null || result.getDocuments() == null) {
            return Collections.emptyList();
        }

        return result.getDocuments().stream().map(this::mapToJobResponse).toList();
    }

    private JobResponse mapToJobResponse(Document doc) {
        String skills = doc.getString("skillNames");
        return JobResponse.builder()
                .id(Integer.parseInt(doc.getString("id")))
                .title(doc.getString("title"))
                .city(doc.getString("city"))
                .district(doc.getString("districtName"))
                .jobLevelName(doc.getString("jobLevelName"))
                .salaryMin(Integer.parseInt(doc.getString("salaryMin")))
                .salaryMax(Integer.parseInt(doc.getString("salaryMax")))
                .jobSkills(Arrays.stream(skills.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList())
                .image(doc.getString("image"))
                .matchPercentage(
                        (int) (1 - Double.parseDouble(doc.getString("score")) * 100)
                )
                .build();
    }

    private String buildExplainPrompt(List<JobResponse> jobs, CvProfile cvProfile) {
        String jobText = jobs.stream().map(job -> """
                jobID: %d
                title: %s
                city: %s
                district: %s
                jobLevel: %s
                skills: %s
                """.formatted(job.getId(), job.getTitle(), job.getCity(), job.getDistrict(), job.getJobLevelName(), job.getJobSkills()))
                .collect(Collectors.joining("\n"));

        return """
                Dưới đây là thông tin ứng viên:
                %s
    
                Danh sách công việc:
                %s
    
                Hãy trả về JSON dạng:
                [
                  { "jobId": "...", "explanation": "..." }
                ]
                Giải thích ngắn gọn 1-2 câu vì sao phù hợp.
                Không trả về text ngoài JSON.
                """.formatted(cvProfile.buildVectorContent(), jobText);
    }
}
