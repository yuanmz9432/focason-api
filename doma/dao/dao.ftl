<#-- このテンプレートに対応するデータモデルのクラスは org.seasar.doma.extension.gen.DaoDesc です -->
<#import "lib.ftl" as lib>
/*
<#if lib.copyright??>
    * ${lib.copyright}
</#if>
*/
<#if packageName??>
    package ${packageName};
</#if>



import attribute.core.com.lemonico.ID;
import entity.com.lemonico.Mg003StoreEntity;
import repository.temporary.com.lemonico.Mg003StoreRepository;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.BatchResult;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.jdbc.SelectOptions;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;


/**
 <#if entityDesc.comment??>
 * ${entityDesc.comment}のDao
 </#if>
 *
 <#if lib.since??>
 * @since ${lib.since}
 </#if>
 */
@Dao<#if configClassSimpleName??>(config = ${configClassSimpleName}.class)</#if>
@ConfigAutowireable
public interface ${simpleName}
{

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
        Collector<${entityDesc.simpleName}Entity, ?, R> collector);

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @param options SQL実行時オプション
     * @return エンティティが {@link Optional} で返されます。
     */
    @Select
    Optional<${entityDesc.simpleName}Entity> selectById(ID<${entityDesc.simpleName}Entity> id, SelectOptions options);

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。
     */
    default Optional<${entityDesc.simpleName}Entity> selectById(ID<${entityDesc.simpleName}Entity> id) {
            return selectById(id, SelectOptions.get());
            }

    /**
     * データベースにエンティティを挿入（新規作成）します。
     *
     * @param entity 挿入するエンティティ
     * @return エンティティ挿入結果が返されます。
     */
    @Insert(excludeNull = true)
    Result<${entityDesc.simpleName}Entity> insert(${entityDesc.simpleName}Entity entity);

    /**
     * データベースのエンティティを更新します。
     *
     * @param entity 更新するエンティティ
     * @return エンティティ更新結果が返されます。
     */
    @Update(excludeNull = true)
    Result<${entityDesc.simpleName}Entity> update(${entityDesc.simpleName}Entity entity);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Delete(sqlFile = true)
    int deleteById(ID<${entityDesc.simpleName}Entity> id);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Update(sqlFile = true)
    int deleteLogicById(ID<${entityDesc.simpleName}Entity> id);

    /**
     * @param entities エンティティリスト
     * @return エンティティ作成結果が返されます。
     */
    @BatchInsert
    BatchResult<${entityDesc.simpleName}Entity> insert(List<<#if entityDesc.entityPrefix??>${entityDesc.entityPrefix}</#if>${entityDesc.simpleName}Entity<#if entityDesc.entitySuffix??>${entityDesc.entitySuffix}</#if>> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ更新結果が返されます。
     */
    @BatchUpdate
    BatchResult<${entityDesc.simpleName}Entity> update(List<<#if entityDesc.entityPrefix??>${entityDesc.entityPrefix}</#if>${entityDesc.simpleName}Entity<#if entityDesc.entitySuffix??>${entityDesc.entitySuffix}</#if>> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ削除結果が返されます。
     */
    @BatchDelete
    BatchResult<${entityDesc.simpleName}Entity> delete(List<<#if entityDesc.entityPrefix??>${entityDesc.entityPrefix}</#if>${entityDesc.simpleName}Entity<#if entityDesc.entitySuffix??>${entityDesc.entitySuffix}</#if>> entities);

}
