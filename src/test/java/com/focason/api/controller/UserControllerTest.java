package com.focason.api.controller;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.focason.api.ApplicationServer;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
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
        logger.info(accessToken);
        mockMvc.perform(get("/users")
            .header(HttpHeaders.AUTHORIZATION, "bearer " + accessToken)
            .queryParam("condition", "ewoiZmlyc3ROYW1lIjoiWXUiCn0=")
            .queryParam("limit", "100")
            .queryParam("page", "1")
            .queryParam("sort", "id:DESC"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
