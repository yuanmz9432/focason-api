/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.repository;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.attribute.LcSort;
import api.lemonico.core.exception.LcEntityNotFoundException;
import api.lemonico.user.dao.UserDepartmentDao;
import api.lemonico.user.entity.UserDepartmentEntity;
import java.time.LocalDateTime;
import java.util.*;
import lombok.*;
import org.seasar.doma.jdbc.BatchResult;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * ユーザー部署リポジトリ
 *
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDepartmentRepository
{

    private final UserDepartmentDao dao;

    /**
     * 検索オプションを指定してエンティティの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return エンティティの結果セットが返されます。
     */
    public LcResultSet<UserDepartmentEntity> findAll(Condition condition, LcPagination pagination, Sort sort) {
        var options = pagination.toSelectOptions().count();
        var entities = dao.selectAll(condition, options, sort, toList());
        return new LcResultSet<>(entities, options.getCount());
    }

    /**
     * エンティティIDを指定してエンティティを一件取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。<br>
     *         エンティティが存在しない場合は空の {@link Optional} が返されます。
     */
    public Optional<UserDepartmentEntity> findById(ID<UserDepartmentEntity> id) throws IllegalArgumentException {
        return dao.selectById(id);
    }

    /**
     * エンティティを作成します。
     *
     * @param entity エンティティ
     * @return 作成したエンティティのIDが返されます。
     */
    public ID<UserDepartmentEntity> create(UserDepartmentEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        return dao.insert(entity
            .withId(null)
            .withCreatedBy(MDC.get("USERNAME"))
            .withCreatedAt(LocalDateTime.now())
            .withModifiedBy(MDC.get("USERNAME"))
            .withModifiedAt(LocalDateTime.now())
            .withIsDeleted(0))
            .getEntity()
            .getId();
    }

    /**
     * エンティティを更新します。
     *
     * @param entity エンティティ
     */
    public void update(ID<UserDepartmentEntity> id, UserDepartmentEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        var result = dao.update(entity.withId(id));
        if (result.getCount() != 1) {
            throw new LcEntityNotFoundException(UserDepartmentEntity.class, entity.getId());
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteById(ID<UserDepartmentEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteById(id);
        if (deleted != 1) {
            throw new LcEntityNotFoundException(UserDepartmentEntity.class, id);
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteLogicById(ID<UserDepartmentEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteLogicById(id);
        if (deleted != 1) {
            throw new LcEntityNotFoundException(UserDepartmentEntity.class, id);
        }
    }

    /**
     * エンティティIDを指定して、エンティティが存在するかを確認します。
     *
     * @param id エンティティID
     * @return エンティティが存在する場合は true が返されます。
     */
    public boolean exists(ID<UserDepartmentEntity> id) {
        return findById(id).isPresent();
    }

    /**
     * 複数エンティティを作成します。
     *
     * @param entities エンティティリスト
     * @return 作成したエンティティが返されます。
     */
    public BatchResult<UserDepartmentEntity> create(List<UserDepartmentEntity> entities) {
        Objects.requireNonNull(entities, "'entities' must not be NULL.");
        return dao.insert(entities);
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
         * ユーザー部署IDのセット（完全一致、複数指定可）
         */
        private Set<ID<UserDepartmentEntity>> ids;

        /**
         * ユーザーUUID（完全一致）
         */
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
        public static final Sort DEFAULT = new Sort(SortColumn.ID, LcSort.Direction.ASC);

        /**
         * ソート列
         */
        SortColumn column;

        /**
         * ソート順序
         */
        LcSort.Direction direction;

        /**
         * このソートパラメータをSQLステートメント形式に変換して返します。
         *
         * @return SQLステートメント
         */
        public String toSql() {
            return column.getColumnName() + " " + direction.name();
        }

        /**
         * {@link LcSort} から新規ソートパラメータを生成します。
         *
         * @param sort {@link LcSort}
         * @return ソートパラメータ
         */
        public static Sort fromLcSort(LcSort sort) {
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
