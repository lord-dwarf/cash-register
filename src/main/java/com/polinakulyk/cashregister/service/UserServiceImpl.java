package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.UserRepository;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.service.vo.UserDetailsVo;
import com.polinakulyk.cashregister.util.CashRegisterSecurityUtil;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CashRegisterSecurityUtil securityUtil;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            CashRegisterSecurityUtil securityUtil,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.securityUtil = securityUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public Optional<User> findById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        quote("User not found", username)));

        // TODO include user name into Authentication object
        return new UserDetailsVo()
                .setUsername(user.getId())
                .setPassword(user.getPassword())
                .setGrantedAuthorities(securityUtil.getAuthRolesFromUserRole(user.getRole()));
    }

    @Override
    @Transactional
    public User create(String username, String password, String role, boolean isPasswordEncoded) {

        // if needed, encode password via password encoder
        if (!isPasswordEncoded) {
            password = passwordEncoder.encode(password);
        }

        return userRepository.save(new User()
                .setUsername(username)
                .setPassword(password)
                .setRole(role));
    }
}
