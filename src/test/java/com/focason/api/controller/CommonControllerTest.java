/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CommonControllerTest extends BaseControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(CommonControllerTest.class);

    private final MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        logger.info("* CommonControllerTest beforeAll()...");
        objectMapper = new ObjectMapper();
    }

    @AfterEach
    void tearDown() {}

    @Test
    @Order(0)
    @DisplayName("GET /heartbeat")
    public void testHeartbeat() throws Exception {
        logger.info("* CommonControllerTest testHeartbeat()...");
        mockMvc.perform(get("/heartbeat")).andExpect(status().isNoContent());
    }

    @Test
    @Order(1)
    @DisplayName("POST /base64")
    public void testBase64Generator() throws Exception {
        logger.info("* CommonControllerTest testBase64Generator()...");
        var requestBody = new JSONObject();
        requestBody.put("username", "TESTER");
        mockMvc.perform(post("/base64")
            .content(objectMapper.writeValueAsString(requestBody.toString()))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isNotEmpty());
    }
}
