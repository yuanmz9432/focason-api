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
import api.lemonico.user.entity.User;
import api.lemonico.user.repository.UserRepository;
import api.lemonico.user.resource.UserResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ユーザーサービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService
{

    /**
     * ユーザーリポジトリ
     */
    private final UserRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ユーザーリソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ユーザーリソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<UserResource> getResourceList(
        UserRepository.Condition condition,
        LcPagination pagination,
        UserRepository.Sort sort) {
        // ユーザーの一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ユーザーエンティティのリストをユーザーリソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ユーザーIDを指定して、ユーザーを取得します。
     *
     * @param id ユーザーID
     * @return ユーザーリソース
     */
    @Transactional(readOnly = true)
    public Optional<UserResource> getResource(ID<User> id) {
        // ユーザーを取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ユーザーを作成します。
     *
     * @param resource ユーザーリソース
     * @return 作成されたユーザーリソース
     */
    @Transactional
    public UserResource createResource(UserResource resource) {
        // ユーザーを作成します。
        var id = repository.create(resource.toEntity());

        // ユーザーを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザーIDを指定して、ユーザーを更新します。
     *
     * @param id ユーザーID
     * @param resource ユーザーリソース
     * @return 更新後のユーザーリソース
     */
    @Transactional
    public UserResource updateResource(ID<User> id, UserResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ユーザーIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(User.class, id);
        }

        // ユーザーを更新します。
        repository.update(id, resource.toEntity());

        // ユーザーを取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザーIDを指定して、ユーザーを削除します。
     *
     * @param id ユーザーID
     */
    @Transactional
    public void deleteResource(ID<User> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ユーザーIDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(User.class, id);
        }

        // ユーザーを削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ユーザーエンティティをユーザーリソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public UserResource convertEntityToResource(User entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ユーザーエンティティのリストをユーザーリソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<UserResource> convertEntitiesToResources(List<User> entities) {
        return entities.stream()
            .map(UserResource::new)
            .collect(toList());
    }

}
