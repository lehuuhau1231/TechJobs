package com.lhh.techjobs.service;

import io.github.bucket4j.Bucket;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    public Bucket resolveBucket(String key) {
        return buckets.computeIfAbsent(key, this::newBucket);
    }

    public Bucket newBucket(String key) {
        return Bucket.builder()
                .addLimit(limit -> limit.capacity(10)
                        .refillGreedy(10, Duration.ofMinutes(1)))
                .build();
    }
}
