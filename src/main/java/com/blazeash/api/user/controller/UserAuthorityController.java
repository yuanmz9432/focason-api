/*
 * Copyright 2021 Blazeash Co.,Ltd. AllRights Reserved.
 */
package com.blazeash.api.user.controller;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import com.blazeash.api.core.annotation.BaConditionParam;
import com.blazeash.api.core.annotation.BaPaginationParam;
import com.blazeash.api.core.annotation.BaSortParam;
import com.blazeash.api.core.attribute.BaPagination;
import com.blazeash.api.core.attribute.BaResultSet;
import com.blazeash.api.core.attribute.BaSort;
import com.blazeash.api.core.attribute.ID;
import com.blazeash.api.core.exception.BaResourceNotFoundException;
import com.blazeash.api.user.entity.UserAuthorityEntity;
import com.blazeash.api.user.repository.UserAuthorityRepository;
import com.blazeash.api.user.resource.UserAuthorityResource;
import com.blazeash.api.user.service.UserAuthorityService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ユーザ権限コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorityController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/userAuthorities";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ユーザ権限サービス
     */
    private final UserAuthorityService service;

    /**
     * ユーザ権限リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param baSort ソートパラメータ
     * @return ユーザ権限リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<BaResultSet<UserAuthorityResource>> getUserAuthorityList(
        @BaConditionParam UserAuthorityRepository.Condition condition,
        @BaPaginationParam BaPagination pagination,
        @BaSortParam(allowedValues = {}) BaSort baSort) {
        if (condition == null) {
            condition = UserAuthorityRepository.Condition.DEFAULT;
        }
        var sort = UserAuthorityRepository.Sort.fromLcSort(baSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ユーザ権限IDを指定して、ユーザ権限リソース取得API
     *
     * @param id ユーザ権限ID
     * @return ユーザ権限リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserAuthorityResource> getUserAuthority(
        @PathVariable("id") ID<UserAuthorityEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new BaResourceNotFoundException(UserAuthorityResource.class, id));
    }

    /**
     * ユーザ権限リソース作成API
     *
     * @param resource ユーザ権限リソース
     * @return ユーザ権限リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createUserAuthority(
        @Valid @RequestBody UserAuthorityResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getUserAuthority(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ユーザ権限IDを指定して、ユーザ権限リソース更新API
     *
     * @param id ユーザ権限ID
     * @param resource ユーザ権限リソース更新APIレスポンス
     * @return ユーザ権限リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserAuthorityResource> updateUserAuthority(
        @PathVariable("id") ID<UserAuthorityEntity> id,
        @Valid @RequestBody UserAuthorityResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ユーザ権限IDを指定して、ユーザ権限リソース削除API
     *
     * @param id ユーザ権限ID
     * @return ユーザ権限リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteUserAuthority(
        @PathVariable("id") ID<UserAuthorityEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
