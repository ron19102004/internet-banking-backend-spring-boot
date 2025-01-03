package com.ronial.internet_banking.common.utils;

import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@UtilityClass
public class StringHashAndShortenerUtils {
    public  String hashAndShorten(String input) {
        try {
            // Sử dụng SHA-256 để tạo băm từ chuỗi
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            // Lấy 8 byte đầu tiên của hash và chuyển thành số nguyên
            long hashValue = 0;
            for (int i = 0; i < 8; i++) { // Lấy 8 byte đầu
                hashValue = (hashValue << 8) | (hash[i] & 0xFF);
            }
            return (int) (Math.abs(hashValue) % 1000000) + "";
        } catch (Exception e) {
            throw new RuntimeException("Error generating hash", e);
        }
    }

}
