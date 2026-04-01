package com.lhh.techjobs.infrastructure.redis;

import com.lhh.techjobs.dto.redis.RedisIndexDefinition;
import org.springframework.stereotype.Service;
import redis.clients.jedis.search.schemafields.*;

import java.util.Map;
@Service
public class CareerChatbotIndexSchema extends IndexSchema{

    public CareerChatbotIndexSchema(RedisIndexInitializer redisIndexInitializer) {
        super(redisIndexInitializer);
    }

    @Override
    public RedisIndexDefinition indexSchemaDefinition() {
        return new RedisIndexDefinition(
                "careerIdx",
                "career:",
                new SchemaField[] {
                        TextField.of("id"),
                        TextField.of("jobTitle"),
                        TextField.of("keySkills"),
                        TextField.of("interests"),
                        TextField.of("workStyles"),
                        TextField.of("characterTraits"),

                        // Vector field: FLAT + COSINE, dim = 768
                        VectorField.builder()
                                .fieldName("vector")
                                .algorithm(VectorField.VectorAlgorithm.FLAT)
                                .attributes(Map.of(
                                        "TYPE", embeddingType,
                                        "DIM", embeddingDimension,
                                        "DISTANCE_METRIC", distanceMetric
                                ))
                                .build()
                });
    }
}
