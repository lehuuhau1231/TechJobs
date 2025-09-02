package com.lhh.techjobs.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ExternalApiService {
    RestTemplate restTemplate;

    public Object callExternalApi(String baseUrl, String query, Integer provinceId) {
        String s = query != null && !query.trim().isEmpty() ? "?query=" + query : "";
        if(provinceId != null) {
            String url = baseUrl + "/" + provinceId + s;
            return restTemplate.getForObject(url, Object.class);
        }
        String url = baseUrl + s;
        return restTemplate.getForObject(url, Object.class);
    }
}
