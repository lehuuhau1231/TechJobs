package com.lhh.techjobs.service;


import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContentModerator {

    private static final List<Pattern> BANNED_PATTERNS = new ArrayList<>();

    static {
        List<String> rawKeywords = List.of(
            "lừa đảo", "đa cấp", "việc nhẹ lương cao", "cờ bạc", "cá độ",
            "game bài", "đánh bài", "rửa tiền", "mại dâm", "đồi trụy", "ma túy"
        );

        for (String keyword : rawKeywords) {
            String regexPattern = generateFlexRegex(keyword);
            BANNED_PATTERNS.add(Pattern.compile(regexPattern, Pattern.CASE_INSENSITIVE));
        }
    }

    private static String generateFlexRegex(String keyword) {
        StringBuilder regex = new StringBuilder();
        String noise = "[\\s._-]*"; 

        for (int i = 0; i < keyword.length(); i++) {
            char c = keyword.charAt(i);
            
            if (c == ' ') {
                regex.append("[\\s._-]+");
            } else {
                regex.append(Pattern.quote(String.valueOf(c)));
                if (i < keyword.length() - 1 && keyword.charAt(i + 1) != ' ') {
                    regex.append(noise);
                }
            }
        }
        return regex.toString();
    }

    public static String checkViolation(String text) {
        if (text == null || text.isBlank()) return null;

        for (Pattern pattern : BANNED_PATTERNS) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                return matcher.group(); 
            }
        }
        return null;
    }
}