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
import com.focason.api.user.dao.AuthorityDao;
import com.focason.api.user.entity.AuthorityEntity;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 権限マスタリポジトリ
 *
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthorityRepository
{

    private final AuthorityDao dao;

    /**
     * 検索オプションを指定してエンティティの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return エンティティの結果セットが返されます。
     */
    public FsResultSet<AuthorityEntity> findAll(Condition condition, FsPagination pagination, Sort sort) {
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
    public Optional<AuthorityEntity> findById(ID<AuthorityEntity> id) throws IllegalArgumentException {
        return dao.selectById(id);
    }

    /**
     * エンティティを作成します。
     *
     * @param entity エンティティ
     * @return 作成したエンティティのIDが返されます。
     */
    public ID<AuthorityEntity> create(AuthorityEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        return dao.insert(entity
            .withId(null))
            .getEntity()
            .getId();
    }

    /**
     * エンティティを更新します。
     *
     * @param entity エンティティ
     */
    public void update(ID<AuthorityEntity> id, AuthorityEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        var result = dao.update(entity.withId(id));
        if (result.getCount() != 1) {
            throw new BaEntityNotFoundException(AuthorityEntity.class, entity.getId());
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteById(ID<AuthorityEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteById(id);
        if (deleted != 1) {
            throw new BaEntityNotFoundException(AuthorityEntity.class, id);
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteLogicById(ID<AuthorityEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteLogicById(id);
        if (deleted != 1) {
            throw new BaEntityNotFoundException(AuthorityEntity.class, id);
        }
    }

    /**
     * エンティティIDを指定して、エンティティが存在するかを確認します。
     *
     * @param id エンティティID
     * @return エンティティが存在する場合は true が返されます。
     */
    public boolean exists(ID<AuthorityEntity> id) {
        return findById(id).isPresent();
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
         * 権限マスタIDのセット（完全一致、複数指定可）
         */
        private Set<ID<AuthorityEntity>> ids;
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
