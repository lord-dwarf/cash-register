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
import com.polinakulyk.cashregister.service.api.dto.LoginResponseDto;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

import static java.lang.String.format;

/**
 * User service.
 * <p>
 * User service implements {@link UserDetailsService} because that is the requirement
 * of Spring Security authentication manager.
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final CashboxRepository cashboxRepository;
    private final AuthHelper authHelper;
    private final PasswordEncoder passwordEncoder;

    // WORKAROUND dor cyclic dependency between CashRegisterWebSecurityConfig and UserService
    @Autowired
    private AuthenticationManager authenticationManager;

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
     * Authenticates user based on the given credentials: username and password.
     *
     * @param login
     * @param password
     * @return
     */
    @Override
    public LoginResponseDto login(String login, String password) {
        log.debug("BEGIN Login of user '{}'", login);

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(login, password));

        UserDetailsDto userDetails = (UserDetailsDto) auth.getPrincipal();

        // clear principal password after we've authenticated
        userDetails.setPassword("");

        // save user details into Spring Security context for the duration of request
        authHelper.setAuthentication(auth);

        String userId = userDetails.getUserId();
        UserRole userRole = authHelper.getUserRoleFromAuthRoles(auth.getAuthorities());
        String jwt = authHelper.createJwt(userId, userRole);

        var loginResponseDto = new LoginResponseDto()
                .setJwt(jwt)
                .setUser(new User()
                        .setId(userId)
                        .setUsername(userDetails.getUsername())
                        .setRole(userRole)
                        .setFullName(userDetails.getFullName()));

        log.info("DONE Login of user '{}' with role '{}'", login, userRole);
        return loginResponseDto;
    }

    /**
     * Find the existing user by id, otherwise throw {@link CashRegisterUserNotFoundException}.
     * <p>
     * Used when we already know that the user exists, and thus it is highly unlikely that
     * the exception will be thrown. But if it happens, an exception specific to our
     * use case will be thrown and provide the necessary HTTP code, (instead of being a general exception
     * {@link java.util.NoSuchElementException} that is thrown by {@link Optional#get()} and will
     * result in HTTP 500).
     *
     * @param userId
     * @return
     */
    @Override
    public User findExistingById(String userId) {
        var user = userRepository.findById(userId).orElseThrow(() ->
                new CashRegisterUserNotFoundException(userId));

        log.debug("DONE Find existing user: '{}'", userId);
        return user;
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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(quote("User not found", username)));

        var userDetailsDto = new UserDetailsDto()
                .setUserId(user.getId())
                .setUsername(user.getUsername())
                .setPassword(user.getPassword())
                .setGrantedAuthorities(authHelper.getAuthRolesFromUserRole(user.getRole()))
                .setFullName(user.getFullName());

        log.debug("DONE Load user by username: '{}', user: '{}'", username, user.getId());
        return userDetailsDto;
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
