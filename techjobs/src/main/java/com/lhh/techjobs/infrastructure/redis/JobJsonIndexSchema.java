package com.lhh.techjobs.infrastructure.redis;

import com.lhh.techjobs.dto.redis.RedisIndexDefinition;
import org.springframework.stereotype.Service;
import redis.clients.jedis.search.schemafields.*;

import java.util.Map;

@Service
public class JobJsonIndexSchema extends IndexSchema{
    public JobJsonIndexSchema(RedisIndexInitializer redisIndexInitializer) {
        super(redisIndexInitializer);
    }

    @Override
    public RedisIndexDefinition indexSchemaDefinition() {
        return new RedisIndexDefinition(
                "jobJsonIdx",
                "jobJson:",
                new SchemaField[] {
                        TextField.of("id"),
                        TextField.of("title"),
                        TextField.of("address"),
                        TextField.of("jobLevelName"),
                        TextField.of("requirements"),
                        TextField.of("skillNames"),
                        TextField.of("softSkill"),
                        TextField.of("jobDetailUrl"),

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
