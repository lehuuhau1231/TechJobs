package com.lhh.techjobs.repository.redis;

import com.lhh.techjobs.dto.redis.JobVectorDto;
import com.lhh.techjobs.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JobVectorRepository {

    private static final String INDEX_NAME = "job_idx";
    private static final String PREFIX_KEY = "job:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final EmbeddingService embeddingService;
    private final JedisPooled jedis;


    public void saveJob(JobVectorDto jobVectorDto) {
        try {
            String key = PREFIX_KEY + jobVectorDto.getId();

            // Tạo embedding vector từ nội dung job
            String vectorContent = jobVectorDto.getVectorContent();
            float[] embedding = embeddingService.getEmbedding(vectorContent);

            Map<String, Object> jobMap = new HashMap<>();
            jobMap.put("id", jobVectorDto.getId().toString());
            jobMap.put("title", jobVectorDto.getTitle() != null ? jobVectorDto.getTitle() : "");
            jobMap.put("description", jobVectorDto.getDescription() != null ? jobVectorDto.getDescription() : "");
            jobMap.put("salaryMin", jobVectorDto.getSalaryMin() != null ? jobVectorDto.getSalaryMin().toString() : "0");
            jobMap.put("salaryMax", jobVectorDto.getSalaryMax() != null ? jobVectorDto.getSalaryMax().toString() : "0");
            jobMap.put("jobLevelName", jobVectorDto.getJobLevelName() != null ? jobVectorDto.getJobLevelName() : "");
            jobMap.put("cityName", jobVectorDto.getCityName() != null ? jobVectorDto.getCityName() : "");
            jobMap.put("districtName", jobVectorDto.getDistrictName() != null ? jobVectorDto.getDistrictName() : "");
//            jobMap.put("skillNames", String.join(",", jobVectorDto.getSkillNames() != null ? jobVectorDto.getSkillNames() : new ArrayList<>()));
            jobMap.put("skillNames", jobVectorDto.getSkillNames());
            jobMap.put("vector_content", vectorContent);

            // Chuyển float[] thành byte[] để lưu vào Redis
            byte[] vectorBytes = new byte[embedding.length * 4];
            for (int i = 0; i < embedding.length; i++) {
                int bits = Float.floatToIntBits(embedding[i]);
                vectorBytes[i * 4] = (byte) (bits);
                vectorBytes[i * 4 + 1] = (byte) (bits >> 8);
                vectorBytes[i * 4 + 2] = (byte) (bits >> 16);
                vectorBytes[i * 4 + 3] = (byte) (bits >> 24);
            }

            jobMap.put("embedding", vectorBytes);

            // Chuyển Map<String, Object> thành Map<String, String> cho Jedis
            Map<String, String> jedisMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : jobMap.entrySet()) {
                if (entry.getValue() instanceof byte[]) {
                    // Với embedding vector, chúng ta cần xử lý riêng
                    continue; // Sẽ xử lý riêng ở dưới
                } else {
                    jedisMap.put(entry.getKey(), entry.getValue().toString());
                }
            }

            // Lưu vào Redis bằng Jedis - lưu các field text trước
            jedis.hset(key, jedisMap);

            // Lưu embedding vector riêng biệt - encode byte[] thành Base64 string
            String encodedVector = Base64.getEncoder().encodeToString(vectorBytes);
            jedis.hset(key, "embedding", encodedVector);

            log.info("Đã lưu job ID {} vào Redis vector database", jobVectorDto.getId());

            Map<String, String> jobData = jedis.hgetAll(key);

            if (jobData.isEmpty()) {
                System.out.println("❌ Không tìm thấy job trong Redis");
                return;
            }

            System.out.println("✅ Thông tin job:");
            jobData.forEach((k, v) -> {
                if ("embedding".equals(k)) {
                    System.out.println(k + " = (Base64 length) " + v.length());
                } else {
                    System.out.println(k + " = " + v);
                }
            });

        } catch (Exception e) {
            log.error("Lỗi khi lưu job ID {} vào Redis: {}", jobVectorDto.getId(), e.getMessage());
            throw new RuntimeException("Không thể lưu job vào Redis", e);
        }
    }

    public void saveJobs(List<JobVectorDto> jobs) {
        log.info("Bắt đầu lưu {} job vào Redis vector database", jobs.size());
        for (JobVectorDto job : jobs) {
            saveJob(job);
        }
        log.info("Hoàn thành lưu {} job vào Redis vector database", jobs.size());
    }

    public List<JobVectorDto> searchJobsByVector(String query, int limit) {
        try {
            // Tạo embedding cho query tìm kiếm
            float[] queryEmbedding = embeddingService.getEmbedding(query);

            // Chuyển float[] thành byte[] để tìm kiếm
            byte[] queryVectorBytes = new byte[queryEmbedding.length * 4];
            for (int i = 0; i < queryEmbedding.length; i++) {
                int bits = Float.floatToIntBits(queryEmbedding[i]);
                queryVectorBytes[i * 4] = (byte) (bits);
                queryVectorBytes[i * 4 + 1] = (byte) (bits >> 8);
                queryVectorBytes[i * 4 + 2] = (byte) (bits >> 16);
                queryVectorBytes[i * 4 + 3] = (byte) (bits >> 24);
            }

            // Tạo query vector search với KNN
            Query q = new Query("*=>[KNN " + limit + " @embedding $BLOB AS score]")
                    .addParam("BLOB", queryVectorBytes)
                    .returnFields("id", "title", "description", "salaryMin", "salaryMax",
                                "jobLevelName", "cityName", "districtName", "skillNames", "score")
                    .setSortBy("score", true)
                    .limit(0, limit);

            SearchResult result = jedis.ftSearch(INDEX_NAME, q);
            List<JobVectorDto> jobs = new ArrayList<>();

            for (Document doc : result.getDocuments()) {
                Map<String, Object> properties = new HashMap<>();

                // Chuyển đổi từ Iterable thành Map
                for (Map.Entry<String, Object> entry : doc.getProperties()) {
                    properties.put(entry.getKey(), entry.getValue());
                }

                List<String> skillsList = new ArrayList<>();
                String skillsStr = (String) properties.get("skillNames");
                if (skillsStr != null && !skillsStr.isEmpty()) {
                    skillsList = List.of(skillsStr.split(","));
                }

                JobVectorDto job = JobVectorDto.builder()
                        .id(Integer.parseInt((String) properties.get("id")))
                        .title((String) properties.get("title"))
                        .description((String) properties.get("description"))
                        .salaryMin(Integer.parseInt((String) properties.get("salaryMin")))
                        .salaryMax(Integer.parseInt((String) properties.get("salaryMax")))
                        .jobLevelName((String) properties.get("jobLevelName"))
                        .cityName((String) properties.get("cityName"))
                        .districtName((String) properties.get("districtName"))
                        .skillNames(skillsList)
                        .build();

                jobs.add(job);
            }

            return jobs;

        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm job với query '{}': {}", query, e.getMessage());
            return new ArrayList<>();
        }
    }

    public void deleteJob(Integer jobId) {
        try {
            redisTemplate.delete(PREFIX_KEY + jobId);
            log.info("Đã xóa job ID {} khỏi Redis", jobId);
        } catch (Exception e) {
            log.error("Lỗi khi xóa job ID {} khỏi Redis: {}", jobId, e.getMessage());
        }
    }
}
