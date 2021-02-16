package com.polinakulyk.cashregister.security.dto;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsDto implements UserDetails {

    private String userId;
    private String username;
    private String password;
    private List<GrantedAuthority> grantedAuthorities;
    private String fullName;

    public String getUserId() {
        return userId;
    }

    public UserDetailsDto setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public UserDetailsDto setUsername(String username) {
        this.username = username;
        return this;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public UserDetailsDto setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return grantedAuthorities;
    }

    public UserDetailsDto setGrantedAuthorities(List<GrantedAuthority> grantedAuthorities) {
        this.grantedAuthorities = grantedAuthorities;
        return this;
    }

    public String getFullName() {
        return fullName;
    }

    public UserDetailsDto setFullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDetailsDto that = (UserDetailsDto) o;

        if (!userId.equals(that.userId)) return false;
        if (!username.equals(that.username)) return false;
        if (!password.equals(that.password)) return false;
        if (!grantedAuthorities.equals(that.grantedAuthorities)) return false;
        return fullName.equals(that.fullName);
    }

    @Override
    public int hashCode() {
        int result = userId.hashCode();
        result = 31 * result + username.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + grantedAuthorities.hashCode();
        result = 31 * result + fullName.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(
                ", ", UserDetailsDto.class.getSimpleName() + "[", "]")
                .add("userId='" + userId + "'")
                .add("username='" + username + "'")
                .add("password='" + password + "'")
                .add("grantedAuthorities=" + grantedAuthorities)
                .add("fullName='" + fullName + "'")
                .toString();
    }
}
