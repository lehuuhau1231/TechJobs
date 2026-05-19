package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.redis.JobJsonDTO;
import com.lhh.techjobs.dto.redis.JobVectorDTO;
import com.lhh.techjobs.dto.redis.Vectorizable;
import com.lhh.techjobs.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobRedisService {
    private final JedisPooled jedis;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper;

    public <T extends Vectorizable> void  saveAllJob(List<T> jobs, String keyPrefix) {
        try(Pipeline pipeline = jedis.pipelined()) {
            for (T job : jobs) {
                String text = job.buildVectorContent();
                float[] embedding = embeddingService.getEmbedding(text);
                byte[] vectorBlob = floatArrayToBytes(embedding);

                String redisKey = keyPrefix + job.getId();

                // Tạo Map cho tất cả các field
                Map<String, String> metadata = job.toMapWithUrl("http://localhost:3000/job-detail/");
                // Save metadata
                pipeline.hset(redisKey, metadata);

                // Save vector
                pipeline.hset(redisKey.getBytes(), Map.of("vector".getBytes(), vectorBlob));
            }
            pipeline.sync();
        } catch (Exception e) {
            log.error("Error saved job list into Redis Vector Database: {}", e.getMessage(), e);
            throw new RuntimeException("Error saved job list: " + e.getMessage(), e);
        }
    }

    public void ingestJobsFromJsonl(InputStream inputStream, String keyPrefix) {
        int batchSize = 50;
        int currentLine = 0;
        int skipCount = 50;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            List<JobJsonDTO> batch = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                currentLine++;
                // Bỏ qua các dòng đã chạy thành công
                if (currentLine <= skipCount) continue;
                try {
                    JobJsonDTO job = objectMapper.readValue(line, JobJsonDTO.class);
                    batch.add(job);
                } catch (Exception e) {
                    log.error("Error parsing line: {}", line, e);
                    continue;
                }

                if (batch.size() >= batchSize) {
                    processBatchWithRetry(batch, keyPrefix);
                    batch.clear();
                }
            }
            if (!batch.isEmpty()) {
                processBatchWithRetry(batch, keyPrefix);
            }
            log.info("Finished ingesting jobs from {}", inputStream);
        } catch (Exception e) {
            log.error("Error ingesting jobs from JSONL: {}", e.getMessage(), e);
            throw new RuntimeException("Ingestion failed: " + e.getMessage(), e);
        }
    }

    private void processBatch(List<JobJsonDTO> batch, String keyPrefix) {
        List<String> texts = batch.stream().map(JobJsonDTO::buildVectorContent).toList();
        List<float[]> embeddings = embeddingService.getEmbeddingsBatch(texts);

        if (embeddings.size() != batch.size()) {
            log.error("Embedding batch size mismatch: expected {}, got {}", batch.size(), embeddings.size());
            throw new RuntimeException("Embedding batch size mismatch");
        }

        try (Pipeline pipeline = jedis.pipelined()) {
            for (int i = 0; i < batch.size(); i++) {
                JobJsonDTO job = batch.get(i);
                float[] embedding = embeddings.get(i);
                byte[] vectorBlob = floatArrayToBytes(embedding);

                String redisKey = keyPrefix + job.getId();
                Map<String, String> metadata = job.toMap();

                pipeline.hset(redisKey, metadata);
                pipeline.hset(redisKey.getBytes(), Map.of("vector".getBytes(), vectorBlob));
            }
            pipeline.sync();
            log.info("Successfully indexed batch of {} jobs", batch.size());
        } catch (Exception e) {
            log.error("Error saving batch to Redis: {}", e.getMessage(), e);
            throw new RuntimeException("Batch save failed: " + e.getMessage(), e);
        }
    }

    private void processBatchWithRetry(List<JobJsonDTO> batch, String keyPrefix) {
        int maxRetries = 5;
        int retryCount = 0;
        long waitTime = 2000;

        while (retryCount < maxRetries) {
            try {
                processBatch(batch, keyPrefix);
                return;
            } catch (HttpClientErrorException.TooManyRequests e) {
                retryCount++;
                log.warn("Lỗi 429. Thử lại lần {} sau {}ms", retryCount, waitTime);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
                waitTime *= 2;
            }
        }
        throw new RuntimeException("Đã thử lại nhiều lần nhưng vẫn thất bại do Rate Limit.");
    }

    private byte[] floatArrayToBytes(float[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * array.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float v : array) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }
}
