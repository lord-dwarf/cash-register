package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.entity.UserWithId;
import com.polinakulyk.cashregister.db.repository.UserRepository;
import com.polinakulyk.cashregister.db.repository.UserWithIdRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.service.api.UserService;
import com.polinakulyk.cashregister.service.vo.UserDetailsVo;
import java.util.Optional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserWithIdRepository userWithIdRepository;
    private final AuthHelper authHelper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            UserWithIdRepository userWithIdRepository,
            AuthHelper authHelper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.userWithIdRepository = userWithIdRepository;
        this.authHelper = authHelper;
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
                .setGrantedAuthorities(authHelper.getAuthRolesFromUserRole(user.getRole()));
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

    @Override
    @Transactional
    public void createWithId(
            String id, String username, String password, String role, boolean isPasswordEncoded) {

        // validate that user id is present
        if (null == id) {
            throw new CashRegisterException(BAD_REQUEST, "User id must be set");
        }

        // if needed, encode password via password encoder
        if (!isPasswordEncoded) {
            password = passwordEncoder.encode(password);
        }

        userWithIdRepository.save(new UserWithId()
                .setId(id)
                .setUsername(username)
                .setPassword(password)
                .setRole(role));
    }
}
