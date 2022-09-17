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
import api.lemonico.user.entity.UserRelationEntity;
import api.lemonico.user.repository.UserRelationRepository;
import api.lemonico.user.resource.UserRelationResource;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 倉庫ストア関連情報サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserRelationService
{

    /**
     * 倉庫ストア関連情報リポジトリ
     */
    private final UserRelationRepository repository;

    /**
     * 検索条件・ページングパラメータ・ソート条件を指定して、倉庫ストア関連情報リソースの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return 倉庫ストア関連情報リソースの結果セットが返されます。
     */
    @Transactional(readOnly = true)
    public LcResultSet<UserRelationResource> getResourceList(
        UserRelationRepository.Condition condition,
        LcPagination pagination,
        UserRelationRepository.Sort sort) {
        // 倉庫ストア関連情報の一覧と全体件数を取得します。
        var resultSet = repository.findAll(condition, pagination, sort);

        // 倉庫ストア関連情報エンティティのリストを倉庫ストア関連情報リソースのリストに変換します。
        var resources = convertEntitiesToResources(resultSet.getData());
        return new LcResultSet<>(resources, resultSet.getCount());
    }

    /**
     * 倉庫ストア関連情報IDを指定して、倉庫ストア関連情報を取得します。
     *
     * @param id 倉庫ストア関連情報ID
     * @return 倉庫ストア関連情報リソース
     */
    @Transactional(readOnly = true)
    public Optional<UserRelationResource> getResource(ID<UserRelationEntity> id) {
        // 倉庫ストア関連情報を取得します。
        return repository.findById(id).map(this::convertEntityToResource);
    }

    /**
     * 倉庫ストア関連情報を作成します。
     *
     * @param resource 倉庫ストア関連情報リソース
     * @return 作成された倉庫ストア関連情報リソース
     */
    @Transactional
    public UserRelationResource createResource(UserRelationResource resource) {
        // 倉庫ストア関連情報を作成します。
        var id = repository.create(resource.toEntity());

        // 倉庫ストア関連情報を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 倉庫ストア関連情報IDを指定して、倉庫ストア関連情報を更新します。
     *
     * @param id 倉庫ストア関連情報ID
     * @param resource 倉庫ストア関連情報リソース
     * @return 更新後の倉庫ストア関連情報リソース
     */
    @Transactional
    public UserRelationResource updateResource(ID<UserRelationEntity> id, UserRelationResource resource) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 倉庫ストア関連情報IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(UserRelationEntity.class, id);
        }

        // 倉庫ストア関連情報を更新します。
        repository.update(id, resource.toEntity());

        // 倉庫ストア関連情報を取得します。
        return getResource(id).orElseThrow(LcUnexpectedPhantomReadException::new);
    }

    /**
     * 倉庫ストア関連情報IDを指定して、倉庫ストア関連情報を削除します。
     *
     * @param id 倉庫ストア関連情報ID
     */
    @Transactional
    public void deleteResource(ID<UserRelationEntity> id) {
        // TODO Waiting for finalization of basic design according to Q&A
        // 倉庫ストア関連情報IDにおいて重複したデータが存在していることを示す。
        if (!repository.exists(id)) {
            throw new LcResourceNotFoundException(UserRelationEntity.class, id);
        }

        // 倉庫ストア関連情報を削除します。
        repository.deleteLogicById(id);
    }

    /**
     * 倉庫ストア関連情報エンティティを倉庫ストア関連情報リソースに変換します。
     *
     * @param entity エンティティ
     * @return リソース
     */
    @Transactional(readOnly = true)
    public UserRelationResource convertEntityToResource(UserRelationEntity entity) {
        return convertEntitiesToResources(Collections.singletonList(entity)).get(0);
    }

    /**
     * 倉庫ストア関連情報エンティティのリストを倉庫ストア関連情報リソースのリストに変換します。
     *
     * @param entities エンティティのリスト
     * @return リソースのリスト
     */
    @Transactional(readOnly = true)
    public List<UserRelationResource> convertEntitiesToResources(List<UserRelationEntity> entities) {
        return entities.stream()
            .map(UserRelationResource::new)
            .collect(toList());
    }

}
