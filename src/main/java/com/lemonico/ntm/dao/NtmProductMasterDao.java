package com.lemonico.ntm.dao;



import com.lemonico.common.bean.Mc107_ntm_product_master;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: NtmProductMasterDao
 * @description: NtmProductMaster dao
 * @date: 2021/6/17
 **/
@Mapper
public interface NtmProductMasterDao
{

    /**
     * @description 根据用户名获取商品master数据
     * @param nameList 商品名
     * @param date 日期
     * @return: List
     * @date 2021/7/15
     **/
    List<Mc107_ntm_product_master> getNtmProductMasterListByName(@Param("nameList") List<String> nameList,
        @Param("date") String date);

}
