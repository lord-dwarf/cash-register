package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.security.dto.UserRole;
import com.polinakulyk.cashregister.service.api.dto.LoginResponseDto;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    LoginResponseDto login(String login, String password);

    User findExistingById(String userId);

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    void createWithId(
            String id,
            String cashboxId,
            String username,
            String password,
            UserRole userRole,
            String fullName
    );
}
