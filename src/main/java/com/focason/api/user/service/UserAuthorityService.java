/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.service;

import static java.util.stream.Collectors.toList;

import com.focason.api.core.attribute.BaPagination;
import com.focason.api.core.attribute.BaResultSet;
import com.focason.api.core.attribute.ID;
import com.focason.api.core.exception.BaResourceNotFoundException;
import com.focason.api.core.exception.BaUnexpectedPhantomReadException;
import com.focason.api.user.entity.UserAuthorityEntity;
import com.focason.api.user.repository.UserAuthorityRepository;
import com.focason.api.user.resource.UserAuthorityResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ユーザ権限サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorityService
{

    /**
     * ユーザ権限リポジトリ
     */
    private final UserAuthorityRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ユーザ権限リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ユーザ権限リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public BaResultSet<UserAuthorityResource> getResourceList(
        UserAuthorityRepository.Condition condition,
        BaPagination pagination,
        UserAuthorityRepository.Sort sort) {
        // ユーザ権限の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ユーザ権限エンティティのリストをユーザ権限リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new BaResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ユーザ権限IDを指定して、ユーザ権限を取得します。
     *
     * @param id ユーザ権限ID
     * @return ユーザ権限リソース
     */
    @Transactional(readOnly = true)
    public Optional<UserAuthorityResource> getResource(ID<UserAuthorityEntity> id) {
        // ユーザ権限を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ユーザ権限を作成します。
     *
     * @param resource ユーザ権限リソース
     * @return 作成されたユーザ権限リソース
     */
    @Transactional
    public UserAuthorityResource createResource(UserAuthorityResource resource) {
        // ユーザ権限を作成します。
        var id = repository.create(resource.toEntity());

        // ユーザ権限を取得します。
        return getResource(id).orElseThrow(BaUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザ権限IDを指定して、ユーザ権限を更新します。
     *
     * @param id ユーザ権限ID
     * @param resource ユーザ権限リソース
     * @return 更新後のユーザ権限リソース
     */
    @Transactional
    public UserAuthorityResource updateResource(ID<UserAuthorityEntity> id, UserAuthorityResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ユーザ権限IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new BaResourceNotFoundException(UserAuthorityEntity.class, id);
        }

        // ユーザ権限を更新します。
        repository.update(id, resource.toEntity());

        // ユーザ権限を取得します。
        return getResource(id).orElseThrow(BaUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザ権限IDを指定して、ユーザ権限を削除します。
     *
     * @param id ユーザ権限ID
     */
    @Transactional
    public void deleteResource(ID<UserAuthorityEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ユーザ権限IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new BaResourceNotFoundException(UserAuthorityEntity.class, id);
        }

        // ユーザ権限を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ユーザ権限エンティティをユーザ権限リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public UserAuthorityResource convertEntityToResource(UserAuthorityEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ユーザ権限エンティティのリストをユーザ権限リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<UserAuthorityResource> convertEntitiesToResources(List<UserAuthorityEntity> entities) {
        return entities.stream()
            .map(UserAuthorityResource::new)
            .collect(toList());
    }

}
