package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.CashboxRepository;
import com.polinakulyk.cashregister.db.repository.UserRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.exception.CashRegisterUserNotFoundException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.UserDetailsDto;
import com.polinakulyk.cashregister.security.dto.UserRole;
import com.polinakulyk.cashregister.service.api.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

/**
 * User service.
 * <p>
 * User service implements {@link UserDetailsService} because that is the requirement
 * of Spring Security authentication manager.
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final CashboxRepository cashboxRepository;
    private final AuthHelper authHelper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(
            UserRepository userRepository,
            CashboxRepository cashboxRepository,
            AuthHelper authHelper,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.cashboxRepository = cashboxRepository;
        this.authHelper = authHelper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Find the existing user by id, otherwise throw {@link CashRegisterUserNotFoundException}.
     * <p>
     * Used when we already know that the user exists, and thus it is highly unlikely that
     * the exception will be thrown. But if that happens, the exception will be specific to our
     * use case and will provide the necessary HTTP code, instead of being a general exception
     * {@link java.util.NoSuchElementException} that is thrown by {@link Optional#get()} and will
     * result in HTTP 500.
     *
     * @param userId
     * @return
     */
    @Override
    @Transactional
    public User findExistingById(String userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new CashRegisterUserNotFoundException(userId));
    }

    /**
     * This method is used by the Spring Security's auth manager to get user details
     * via our user service (that implements {@link UserDetailsService}).
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException when user not found
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(quote("User not found", username)));

        return new UserDetailsDto()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setGrantedAuthorities(authHelper.getAuthRolesFromUserRole(user.getRole()))
                .setFullName(user.getFullName());
    }


    @Override
    @Transactional
    public void createWithId(
            String userId,
            String cashboxId,
            String username,
            String password,
            UserRole userRole,
            String fullName
    ) {
        Assert.notNull(userId, "User id must be set");

        Cashbox cashbox = cashboxRepository.findById(cashboxId).orElseThrow(() ->
                new CashRegisterException("Cash box id must be set"));

        userRepository.save(new User()
                .setId(userId)
                .setCashbox(cashbox)
                .setUsername(username)
                .setPassword(passwordEncoder.encode(password))
                .setRole(userRole)
                .setFullName(fullName));
    }
}
