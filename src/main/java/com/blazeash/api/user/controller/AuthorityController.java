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
import com.blazeash.api.user.entity.AuthorityEntity;
import com.blazeash.api.user.repository.AuthorityRepository;
import com.blazeash.api.user.resource.AuthorityResource;
import com.blazeash.api.user.service.AuthorityService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 権限マスタコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorityController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/authorities";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * 権限マスタサービス
     */
    private final AuthorityService service;

    /**
     * 権限マスタリソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param baSort ソートパラメータ
     * @return 権限マスタリソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<BaResultSet<AuthorityResource>> getAuthorityList(
        @BaConditionParam AuthorityRepository.Condition condition,
        @BaPaginationParam BaPagination pagination,
        @BaSortParam(allowedValues = {}) BaSort baSort) {
        if (condition == null) {
            condition = AuthorityRepository.Condition.DEFAULT;
        }
        var sort = AuthorityRepository.Sort.fromLcSort(baSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * 権限マスタIDを指定して、権限マスタリソース取得API
     *
     * @param id 権限マスタID
     * @return 権限マスタリソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<AuthorityResource> getAuthority(
        @PathVariable("id") ID<AuthorityEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new BaResourceNotFoundException(AuthorityResource.class, id));
    }

    /**
     * 権限マスタリソース作成API
     *
     * @param resource 権限マスタリソース
     * @return 権限マスタリソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createAuthority(
        @Valid @RequestBody AuthorityResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getAuthority(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * 権限マスタIDを指定して、権限マスタリソース更新API
     *
     * @param id 権限マスタID
     * @param resource 権限マスタリソース更新APIレスポンス
     * @return 権限マスタリソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<AuthorityResource> updateAuthority(
        @PathVariable("id") ID<AuthorityEntity> id,
        @Valid @RequestBody AuthorityResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * 権限マスタIDを指定して、権限マスタリソース削除API
     *
     * @param id 権限マスタID
     * @return 権限マスタリソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteAuthority(
        @PathVariable("id") ID<AuthorityEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
