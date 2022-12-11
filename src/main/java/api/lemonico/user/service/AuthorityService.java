/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.service;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.exception.LcResourceNotFoundException;
import api.lemonico.core.exception.LcUnexpectedPhantomReadException;
import api.lemonico.user.entity.AuthorityEntity;
import api.lemonico.user.repository.AuthorityRepository;
import api.lemonico.user.resource.AuthorityResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 権限マスタサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorityService
{

    /**
     * 権限マスタリポジトリ
     */
    private final AuthorityRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、権限マスタリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 権限マスタリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<AuthorityResource> getResourceList(
        AuthorityRepository.Condition condition,
        LcPagination pagination,
        AuthorityRepository.Sort sort) {
        // 権限マスタの一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // 権限マスタエンティティのリストを権限マスタリソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * 権限マスタIDを指定して、権限マスタを取得します。
     *
     * @param id 権限マスタID
     * @return 権限マスタリソース
     */
    @Transactional(readOnly = true)
    public Optional<AuthorityResource> getResource(ID<AuthorityEntity> id) {
        // 権限マスタを取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * 権限マスタを作成します。
     *
     * @param resource 権限マスタリソース
     * @return 作成された権限マスタリソース
     */
    @Transactional
    public AuthorityResource createResource(AuthorityResource resource) {
        // 権限マスタを作成します。
        var id = repository.create(resource.toEntity());

        // 権限マスタを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 権限マスタIDを指定して、権限マスタを更新します。
     *
     * @param id 権限マスタID
     * @param resource 権限マスタリソース
     * @return 更新後の権限マスタリソース
     */
    @Transactional
    public AuthorityResource updateResource(ID<AuthorityEntity> id, AuthorityResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 権限マスタIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(AuthorityEntity.class, id);
        }

        // 権限マスタを更新します。
        repository.update(id, resource.toEntity());

        // 権限マスタを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 権限マスタIDを指定して、権限マスタを削除します。
     *
     * @param id 権限マスタID
     */
    @Transactional
    public void deleteResource(ID<AuthorityEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 権限マスタIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(AuthorityEntity.class, id);
        }

        // 権限マスタを削除します。
        repository.deleteLogicById(id);
    }

    /**
     * 権限マスタエンティティを権限マスタリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public AuthorityResource convertEntityToResource(AuthorityEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * 権限マスタエンティティのリストを権限マスタリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<AuthorityResource> convertEntitiesToResources(List<AuthorityEntity> entities) {
        return entities.stream()
            .map(AuthorityResource::new)
            .collect(toList());
    }

}
