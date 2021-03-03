package com.polinakulyk.cashregister.db.repository;

import com.polinakulyk.cashregister.db.entity.User;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByUsername(String username);
}
