package com.ronial.internet_banking.infrastructure.cache;

import lombok.experimental.UtilityClass;

@UtilityClass
public class RedisKeysUtils {
    public String authEmail(String emailHash){
        return "auth:valid:email:" + emailHash;
    }
    public String authPhone(String phoneHash){
        return "auth:valid:phone:" + phoneHash;
    }
    public String authCCCD(String cccdHash){
        return "auth:valid:cccd:" + cccdHash;
    }
    public String authSession(Long userId){
        return "auth:session:" + userId;
    }
    public String authToken(Long userId){
        return "auth:token:" + userId;
    }
}
