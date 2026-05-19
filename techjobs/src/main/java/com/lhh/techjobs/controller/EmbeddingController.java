package com.lhh.techjobs.controller;

import com.lhh.techjobs.service.EmbeddingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/embedding")
@RequiredArgsConstructor
public class EmbeddingController {
    private final EmbeddingService embeddingService;

    @GetMapping
    public float[] generateEmbedding() {
        return embeddingService.getEmbedding("Hello, world!");
    }
}
