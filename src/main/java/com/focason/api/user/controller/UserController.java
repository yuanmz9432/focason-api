/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.controller;



import com.focason.api.core.annotation.FsConditionParam;
import com.focason.api.core.annotation.FsPaginationParam;
import com.focason.api.core.annotation.FsSortParam;
import com.focason.api.core.attribute.FsPagination;
import com.focason.api.core.attribute.FsResultSet;
import com.focason.api.core.attribute.FsSort;
import com.focason.api.core.attribute.ID;
import com.focason.api.core.exception.FsResourceNotFoundException;
import com.focason.api.user.entity.UserEntity;
import com.focason.api.user.repository.UserRepository;
import com.focason.api.user.resource.UserResource;
import com.focason.api.user.service.UserService;
import javax.validation.Valid;
import javax.validation.groups.Default;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * ユーザーコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/users";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ユーザーサービス
     */
    private final UserService service;

    /**
     * ユーザーリソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param fsSort ソートパラメータ
     * @return ユーザーリソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<FsResultSet<UserResource>> getUserList(
        @FsConditionParam UserRepository.Condition condition,
        @FsPaginationParam FsPagination pagination,
        @FsSortParam(allowedValues = {}) FsSort fsSort) {
        if (condition == null) {
            condition = UserRepository.Condition.DEFAULT;
        }
        var sort = UserRepository.Sort.fromLcSort(fsSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ユーザーIDを指定して、ユーザーリソース取得API
     *
     * @param id ユーザーID
     * @return ユーザーリソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserResource> getUser(
        @PathVariable("id") ID<UserEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new FsResourceNotFoundException(UserResource.class, id));
    }

    /**
     * ユーザーIDを指定して、ユーザーリソース更新API
     *
     * @param id ユーザーID
     * @param resource ユーザーリソース更新APIレスポンス
     * @return ユーザーリソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserResource> updateUser(
        @PathVariable("id") ID<UserEntity> id,
        @Valid @RequestBody UserResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ユーザーIDを指定して、ユーザーリソース削除API
     *
     * @param id ユーザーID
     * @return ユーザーリソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteUser(
        @PathVariable("id") ID<UserEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
