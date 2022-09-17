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
import api.lemonico.store.entity.StoreEntity;
import api.lemonico.store.repository.StoreRepository;
import api.lemonico.store.resource.StoreResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ストア情報サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreService
{

    /**
     * ストア情報リポジトリ
     */
    private final StoreRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ストア情報リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ストア情報リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<StoreResource> getResourceList(
        StoreRepository.Condition condition,
        LcPagination pagination,
        StoreRepository.Sort sort) {
        // ストア情報の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ストア情報エンティティのリストをストア情報リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ストア情報IDを指定して、ストア情報を取得します。
     *
     * @param id ストア情報ID
     * @return ストア情報リソース
     */
    @Transactional(readOnly = true)
    public Optional<StoreResource> getResource(ID<StoreEntity> id) {
        // ストア情報を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ストア情報を作成します。
     *
     * @param resource ストア情報リソース
     * @return 作成されたストア情報リソース
     */
    @Transactional
    public StoreResource createResource(StoreResource resource) {
        // ストア情報を作成します。
        var id = repository.create(resource.toEntity());

        // ストア情報を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ストア情報IDを指定して、ストア情報を更新します。
     *
     * @param id ストア情報ID
     * @param resource ストア情報リソース
     * @return 更新後のストア情報リソース
     */
    @Transactional
    public StoreResource updateResource(ID<StoreEntity> id, StoreResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ストア情報IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(StoreEntity.class, id);
        }

        // ストア情報を更新します。
        repository.update(id, resource.toEntity());

        // ストア情報を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ストア情報IDを指定して、ストア情報を削除します。
     *
     * @param id ストア情報ID
     */
    @Transactional
    public void deleteResource(ID<StoreEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ストア情報IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(StoreEntity.class, id);
        }

        // ストア情報を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ストア情報エンティティをストア情報リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public StoreResource convertEntityToResource(StoreEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ストア情報エンティティのリストをストア情報リソースのリストに変換します。
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
