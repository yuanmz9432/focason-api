/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@SpringBootTest(classes = ApplicationServer.class)
@EnableJpaRepositories(bootstrapMode = BootstrapMode.LAZY)
public class ApplicationTest
{
    @Test
    private void contextLoads() {}

    @Autowired
    private WebApplicationContext webApplicationContext;

    public MockMvc mockMvc;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

}