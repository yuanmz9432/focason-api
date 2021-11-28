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
import api.lemonico.entity.Client;
import api.lemonico.repository.ClientRepository;
import api.lemonico.resource.ClientResource;
import api.lemonico.service.ClientService;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * クライアントコントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ClientController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/clients";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * クライアントサービス
     */
    private final ClientService service;

    /**
     * クライアントリソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return クライアントリソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<ClientResource>> getClientList(
        @LcConditionParam ClientRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = ClientRepository.Condition.DEFAULT;
        }
        var sort = ClientRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * クライアントIDを指定して、クライアントリソース取得API
     *
     * @param id クライアントID
     * @return クライアントリソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<ClientResource> getClient(
        @PathVariable("id") ID<Client> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(ClientResource.class, id));
    }

    /**
     * クライアントリソース作成API
     *
     * @param resource クライアントリソース
     * @return クライアントリソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createClient(
        @Valid @RequestBody ClientResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(
            resource.withClientCode(UUID.randomUUID().toString().substring(0, 8))).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getClient(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * クライアントIDを指定して、クライアントリソース更新API
     *
     * @param id クライアントID
     * @param resource クライアントリソース更新APIレスポンス
     * @return クライアントリソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<ClientResource> updateClient(
        @PathVariable("id") ID<Client> id,
        @Valid @RequestBody ClientResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * クライアントIDを指定して、クライアントリソース削除API
     *
     * @param id クライアントID
     * @return クライアントリソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteClient(
        @PathVariable("id") ID<Client> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
