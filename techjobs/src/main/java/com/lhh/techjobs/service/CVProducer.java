package com.lhh.techjobs.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.lhh.techjobs.entity.Candidate;
import com.lhh.techjobs.exception.AppException;
import com.lhh.techjobs.exception.ErrorCode;
import com.lhh.techjobs.repository.CandidateRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CVProducer {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CV_QUEUE = "cvQueue";
    Cloudinary cloudinaryClient;
    CandidateRepository candidateRepository;

    public void queueCVFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_INVALID);
        }
        updateCandidateCV(file);
        Map<String, Object> payload = new HashMap<>();
        payload.put("fileName", file.getOriginalFilename());
        payload.put("content", file.getBytes());
        //Serializer file về dạng byte[] chứ redis nếu lưu MultipartFile trực tiếp khi lấy ra sẽ lỗi định dạng chữ
        //còn nếu ban đầu là String thì không cần chuển về byte[]
        redisTemplate.opsForList().rightPush(CV_QUEUE, payload);
        //thêm phần tử vào cuối FIFO, leftPop sẽ lấy phần tử đầu tiên
    }

    private void updateCandidateCV(MultipartFile cvFile) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserEmail(email);
        if (candidate != null && cvFile != null && !cvFile.isEmpty()) {
            try {
                Map uploadResult = cloudinaryClient.uploader().upload(cvFile.getBytes(),
                        ObjectUtils.asMap("resource_type", "auto"));
                candidate.setCv(uploadResult.get("secure_url").toString());
                candidateRepository.save(candidate);
            } catch (IOException e) {
                throw new RuntimeException("Failed to upload CV file", e);
            }
        } else {
            throw new AppException(ErrorCode.FILE_INVALID);
        }
    }
}
