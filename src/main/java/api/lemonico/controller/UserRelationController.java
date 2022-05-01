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
import api.lemonico.entity.UserRelationEntity;
import api.lemonico.repository.UserRelationRepository;
import api.lemonico.resource.UserRelationResource;
import api.lemonico.service.UserRelationService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * 倉庫ストア関連情報コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRelationController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/user-relations";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * 倉庫ストア関連情報サービス
     */
    private final UserRelationService service;

    /**
     * 倉庫ストア関連情報リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return 倉庫ストア関連情報リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<UserRelationResource>> getUserRelationList(
        @LcConditionParam UserRelationRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = UserRelationRepository.Condition.DEFAULT;
        }
        var sort = UserRelationRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * 倉庫ストア関連情報IDを指定して、倉庫ストア関連情報リソース取得API
     *
     * @param id 倉庫ストア関連情報ID
     * @return 倉庫ストア関連情報リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserRelationResource> getUserRelation(
        @PathVariable("id") ID<UserRelationEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(UserRelationResource.class, id));
    }

    /**
     * 倉庫ストア関連情報リソース作成API
     *
     * @param resource 倉庫ストア関連情報リソース
     * @return 倉庫ストア関連情報リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createUserRelation(
        @Valid @RequestBody UserRelationResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getUserRelation(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * 倉庫ストア関連情報IDを指定して、倉庫ストア関連情報リソース更新API
     *
     * @param id 倉庫ストア関連情報ID
     * @param resource 倉庫ストア関連情報リソース更新APIレスポンス
     * @return 倉庫ストア関連情報リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserRelationResource> updateUserRelation(
        @PathVariable("id") ID<UserRelationEntity> id,
        @Valid @RequestBody UserRelationResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * 倉庫ストア関連情報IDを指定して、倉庫ストア関連情報リソース削除API
     *
     * @param id 倉庫ストア関連情報ID
     * @return 倉庫ストア関連情報リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteUserRelation(
        @PathVariable("id") ID<UserRelationEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
