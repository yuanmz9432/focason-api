/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommonControllerTest extends BaseControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(CommonControllerTest.class);

    private final MockMvc mockMvc;

    @BeforeAll
    static void beforeAll() {}

    @AfterEach
    void tearDown() {}

    @Test
    @Order(0)
    @DisplayName("testHeartbeat GET /heartbeat")
    public void testHeartbeat() throws Exception {
        logger.info("* CommonControllerTest setUpBeforeAll()...");
        mockMvc.perform(get("/heartbeat")).andExpect(status().isNoContent());
    }
}
