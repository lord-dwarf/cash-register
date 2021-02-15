package com.polinakulyk.cashregister.service;

import com.polinakulyk.cashregister.db.entity.Cashbox;
import com.polinakulyk.cashregister.db.entity.User;
import com.polinakulyk.cashregister.db.repository.CashboxRepository;
import com.polinakulyk.cashregister.db.repository.UserRepository;
import com.polinakulyk.cashregister.exception.CashRegisterException;
import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.dto.UserDetailsDto;
import com.polinakulyk.cashregister.service.api.UserService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.generateUuid;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;

@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

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

        return new UserDetailsDto()
                .setUsername(user.getId())
                .setPassword(user.getPassword())
                .setPrincipalName(user.getUsername())
                .setGrantedAuthorities(authHelper.getAuthRolesFromUserRole(user.getRole()));
    }

    @Override
    @Transactional
    public User create(
            String username,
            String password,
            String role,
            String fullName,
            boolean isPasswordEncoded
    ) {

        // if needed, encode password via password encoder
        if (!isPasswordEncoded) {
            password = passwordEncoder.encode(password);
        }

        // here we assume that auto-generated user UUID is unique
        return userRepository.save(new User()
                .setId(generateUuid())
                .setUsername(username)
                .setPassword(password)
                .setRole(role)
                .setFullName(fullName));
    }

    @Override
    @Transactional
    public void createWithId(
            String id,
            String cashboxId,
            String username,
            String password,
            String role,
            String fullName,
            boolean isPasswordEncoded
    ) {

        // validate that user id is present
        if (null == id) {
            throw new CashRegisterException("User id must be set");
        }

        // TODO user cashbox should rather be set on login, and cleaned on logout
        Cashbox cashbox = null;
        if (null != cashboxId) {
            cashbox = cashboxRepository.findById(cashboxId).orElseThrow(() ->
                    new CashRegisterException("Cashbox id must be set"));
        }

        // if needed, encode password via password encoder
        if (!isPasswordEncoded) {
            password = passwordEncoder.encode(password);
        }

        userRepository.save(new User()
                .setId(id)
                .setCashbox(cashbox)
                .setUsername(username)
                .setPassword(password)
                .setRole(role)
                .setFullName(fullName));
    }
}
