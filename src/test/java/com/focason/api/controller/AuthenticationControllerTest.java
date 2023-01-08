/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focason.api.ApplicationServer;
import com.focason.api.auth.config.LoginUser;
import com.focason.api.core.domain.Gender;
import com.focason.api.core.domain.UserStatus;
import com.focason.api.user.resource.UserResource;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(classes = ApplicationServer.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AuthenticationControllerTest extends BaseControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationControllerTest.class);

    private final MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        logger.info("* AuthenticationControllerTest setUpBeforeAll()...");
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {}

    @Test
    @Order(0)
    @DisplayName("register POST /auth/register")
    @Transactional
    @Rollback()
    void testRegister() throws Exception {
        var requestBody = UserResource.builder()
            .username("tester")
            .password("admin123456")
            .gender(Gender.MALE.getValue())
            .email("tester@focason.com")
            .status(UserStatus.AVAILABLE.getValue())
            .type(1)
            .authorities(List.of("AUTH_USER"))
            .build();
        mockMvc.perform(post("/auth/register")
            .content(objectMapper.writeValueAsString(requestBody))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(1)
    @DisplayName("login POST /auth/login")
    void testLogin() throws Exception {
        var requestBody = LoginUser.builder()
            .username("admin@focason.com")
            .password("admin123456").build();
        mockMvc.perform(post("/auth/login")
            .content(objectMapper.writeValueAsString(requestBody))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
