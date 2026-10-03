package com.example.boardservice.dto;

public class BoardResponseDto {
    private Long id;
    private String title;
    private String content;
    private UserDto user;

    public BoardResponseDto(Long id, String title, String content, UserDto user) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public UserDto getUser() {
        return user;
    }
}
