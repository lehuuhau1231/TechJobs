package com.lhh.techjobs.infrastructure.redis;

import com.lhh.techjobs.dto.redis.RedisIndexDefinition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisIndexInitializer {
    private final JedisPooled jedis;

    public void initIndex(RedisIndexDefinition redisIndexDefinition) {
        try {
            if(isIndexExists(redisIndexDefinition.indexName())) {
                log.info("Index {} already exists", redisIndexDefinition.indexName());
                return;
            }

            jedis.ftCreate(
                    redisIndexDefinition.indexName(),
                    FTCreateParams.createParams()
                            .on(IndexDataType.HASH)
                            .addPrefix(redisIndexDefinition.prefixName()),
                    redisIndexDefinition.schemaFields()
            );

            log.info("Created index {} success", redisIndexDefinition.indexName());
        } catch (Exception e) {
            log.error("Error when create index: {}", e.getMessage(), e);
        }
    }

    private boolean isIndexExists(String indexName) {
        return jedis.ftList().contains(indexName);
    }
}
