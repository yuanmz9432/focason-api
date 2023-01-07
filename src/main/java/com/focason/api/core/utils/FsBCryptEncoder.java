/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.utils;



import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class FsBCryptEncoder extends BCryptPasswordEncoder
{

    private static volatile BCryptPasswordEncoder INSTANCE = null;

    private FsBCryptEncoder() {

    }

    public static BCryptPasswordEncoder getInstance() {
        if (INSTANCE == null) {
            synchronized (BCryptPasswordEncoder.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BCryptPasswordEncoder();
                }
            }
        }
        return INSTANCE;
    }

}
