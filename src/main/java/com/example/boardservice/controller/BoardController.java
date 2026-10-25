package com.example.boardservice.controller;

import com.example.boardservice.dto.BoardResponseDto;
import com.example.boardservice.dto.CreateRequestDto;
import com.example.boardservice.service.BoardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
public class BoardController {
    private final BoardService boardService;

    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    @PostMapping
    public ResponseEntity<Void> create(@RequestBody CreateRequestDto createRequestDto, @RequestHeader("X-User-Id") Long userId) {
        boardService.create(createRequestDto, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponseDto> getBoard(@PathVariable Long id) {
        BoardResponseDto boardResponseDto = boardService.getBoard(id);
        return ResponseEntity.ok(boardResponseDto);
    }

    @GetMapping
    public ResponseEntity<List<BoardResponseDto>> getBoards() {
        List<BoardResponseDto> boardResponseDtos = boardService.getBoards();
        return ResponseEntity.ok(boardResponseDtos);
    }
}
