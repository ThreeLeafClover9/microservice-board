package com.example.boardservice.event;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

public class UserSignedUpEvent {
    private Long userId;
    private String name;

    public UserSignedUpEvent() {
    }

    public static UserSignedUpEvent formJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(json, UserSignedUpEvent.class);
        } catch (JacksonException e) {
            throw new RuntimeException("Json 파싱 실패");
        }
    }

    public Long getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }
}
