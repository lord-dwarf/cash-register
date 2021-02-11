package com.polinakulyk.cashregister.security.dto;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserDetailsDto implements UserDetails {
    // username field is needed by authentication manager, but the real value is user id
    private String username;
    // actual username
    private String principalName;
    private String password;
    private List<GrantedAuthority> grantedAuthorities;

    @Override
    public String getUsername() {
        return username;
    }

    public UserDetailsDto setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPrincipalName() {
        return principalName;
    }

    public UserDetailsDto setPrincipalName(String principalName) {
        this.principalName = principalName;
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

        if (!username.equals(that.username)) return false;
        if (!principalName.equals(that.principalName)) return false;
        if (!password.equals(that.password)) return false;
        return grantedAuthorities.equals(that.grantedAuthorities);
    }

    @Override
    public int hashCode() {
        int result = username.hashCode();
        result = 31 * result + principalName.hashCode();
        result = 31 * result + password.hashCode();
        result = 31 * result + grantedAuthorities.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserDetailsDto.class.getSimpleName() + "[", "]")
                .add("username='" + username + "'")
                .add("principalName='" + principalName + "'")
                .add("password='" + password + "'")
                .add("grantedAuthorities=" + grantedAuthorities)
                .toString();
    }
}
