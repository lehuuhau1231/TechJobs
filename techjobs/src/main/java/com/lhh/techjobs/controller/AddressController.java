package com.lhh.techjobs.controller;
import com.lhh.techjobs.service.ExternalApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RequestMapping("/api/address")
@RestController
@RequiredArgsConstructor
public class AddressController {
    @Value("${address.provincesUrl}")
    private String provincesUrl;

    @Value("${address.districtsUrl}")
    private String districtsUrl;

    private final ExternalApiService externalApiService;

    @GetMapping("/provinces")
    public ResponseEntity<Object> getProvince(@RequestParam(required = false) String query) {
        return ResponseEntity.ok(externalApiService.callExternalApi(provincesUrl, query, null));
    }

    @GetMapping("/districts/{provinceId}")
    public ResponseEntity<Object> getDistrict(@PathVariable Integer provinceId, @RequestParam(required = false) String query) {
        return ResponseEntity.ok(externalApiService.callExternalApi(districtsUrl, query, provinceId));
    }
}
