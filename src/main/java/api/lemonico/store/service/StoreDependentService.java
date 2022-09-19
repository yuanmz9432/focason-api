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
import api.lemonico.store.entity.StoreDependentEntity;
import api.lemonico.store.repository.StoreDependentRepository;
import api.lemonico.store.resource.StoreDependentResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ストア所属サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class StoreDependentService
{

    /**
     * ストア所属リポジトリ
     */
    private final StoreDependentRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ストア所属リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ストア所属リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<StoreDependentResource> getResourceList(
        StoreDependentRepository.Condition condition,
        LcPagination pagination,
        StoreDependentRepository.Sort sort) {
        // ストア所属の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ストア所属エンティティのリストをストア所属リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ストア所属IDを指定して、ストア所属を取得します。
     *
     * @param id ストア所属ID
     * @return ストア所属リソース
     */
    @Transactional(readOnly = true)
    public Optional<StoreDependentResource> getResource(ID<StoreDependentEntity> id) {
        // ストア所属を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ストア所属を作成します。
     *
     * @param resource ストア所属リソース
     * @return 作成されたストア所属リソース
     */
    @Transactional
    public StoreDependentResource createResource(StoreDependentResource resource) {
        // ストア所属を作成します。
        var id = repository.create(resource.toEntity());

        // ストア所属を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ストア所属IDを指定して、ストア所属を更新します。
     *
     * @param id ストア所属ID
     * @param resource ストア所属リソース
     * @return 更新後のストア所属リソース
     */
    @Transactional
    public StoreDependentResource updateResource(ID<StoreDependentEntity> id, StoreDependentResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ストア所属IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(StoreDependentEntity.class, id);
        }

        // ストア所属を更新します。
        repository.update(id, resource.toEntity());

        // ストア所属を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ストア所属IDを指定して、ストア所属を削除します。
     *
     * @param id ストア所属ID
     */
    @Transactional
    public void deleteResource(ID<StoreDependentEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ストア所属IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(StoreDependentEntity.class, id);
        }

        // ストア所属を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ストア所属エンティティをストア所属リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public StoreDependentResource convertEntityToResource(StoreDependentEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ストア所属エンティティのリストをストア所属リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<StoreDependentResource> convertEntitiesToResources(List<StoreDependentEntity> entities) {
        return entities.stream()
            .map(StoreDependentResource::new)
            .collect(toList());
    }

}
