/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.data;



import com.focason.api.core.attribute.ID;
import com.focason.api.core.domain.Gender;
import com.focason.api.user.entity.UserEntity;
import com.focason.api.user.resource.UserAuthorityResource;
import com.focason.api.user.resource.UserResource;
import java.time.LocalDateTime;
import java.util.List;

public class TestData
{

    /**
     * テストユーザリソース
     */
    public final static UserResource USER_RESOURCE_1 = UserResource.builder()
        .id(ID.of(1L))
        .uuid("bb18a80a-088b-4a99-aea6-fe25523ba2f9")
        .username("admin")
        .email("admin@focason.com")
        .password("admin123456")
        .authorities(List.of("AUTH_USER"))
        .gender(Gender.MALE.getValue())
        .createdAt(LocalDateTime.now())
        .createdBy("ADMIN")
        .modifiedAt(LocalDateTime.now())
        .modifiedBy("ADMIN")
        .isDeleted(0)
        .build();

    /**
     * テストユーザエンティティ
     */
    public final static UserEntity USER_ENTITY_1 = UserEntity.builder()
        .id(ID.of(1L))
        .uuid("bb18a80a-088b-4a99-aea6-fe25523ba2f9")
        .username("admin")
        .email("admin@focason.com")
        .password("admin123456")
        .gender(Gender.MALE.getValue())
        .createdAt(LocalDateTime.now())
        .createdBy("ADMIN")
        .modifiedAt(LocalDateTime.now())
        .modifiedBy("ADMIN")
        .isDeleted(0)
        .build();

    /**
     * テストユーザ権限リソース
     */
    public final static UserAuthorityResource USER_AUTHORITY_RESOURCE = UserAuthorityResource.builder()
        .id(ID.of(1L))
        .uuid("bb18a80a-088b-4a99-aea6-fe25523ba2f9")
        .authorityCode("AUTH_USER")
        .build();
}
