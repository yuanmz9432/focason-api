/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.dao;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.Collection;
import api.lemonico.repository.CollectionRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.BatchResult;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.jdbc.SelectOptions;

/**
 * クレクションのDao
 *
 * @since 1.0.0
 */
@Dao
@ConfigAutowireable
public interface CollectionDao
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
        CollectionRepository.Condition condition,
        SelectOptions options,
        CollectionRepository.Sort sort,
        Collector<Collection, ?, R> collector);

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
        CollectionRepository.Condition condition,
        SelectOptions options,
        Collector<Collection, ?, R> collector) {
        return selectAll(condition, options, CollectionRepository.Sort.DEFAULT, collector);
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
        CollectionRepository.Sort sort,
        Collector<Collection, ?, R> collector) {
        return selectAll(CollectionRepository.Condition.DEFAULT, options, sort, collector);
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
        Collector<Collection, ?, R> collector) {
        return selectAll(CollectionRepository.Condition.DEFAULT, options, CollectionRepository.Sort.DEFAULT, collector);
    }

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @param options SQL実行時オプション
     * @return エンティティが {@link Optional} で返されます。
     */
    @Select
    Optional<Collection> selectById(ID<Collection> id, SelectOptions options);

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。
     */
    default Optional<Collection> selectById(ID<Collection> id) {
        return selectById(id, SelectOptions.get());
    }

    /**
     * データベースにエンティティを挿入（新規作成）します。
     *
     * @param entity 挿入するエンティティ
     * @return エンティティ挿入結果が返されます。
     */
    @Insert(excludeNull = true)
    Result<Collection> insert(Collection entity);

    /**
     * データベースのエンティティを更新します。
     *
     * @param entity 更新するエンティティ
     * @return エンティティ更新結果が返されます。
     */
    @Update(excludeNull = true)
    Result<Collection> update(Collection entity);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Delete(sqlFile = true)
    int deleteById(ID<Collection> id);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Update(sqlFile = true)
    int deleteLogicById(ID<Collection> id);

    /**
     * @param entities エンティティリスト
     * @return エンティティ作成結果が返されます。
     */
    @BatchInsert
    BatchResult<Collection> insert(List<Collection> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ更新結果が返されます。
     */
    @BatchUpdate
    BatchResult<Collection> update(List<Collection> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ削除結果が返されます。
     */
    @BatchDelete
    BatchResult<Collection> delete(List<Collection> entities);
}