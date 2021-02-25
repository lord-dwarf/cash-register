package com.polinakulyk.cashregister.security;

import com.polinakulyk.cashregister.exception.CashRegisterException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import org.springframework.security.crypto.password.PasswordEncoder;

import static com.polinakulyk.cashregister.util.CashRegisterUtil.quote;
import static com.polinakulyk.cashregister.util.CashRegisterUtil.toBase64;
import static java.nio.charset.StandardCharsets.UTF_8;

public class CashRegisterPasswordEncoder implements PasswordEncoder {

    private final String passwordSalt;

    public CashRegisterPasswordEncoder(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    @Override
    public String encode(CharSequence rawPassword) {
        KeySpec spec = new PBEKeySpec(
                rawPassword.toString().toCharArray(),
                passwordSalt.getBytes(UTF_8),
                65536,
                128);
        try {
            var passwordEncoder = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            return toBase64(passwordEncoder.generateSecret(spec).getEncoded());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new CashRegisterException(quote(e.getClass().getSimpleName(), e.getMessage()));
        }

    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return rawPassword != null && encode(rawPassword).equals(encodedPassword);
    }

    @Override
    public boolean upgradeEncoding(String encodedPassword) {
        throw new UnsupportedOperationException("Not supported");
    }
}
