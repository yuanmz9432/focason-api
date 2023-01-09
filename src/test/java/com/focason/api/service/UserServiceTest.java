/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.service;

import static org.mockito.Mockito.*;

import com.focason.api.core.attribute.FsPagination;
import com.focason.api.core.attribute.FsResultSet;
import com.focason.api.data.TestData;
import com.focason.api.user.repository.UserAuthorityRepository;
import com.focason.api.user.repository.UserRepository;
import com.focason.api.user.service.UserAuthorityService;
import com.focason.api.user.service.UserService;
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
        when(userRepository.findById(TestData.USER_RESOURCE_1.getId()))
            .thenReturn(Optional.ofNullable(TestData.USER_ENTITY_1));
        var user = userService.getResource(TestData.USER_RESOURCE_1.getId());
        user.ifPresent((item) -> Assertions.assertEquals("admin", item.getUsername()));
    }

    @Test
    @Order(1)
    @Transactional
    @Rollback()
    public void When_SearchUserByCondition_Expect_ReturnTestUserList() {
        logger.info("* When_SearchUserByCondition_Expect_ReturnTestUserList() start...");
        when(userRepository.findAll(
            UserRepository.Condition.builder().username(TestData.USER_RESOURCE_1.getUsername()).build(),
            FsPagination.DEFAULT,
            UserRepository.Sort.DEFAULT)).thenReturn(new FsResultSet<>(List.of(TestData.USER_ENTITY_1), 1));
        var users = userService.getResourceList(
            UserRepository.Condition.builder().username(TestData.USER_RESOURCE_1.getUsername()).build(),
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
        when(userRepository.exists(TestData.USER_RESOURCE_1.getId())).thenReturn(true);
        when(userRepository.findById(TestData.USER_RESOURCE_1.getId()))
            .thenReturn(Optional.ofNullable(TestData.USER_ENTITY_1.withUsername("updated_username")));
        var user = userService.updateResource(TestData.USER_RESOURCE_1.getId(),
            TestData.USER_RESOURCE_1.withUsername("updated_username"));
        Assertions.assertEquals("updated_username", user.getUsername());
    }

    @Test
    @Order(2)
    @Transactional
    @Rollback()
    public void When_GetLoginUserBySubject_Expect_ReturnLoginUser() {
        logger.info("* When_GetLoginUserBySubject_Expect_ReturnLoginUser() start...");
        when(userRepository.findAll(
            UserRepository.Condition.builder().email(TestData.USER_RESOURCE_1.getEmail()).build(),
            FsPagination.DEFAULT,
            UserRepository.Sort.DEFAULT)).thenReturn(new FsResultSet<>(List.of(TestData.USER_ENTITY_1), 1));
        when(userAuthorityService.getResourceList(
            UserAuthorityRepository.Condition.builder().uuid(TestData.USER_RESOURCE_1.getUuid()).build(),
            FsPagination.DEFAULT,
            UserAuthorityRepository.Sort.DEFAULT))
                .thenReturn(new FsResultSet<>(List.of(TestData.USER_AUTHORITY_RESOURCE), 1));
        var user = userService.getLoginUserBySubject("admin@focason.com");
        Assertions.assertEquals("admin", user.getUsername());
    }
}
