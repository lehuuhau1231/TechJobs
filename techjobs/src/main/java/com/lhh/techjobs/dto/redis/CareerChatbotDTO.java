package com.lhh.techjobs.dto.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareerChatbotDTO implements Vectorizable{
    private Integer id;
    private String jobTitle;
    private String keySkills;
    private String characterTraits;
    private String hollandCode;

    @Override
    public String getId() {
        return id != null ? id.toString() : null;
    }

    @Override
    public String buildVectorContent() {
        return String.format("""
                Job title: %s | Key skills: %s | Character traits: %s | Holland Code: %s
                """,
                jobTitle, keySkills, characterTraits, hollandCode);
    }

    @Override
    public Map<String, String> toMap() {
        return Map.of(
                "id", stringify(id),
                "jobTitle", stringify(jobTitle),
                "keySkills", stringify(safeLowerCase(keySkills)),
                "characterTraits", stringify(safeLowerCase(characterTraits))
        );
    }

    private String stringify(Object value) {
        return value != null ? value.toString() : "";
    }

    private String safeLowerCase(String value) {
        return value != null ? value.toLowerCase() : null;
    }
}
