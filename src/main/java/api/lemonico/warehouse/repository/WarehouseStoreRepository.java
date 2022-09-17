/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.warehouse.repository;

import static java.util.stream.Collectors.toList;

import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcPagination;
import api.lemonico.core.attribute.LcResultSet;
import api.lemonico.core.attribute.LcSort;
import api.lemonico.core.exception.LcEntityNotFoundException;
import api.lemonico.warehouse.dao.WarehouseStoreDao;
import api.lemonico.warehouse.entity.WarehouseStoreEntity;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 倉庫ストア関連情報リポジトリ
 *
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehouseStoreRepository
{

    private final WarehouseStoreDao dao;

    /**
     * 検索オプションを指定してエンティティの一覧を取得します。
     *
     * @param condition 検索条件
     * @param pagination ページングパラメータ
     * @param sort ソートパラメータ
     * @return エンティティの結果セットが返されます。
     */
    public LcResultSet<WarehouseStoreEntity> findAll(Condition condition, LcPagination pagination, Sort sort) {
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
    public Optional<WarehouseStoreEntity> findById(ID<WarehouseStoreEntity> id) throws IllegalArgumentException {
        return dao.selectById(id);
    }

    /**
     * エンティティを作成します。
     *
     * @param entity エンティティ
     * @return 作成したエンティティのIDが返されます。
     */
    public ID<WarehouseStoreEntity> create(WarehouseStoreEntity entity) {
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
    public void update(ID<WarehouseStoreEntity> id, WarehouseStoreEntity entity) {
        Objects.requireNonNull(entity, "'entity' must not be NULL.");
        var result = dao.update(entity.withId(id));
        if (result.getCount() != 1) {
            throw new LcEntityNotFoundException(WarehouseStoreEntity.class, entity.getId());
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteById(ID<WarehouseStoreEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteById(id);
        if (deleted != 1) {
            throw new LcEntityNotFoundException(WarehouseStoreEntity.class, id);
        }
    }

    /**
     * エンティティを削除します。
     *
     * @param id エンティティID
     */
    public void deleteLogicById(ID<WarehouseStoreEntity> id) throws IllegalArgumentException {
        var deleted = dao.deleteLogicById(id);
        if (deleted != 1) {
            throw new LcEntityNotFoundException(WarehouseStoreEntity.class, id);
        }
    }

    /**
     * エンティティIDを指定して、エンティティが存在するかを確認します。
     *
     * @param id エンティティID
     * @return エンティティが存在する場合は true が返されます。
     */
    public boolean exists(ID<WarehouseStoreEntity> id) {
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
         * 倉庫ストア関連情報IDのセット（完全一致、複数指定可）
         */
        private Set<ID<WarehouseStoreEntity>> ids;

        /**
         * 倉庫コードのセット（完全一致、複数指定可）
         */
        private Set<String> warehouseCodes;
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
