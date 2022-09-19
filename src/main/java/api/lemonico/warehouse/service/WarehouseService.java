/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.warehouse.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceAlreadyExistsException;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.warehouse.entity.WarehouseEntity;
import api.lemonico.warehouse.repository.WarehouseRepository;
import api.lemonico.warehouse.resource.WarehouseResource;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 倉庫情報サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehouseService
{

    /**
     * 倉庫情報リポジトリ
     */
    private final WarehouseRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、倉庫情報リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 倉庫情報リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<WarehouseResource> getResourceList(
        WarehouseRepository.Condition condition,
        LcPagination pagination,
        WarehouseRepository.Sort sort) {
        // 倉庫情報の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // 倉庫情報エンティティのリストを倉庫情報リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報を取得します。
     *
     * @param id 倉庫情報ID
     * @return 倉庫情報リソース
     */
    @Transactional(readOnly = true)
    public Optional<WarehouseResource> getResource(ID<WarehouseEntity> id) {
        // 倉庫情報を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * 倉庫情報を作成します。
     *
     * @param resource 倉庫情報リソース
     * @return 作成された倉庫情報リソース
     */
    @Transactional
    public WarehouseResource createResource(WarehouseResource resource) {
        // ユーザ重複性チェック（username と email）
        if (isWarehouseExisted(resource)) {
            throw new LcResourceAlreadyExistsException(WarehouseEntity.class,
                resource.getWarehouseCode() + ":" + resource.getWarehouseName());
        }
        // 倉庫情報を作成します。
        var id = repository.create(resource.toEntity());

        // 倉庫情報を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザ重複性チェック
     *
     * @param resource ユーザリソース
     * @return true：重複した、false：重複しない
     */
    private boolean isWarehouseExisted(WarehouseResource resource) {
        var warehouses = getResourceList(
            WarehouseRepository.Condition.builder()
                .warehouseCodes(Set.of(resource.getWarehouseCode()))
                .build(),
            LcPagination.DEFAULT,
            WarehouseRepository.Sort.DEFAULT);
        return warehouses.getCount() > 0;
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報を更新します。
     *
     * @param id 倉庫情報ID
     * @param resource 倉庫情報リソース
     * @return 更新後の倉庫情報リソース
     */
    @Transactional
    public WarehouseResource updateResource(ID<WarehouseEntity> id, WarehouseResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 倉庫情報IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(WarehouseEntity.class, id);
        }

        // 倉庫情報を更新します。
        repository.update(id, resource.toEntity());

        // 倉庫情報を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 倉庫情報IDを指定して、倉庫情報を削除します。
     *
     * @param id 倉庫情報ID
     */
    @Transactional
    public void deleteResource(ID<WarehouseEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 倉庫情報IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(WarehouseEntity.class, id);
        }

        // 倉庫情報を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * 倉庫情報エンティティを倉庫情報リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public WarehouseResource convertEntityToResource(WarehouseEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * 倉庫情報エンティティのリストを倉庫情報リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<WarehouseResource> convertEntitiesToResources(List<WarehouseEntity> entities) {
        return entities.stream()
            .map(WarehouseResource::new)
            .collect(toList());
    }

}
