package com.ronial.internet_banking.common.aspects.annotations;


import com.ronial.internet_banking.domain.entities.future_account.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthSecurity {
    UserRole[] roles() default {};
    boolean checkSession() default true;
}