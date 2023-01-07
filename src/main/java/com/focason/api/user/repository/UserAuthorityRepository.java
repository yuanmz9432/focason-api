/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.repository;

import static java.util.stream.Collectors.toList;

import com.focason.api.core.attribute.FsPagination;
import com.focason.api.core.attribute.FsResultSet;
import com.focason.api.core.attribute.FsSort;
import com.focason.api.core.attribute.ID;
import com.focason.api.core.exception.BaEntityNotFoundException;
import com.focason.api.core.exception.BaValidationErrorException;
import com.focason.api.user.dao.UserAuthorityDao;
import com.focason.api.user.entity.UserAuthorityEntity;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import lombok.*;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ユーザ権限リポジトリ
 *
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserAuthorityRepository
{

    private final UserAuthorityDao dao;

    /**
     * 検索オプションを指定してエンティティの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return エンティティの結果セットが返されます。
     */
    public FsResultSet<UserAuthorityEntity> findAll(Condition condition, FsPagination pagination, Sort sort) {
        var options = pagination.toSelectOptions().count();
        var entities = dao.selectAll(condition, options, sort, toList());
        return new FsResultSet<>(entities, options.getCount());
    }

    /**
     * エンティティIDを指定してエンティティを一件取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。<br>
     *         エンティティが存在しない場合は空の {@link Optional} が返されます。
     */
    public Optional<UserAuthorityEntity> findById(ID<UserAuthorityEntity> id) throws IllegalArgumentException {
        return dao.selectById(id);
    }

    /**
     * エンティティを作成します。
     *
     * @param entity エンティティ
     * @return 作成したエンティティのIDが返されます。
     */
    public ID<UserAuthorityEntity> create(UserAuthorityEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        return dao.insert(entity
            .withId(null)
            .withCreatedBy("")
            .withModifiedBy(""))
            .getEntity()
            .getId();
    }

    /**
     * エンティティを更新します。
     *
     * @param entity エンティティ
     */
    public void update(ID<UserAuthorityEntity> id, UserAuthorityEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        var result = dao.update(entity.withId(id));
        if (result.getCount() != 1) {
            throw new BaEntityNotFoundException(UserAuthorityEntity.class, entity.getId());
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteById(ID<UserAuthorityEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteById(id);
        if (deleted != 1) {
            throw new BaEntityNotFoundException(UserAuthorityEntity.class, id);
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteLogicById(ID<UserAuthorityEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteLogicById(id);
        if (deleted != 1) {
            throw new BaEntityNotFoundException(UserAuthorityEntity.class, id);
        }
    }

    /**
     * エンティティIDを指定して、エンティティが存在するかを確認します。
     *
     * @param id エンティティID
     * @return エンティティが存在する場合は true が返されます。
     */
    public boolean exists(ID<UserAuthorityEntity> id) {
        return findById(id).isPresent();
    }

    /**
     * ユーザに権限情報を付与する。
     *
     * @param uuid ユーザUUID
     * @param authorities 権限リスト
     */
    public void grantAuthorization(@NonNull String uuid, List<String> authorities) {
        if (authorities == null || authorities.size() == 0) {
            throw new BaValidationErrorException("ユーザ権限情報が存在しません。");
        }
        List<UserAuthorityEntity> entities = authorities.stream().map((authority) -> UserAuthorityEntity.builder()
            .authorityCode(authority)
            .uuid(uuid)
            .createdAt(LocalDateTime.now())
            .createdBy(MDC.get("USERNAME"))
            .modifiedAt(LocalDateTime.now())
            .modifiedBy(MDC.get("USERNAME"))
            .isDeleted(0).build()).collect(Collectors.toList());
        dao.insert(entities);
    }

    /**
     * 検索条件
     */
    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @Builder(toBuilder = true)
    @With
    public static class Condition
    {

        /**
         * デフォルトの検索条件
         */
        public static final Condition DEFAULT = new Condition();

        /**
         * ユーザ権限IDのセット（完全一致、複数指定可）
         */
        private Set<ID<UserAuthorityEntity>> ids;

        private String uuid;
    }

    /**
     * ソートパラメータ
     */
    @AllArgsConstructor
    @Value
    public static class Sort
    {

        /**
         * デフォルトの検索条件
         */
        public static final Sort DEFAULT = new Sort(SortColumn.ID, FsSort.Direction.ASC);

        /**
         * ソート列
         */
        SortColumn column;

        /**
         * ソート順序
         */
        FsSort.Direction direction;

        /**
         * このソートパラメータをSQLステートメント形式に変換して返します。
         *
         * @return SQLステートメント
         */
        public String toSql() {
            return column.getColumnName() + " " + direction.name();
        }

        /**
         * {@link FsSort} から新規ソートパラメータを生成します。
         *
         * @param sort {@link FsSort}
         * @return ソートパラメータ
         */
        public static Sort fromLcSort(FsSort sort) {
            return new Sort(SortColumn.fromPropertyName(sort.getProperty()), sort.getDirection());
        }
    }

    /**
     * ソート列
     */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum SortColumn
    {

        ID("id", "id");

        /**
         * プロパティ名
         */
        @Getter
        private final String propertyName;

        /**
         * データベース列名
         */
        @Getter
        private final String columnName;

        public static SortColumn fromPropertyName(String propertyName) {
            return Arrays.stream(values())
                .filter(v -> v.propertyName.equals(propertyName))
                .findFirst()
                .orElseThrow(
                    () -> new IllegalArgumentException("propertyName = '" + propertyName + "' is not supported."));
        }
    }
}
