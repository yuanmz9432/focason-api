/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.controller;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import api.lemonico.core.annotation.LcConditionParam;
import api.lemonico.core.annotation.LcPaginationParam;
import api.lemonico.core.annotation.LcSortParam;
import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.attribute.LcSort;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.entity.RoleEntity;
import api.lemonico.repository.RoleRepository;
import api.lemonico.resource.RoleResource;
import api.lemonico.service.RoleService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ロールマスタコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/role";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ロールマスタサービス
     */
    private final RoleService service;

    /**
     * ロールマスタリソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return ロールマスタリソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<RoleResource>> getRoleList(
        @LcConditionParam RoleRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = RoleRepository.Condition.DEFAULT;
        }
        var sort = RoleRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ロールマスタIDを指定して、ロールマスタリソース取得API
     *
     * @param id ロールマスタID
     * @return ロールマスタリソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<RoleResource> getRole(
        @PathVariable("id") ID<RoleEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(RoleResource.class, id));
    }

    /**
     * ロールマスタリソース作成API
     *
     * @param resource ロールマスタリソース
     * @return ロールマスタリソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createRole(
        @Valid @RequestBody RoleResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getRole(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ロールマスタIDを指定して、ロールマスタリソース更新API
     *
     * @param id ロールマスタID
     * @param resource ロールマスタリソース更新APIレスポンス
     * @return ロールマスタリソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<RoleResource> updateRole(
        @PathVariable("id") ID<RoleEntity> id,
        @Valid @RequestBody RoleResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ロールマスタIDを指定して、ロールマスタリソース削除API
     *
     * @param id ロールマスタID
     * @return ロールマスタリソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteRole(
        @PathVariable("id") ID<RoleEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
