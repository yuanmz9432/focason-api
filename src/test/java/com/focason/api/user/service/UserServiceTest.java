/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.service;

import static org.mockito.Mockito.*;

import com.focason.api.core.attribute.FsPagination;
import com.focason.api.core.attribute.FsResultSet;
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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
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
        logger.info("* UserServiceTest beforeEach()...");
        userRepository = mock(UserRepository.class);
        userAuthorityRepository = mock(UserAuthorityRepository.class);
        userAuthorityService = mock(UserAuthorityService.class);
        userService = new UserService(userRepository, userAuthorityRepository, userAuthorityService);
    }

    @AfterEach
    void tearDown() {}

    @Test
    @Order(0)
    @Transactional
    @Rollback()
    public void When_GetUserById_Expect_ReturnTestUser() {
        logger.info("* When_GetUserById_Expect_ReturnTestUser() start...");
        when(userRepository.findById(TestData.TEST_USER_1_RESOURCE.getId()))
            .thenReturn(Optional.ofNullable(TestData.TEST_USER_1_ENTITY));
        var user = userService.getResource(TestData.TEST_USER_1_RESOURCE.getId());
        user.ifPresent((item) -> Assertions.assertEquals("admin", item.getUsername()));
    }

    @Test
    @Order(1)
    @Transactional
    @Rollback()
    public void When_SearchUserByCondition_Expect_ReturnTestUserList() {
        logger.info("* When_SearchUserByCondition_Expect_ReturnTestUserList() start...");
        when(userRepository.findAll(
            UserRepository.Condition.builder().username(TestData.TEST_USER_1_RESOURCE.getUsername()).build(),
            FsPagination.DEFAULT,
            UserRepository.Sort.DEFAULT)).thenReturn(new FsResultSet<>(List.of(TestData.TEST_USER_1_ENTITY), 1));
        var users = userService.getResourceList(
            UserRepository.Condition.builder().username(TestData.TEST_USER_1_RESOURCE.getUsername()).build(),
            FsPagination.DEFAULT,
            UserRepository.Sort.DEFAULT);
        Assertions.assertFalse(users.getData().isEmpty());
    }

    @Test
    @Order(2)
    @Transactional
    @Rollback()
    public void When_UpdateUserName_Expect_UsernameUpdated() {
        logger.info("* When_UpdateUserName_Expect_UsernameUpdated() start...");
        when(userRepository.exists(TestData.TEST_USER_1_RESOURCE.getId())).thenReturn(true);
        when(userRepository.findById(TestData.TEST_USER_1_RESOURCE.getId()))
            .thenReturn(Optional.ofNullable(TestData.TEST_USER_1_ENTITY.withUsername("updated_username")));
        var user = userService.updateResource(TestData.TEST_USER_1_RESOURCE.getId(),
            TestData.TEST_USER_1_RESOURCE.withUsername("updated_username"));
        Assertions.assertEquals("updated_username", user.getUsername());
    }
}
