package com.ronial.internet_banking.app.dto.user;

import com.ronial.internet_banking.domain.entities.future_account.UserRole;
import com.ronial.internet_banking.domain.entities.future_account.UserStatus;
import lombok.Builder;

@Builder
public record UserDetailsResponse(
        Long id,
        String fullName,
        String email,
        String phoneNumber,
        String cccd,
        String address,
        UserStatus status,
        Long rewardPoints,
        UserRole role
) {
}
