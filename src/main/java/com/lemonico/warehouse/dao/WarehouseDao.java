package com.lemonico.warehouse.dao;



import com.lemonico.core.attribute.ID;
import com.lemonico.entity.Mg002WarehouseEntity;
import com.lemonico.warehouse.repository.WarehouseRepository;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.seasar.doma.boot.ConfigAutowireable;
import org.seasar.doma.jdbc.SelectOptions;

/**
 * 倉庫情報のDao
 *
 * @since 1.0.0
 */
@Dao
@ConfigAutowireable
public interface WarehouseDao
{
    @Select
    List<Mg002WarehouseEntity> selectAll(WarehouseRepository.Condition condition, SelectOptions options,
        WarehouseRepository.Sort sort, Collector<Object, ?, List<Object>> toList);

    @Select
    Optional<Mg002WarehouseEntity> selectById(ID<Mg002WarehouseEntity> id);
}
