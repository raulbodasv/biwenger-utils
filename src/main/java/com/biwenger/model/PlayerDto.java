package com.biwenger.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PlayerDto(
        String playerName,
        String owner,
        String clause,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime clauseLockedUntil,
        String invested,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime date,
        String price
) {}

