/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.dao;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.RoleEntity;
import api.lemonico.repository.RoleRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.BatchResult;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.jdbc.SelectOptions;

/**
 * ロールマスタのDao
 *
 * @since 1.0.0
 */
@Dao
@ConfigAutowireable
public interface RoleDao
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
        RoleRepository.Condition condition,
        SelectOptions options,
        RoleRepository.Sort sort,
        Collector<RoleEntity, ?, R> collector);

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
        RoleRepository.Condition condition,
        SelectOptions options,
        Collector<RoleEntity, ?, R> collector) {
        return selectAll(condition, options, RoleRepository.Sort.DEFAULT, collector);
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
        RoleRepository.Sort sort,
        Collector<RoleEntity, ?, R> collector) {
        return selectAll(RoleRepository.Condition.DEFAULT, options, sort, collector);
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
        Collector<RoleEntity, ?, R> collector) {
        return selectAll(RoleRepository.Condition.DEFAULT, options, RoleRepository.Sort.DEFAULT, collector);
    }

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @param options SQL実行時オプション
     * @return エンティティが {@link Optional} で返されます。
     */
    @Select
    Optional<RoleEntity> selectById(ID<RoleEntity> id, SelectOptions options);

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。
     */
    default Optional<RoleEntity> selectById(ID<RoleEntity> id) {
        return selectById(id, SelectOptions.get());
    }

    /**
     * データベースにエンティティを挿入（新規作成）します。
     *
     * @param entity 挿入するエンティティ
     * @return エンティティ挿入結果が返されます。
     */
    @Insert(excludeNull = true)
    Result<RoleEntity> insert(RoleEntity entity);

    /**
     * データベースのエンティティを更新します。
     *
     * @param entity 更新するエンティティ
     * @return エンティティ更新結果が返されます。
     */
    @Update(excludeNull = true)
    Result<RoleEntity> update(RoleEntity entity);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Delete(sqlFile = true)
    int deleteById(ID<RoleEntity> id);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Update(sqlFile = true)
    int deleteLogicById(ID<RoleEntity> id);

    /**
     * @param entities エンティティリスト
     * @return エンティティ作成結果が返されます。
     */
    @BatchInsert
    BatchResult<RoleEntity> insert(List<RoleEntity> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ更新結果が返されます。
     */
    @BatchUpdate
    BatchResult<RoleEntity> update(List<RoleEntity> entities);

    /**
     * @param entities エンティティリスト
     * @return エンティティ削除結果が返されます。
     */
    @BatchDelete
    BatchResult<RoleEntity> delete(List<RoleEntity> entities);
}
