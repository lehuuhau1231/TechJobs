package com.lhh.techjobs.service;

import com.lhh.techjobs.dto.redis.CareerChatbotDTO;
import com.lhh.techjobs.repository.ITCareerRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CareerVectorService {
    ITCareerRepository itCareerRepository;
    JobRedisService jobRedisService;

    @Transactional
    public void syncAllCareerToRedis() {
        log.info("Starting to synchronize all careers to Redis Vector Database...");

        int pageSize = 50;
        int pageNumber = 0;
        int totalSynced = 0;
        Page<CareerChatbotDTO> careers;

        do {
            careers = itCareerRepository.findTop50ByVectorUpdatedAtIsNull(PageRequest.of(pageNumber, pageSize));
            List<CareerChatbotDTO> careerList = careers.getContent();
            if(careers.isEmpty()) break;
            jobRedisService.saveAllJob(careerList, "career: ");

            List<Integer> careerIds = careerList.stream().map(career -> Integer.parseInt(career.getId())).toList();
            if(careerIds.isEmpty()) break;
            itCareerRepository.updateVectorUpdatedAtForCareers(careerIds);

            totalSynced += careerList.size();
            pageNumber++;
            log.info("Synchronized: {} size, page size: {}, total page: {}", careerList.size(), pageNumber + 1, totalSynced);

            pageNumber++;
        } while (careers.hasNext());

        log.info("Completed synchronize {} career to vector database Redis", totalSynced);
    }
}
