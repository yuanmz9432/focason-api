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
import api.lemonico.store.entity.StoreEntity;
import api.lemonico.store.repository.StoreRepository;
import api.lemonico.store.resource.StoreResource;
import api.lemonico.store.service.StoreService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ストア情報コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/stores";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ストア情報サービス
     */
    private final StoreService service;

    /**
     * ストア情報リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return ストア情報リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<StoreResource>> getStoreList(
        @LcConditionParam StoreRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = StoreRepository.Condition.DEFAULT;
        }
        var sort = StoreRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ストア情報IDを指定して、ストア情報リソース取得API
     *
     * @param id ストア情報ID
     * @return ストア情報リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<StoreResource> getStore(
        @PathVariable("id") ID<StoreEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(StoreResource.class, id));
    }

    /**
     * ストア情報リソース作成API
     *
     * @param resource ストア情報リソース
     * @return ストア情報リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createStore(
        @Valid @RequestBody StoreResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getStore(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ストア情報IDを指定して、ストア情報リソース更新API
     *
     * @param id ストア情報ID
     * @param resource ストア情報リソース更新APIレスポンス
     * @return ストア情報リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<StoreResource> updateStore(
        @PathVariable("id") ID<StoreEntity> id,
        @Valid @RequestBody StoreResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ストア情報IDを指定して、ストア情報リソース削除API
     *
     * @param id ストア情報ID
     * @return ストア情報リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteStore(
        @PathVariable("id") ID<StoreEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
