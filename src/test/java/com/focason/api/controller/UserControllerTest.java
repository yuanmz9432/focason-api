package com.focason.api.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focason.api.user.request.UserUpdateRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerTest extends BaseControllerTest
{
    private static final Logger logger = LoggerFactory.getLogger(UserControllerTest.class);

    private final MockMvc mockMvc;

    private static ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll() {
        logger.info("* UserControllerTest beforeAll()...");
        objectMapper = new ObjectMapper();
    }

    @BeforeEach
    void beforeEach() {}

    @AfterEach
    void tearDown() {}

    @Test
    @Order(0)
    @DisplayName("GET /users")
    @Transactional
    @Rollback()
    void testGetUserList() throws Exception {
        mockMvc.perform(get("/users")
            .header(HttpHeaders.AUTHORIZATION, "bearer " + accessToken)
            .queryParam("condition", "ewoiZmlyc3ROYW1lIjoiWXUiCn0=")
            .queryParam("limit", "100")
            .queryParam("page", "1")
            .queryParam("sort", "id:DESC"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(1)
    @DisplayName("GET /users/{id}")
    @Transactional
    @Rollback()
    void testGetUser() throws Exception {
        mockMvc.perform(get("/users/{id}", 1)
            .header(HttpHeaders.AUTHORIZATION, "bearer " + accessToken))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(1)
    @DisplayName("PUT /users/{id}")
    @Transactional
    @Rollback()
    void testUpdateUser() throws Exception {
        var requestBody = UserUpdateRequest.builder()
            .username("update_user@focason.com").build();
        mockMvc.perform(put("/users/{id}", 1)
            .header(HttpHeaders.AUTHORIZATION, "bearer " + accessToken)
            .content(objectMapper.writeValueAsString(requestBody))
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @Order(1)
    @DisplayName("DELETE /users/{id}")
    @Transactional
    @Rollback()
    void testDeleteUser() throws Exception {
        mockMvc.perform(delete("/users/{id}", 1)
            .header(HttpHeaders.AUTHORIZATION, "bearer " + accessToken))
            .andExpect(status().isNoContent());
    }
}
