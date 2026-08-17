package com.example.boardservice.service;

import com.example.boardservice.domain.Board;
import com.example.boardservice.dto.CreateRequestDto;
import com.example.boardservice.domain.BoardRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class BoardService {
    private final BoardRepository boardRepository;

    public BoardService(BoardRepository boardRepository) {
        this.boardRepository = boardRepository;
    }

    @Transactional
    public void create(CreateRequestDto createRequestDto) {
        Board board = new Board(createRequestDto.getTitle(), createRequestDto.getContent(), createRequestDto.getUserId());
        this.boardRepository.save(board);
    }
}
