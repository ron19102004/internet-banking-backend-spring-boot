package com.ronial.internet_banking.common.aspects.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RateLimiter {
    String name();

    boolean enableLimitIPAdr() default true;

    boolean enableLimitRequest() default false;

    int timeLimitIPAdr() default 30; // 30s

    int limitIPAdr() default 5;

    int timeLimitRequest() default 30; //30s

    int limitRequest() default 100;

    String overLimitIPMessage() default "";

    String overLimitRequestMessage() default "";
}
