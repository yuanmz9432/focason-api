<#-- このテンプレートに対応するデータモデルのクラスは org.seasar.doma.extension.gen.DaoDesc です -->
<#import "lib.ftl" as lib>
/**
<#if lib.copyright??>
 * ${lib.copyright}
</#if>
 */
<#if packageName??>
package ${packageName};
</#if>

import api.lemonico.attribute.ID;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.*;
import api.lemonico.entity.${entityDesc.simpleName};
import api.lemonico.repository.${entityDesc.simpleName}Repository;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.jdbc.SelectOptions;
import java.util.Optional;

import java.util.stream.Collector;

/**
<#if entityDesc.tableName??>
 * ${entityDesc.tableName}のDao
</#if>
 *
<#if lib.since??>
 * @since ${lib.since}
</#if>
 */
@Dao<#if configClassSimpleName??>(config = ${configClassSimpleName}.class)</#if>
@ConfigAutowireable
public interface ${simpleName} {

    /**
    * 指定したパラメータを使用してエンティティの一覧を取得します。
    *
    * @param condition 検索条件
    * @param options SQL実行時オプション
    * @param sort ソートオプション
    * @param collector 検索結果のコレクタ
    * @param <R> 戻り値の型
    * @return 検索結果
    */
    @Select(strategy = SelectType.COLLECT)
    <R> R selectAll(
        ${entityDesc.simpleName}Repository.Condition condition,
        SelectOptions options,
        ${entityDesc.simpleName}Repository.Sort sort,
        Collector<${entityDesc.simpleName}, ?, R> collector);

    /**
    * 指定したパラメータを使用してエンティティの一覧を取得します。
    *
    * @param condition 検索条件
    * @param options SQL実行時オプション
    * @param collector 検索結果のコレクタ
    * @param <R> 戻り値の型
    * @return 検索結果
    */
    default <R> R selectAll(
        ${entityDesc.simpleName}Repository.Condition condition,
        SelectOptions options,
        Collector<${entityDesc.simpleName}, ?, R> collector) {
        return selectAll(condition, options, ${entityDesc.simpleName}Repository.Sort.DEFAULT, collector);
    }

    /**
    * 指定したパラメータを使用してエンティティの一覧を取得します。
    *
    * @param options SQL実行時オプション
    * @param sort ソートオプション
    * @param collector 検索結果のコレクタ
    * @param <R> 戻り値の型
    * @return 検索結果
    */
    default <R> R selectAll(
        SelectOptions options,
        ${entityDesc.simpleName}Repository.Sort sort,
        Collector<${entityDesc.simpleName}, ?, R> collector) {
        return selectAll(${entityDesc.simpleName}Repository.Condition.DEFAULT, options, sort, collector);
    }

    /**
    * 指定したパラメータを使用してエンティティの一覧を取得します。
    *
    * @param options SQL実行時オプション
    * @param collector 検索結果のコレクタ
    * @param <R> 戻り値の型
    * @return 検索結果
    */
    default <R> R selectAll(
        SelectOptions options,
        Collector<${entityDesc.simpleName}, ?, R> collector) {
        return selectAll(${entityDesc.simpleName}Repository.Condition.DEFAULT, options, ${entityDesc.simpleName}Repository.Sort.DEFAULT, collector);
    }

    /**
    * エンティティIDを指定して、データベースからエンティティを一件を取得します。
    *
    * @param id エンティティID
    * @param options SQL実行時オプション
    * @return エンティティが {@link Optional} で返されます。
    */
    @Select
    Optional<${entityDesc.simpleName}> selectById(ID<${entityDesc.simpleName}> id, SelectOptions options);

    /**
    * エンティティIDを指定して、データベースからエンティティを一件を取得します。
    *
    * @param id エンティティID
    * @return エンティティが {@link Optional} で返されます。
    */
    default Optional<${entityDesc.simpleName}> selectById(ID<${entityDesc.simpleName}> id) { return selectById(id, SelectOptions.get()); }

    /**
    * データベースにエンティティを挿入（新規作成）します。
    *
    * @param entity 挿入するエンティティ
    * @return エンティティ挿入結果が返されます。
    */
    @Insert(excludeNull = true)
    Result<${entityDesc.simpleName}> insert(${entityDesc.simpleName} entity);

    /**
    * データベースのエンティティを更新します。
    *
    * @param entity 更新するエンティティ
    * @return エンティティ更新結果が返されます。
    */
    @Update(excludeNull = true)
    Result<${entityDesc.simpleName}> update(${entityDesc.simpleName} entity);

    /**
    * エンティティIDを指定して、データベースからエンティティを削除します。
    *
    * @param id エンティティID
    * @return エンティティ削除件数が返されます。
    */
    @Delete(sqlFile = true)
    int deleteById(ID<${entityDesc.simpleName}> id);

    /**
    * @param entities the <#if entityDesc.simpleName??>${entityDesc.simpleName}</#if>
    * @return affected rows
    */
    @org.seasar.doma.BatchInsert
    int[] insert(Iterable<<#if entityDesc.entityPrefix??>${entityDesc.entityPrefix}</#if>${entityDesc.simpleName}<#if entityDesc.entitySuffix??>${entityDesc.entitySuffix}</#if>> entities);

    /**
    * @param entities the <#if entityDesc.simpleName??>${entityDesc.simpleName}</#if>
    * @return affected rows
    */
    @org.seasar.doma.BatchUpdate
    int[] update(Iterable<<#if entityDesc.entityPrefix??>${entityDesc.entityPrefix}</#if>${entityDesc.simpleName}<#if entityDesc.entitySuffix??>${entityDesc.entitySuffix}</#if>> entities);

    /**
    * @param entities the <#if entityDesc.simpleName??>${entityDesc.simpleName}</#if>
    * @return affected rows
    */
    @org.seasar.doma.BatchDelete
    int[] delete(Iterable<<#if entityDesc.entityPrefix??>${entityDesc.entityPrefix}</#if>${entityDesc.simpleName}<#if entityDesc.entitySuffix??>${entityDesc.entitySuffix}</#if>> entities);
}