package com.ronial.internet_banking.app.controllers;

import com.ronial.internet_banking.app.components.SessionHandler;
import com.ronial.internet_banking.common.aspects.annotations.AuthSecurity;
import com.ronial.internet_banking.common.utils.ResponseLayout;
import com.ronial.internet_banking.domain.entities.future_account.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final SessionHandler sessionHandler;
    public UserController(final SessionHandler sessionHandler) {
        this.sessionHandler = sessionHandler;
    }
    @GetMapping("/me")
    @AuthSecurity
    public ResponseEntity<ResponseLayout<User>> getMe(@AuthenticationPrincipal final User user) {
        sessionHandler.putSession(user);
        return ResponseLayout
                .<User>builder()
                .data(user)
                .code(200)
                .success(true)
                .build()
                .toResponseEntity();
    }
}
