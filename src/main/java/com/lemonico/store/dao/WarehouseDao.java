package com.lemonico.store.dao;



import com.lemonico.common.bean.Mw400_warehouse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: Mw400WarehouseDao
 * @description: 倉庫に対するマスタのデータを操作する
 * @date: 2020/05/29 13:10
 **/
@Mapper
public interface WarehouseDao
{

    /**
     * @Param: warehouse_cd : 倉庫Id
     * @description: 倉庫情報を調べる
     * @return: com.lemonico.common.bean.Mw400_warehouse
     * @date: 2020/05/29
     */
    Mw400_warehouse getInfoByWarehouseCd(@Param("warehouse_cd") String warehouse_cd);

    /**
     * @Description:
     * @Param:
     * @return:
     * @Date: 2020/10/28
     */
    String getWarehouseName(@Param("client_id") String client_id);
}
