package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.redis.JobVectorDTO;
import com.lhh.techjobs.dto.redis.Vectorizable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.Pipeline;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobRedisService {
    private final JedisPooled jedis;
    private final EmbeddingService embeddingService;

    public <T extends Vectorizable> void saveAllJob(List<T> jobs, String keyPrefix) {
        try(Pipeline pipeline = jedis.pipelined()) {
            for (T job : jobs) {
                String text = job.buildVectorContent();
                float[] embedding = embeddingService.getEmbedding(text);
                byte[] vectorBlob = floatArrayToBytes(embedding);

                String redisKey = keyPrefix + job.getId();

                // Tạo Map cho tất cả các field
                Map<String, String> metadata = job.toMap();
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

    private byte[] floatArrayToBytes(float[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * array.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float v : array) {
            buffer.putFloat(v);
        }
        return buffer.array();
    }
}
