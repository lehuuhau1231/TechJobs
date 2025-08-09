package com.lhh.techjobs.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.utils.ObjectUtils;


@Configuration
public class Cloudinary {
    @Bean
    public com.cloudinary.Cloudinary cloudinaryClient() {
        return new com.cloudinary.Cloudinary(ObjectUtils.asMap(
                "cloud_name", "dndsrbf9s",
                "api_key", "932944391659178",
                "api_secret", "_UlDjHd_T5WxNV0iZMMN9tGJuy0",
                "secure", true
        ));
    }
}
