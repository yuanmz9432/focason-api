package com.lemonico.warehouse.repository;

import static java.util.stream.Collectors.toList;

import com.lemonico.core.attribute.ID;
import com.lemonico.core.attribute.PlPagination;
import com.lemonico.core.attribute.PlResultSet;
import com.lemonico.core.attribute.PlSort;
import com.lemonico.entity.Mg002WarehouseEntity;
import com.lemonico.warehouse.dao.WarehouseDao;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.*;
import org.seasar.doma.jdbc.SelectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * 倉庫情報のリポジトリ
 *
 * @since 1.0.0
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class WarehouseRepository
{

    private final WarehouseDao warehouseDao;

    public PlResultSet<Mg002WarehouseEntity> findAll(Condition condition, PlPagination pagination, Sort sort) {
        SelectOptions options = pagination.toSelectOptions().count();
        List<Mg002WarehouseEntity> entities = warehouseDao.selectAll(condition, options, sort, toList());
        return new PlResultSet<>(entities, options.getCount());
    }

    public Optional<Mg002WarehouseEntity> findById(ID<Mg002WarehouseEntity> id) {
        return warehouseDao.selectById(id);
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
         * 倉庫コード（完全一致、複数指定可）
         */
        public Set<String> warehouseCode;
        /**
         * 倉庫情報IDのセット（完全一致、複数指定可）
         */
        private Set<ID<Mg002WarehouseEntity>> ids;

        public Set<String> getWarehouseCode() {
            return warehouseCode;
        }
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
        public static final Sort DEFAULT = new Sort(SortColumn.ID, PlSort.Direction.ASC);

        /**
         * ソート列
         */
        SortColumn column;

        /**
         * ソート順序
         */
        PlSort.Direction direction;

        /**
         * {@link PlSort} から新規ソートパラメータを生成します。
         *
         * @param sort {@link PlSort}
         * @return ソートパラメータ
         */
        public static Sort fromPlSort(PlSort sort) {
            return new Sort(SortColumn.fromPropertyName(sort.getProperty()), sort.getDirection());
        }

        /**
         * このソートパラメータをSQLステートメント形式に変換して返します。
         *
         * @return SQLステートメント
         */
        public String toSql() {
            return column.getColumnName() + " " + direction.name();
        }
    }
}
