package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.redis.JobVectorDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Protocol;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TextField;
import redis.clients.jedis.search.schemafields.TagField;
import redis.clients.jedis.search.schemafields.NumericField;
import redis.clients.jedis.search.schemafields.VectorField;
import redis.clients.jedis.exceptions.JedisDataException;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobRedisService {
    private final JedisPooled jedis;
    private final EmbeddingService embeddingService;

    private static final String INDEX_NAME = "jobIdx";

    @PostConstruct
    public void initIndex() {
        try {
            // Nếu tồn tại thì thôi
            try {
                jedis.ftInfo(INDEX_NAME);
                log.info("Index {} đã tồn tại", INDEX_NAME);
                return;
            } catch (JedisDataException notFound) {
                log.info("Index chưa tồn tại, đang tạo mới...");
            }

            // Khai báo schema (Jedis 5.x dùng SchemaField[])
            SchemaField[] schema = new SchemaField[] {
                    TextField.of("id"),
                    TextField.of("title"),
                    TagField.of("city"),
                    NumericField.of("salaryMin"),
                    NumericField.of("salaryMax"),
                    TagField.of("jobLevelName"),
                    TagField.of("districtName"),
                    TagField.of("skillNames"),
                    TextField.of("image"),

                    // Vector field: FLAT + COSINE, dim = 768
                    VectorField.builder()
                            .fieldName("vector")
                            .algorithm(VectorField.VectorAlgorithm.FLAT)
                            .attributes(Map.of(
                                    "TYPE", "FLOAT32",
                                    "DIM", 768,
                                    "DISTANCE_METRIC", "COSINE"
                            ))
                            .build()
            };

            jedis.ftCreate(
                    INDEX_NAME,
                    FTCreateParams.createParams()
                            .on(IndexDataType.HASH)     // hoặc JSON nếu bạn lưu bằng ReJSON
                            .addPrefix("job:"),
                    schema
            );

            log.info("Tạo index {} thành công", INDEX_NAME);
        } catch (Exception e) {
            log.error("Lỗi khi tạo index: {}", e.getMessage(), e);
        }
    }

    public void saveJob(JobVectorDto job) throws IOException {
        try {
            // Ghép text để embedding
            String text = job.getVectorContent();
            log.info("skills: {}", job.getSkillNames());
            float[] embedding = embeddingService.getEmbedding(text);
            byte[] vectorBlob = floatArrayToBytes(embedding);

            String redisKey = "job:" + job.getId();

            // Tạo Map cho tất cả các field
            Map<byte[], byte[]> fields = new HashMap<>();
            fields.put("id".getBytes(), job.getId().toString().getBytes());
            fields.put("title".getBytes(), job.getTitle().getBytes());
            fields.put("city".getBytes(), job.getCityName().toLowerCase().getBytes());
            fields.put("salaryMin".getBytes(), job.getSalaryMin().toString().getBytes());
            fields.put("salaryMax".getBytes(), job.getSalaryMax().toString().getBytes());
            fields.put("jobLevelName".getBytes(), job.getJobLevelName().getBytes());
            fields.put("districtName".getBytes(), job.getDistrictName().getBytes());
            fields.put(
                    "skillNames".getBytes(),
                    String.join(",", job.getSkillNames() != null ? job.getSkillNames() : new java.util.ArrayList<>()).getBytes()
            );
            fields.put("image".getBytes(), job.getImage().getBytes());
            fields.put("vector".getBytes(), vectorBlob);
            // Lưu các field text
            jedis.hset(redisKey.getBytes(), fields);

            log.debug("Đã lưu job {} vào Redis", job.getId());
        } catch (Exception e) {
            log.error("Lỗi khi lưu job {}: {}", job.getId(), e.getMessage());
            throw new IOException("Không thể lưu job vào Redis", e);
        }
    }

    public void saveAllJob(List<JobVectorDto> jobs) {
        for (JobVectorDto job : jobs) {
            try {
                saveJob(job);
            } catch (IOException e) {
                log.error("Lỗi khi lưu job {}: {}", job.getId(), e.getMessage());
            }
        }
    }

    /**
     * Helper: convert float array to byte array
     */
    private byte[] floatArrayToBytes(float[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * array.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float v : array) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }
}
