package com.example.boardservice.consumer;

import com.example.boardservice.dto.SaveUserRequestDto;
import com.example.boardservice.event.UserSignedUpEvent;
import com.example.boardservice.service.UserService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserSignedUpEventConsumer {
    private final UserService userService;

    public UserSignedUpEventConsumer(UserService userService) {
        this.userService = userService;
    }

    @KafkaListener(topics = "user.signed-up", groupId = "board-service")
    public void consume(String message) {
        UserSignedUpEvent userSignedUpEvent = UserSignedUpEvent.formJson(message);

        SaveUserRequestDto saveUserRequestDto = new SaveUserRequestDto(userSignedUpEvent.getUserId(), userSignedUpEvent.getName());
        userService.save(saveUserRequestDto);
    }
}
