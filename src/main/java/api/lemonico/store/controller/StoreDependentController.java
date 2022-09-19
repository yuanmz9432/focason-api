/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.store.controller;


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
import api.lemonico.store.entity.StoreDependentEntity;
import api.lemonico.store.repository.StoreDependentRepository;
import api.lemonico.store.resource.StoreDependentResource;
import api.lemonico.store.service.StoreDependentService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ストア所属コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreDependentController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/storeDependent";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ストア所属サービス
     */
    private final StoreDependentService service;

    /**
     * ストア所属リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return ストア所属リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<StoreDependentResource>> getStoreDependentList(
        @LcConditionParam StoreDependentRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = StoreDependentRepository.Condition.DEFAULT;
        }
        var sort = StoreDependentRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ストア所属IDを指定して、ストア所属リソース取得API
     *
     * @param id ストア所属ID
     * @return ストア所属リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<StoreDependentResource> getStoreDependent(
        @PathVariable("id") ID<StoreDependentEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(StoreDependentResource.class, id));
    }

    /**
     * ストア所属リソース作成API
     *
     * @param resource ストア所属リソース
     * @return ストア所属リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createStoreDependent(
        @Valid @RequestBody StoreDependentResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getStoreDependent(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ストア所属IDを指定して、ストア所属リソース更新API
     *
     * @param id ストア所属ID
     * @param resource ストア所属リソース更新APIレスポンス
     * @return ストア所属リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<StoreDependentResource> updateStoreDependent(
        @PathVariable("id") ID<StoreDependentEntity> id,
        @Valid @RequestBody StoreDependentResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ストア所属IDを指定して、ストア所属リソース削除API
     *
     * @param id ストア所属ID
     * @return ストア所属リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteStoreDependent(
        @PathVariable("id") ID<StoreDependentEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
