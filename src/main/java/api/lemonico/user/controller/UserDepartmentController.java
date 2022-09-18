/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.controller;


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
import api.lemonico.user.entity.UserDepartmentEntity;
import api.lemonico.user.repository.UserDepartmentRepository;
import api.lemonico.user.resource.UserDepartmentResource;
import api.lemonico.user.service.UserDepartmentService;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * ユーザー部署コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDepartmentController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/userDepartments";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * ユーザー部署サービス
     */
    private final UserDepartmentService service;

    /**
     * ユーザー部署リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return ユーザー部署リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<UserDepartmentResource>> getUserDepartmentList(
        @LcConditionParam UserDepartmentRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = UserDepartmentRepository.Condition.DEFAULT;
        }
        var sort = UserDepartmentRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * ユーザー部署IDを指定して、ユーザー部署リソース取得API
     *
     * @param id ユーザー部署ID
     * @return ユーザー部署リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserDepartmentResource> getUserDepartment(
        @PathVariable("id") ID<UserDepartmentEntity> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(UserDepartmentResource.class, id));
    }

    /**
     * ユーザー部署リソース作成API
     *
     * @param resource ユーザー部署リソース
     * @return ユーザー部署リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createUserDepartment(
        @Valid @RequestBody UserDepartmentResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getUserDepartment(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * ユーザー部署IDを指定して、ユーザー部署リソース更新API
     *
     * @param id ユーザー部署ID
     * @param resource ユーザー部署リソース更新APIレスポンス
     * @return ユーザー部署リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<UserDepartmentResource> updateUserDepartment(
        @PathVariable("id") ID<UserDepartmentEntity> id,
        @Valid @RequestBody UserDepartmentResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * ユーザー部署IDを指定して、ユーザー部署リソース削除API
     *
     * @param id ユーザー部署ID
     * @return ユーザー部署リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteUserDepartment(
        @PathVariable("id") ID<UserDepartmentEntity> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
