/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.service;

import static org.mockito.Mockito.*;

import com.focason.api.core.attribute.FsPagination;
import com.focason.api.core.attribute.FsResultSet;
import com.focason.api.core.attribute.ID;
import com.focason.api.data.TestData;
import com.focason.api.user.repository.UserAuthorityRepository;
import com.focason.api.user.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest
{
    private static final Logger logger = LoggerFactory.getLogger(UserServiceTest.class);

    @InjectMocks
    private UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    UserAuthorityRepository userAuthorityRepository;
    @Mock
    UserAuthorityService userAuthorityService;

    @BeforeEach
    void beforeEach() {
        logger.info("* AuthenticationControllerTest setUpBeforeEach()...");
        // ユーザサービス初期化
        userRepository = mock(UserRepository.class);
        userAuthorityRepository = mock(UserAuthorityRepository.class);
        userAuthorityService = mock(UserAuthorityService.class);
        userService = new UserService(userRepository, userAuthorityRepository, userAuthorityService);

        lenient().when(userRepository.findById(ID.of(1L))).thenReturn(Optional.of(TestData.TEST_USER_1_ENTITY));
        lenient().when(userRepository.findAll(
            UserRepository.Condition.builder().username("admin").build(), FsPagination.DEFAULT,
            UserRepository.Sort.DEFAULT)).thenReturn(new FsResultSet<>(List.of(TestData.TEST_USER_1_ENTITY), 1));
    }

    @AfterEach
    void tearDown() {}

    @Test
    @Transactional
    @Rollback(true)
    public void When_GetUserById_Expect_ReturnTestUser() {
        logger.info("* When_GetUserById_Expect_ReturnTestUser() start...");
        var user = userService.getResource(ID.of(1L));
        user.ifPresent((item) -> {
            Assertions.assertEquals(item.getUsername(), "Yuan");
        });
    }

    @Test
    @Transactional
    @Rollback(true)
    public void When_SearchUserByCondition_Expect_ReturnTestUserList() {
        logger.info("* When_SearchUserByCondition_Expect_ReturnTestUserList() start...");
        var users = userService.getResourceList(
            UserRepository.Condition.builder().username("admin").build(), FsPagination.DEFAULT,
            UserRepository.Sort.DEFAULT);
        Assertions.assertFalse(users.getData().isEmpty());
    }
}
