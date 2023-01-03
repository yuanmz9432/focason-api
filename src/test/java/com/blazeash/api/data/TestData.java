/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.data;



import com.blazeash.api.core.attribute.ID;
import com.blazeash.api.core.domain.Gender;
import com.blazeash.api.user.entity.UserEntity;
import com.blazeash.api.user.resource.UserResource;
import java.time.LocalDateTime;

public class TestData
{

    /**
     * テストユーザリソース
     */
    public final static UserResource TEST_USER_1_RESOURCE = UserResource.builder()
        .id(ID.of(9999999L))
        .uuid("52622b06-d8c8-4f64-a6f5-0bc400e59b18")
        .username("Yuan")
        .email("admin@blazeash.com")
        .password("123456")
        .authorities(null)
        .gender(Gender.MALE.getValue())
        .createdAt(LocalDateTime.now())
        .createdBy("TESTER")
        .modifiedAt(LocalDateTime.now())
        .modifiedBy("TESTER")
        .isDeleted(0)
        .build();

    /**
     * テストユーザエンティティ
     */
    public final static UserEntity TEST_USER_1_ENTITY = UserEntity.builder()
        .id(ID.of(1L))
        .uuid("52622b06-d8c8-4f64-a6f5-0bc400e59b18")
        .username("Yuan")
        .email("admin@blazeash.com")
        .password("123456")
        .gender(Gender.MALE.getValue())
        .createdAt(LocalDateTime.now())
        .createdBy("TESTER")
        .modifiedAt(LocalDateTime.now())
        .modifiedBy("TESTER")
        .isDeleted(0)
        .build();
}
