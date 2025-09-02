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
    CandidateRepository candidateRepository;
    CloudinaryService cloudinaryService;
    CVConsumer cvConsumer;

    public String queueCVFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.FILE_INVALID);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Candidate candidate = candidateRepository.findByUserEmail(email);

        String fileUrl = cloudinaryService.uploadFile(file);
        candidate.setCv(fileUrl);
        candidateRepository.save(candidate);

        Map<String, Object> payload = new HashMap<>();
        payload.put("fileName", file.getOriginalFilename());
        payload.put("content", file.getBytes());
        //Serializer file về dạng byte[] chứ redis nếu lưu MultipartFile trực tiếp khi lấy ra sẽ lỗi định dạng chữ
        //còn nếu ban đầu là String thì không cần chuển về byte[]
        redisTemplate.opsForList().rightPush(CV_QUEUE, payload);
        //thêm phần tử vào cuối FIFO, leftPop sẽ lấy phần tử đầu tiên

        cvConsumer.processQueue(candidate);
        return fileUrl;
    }
}
