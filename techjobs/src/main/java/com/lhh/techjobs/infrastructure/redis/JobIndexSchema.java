package com.lhh.techjobs.infrastructure.redis;
import com.lhh.techjobs.dto.redis.RedisIndexDefinition;
import org.springframework.stereotype.Service;
import redis.clients.jedis.search.schemafields.*;

import java.util.Map;

@Service
public class JobIndexSchema extends IndexSchema{
    public JobIndexSchema(RedisIndexInitializer redisIndexInitializer) {
        super(redisIndexInitializer);
    }

    @Override
    public RedisIndexDefinition indexSchemaDefinition() {
        return new RedisIndexDefinition(
                "jobIdx",
                "job:",
                new SchemaField[] {
                        TextField.of("id"),
                        TextField.of("title"),
                        TagField.of("city"),
                        NumericField.of("salaryMin"),
                        NumericField.of("salaryMax"),
                        TagField.of("jobLevelName"),
                        TagField.of("districtName"),
                        TagField.of("skillNames"),
                        TextField.of("image"),
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
