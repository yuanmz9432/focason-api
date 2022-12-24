package com.lemonico.product.repository;

import static java.util.stream.Collectors.toList;

import com.lemonico.core.attribute.ID;
import com.lemonico.core.attribute.PlPagination;
import com.lemonico.core.attribute.PlSort;
import com.lemonico.entity.Pd001ProductEntity;
import com.lemonico.product.dao.NewProductDao;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.*;
import org.seasar.doma.jdbc.SelectOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired
}))
public class ProductRepository
{
    private final static Logger logger = LoggerFactory.getLogger(ProductRepository.class);
    private final NewProductDao newProductDao;

    public Object findAll(Condition condition, PlPagination pagination, Sort sort) {
        SelectOptions options = pagination.toSelectOptions().count();
        List<Pd001ProductEntity> pd001ProductEntities =
            newProductDao.selectProducts(condition, options, sort, toList());
        logger.info(pd001ProductEntities.toString());
        return null;
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
         * ストア情報IDのセット（完全一致、複数指定可）
         */
        private Set<ID<Pd001ProductEntity>> ids;

        /**
         * ストアコード（完全一致、複数指定可）
         */
        public Set<String> productCode;

        public Set<String> getProductCode() {
            return productCode;
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
         * このソートパラメータをSQLステートメント形式に変換して返します。
         *
         * @return SQLステートメント
         */
        public String toSql() {
            return column.getColumnName() + " " + direction.name();
        }

        /**
         * {@link PlSort} から新規ソートパラメータを生成します。
         *
         * @param sort {@link PlSort}
         * @return ソートパラメータ
         */
        public static Sort fromPlSort(PlSort sort) {
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
