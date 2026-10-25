package com.example.boardservice.service;

import com.example.boardservice.client.PointClient;
import com.example.boardservice.domain.Board;
import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateRequestDto;
import com.example.boardservice.domain.BoardRepository;
import com.example.boardservice.dto.UserDto;
import com.example.boardservice.event.BoardCreatedEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Service
public class BoardService {
    private final BoardRepository boardRepository;
    private final PointClient pointClient;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public BoardService(BoardRepository boardRepository, PointClient pointClient, KafkaTemplate<String, String> kafkaTemplate) {
        this.boardRepository = boardRepository;
        this.pointClient = pointClient;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void create(CreateRequestDto createRequestDto, Long userId) {
        boolean isPointDeducted = false;
        Long savedBoardId = null;
        boolean isBoardCreated = false;

        try {
            pointClient.deductPoint(userId, 100);
            isPointDeducted = true;

            Board board = new Board(
                    createRequestDto.getTitle(),
                    createRequestDto.getContent(),
                    userId
            );
            Board savedBoard = boardRepository.save(board);
            savedBoardId = savedBoard.getId();
            isBoardCreated = true;

            BoardCreatedEvent boardCreatedEvent = new BoardCreatedEvent(userId);
            kafkaTemplate.send("board.created", toJsonString(boardCreatedEvent));
        } catch (Exception e) {
            if (isBoardCreated) {
                boardRepository.deleteById(savedBoardId);
            }
            if (isPointDeducted) {
                pointClient.addPoint(userId, 100);
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

        return new BoardResponseDto(
                board.getId(),
                board.getTitle(),
                board.getContent(),
                new UserDto(board.getUser().getId(), board.getUser().getName())
        );
    }

    public List<BoardResponseDto> getBoards() {
        List<Board> boards = boardRepository.findAll();

        return boards.stream()
                .map(board -> new BoardResponseDto(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        new UserDto(board.getUser().getId(), board.getUser().getName())
                ))
                .toList();
    }
}
