package com.lemonico.product.dao;



import com.lemonico.entity.Pd001ProductEntity;
import com.lemonico.product.repository.ProductRepository;
import java.util.stream.Collector;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.SelectType;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

@Dao
@ConfigAutowireable
public interface NewProductDao
{

    // @Select(strategy = SelectType.COLLECT)
    @Select(strategy = SelectType.COLLECT)
    <R> R selectProducts(
        ProductRepository.Condition condition,
        SelectOptions options,
        ProductRepository.Sort sort,
        Collector<Pd001ProductEntity, ?, R> collector);
}
