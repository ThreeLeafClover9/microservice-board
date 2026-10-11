package com.example.boardservice.service;

import com.example.boardservice.client.PointClient;
import com.example.boardservice.client.UserClient;
import com.example.boardservice.domain.Board;
import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateRequestDto;
import com.example.boardservice.domain.BoardRepository;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.dto.UserResponseDto;
import com.example.boardservice.event.BoardCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserClient userClient;
    private final PointClient pointClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BoardService(BoardRepository boardRepository, UserClient userClient, PointClient pointClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.boardRepository = boardRepository;
        this.userClient = userClient;
        this.pointClient = pointClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void create(CreateRequestDto createRequestDto) {
        boolean isPointDeducted = false;
        Long savedBoardId = null;
        boolean isBoardCreated = false;

        try {
            pointClient.deductPoint(createRequestDto.getUserId(), 100);
            isPointDeducted = true;

            Board board = new Board(
                    createRequestDto.getTitle(),
                    createRequestDto.getContent(),
                    createRequestDto.getUserId()
            );
            Board savedBoard = boardRepository.save(board);
            savedBoardId = savedBoard.getId();
            isBoardCreated = true;

            BoardCreatedEvent boardCreatedEvent = new BoardCreatedEvent(createRequestDto.getUserId());
            kafkaTemplate.send("board.created", toJsonString(boardCreatedEvent));
        } catch (Exception e) {
            if (isBoardCreated) {
                boardRepository.deleteById(savedBoardId);
            }

            if (isPointDeducted) {
                pointClient.addPoint(createRequestDto.getUserId(), 100);
            }

            throw e;
        }
    }

    private String toJsonString(Object object) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new RuntimeException("Json 직렬화 실패");
        }
    }

    public BoardResponseDto getBoard(Long id) {
        Board board = boardRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        Optional<UserResponseDto> optionalUserResponseDto = userClient.fetchUser(board.getUserId());
        UserDto userDto = null;
        if (optionalUserResponseDto.isPresent()) {
            UserResponseDto userResponseDto = optionalUserResponseDto.get();
            userDto = new UserDto(
                    userResponseDto.getId(),
                    userResponseDto.getName()
            );
        }

        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                userDto
        );
    }

    public List<BoardResponseDto> getBoards() {
        List<Board> boards = boardRepository.findAll();

        List<Long> userIds = boards.stream()
                .map(Board::getUserId)
                .distinct()
                .toList();
        List<UserResponseDto> userResponseDtos = userClient.fetchUsersByIds(userIds);

        Map<Long, UserDto> userMap = new HashMap<>();
        for (UserResponseDto userResponseDto : userResponseDtos) {
            Long id = userResponseDto.getId();
            String name = userResponseDto.getName();
            userMap.put(id, new UserDto(id, name));
        }

        return boards.stream()
                .map(board -> new BoardResponseDto(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        userMap.get(board.getUserId())
                ))
                .toList();
    }
}
