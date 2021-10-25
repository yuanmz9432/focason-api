/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.file_transfer.controller;


import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.on;
import static org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder.relativeTo;

import api.lemonico.cloud.service.S3Service;
import api.lemonico.core.annotation.LcConditionParam;
import api.lemonico.core.annotation.LcPaginationParam;
import api.lemonico.core.annotation.LcSortParam;
import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.attribute.LcSort;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.file.resource.FileDownloadResource;
import api.lemonico.file_transfer.entity.FileTransfer;
import api.lemonico.file_transfer.repository.FileTransferRepository;
import api.lemonico.file_transfer.resource.FileTransferResource;
import api.lemonico.file_transfer.service.FileTransferService;
import javax.validation.Valid;
import javax.validation.groups.Default;

import api.lemonico.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * フィアル転送コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileTransferController
{
    /**
     * コレクションリソースURI
     */
    private static final String COLLECTION_RESOURCE_URI = "/file-transfer";

    /**
     * メンバーリソースURI
     */
    private static final String MEMBER_RESOURCE_URI = COLLECTION_RESOURCE_URI + "/{id}";

    /**
     * フィアル転送サービス
     */
    private final FileTransferService service;

    /**
     * S3サービス
     */
    private final S3Service s3Service;

    /**
     * フィアル転送リソースの一覧取得API
     *
     * @param condition 検索条件パラメータ
     * @param pagination ページネーションパラメータ
     * @param lcSort ソートパラメータ
     * @return フィアル転送リソース一覧取得APIレスポンス
     */
    @GetMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<LcResultSet<FileTransferResource>> getFileTransferList(
        @LcConditionParam FileTransferRepository.Condition condition,
        @LcPaginationParam LcPagination pagination,
        @LcSortParam(allowedValues = {}) LcSort lcSort) {
        if (condition == null) {
            condition = FileTransferRepository.Condition.DEFAULT;
        }
        var sort = FileTransferRepository.Sort.fromLcSort(lcSort);
        return ResponseEntity.ok(service.getResourceList(condition, pagination, sort));
    }

    /**
     * フィアル転送IDを指定して、フィアル転送リソース取得API
     *
     * @param id フィアル転送ID
     * @return フィアル転送リソース取得APIレスポンス
     */
    @GetMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<FileTransferResource> getFileTransfer(
        @PathVariable("id") ID<FileTransfer> id) {
        return service.getResource(id)
            .map(ResponseEntity::ok)
            .orElseThrow(() -> new LcResourceNotFoundException(FileTransferResource.class, id));
    }

    /**
     * フィアル転送リソース作成API
     *
     * @param resource フィアル転送リソース
     * @return フィアル転送リソース作成APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(COLLECTION_RESOURCE_URI)
    public ResponseEntity<Void> createFileTransfer(
        @Valid @RequestBody FileTransferResource resource,
        UriComponentsBuilder uriBuilder) {
        var id = service.createResource(resource).getId();
        var uri = relativeTo(uriBuilder)
            .withMethodCall(on(getClass()).getFileTransfer(id))
            .build()
            .encode()
            .toUri();
        return ResponseEntity.created(uri).build();
    }

    /**
     * フィアル転送IDを指定して、フィアル転送リソース更新API
     *
     * @param id フィアル転送ID
     * @param resource フィアル転送リソース更新APIレスポンス
     * @return フィアル転送リソース更新APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PutMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<FileTransferResource> updateFileTransfer(
        @PathVariable("id") ID<FileTransfer> id,
        @Valid @RequestBody FileTransferResource resource) {
        var updatedResource = service.updateResource(id, resource);
        return ResponseEntity.ok(updatedResource);
    }

    /**
     * フィアル転送IDを指定して、フィアル転送リソース削除API
     *
     * @param id フィアル転送ID
     * @return フィアル転送リソース削除APIレスポンス
     */
    @DeleteMapping(MEMBER_RESOURCE_URI)
    public ResponseEntity<Void> deleteFileTransfer(
        @PathVariable("id") ID<FileTransfer> id) {
        service.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
