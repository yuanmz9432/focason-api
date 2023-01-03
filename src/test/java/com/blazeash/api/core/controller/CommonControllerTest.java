package com.blazeash.api.core.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blazeash.api.ApplicationTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;

@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY)
public class CommonControllerTest extends ApplicationTest
{

    @BeforeEach
    void setUp() {}

    @AfterEach
    void tearDown() {}

    @Test
    @DisplayName("ヘルスチェックテスト")
    public void testHeartbeat() throws Exception {
        mockMvc.perform(get("/heartbeat")).andExpect(status().isNoContent());
    }
}
