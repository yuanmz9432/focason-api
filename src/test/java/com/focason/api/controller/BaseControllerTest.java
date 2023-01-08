package com.focason.api.controller;



import com.focason.api.ApplicationServer;
import com.focason.api.auth.config.JWTGenerator;
import com.focason.api.data.TestData;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest(classes = ApplicationServer.class)
@ExtendWith(SpringExtension.class)
public abstract class BaseControllerTest
{
    protected static String accessToken;
    @Autowired
    private JWTGenerator generator;

    @BeforeEach
    void loadContext() {
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = generator.generateAccessToken(TestData.TEST_USER_1_RESOURCE.getUuid(),
                new Date(System.currentTimeMillis() + 3600 * 1000));
        }
    }

}
