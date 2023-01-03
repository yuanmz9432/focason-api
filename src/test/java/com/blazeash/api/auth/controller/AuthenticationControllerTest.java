package com.blazeash.api.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blazeash.api.ApplicationTest;
import com.blazeash.api.auth.config.LoginUser;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

class AuthenticationControllerTest extends ApplicationTest
{
    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    @DisplayName("テスト_ログイン")
    void login() throws Exception {
        var requestBody = LoginUser.builder().username("yuanmz9432@gmail.com").password("123456").build();
        mockMvc.perform(post("/auth/login")
            .content(objectMapper.writeValueAsString(requestBody))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("テスト_新規登録")
    void register() {}
}
