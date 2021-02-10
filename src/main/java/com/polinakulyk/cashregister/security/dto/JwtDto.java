package com.polinakulyk.cashregister.security.dto;

import java.time.LocalDateTime;
import java.util.StringJoiner;

public class JwtDto {
    private String userId;
    private String role;
    private LocalDateTime issuedAt;
    private LocalDateTime expireAt;

    public String getUserId() {
        return userId;
    }

    public JwtDto setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    public String getRole() {
        return role;
    }

    public JwtDto setRole(String role) {
        this.role = role;
        return this;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public JwtDto setIssuedAt(LocalDateTime issuedAt) {
        this.issuedAt = issuedAt;
        return this;
    }

    public LocalDateTime getExpireAt() {
        return expireAt;
    }

    public JwtDto setExpireAt(LocalDateTime expireAt) {
        this.expireAt = expireAt;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JwtDto jwtDto = (JwtDto) o;

        if (!userId.equals(jwtDto.userId)) return false;
        if (!role.equals(jwtDto.role)) return false;
        if (!issuedAt.equals(jwtDto.issuedAt)) return false;
        return expireAt.equals(jwtDto.expireAt);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + role.hashCode();
        result = 31 * result + issuedAt.hashCode();
        result = 31 * result + expireAt.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", JwtDto.class.getSimpleName() + "[", "]")
                .add("userId='" + userId + "'")
                .add("role='" + role + "'")
                .add("issuedAt=" + issuedAt)
                .add("expireAt=" + expireAt)
                .toString();
    }
}
