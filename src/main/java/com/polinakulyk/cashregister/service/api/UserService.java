package com.polinakulyk.cashregister.service.api;

import com.polinakulyk.cashregister.db.entity.User;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public interface UserService extends UserDetailsService {
    Optional<User> findById(String userId);
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
    User create(
            String username,
            String password,
            String role,
            String fullName,
            boolean isPasswordEncoded
    );
    void createWithId(
            String id,
            String cashboxId,
            String username,
            String password,
            String role,
            String fullName,
            boolean isPasswordEncoded
    );
}
