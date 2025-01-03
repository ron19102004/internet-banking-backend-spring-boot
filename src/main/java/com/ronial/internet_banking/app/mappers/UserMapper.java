package com.ronial.internet_banking.app.mappers;

import com.ronial.internet_banking.app.dto.user.CreateUserRequest;
import com.ronial.internet_banking.app.dto.user.UserDetailsResponse;
import com.ronial.internet_banking.common.security.StringHashSecurity;
import com.ronial.internet_banking.domain.entities.future_account.User;
import com.ronial.internet_banking.domain.entities.future_account.UserRole;
import com.ronial.internet_banking.domain.entities.future_account.UserStatus;
import lombok.experimental.UtilityClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Component
public class UserMapper {
    private final StringHashSecurity stringHashSecurity;
    @Autowired
    public UserMapper(StringHashSecurity stringHashSecurity) {
        this.stringHashSecurity = stringHashSecurity;
    }
    public User toUser(final CreateUserRequest createUserRequest) {
        return User.builder()
                .email(createUserRequest.email())
                .address(createUserRequest.address())
                .role(UserRole.CUSTOMER)
                .password(createUserRequest.password())
                .cccd(createUserRequest.cccd())
                .phoneNumber(createUserRequest.phoneNumber())
                .fullName(createUserRequest.fullName())
                .rewardPoints(0L)
                .status(UserStatus.PENDING)
                .build();
    }

    public UserDetailsResponse toUserDetailsResponse(final User user) {
        return UserDetailsResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .address(user.getAddress())
                .role(user.getRole())
                .rewardPoints(user.getRewardPoints())
                .cccd(user.getCccd())
                .phoneNumber(user.getPhoneNumber())
                .fullName(user.getFullName())
                .status(user.getStatus())
                .build();
    }
}
