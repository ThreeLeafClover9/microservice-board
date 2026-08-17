package com.example.boardservice.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "boards")
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String content;
    private Long userId;

    public Board() {
    }

    public Board(String title, String content, Long userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }
}
