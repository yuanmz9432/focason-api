/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.temporary.controller;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import com.lemonico.core.annotation.PlConditionParam;
import com.lemonico.core.annotation.PlPaginationParam;
import com.lemonico.core.annotation.PlSortParam;
import com.lemonico.core.attribute.ID;
import com.lemonico.core.attribute.PlPagination;
import com.lemonico.core.attribute.PlResultSet;
import com.lemonico.core.attribute.PlSort;
import com.lemonico.core.exception.PlResourceNotFoundException;
import com.lemonico.entity.Mg003StoreEntity;
import com.lemonico.temporary.repository.Mg003StoreRepository;
import com.lemonico.temporary.resource.Mg003StoreResource;
import com.lemonico.temporary.service.Mg003StoreService;
import java.net.URI;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


/**
 * 店舗管理テーブルコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Mg003StoreController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * 店舗管理テーブルサービス
     */
    private final Mg003StoreService service;

    /**
     * 店舗管理テーブルリソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return 店舗管理テーブルリソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<PlResultSet<Mg003StoreResource>> getMg003StoreList(
        @PlConditionParam Mg003StoreRepository.Condition condition,
        @PlPaginationParam PlPagination pagination,
        @PlSortParam(allowedValues = {}) PlSort lcSort) {
        if (condition == null) {
            condition = Mg003StoreRepository.Condition.DEFAULT;
        }
        Mg003StoreRepository.Sort sort = Mg003StoreRepository.Sort.fromPlSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * 店舗管理テーブルIDを指定して、店舗管理テーブルリソース取得API
     *
     * @param id 店舗管理テーブルID
     * @return 店舗管理テーブルリソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Mg003StoreResource> getMg003Store(
        @PathVariable("id") ID<Mg003StoreEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new PlResourceNotFoundException(Mg003StoreResource.class, id));
    }

    /**
     * 店舗管理テーブルリソース作成API
     *
     * @param resource 店舗管理テーブルリソース
     * @return 店舗管理テーブルリソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createMg003Store(
        @Valid @RequestBody Mg003StoreResource resource,
        UriComponentsBuilder uriBuilder) {
        ID<Mg003StoreEntity> id = service.createResource(resource).getId();
        URI uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getMg003Store(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * 店舗管理テーブルIDを指定して、店舗管理テーブルリソース更新API
     *
     * @param id 店舗管理テーブルID
     * @param resource 店舗管理テーブルリソース更新APIレスポンス
     * @return 店舗管理テーブルリソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Mg003StoreResource> updateMg003Store(
        @PathVariable("id") ID<Mg003StoreEntity> id,
        @Valid @RequestBody Mg003StoreResource resource) {
        Mg003StoreResource updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * 店舗管理テーブルIDを指定して、店舗管理テーブルリソース削除API
     *
     * @param id 店舗管理テーブルID
     * @return 店舗管理テーブルリソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteMg003Store(
        @PathVariable("id") ID<Mg003StoreEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
