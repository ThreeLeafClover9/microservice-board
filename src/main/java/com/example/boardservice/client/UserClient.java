package com.example.boardservice.client;

import com.example.boardservice.dto.AddActivityScoreRequestDto;
import com.example.boardservice.dto.UserResponseDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Component
public class UserClient {
    private static final Logger log = LoggerFactory.getLogger(UserClient.class);
    private final RestClient restClient;

    public UserClient(@Value("${client.user-service.url}") String userServiceUrl) {
        restClient = RestClient.builder().baseUrl(userServiceUrl).build();
    }

    public Optional<UserResponseDto> fetchUser(Long id) {
        try {
            UserResponseDto userResponseDto = restClient.get()
                    .uri("/users/{id}", id)
                    .retrieve()
                    .body(UserResponseDto.class);

            return Optional.ofNullable(userResponseDto);
        } catch (RestClientException e) {
            log.error("사용자 정보 조회 실패", e);

            return Optional.empty();
        }
    }

    public List<UserResponseDto> fetchUsersByIds(List<Long> ids) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/users")
                            .queryParam("ids", ids)
                            .build()
                    )
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });
        } catch (RestClientException e) {
            log.error("사용자 목록 조회 실패", e);

            return Collections.emptyList();
        }
    }

    public void addActivityScore(Long userId, int score) {
        AddActivityScoreRequestDto addActivityScoreRequestDto = new AddActivityScoreRequestDto(userId, score);

        try {
            restClient.post()
                    .uri("/users/activity-score/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(addActivityScoreRequestDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("활동 점수 적립 실패", e);
        }
    }
}
