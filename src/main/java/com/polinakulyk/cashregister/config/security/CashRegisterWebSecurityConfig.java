package com.polinakulyk.cashregister.config.security;

import com.polinakulyk.cashregister.security.api.AuthHelper;
import com.polinakulyk.cashregister.security.filter.JwtAuthFilter;
import com.polinakulyk.cashregister.service.api.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Web security configuration via Spring Security.
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class CashRegisterWebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final AuthHelper authHelper;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public CashRegisterWebSecurityConfig(
            AuthHelper authHelper,
            PasswordEncoder passwordEncoder,
            UserService userService
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.authHelper = authHelper;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public JwtAuthFilter authenticationJwtTokenFilter() {
        return new JwtAuthFilter(authHelper, userService);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().and()
                // TODO enable CSRF check
                .csrf().disable()
                // app is stateless thanks to JWT
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated();

        http.addFilterBefore(
                authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Override
    public void configure(
            AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder
                // ensure that Authentication object will not hold credentials after auth by manager
                .eraseCredentials(true)
                .userDetailsService(userService)
                .passwordEncoder(passwordEncoder);
    }
}