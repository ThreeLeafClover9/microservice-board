package com.example.boardservice.dto;

public class AddPointRequestDto {
    private Long userId;
    private int amount;

    public AddPointRequestDto(Long userId, int amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public Long getUserId() {
        return userId;
    }

    public int getAmount() {
        return amount;
    }
}
