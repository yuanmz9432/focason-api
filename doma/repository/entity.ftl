<#-- このテンプレートに対応するデータモデルのクラスは org.seasar.doma.extension.gen.EntityDesc です -->
<#import "lib.ftl" as lib>
/*
<#if lib.copyright??>
 * ${lib.copyright}
</#if>
 */
<#if packageName??>
package ${packageName};
</#if>

import api.lemonico.attribute.LemonicoSort;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;

/**
<#if tableName??>
 * ${tableName}のリポジトリ
 *
</#if>
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@Repository
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class ${simpleName}${entitySuffix} {

    /**
    * 検索条件
    */
    @Data
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @NoArgsConstructor
    @Builder(toBuilder = true)
    @With
    public static class Condition {

        /**
        * デフォルトの検索条件
        */
        public static final Condition DEFAULT = new Condition();
    }

    /**
    * ソートパラメータ
    */
    @AllArgsConstructor
    @Value
    public static class Sort {

        /**
        * デフォルトの検索条件
        */
        public static final Sort DEFAULT = new Sort(SortColumn.ID, LemonicoSort.Direction.ASC);

        /**
        * ソート列
        */
        SortColumn column;

        /**
        * ソート順序
        */
        LemonicoSort.Direction direction;

        /**
        * このソートパラメータをSQLステートメント形式に変換して返します。
        *
        * @return SQLステートメント
        */
        public String toSql() { return column.getColumnName() + " " + direction.name(); }
    }

    /**
    * ソート列
    */
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public enum SortColumn {

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