package com.lhh.techjobs.dto.redis;

import java.util.Map;

public interface Vectorizable {
    String getId();
    String buildVectorContent();
    Map<String, String> toMap();
    Map<String, String> toMapWithUrl(String baseUrl);
}
