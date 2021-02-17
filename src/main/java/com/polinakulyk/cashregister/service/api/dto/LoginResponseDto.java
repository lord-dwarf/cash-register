package com.polinakulyk.cashregister.service.api.dto;

import com.polinakulyk.cashregister.db.entity.User;
import java.util.StringJoiner;

public class LoginResponseDto {

    private String jwt;
    private User user;

    public String getJwt() {
        return jwt;
    }

    public LoginResponseDto setJwt(String jwt) {
        this.jwt = jwt;
        return this;
    }

    public User getUser() {
        return user;
    }

    public LoginResponseDto setUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LoginResponseDto that = (LoginResponseDto) o;

        if (!jwt.equals(that.jwt)) return false;
        return user.equals(that.user);
    }

    @Override
    public int hashCode() {
        int result = jwt.hashCode();
        result = 31 * result + user.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", LoginResponseDto.class.getSimpleName() + "[", "]")
                .add("jwt='" + jwt + "'")
                .add("user=" + user)
                .toString();
    }
}
