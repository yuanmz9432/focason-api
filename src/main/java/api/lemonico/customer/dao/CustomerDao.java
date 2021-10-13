/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.customer.dao;



import api.lemonico.core.attribute.ID;
import api.lemonico.customer.entity.Customer;
import api.lemonico.customer.repository.CustomerRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import org.seasar.doma.*;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.BatchResult;
import org.seasar.doma.jdbc.Result;
import org.seasar.doma.jdbc.SelectOptions;

/**
 * customerのDao
 *
 * @since 1.0.0
 */
@Dao
@ConfigAutowireable
public interface CustomerDao
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
        CustomerRepository.Condition condition,
        SelectOptions options,
        CustomerRepository.Sort sort,
        Collector<Customer, ?, R> collector);

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
        CustomerRepository.Condition condition,
        SelectOptions options,
        Collector<Customer, ?, R> collector) {
        return selectAll(condition, options, CustomerRepository.Sort.DEFAULT, collector);
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
        CustomerRepository.Sort sort,
        Collector<Customer, ?, R> collector) {
        return selectAll(CustomerRepository.Condition.DEFAULT, options, sort, collector);
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
        Collector<Customer, ?, R> collector) {
        return selectAll(CustomerRepository.Condition.DEFAULT, options, CustomerRepository.Sort.DEFAULT, collector);
    }

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @param options SQL実行時オプション
     * @return エンティティが {@link Optional} で返されます。
     */
    @Select
    Optional<Customer> selectById(ID<Customer> id, SelectOptions options);

    /**
     * エンティティIDを指定して、データベースからエンティティを一件を取得します。
     *
     * @param id エンティティID
     * @return エンティティが {@link Optional} で返されます。
     */
    default Optional<Customer> selectById(ID<Customer> id) {
        return selectById(id, SelectOptions.get());
    }

    /**
     * データベースにエンティティを挿入（新規作成）します。
     *
     * @param entity 挿入するエンティティ
     * @return エンティティ挿入結果が返されます。
     */
    @Insert(excludeNull = true)
    Result<Customer> insert(Customer entity);

    /**
     * データベースのエンティティを更新します。
     *
     * @param entity 更新するエンティティ
     * @return エンティティ更新結果が返されます。
     */
    @Update(excludeNull = true)
    Result<Customer> update(Customer entity);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Delete(sqlFile = true)
    int deleteById(ID<Customer> id);

    /**
     * エンティティIDを指定して、データベースからエンティティを削除します。
     *
     * @param id エンティティID
     * @return エンティティ削除件数が返されます。
     */
    @Update(sqlFile = true)
    int deleteLogicById(ID<Customer> id);

    /**
     * @param entities the Customer
     * @return affected rows
     */
    @BatchInsert
    BatchResult<Customer> insert(List<Customer> entities);

    /**
     * @param entities the Customer
     * @return affected rows
     */
    @BatchUpdate
    BatchResult<Customer> update(List<Customer> entities);

    /**
     * @param entities the Customer
     * @return affected rows
     */
    @BatchDelete
    BatchResult<Customer> delete(List<Customer> entities);
}
