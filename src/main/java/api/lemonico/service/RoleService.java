/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.entity.RoleEntity;
import api.lemonico.repository.RoleRepository;
import api.lemonico.resource.RoleResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ロールマスタサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleService
{

    /**
     * ロールマスタリポジトリ
     */
    private final RoleRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ロールマスタリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ロールマスタリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<RoleResource> getResourceList(
        RoleRepository.Condition condition,
        LcPagination pagination,
        RoleRepository.Sort sort) {
        // ロールマスタの一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ロールマスタエンティティのリストをロールマスタリソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ロールマスタIDを指定して、ロールマスタを取得します。
     *
     * @param id ロールマスタID
     * @return ロールマスタリソース
     */
    @Transactional(readOnly = true)
    public Optional<RoleResource> getResource(ID<RoleEntity> id) {
        // ロールマスタを取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ロールマスタを作成します。
     *
     * @param resource ロールマスタリソース
     * @return 作成されたロールマスタリソース
     */
    @Transactional
    public RoleResource createResource(RoleResource resource) {
        // ロールマスタを作成します。
        var id = repository.create(resource.toEntity());

        // ロールマスタを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ロールマスタIDを指定して、ロールマスタを更新します。
     *
     * @param id ロールマスタID
     * @param resource ロールマスタリソース
     * @return 更新後のロールマスタリソース
     */
    @Transactional
    public RoleResource updateResource(ID<RoleEntity> id, RoleResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ロールマスタIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(RoleEntity.class, id);
        }

        // ロールマスタを更新します。
        repository.update(id, resource.toEntity());

        // ロールマスタを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ロールマスタIDを指定して、ロールマスタを削除します。
     *
     * @param id ロールマスタID
     */
    @Transactional
    public void deleteResource(ID<RoleEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ロールマスタIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(RoleEntity.class, id);
        }

        // ロールマスタを削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ロールマスタエンティティをロールマスタリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public RoleResource convertEntityToResource(RoleEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ロールマスタエンティティのリストをロールマスタリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<RoleResource> convertEntitiesToResources(List<RoleEntity> entities) {
        return entities.stream()
            .map(RoleResource::new)
            .collect(toList());
    }

}
