package com.lhh.techjobs.service;

import com.google.genai.Client;
import com.lhh.techjobs.dto.response.JobResponse;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.repository.CandidateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class RecommendationService {
    private final JedisPooled jedis;
    private final EmbeddingService embeddingService;
    private final ChatModel chatModel;
    private final CandidateRepository candidateRepository;

    private static final String INDEX_NAME = "jobIdx";

    public List<JobResponse> recommendationFromCV() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CvProfile cvProfile = candidateRepository.findCvProfileIdByUserEmail(email);
        if (cvProfile == null) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        String cvText = cvProfile.getVectorContent();
        String city = cvProfile.getPreferredLocation();
        return recommendJobs(cvText, city);
    }

    public List<JobResponse> recommendationFromRequest() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        CvProfile cvProfile = candidateRepository.findCvProfileIdByUserEmail(email);
        if (cvProfile == null) {
            throw new AppException(ErrorCode.FILE_NOT_FOUND);
        }
        String cvText = cvProfile.getVectorContent();
        String city = cvProfile.getPreferredLocation();
        return recommendJobs(cvText, city);
    }

    private List<JobResponse> recommendJobs(String text, String city) {
        // B1: tạo embedding từ CV
        float[] embedding = embeddingService.getEmbedding(text);

        // B2: tìm job gần nhất bằng KNN
        byte[] vecBytes = floatArrayToBytes(embedding);

        Query q = new Query("@city:{" + city + "}=>[KNN 10 @vector $vec AS score]")
                .addParam("vec", vecBytes)
                .returnFields("id","title", "city", "districtName", "jobLevelName", "skillNames", "salaryMin", "salaryMax", "image", "score")
                .setSortBy("score", true)
                .dialect(2);

        SearchResult result = jedis.ftSearch(INDEX_NAME, q);

        List<JobResponse> jobResponses = new ArrayList<>();
        for (Document doc : result.getDocuments()) {
            String skills = doc.getString("skillNames");
            JobResponse job = JobResponse.builder()
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
                    .build();

            jobResponses.add(job);

            System.out.println("---- Job found ----");
            System.out.println("id" + doc.getString("id"));
            System.out.println("title" + doc.getString("title"));
            System.out.println("description" + doc.getString("description"));
            System.out.println("city" + doc.getString("city"));
            System.out.println("districtName" + doc.getString("districtName"));
            System.out.println("jobLevelName" + doc.getString("jobLevelName"));
            System.out.println("skillNames" + doc.getString("skillNames"));
            System.out.println("salaryMin" + doc.getString("salaryMin"));
            System.out.println("salaryMax" + doc.getString("salaryMax"));
            System.out.println("score" + doc.getString("score"));
        }

        return jobResponses;

        // B3: gọi Gemini để sinh gợi ý
//        String prompt = "CV: " + cvText + "\n\nCác công việc phù hợp:\n" +
//                String.join("\n", jobs) +
//                "\n\nHãy phân tích và đề xuất công việc nào phù hợp nhất, giải thích ngắn gọn.";
//
//        ChatResponse chatResp = chatModel.call(new Prompt(prompt));
//        return chatResp.getResult().getOutput().getText();
    }

    private byte[] floatArrayToBytes(float[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * array.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float v : array) buffer.putFloat(v);
        return buffer.array();
    }
}
