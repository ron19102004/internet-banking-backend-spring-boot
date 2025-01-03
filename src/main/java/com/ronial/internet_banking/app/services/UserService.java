package com.ronial.internet_banking.app.services;

import com.ronial.internet_banking.domain.entities.future_account.User;
import org.springframework.security.provisioning.UserDetailsManager;

public interface UserService extends UserDetailsManager {
    User findByPhoneNumber(String phoneNumber);
    User findById(Long id);
}
