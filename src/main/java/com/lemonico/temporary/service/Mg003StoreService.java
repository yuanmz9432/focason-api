/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.temporary.service;


import static java.util.stream.Collectors.toList;

import com.lemonico.core.attribute.ID;
import com.lemonico.core.attribute.PlPagination;
import com.lemonico.core.attribute.PlResultSet;
import com.lemonico.core.exception.PlResourceNotFoundException;
import com.lemonico.core.exception.PlUnexpectedPhantomReadException;
import com.lemonico.entity.Mg003StoreEntity;
import com.lemonico.temporary.repository.Mg003StoreRepository;
import com.lemonico.temporary.resource.Mg003StoreResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 店舗管理テーブルサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Mg003StoreService
{

    /**
     * 店舗管理テーブルリポジトリ
     */
    private final Mg003StoreRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、店舗管理テーブルリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 店舗管理テーブルリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public PlResultSet<Mg003StoreResource> getResourceList(
        Mg003StoreRepository.Condition condition,
        PlPagination pagination,
        Mg003StoreRepository.Sort sort) {
        // 店舗管理テーブルの一覧と全体件数を取得します。
        PlResultSet<Mg003StoreEntity> resultSet = repository.findAll(condition, pagination, sort);

        // 店舗管理テーブルエンティティのリストを店舗管理テーブルリソースのリストに変換します。
        List<Mg003StoreResource> resources = convertEntitiesToResources(resultSet.getData());
        return new PlResultSet<>(resources, resultSet.getCount());
    }

    /**
     * 店舗管理テーブルIDを指定して、店舗管理テーブルを取得します。
     *
     * @param id 店舗管理テーブルID
     * @return 店舗管理テーブルリソース
     */
    @Transactional(readOnly = true)
    public Optional<Mg003StoreResource> getResource(ID<Mg003StoreEntity> id) {
        // 店舗管理テーブルを取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * 店舗管理テーブルを作成します。
     *
     * @param resource 店舗管理テーブルリソース
     * @return 作成された店舗管理テーブルリソース
     */
    @Transactional
    public Mg003StoreResource createResource(Mg003StoreResource resource) {
        // 店舗管理テーブルを作成します。
        ID<Mg003StoreEntity> id = repository.create(resource.toEntity());

        // 店舗管理テーブルを取得します。
        return getResource(id).orElseThrow(PlUnexpectedPhantomReadException::new);
    }

    /**
     * 店舗管理テーブルIDを指定して、店舗管理テーブルを更新します。
     *
     * @param id 店舗管理テーブルID
     * @param resource 店舗管理テーブルリソース
     * @return 更新後の店舗管理テーブルリソース
     */
    @Transactional
    public Mg003StoreResource updateResource(ID<Mg003StoreEntity> id, Mg003StoreResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 店舗管理テーブルIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new PlResourceNotFoundException(Mg003StoreEntity.class, id);
        }

        // 店舗管理テーブルを更新します。
        repository.update(id, resource.toEntity());

        // 店舗管理テーブルを取得します。
        return getResource(id).orElseThrow(PlUnexpectedPhantomReadException::new);
    }

    /**
     * 店舗管理テーブルIDを指定して、店舗管理テーブルを削除します。
     *
     * @param id 店舗管理テーブルID
     */
    @Transactional
    public void deleteResource(ID<Mg003StoreEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 店舗管理テーブルIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new PlResourceNotFoundException(Mg003StoreEntity.class, id);
        }

        // 店舗管理テーブルを削除します。
        repository.deleteLogicById(id);
    }

    /**
     * 店舗管理テーブルエンティティを店舗管理テーブルリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public Mg003StoreResource convertEntityToResource(Mg003StoreEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * 店舗管理テーブルエンティティのリストを店舗管理テーブルリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<Mg003StoreResource> convertEntitiesToResources(List<Mg003StoreEntity> entities) {
        return entities.stream()
            .map(Mg003StoreResource::new)
            .collect(toList());
    }

}
