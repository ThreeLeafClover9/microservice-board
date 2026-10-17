package com.example.boardservice.client;

import com.example.boardservice.dto.AddPointRequestDto;
import com.example.boardservice.dto.DeductPointRequestDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class PointClient {
    private static final Logger log = LoggerFactory.getLogger(PointClient.class);
    private final RestClient restClient;

    public PointClient(@Value("${client.point-service.url}") String pointServiceUrl) {
        this.restClient = RestClient.builder().baseUrl(pointServiceUrl).build();
    }

    public void deductPoint(Long userId, int amount) {
        DeductPointRequestDto deductPointRequestDto = new DeductPointRequestDto(userId, amount);

        try {
            restClient.post()
                    .uri("/internal/points/duduct")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(deductPointRequestDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("포인트 차감 실패", e);
        }
    }

    public void addPoint(Long userId, int amount) {
        AddPointRequestDto addPointRequestDto = new AddPointRequestDto(userId, amount);

        try {
            restClient.post()
                    .uri("/internal/points/add")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(addPointRequestDto)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            log.error("포인트 적립 실패", e);
        }
    }
}
