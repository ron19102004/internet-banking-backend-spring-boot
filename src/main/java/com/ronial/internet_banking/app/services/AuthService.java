package com.ronial.internet_banking.app.services;

import com.ronial.internet_banking.app.dto.user.UserDetailsResponse;
import com.ronial.internet_banking.domain.entities.future_account.User;

public interface AuthService {
    UserDetailsResponse register(final User user);
}
