/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.dao;



import com.focason.api.core.attribute.ID;
import com.focason.api.user.entity.AuthorityEntity;
import com.focason.api.user.repository.AuthorityRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.BatchResult;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.jdbc.SelectOptions;

/**
 * 権限マスタのDao
 *
 * @since 1.0.0
 */
@Dao
@ConfigAutowireable
public interface AuthorityDao
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
        AuthorityRepository.Condition condition,
        SelectOptions options,
        AuthorityRepository.Sort sort,
        Collector<AuthorityEntity, ?, R> collector);

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
        AuthorityRepository.Condition condition,
        SelectOptions options,
        Collector<AuthorityEntity, ?, R> collector) {
        return selectAll(condition, options, AuthorityRepository.Sort.DEFAULT, collector);
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
        AuthorityRepository.Sort sort,
        Collector<AuthorityEntity, ?, R> collector) {
        return selectAll(AuthorityRepository.Condition.DEFAULT, options, sort, collector);
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
        Collector<AuthorityEntity, ?, R> collector) {
        return selectAll(AuthorityRepository.Condition.DEFAULT, options, AuthorityRepository.Sort.DEFAULT, collector);
    }

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @param options SQL実行時オプション
     * @return エンティティが {@link Optional} で返されます。
     */
    @Select
    Optional<AuthorityEntity> selectById(ID<AuthorityEntity> id, SelectOptions options);

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。
     */
    default Optional<AuthorityEntity> selectById(ID<AuthorityEntity> id) {
        return selectById(id, SelectOptions.get());
    }

    /**
     * データベースにエンティティを挿入（新規作成）します。
     *
     * @param entity 挿入するエンティティ
     * @return エンティティ挿入結果が返されます。
     */
    @Insert(excludeNull = true)
    Result<AuthorityEntity> insert(AuthorityEntity entity);

    /**
     * データベースのエンティティを更新します。
     *
     * @param entity 更新するエンティティ
     * @return エンティティ更新結果が返されます。
     */
    @Update(excludeNull = true)
    Result<AuthorityEntity> update(AuthorityEntity entity);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Delete(sqlFile = true)
    int deleteById(ID<AuthorityEntity> id);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Update(sqlFile = true)
    int deleteLogicById(ID<AuthorityEntity> id);

    /**
     * @param entities エンティティリスト
     * @return エンティティ作成結果が返されます。
     */
    @BatchInsert
    BatchResult<AuthorityEntity> insert(List<AuthorityEntity> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ更新結果が返されます。
     */
    @BatchUpdate
    BatchResult<AuthorityEntity> update(List<AuthorityEntity> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ削除結果が返されます。
     */
    @BatchDelete
    BatchResult<AuthorityEntity> delete(List<AuthorityEntity> entities);
}
