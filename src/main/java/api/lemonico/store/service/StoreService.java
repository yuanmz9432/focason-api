/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.store.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.core.exception.LcValidationErrorException;
import api.lemonico.store.entity.StoreDependentEntity;
import api.lemonico.store.entity.StoreEntity;
import api.lemonico.store.repository.StoreDependentRepository;
import api.lemonico.store.repository.StoreRepository;
import api.lemonico.store.resource.StoreResource;
import api.lemonico.warehouse.repository.WarehouseRepository;
import api.lemonico.warehouse.resource.WarehouseResource;
import api.lemonico.warehouse.service.WarehouseService;
import java.time.LocalDateTime;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ストアサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class StoreService
{

    /**
     * ストアリポジトリ
     */
    private final StoreRepository repository;

    /**
     * ストア所属リポジトリ
     */
    private final StoreDependentRepository storeDependentRepository;

    /**
     * 倉庫サービス
     */
    private final WarehouseService warehouseService;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ストアリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ストアリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<StoreResource> getResourceList(
        StoreRepository.Condition condition,
        LcPagination pagination,
        StoreRepository.Sort sort) {
        // ストアの一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ストアエンティティのリストをストアリソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ストアIDを指定して、ストアを取得します。
     *
     * @param id ストアID
     * @return ストアリソース
     */
    @Transactional(readOnly = true)
    public Optional<StoreResource> getResource(ID<StoreEntity> id) {
        // ストアを取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ストアを作成します。
     *
     * @param resource ストアリソース
     * @return 作成されたストアリソース
     */
    @Transactional
    public StoreResource createResource(StoreResource resource) {
        if (resource.getStoreDependents() == null || resource.getStoreDependents().isEmpty()) {
            throw new LcValidationErrorException("storeDependents can not be null or empty.");
        }
        // ストアを作成します。
        var id = repository.create(resource.toEntity());

        var storeDependentEntities = new ArrayList<StoreDependentEntity>();
        resource.getStoreDependents().forEach((item) -> {
            // 指定する倉庫コードが存在するかチェック
            var warehouses = warehouseService.getResourceList(
                WarehouseRepository.Condition.builder().warehouseCodes(Set.of(item.getWarehouseCode())).build(),
                LcPagination.DEFAULT,
                WarehouseRepository.Sort.DEFAULT);
            if (warehouses == null || warehouses.isEmpty()) {
                throw new LcResourceNotFoundException(WarehouseResource.class, item.getWarehouseCode());
            }
            storeDependentEntities.add(item
                .withId(null)
                .withStoreCode(item.getStoreCode())
                .withWarehouseCode(item.getWarehouseCode())
                .withCreatedBy(MDC.get("USERNAME"))
                .withCreatedAt(LocalDateTime.now())
                .withModifiedBy(MDC.get("USERNAME"))
                .withModifiedAt(LocalDateTime.now())
                .withIsDeleted(0)
                .toEntity());
        });
        storeDependentRepository.create(storeDependentEntities);

        // ストアを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ストアIDを指定して、ストアを更新します。
     *
     * @param id ストアID
     * @param resource ストアリソース
     * @return 更新後のストアリソース
     */
    @Transactional
    public StoreResource updateResource(ID<StoreEntity> id, StoreResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ストアIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(StoreEntity.class, id);
        }

        // ストアを更新します。
        repository.update(id, resource.toEntity());

        // ストアを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ストアIDを指定して、ストアを削除します。
     *
     * @param id ストアID
     */
    @Transactional
    public void deleteResource(ID<StoreEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ストアIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(StoreEntity.class, id);
        }

        // ストアを削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ストアエンティティをストアリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public StoreResource convertEntityToResource(StoreEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ストアエンティティのリストをストアリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<StoreResource> convertEntitiesToResources(List<StoreEntity> entities) {
        return entities.stream()
            .map(StoreResource::new)
            .collect(toList());
    }

}
