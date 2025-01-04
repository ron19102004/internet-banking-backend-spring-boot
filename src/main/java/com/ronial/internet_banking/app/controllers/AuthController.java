package com.ronial.internet_banking.app.controllers;

import com.ronial.internet_banking.app.components.SessionHandler;
import com.ronial.internet_banking.app.dto.user.*;
import com.ronial.internet_banking.app.mappers.UserMapper;
import com.ronial.internet_banking.app.services.AuthService;
import com.ronial.internet_banking.app.validations.AuthValidation;
import com.ronial.internet_banking.common.aspects.annotations.AuthSecurity;
import com.ronial.internet_banking.common.aspects.annotations.RateLimiter;
import com.ronial.internet_banking.common.exceptions.AuthException;
import com.ronial.internet_banking.common.exceptions.AuthExceptionMessage;
import com.ronial.internet_banking.common.utils.ResponseLayout;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.infrastructure.cache.RedisConstant;
import com.ronial.internet_banking.infrastructure.cache.RedisDistributedService;
import com.ronial.internet_banking.infrastructure.cache.RedisKeysUtils;
import com.ronial.internet_banking.infrastructure.security.jwt.JwtPayload;
import com.ronial.internet_banking.infrastructure.security.jwt.JwtService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final AuthValidation authValidation;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final DaoAuthenticationProvider authenticationProvider;
    private final SessionHandler sessionHandler;
    private final RedisDistributedService redisDistributedService;

    @Autowired
    public AuthController(final AuthService authService, final AuthValidation authValidation,
                          final JwtService jwtService, final UserMapper userMapper,
                          final DaoAuthenticationProvider authenticationProvider,
                          final SessionHandler sessionHandler, final RedisDistributedService redisDistributedService) {
        this.authService = authService;
        this.authValidation = authValidation;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.authenticationProvider = authenticationProvider;
        this.sessionHandler = sessionHandler;
        this.redisDistributedService = redisDistributedService;
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseLayout<CreateUserResponse>> register(@RequestBody @Valid CreateUserRequest request) {
        User user = userMapper.toUser(request);
        authValidation.register(user);
        UserDetailsResponse response = authService.register(user);

        JwtPayload jwtPayload = JwtPayload.builder()
                .claims(new HashMap<>())
                .subject(response.id().toString())
                .timeLive(60 * 24 * 15) //15days
                .build();
        String token = jwtService.generate(jwtPayload);
        //Token live in 15days
        redisDistributedService.putWithTimeLive(RedisKeysUtils.authToken(user.getId()), token, RedisConstant.TOKEN_TIME_LIVE);
        return ResponseLayout.<CreateUserResponse>builder()
                .data(new CreateUserResponse(response, token))
                .code(200)
                .success(true)
                .build()
                .toResponseEntity();
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseLayout<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        Authentication authentication = authenticationProvider
                .authenticate(new UsernamePasswordAuthenticationToken(request.phoneNumber(), request.password()));
        if (!authentication.isAuthenticated()) {
            throw new AuthException(AuthExceptionMessage.REQUIRE_AUTHENTICATION);
        }
        User user = (User) authentication.getPrincipal();
        sessionHandler.putSession(user);
        JwtPayload jwtPayload = JwtPayload.builder()
                .claims(new HashMap<>())
                .subject(user.getId().toString())
                .timeLive(60 * 24 * 15) //15days
                .build();
        String token = jwtService.generate(jwtPayload);
        //Token live in 15days
        redisDistributedService.putWithTimeLive(RedisKeysUtils.authToken(user.getId()), token, RedisConstant.TOKEN_TIME_LIVE);
        return ResponseLayout.<LoginResponse>builder()
                .data(new LoginResponse(userMapper.toUserDetailsResponse(user), token))
                .code(200)
                .success(true)
                .build()
                .toResponseEntity();
    }

    @PostMapping("/e-session")
    @AuthSecurity
    public ResponseEntity<ResponseLayout<Object>> endSession(@AuthenticationPrincipal User user) {
        sessionHandler.popSession(user);
        return ResponseLayout.builder()
                .code(200)
                .message("Terminal session successfully")
                .success(true)
                .build()
                .toResponseEntity();
    }

    @PostMapping("/logout")
    @AuthSecurity(checkSession = false)
    public ResponseEntity<ResponseLayout<Object>> logout(@AuthenticationPrincipal User user) {
        sessionHandler.popSession(user);
        redisDistributedService.release(RedisKeysUtils.authToken(user.getId()));
        return ResponseLayout.builder()
                .code(200)
                .message("Logged out successfully")
                .success(true)
                .build()
                .toResponseEntity();
    }

    @PostMapping("/rf-session")
    @AuthSecurity
    @RateLimiter(
            name = "rf-session",
            timeLimitIPAdr = 60 * 10,
            overLimitIPMessage = "Try again after 10 minutes",
            limitIPAdr = 1
    )
    public ResponseEntity<ResponseLayout<Object>> refreshSession(@AuthenticationPrincipal User user) {
        redisDistributedService.refresh(RedisKeysUtils.authSession(user.getId()), RedisConstant.SESSION_TIME_LIVE);
        return ResponseLayout.builder()
                .code(200)
                .message("Refresh session successfully")
                .success(true)
                .build()
                .toResponseEntity();
    }

    @PostMapping("/resend-otp")
    @AuthSecurity
    @RateLimiter(
            name = "resend-otp",
            timeLimitIPAdr = 60,
            limitIPAdr = 1,
            overLimitIPMessage = "Try again after 1 minute"
    )
    public ResponseEntity<ResponseLayout<Object>> resendOTP() {
        return ResponseLayout.builder()
                .code(200)
                .message("Resend OTP successfully")
                .success(true)
                .build()
                .toResponseEntity();
    }
}
