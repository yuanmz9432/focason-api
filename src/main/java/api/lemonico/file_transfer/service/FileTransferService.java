/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.file_transfer.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.cloud.service.S3Service;
import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.file.resource.FileDownloadResource;
import api.lemonico.file_transfer.entity.FileTransfer;
import api.lemonico.file_transfer.repository.FileTransferRepository;
import api.lemonico.file_transfer.resource.FileTransferResource;

import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * フィアル転送サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class FileTransferService
{

    /**
     * フィアル転送リポジトリ
     */
    private final FileTransferRepository repository;

    /**
     * S3サービス
     */
    private final S3Service s3Service;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、フィアル転送リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return フィアル転送リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<FileTransferResource> getResourceList(
        FileTransferRepository.Condition condition,
        LcPagination pagination,
        FileTransferRepository.Sort sort) {
        // フィアル転送の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // フィアル転送エンティティのリストをフィアル転送リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * フィアル転送IDを指定して、フィアル転送を取得します。
     *
     * @param id フィアル転送ID
     * @return フィアル転送リソース
     */
    @Transactional(readOnly = true)
    public Optional<FileTransferResource> getResource(ID<FileTransfer> id) {
        // フィアル転送を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * フィアル転送を作成します。
     *
     * @param resource フィアル転送リソース
     * @return 作成されたフィアル転送リソース
     */
    @Transactional
    public FileTransferResource createResource(FileTransferResource resource) {
        // フィアル転送を作成します。
        var id = repository.create(resource.toEntity());

        // フィアル転送を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * フィアル転送IDを指定して、フィアル転送を更新します。
     *
     * @param id フィアル転送ID
     * @param resource フィアル転送リソース
     * @return 更新後のフィアル転送リソース
     */
    @Transactional
    public FileTransferResource updateResource(ID<FileTransfer> id, FileTransferResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // フィアル転送IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(FileTransfer.class, id);
        }

        // フィアル転送を更新します。
        repository.update(id, resource.toEntity());

        // フィアル転送を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * フィアル転送IDを指定して、フィアル転送を削除します。
     *
     * @param id フィアル転送ID
     */
    @Transactional
    public void deleteResource(ID<FileTransfer> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // フィアル転送IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(FileTransfer.class, id);
        }

        // フィアル転送を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * フィアル転送エンティティをフィアル転送リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public FileTransferResource convertEntityToResource(FileTransfer entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * フィアル転送エンティティのリストをフィアル転送リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<FileTransferResource> convertEntitiesToResources(List<FileTransfer> entities) {
        return entities.stream()
            .map(FileTransferResource::new)
            .collect(toList());
    }
}
