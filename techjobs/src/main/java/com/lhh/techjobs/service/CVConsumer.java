package com.lhh.techjobs.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lhh.techjobs.dto.response.CvExtractedResponse;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.entity.CvProfile;
import com.lhh.techjobs.entity.Employer;
import com.lhh.techjobs.mapper.CvProfileMapper;
import com.lhh.techjobs.repository.CandidateRepository;
import com.lhh.techjobs.repository.CvProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CVConsumer {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CV_QUEUE = "cvQueue";
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final CvProfileRepository cvProfileRepository;
    private final CvProfileMapper cvProfileMapper;
    private final CandidateRepository candidateRepository;

    @Async
    public void processQueue(Candidate candidate) {
        log.info("Waiting for queue to process");
        try {
            Map<String, Object> cvData = (Map<String, Object>) redisTemplate.opsForList().leftPop(CV_QUEUE, 5, TimeUnit.SECONDS);

            if (cvData != null) {
                byte[] content = (byte[]) cvData.get("content");
                String parseCV = parseCV(content);
                String json = geminiService.extractInfoFromCV(parseCV);
                CvExtractedResponse cvExtractedResponse = this.parseToDto(json);
                Candidate saved = this.saveToDb(cvExtractedResponse, parseCV, candidate);
                log.info("Saved CV: {}", saved);
            }
        } catch (Exception e) {
            log.error("Error processing queue", e);
        }
    }

    private String parseCV(byte[] fileBytes) throws IOException {
        try (PDDocument document = PDDocument.load(new ByteArrayInputStream(fileBytes))) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private CvExtractedResponse parseToDto(String json) {
        try {
            log.info("DTO Mapper: {}", objectMapper.readValue(json, CvExtractedResponse.class));
            return objectMapper.readValue(json, CvExtractedResponse.class);
        } catch (JsonProcessingException e) {
            log.warn("JSON parse failed, try to extract object only. Raw: {}", json);
            return null;
        }
    }

    // ===== 3) Map DTO -> Entity -> Save =====
    private Candidate saveToDb(CvExtractedResponse dto, String rawText, Candidate candidate) {
        String skillsJson = "";
        try {
            skillsJson = objectMapper.writeValueAsString(dto.getSkills());
        } catch (Exception ignore) { }

        CvProfile entity = cvProfileMapper.toCvProfile(dto);
        entity.setSkills(skillsJson);
        entity.setRawText(rawText);
        cvProfileRepository.save(entity);
        candidate.setCvProfile(entity);

        return candidateRepository.save(candidate);
    }
}
