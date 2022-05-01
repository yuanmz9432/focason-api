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
import api.lemonico.entity.WarehouseEntity;
import api.lemonico.repository.WarehouseRepository;
import api.lemonico.resource.WarehouseResource;
import api.lemonico.service.WarehouseService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 倉庫情報コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehouseController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/warehouses";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * 倉庫情報サービス
     */
    private final WarehouseService service;

    /**
     * 倉庫情報リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return 倉庫情報リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<WarehouseResource>> getWarehouseList(
        @LcConditionParam WarehouseRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = WarehouseRepository.Condition.DEFAULT;
        }
        var sort = WarehouseRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報リソース取得API
     *
     * @param id 倉庫情報ID
     * @return 倉庫情報リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<WarehouseResource> getWarehouse(
        @PathVariable("id") ID<WarehouseEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(WarehouseResource.class, id));
    }

    /**
     * 倉庫情報リソース作成API
     *
     * @param resource 倉庫情報リソース
     * @return 倉庫情報リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createWarehouse(
        @Valid @RequestBody WarehouseResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getWarehouse(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報リソース更新API
     *
     * @param id 倉庫情報ID
     * @param resource 倉庫情報リソース更新APIレスポンス
     * @return 倉庫情報リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<WarehouseResource> updateWarehouse(
        @PathVariable("id") ID<WarehouseEntity> id,
        @Valid @RequestBody WarehouseResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報リソース削除API
     *
     * @param id 倉庫情報ID
     * @return 倉庫情報リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteWarehouse(
        @PathVariable("id") ID<WarehouseEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
