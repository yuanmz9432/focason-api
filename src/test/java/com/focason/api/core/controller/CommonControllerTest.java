/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.focason.api.ApplicationServer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(classes = ApplicationServer.class)
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CommonControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(CommonControllerTest.class);

    private final MockMvc mockMvc;

    @BeforeAll
    static void setUpBeforeAll() {

    }

    @AfterEach
    void tearDown() {}

    @Test
    @Order(0)
    @DisplayName("ヘルスチェックテスト")
    public void testHeartbeat() throws Exception {
        logger.info("* CommonControllerTest setUpBeforeAll()...");
        mockMvc.perform(get("/heartbeat")).andExpect(status().isNoContent());
    }
}
