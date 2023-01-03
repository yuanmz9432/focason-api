/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.auth.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.blazeash.api.ApplicationServer;
import com.blazeash.api.auth.config.LoginUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApplicationServer.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class AuthenticationControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    private final MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUpBeforeAll() {
        logger.info("* AuthenticationControllerTest setUpBeforeAll()...");
        objectMapper = new ObjectMapper();
    }

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
