package com.ronial.internet_banking.common.utils;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;

@Data
@Builder
public class ResponseLayout<T> {
    private String message;
    public T data;
    private boolean success;
    private int code;
    public ResponseEntity<ResponseLayout<T>> toResponseEntity() {
        return ResponseEntity.ok().body(this);
    }
}
