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
import api.lemonico.user.entity.UserDepartmentEntity;
import api.lemonico.user.repository.UserDepartmentRepository;
import api.lemonico.user.resource.UserDepartmentResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * ユーザー部署サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDepartmentService
{

    /**
     * ユーザー部署リポジトリ
     */
    private final UserDepartmentRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、ユーザー部署リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return ユーザー部署リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<UserDepartmentResource> getResourceList(
        UserDepartmentRepository.Condition condition,
        LcPagination pagination,
        UserDepartmentRepository.Sort sort) {
        // ユーザー部署の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // ユーザー部署エンティティのリストをユーザー部署リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * ユーザー部署IDを指定して、ユーザー部署を取得します。
     *
     * @param id ユーザー部署ID
     * @return ユーザー部署リソース
     */
    @Transactional(readOnly = true)
    public Optional<UserDepartmentResource> getResource(ID<UserDepartmentEntity> id) {
        // ユーザー部署を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * ユーザー部署を作成します。
     *
     * @param resource ユーザー部署リソース
     * @return 作成されたユーザー部署リソース
     */
    @Transactional
    public UserDepartmentResource createResource(UserDepartmentResource resource) {
        // ユーザー部署を作成します。
        var id = repository.create(resource.toEntity());

        // ユーザー部署を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザー部署IDを指定して、ユーザー部署を更新します。
     *
     * @param id ユーザー部署ID
     * @param resource ユーザー部署リソース
     * @return 更新後のユーザー部署リソース
     */
    @Transactional
    public UserDepartmentResource updateResource(ID<UserDepartmentEntity> id, UserDepartmentResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ユーザー部署IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(UserDepartmentEntity.class, id);
        }

        // ユーザー部署を更新します。
        repository.update(id, resource.toEntity());

        // ユーザー部署を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * ユーザー部署IDを指定して、ユーザー部署を削除します。
     *
     * @param id ユーザー部署ID
     */
    @Transactional
    public void deleteResource(ID<UserDepartmentEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // ユーザー部署IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(UserDepartmentEntity.class, id);
        }

        // ユーザー部署を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * ユーザー部署エンティティをユーザー部署リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public UserDepartmentResource convertEntityToResource(UserDepartmentEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * ユーザー部署エンティティのリストをユーザー部署リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<UserDepartmentResource> convertEntitiesToResources(List<UserDepartmentEntity> entities) {
        return entities.stream()
            .map(UserDepartmentResource::new)
            .collect(toList());
    }

}
