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
    @Column(name = "user_id")
    private Long userId;
    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

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

    public User getUser() {
        return user;
    }
}
