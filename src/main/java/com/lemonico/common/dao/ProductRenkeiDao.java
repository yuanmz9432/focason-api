package com.lemonico.common.dao;



import com.lemonico.common.bean.Mc106_produce_renkei;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ProduceRenkei
 * @description: 他店舗商品ID管理dao
 * @date: 2020/12/22 16:05
 **/
@Mapper
public interface ProductRenkeiDao
{

    /**
     * @param productId : サンロジ商品ID
     * @param client_id : 店舗ID
     * @param tc203Id ： 識別番号
     * @param renkei_product_id ： 他店舗商品ID
     * @param nowTime ： 时间
     * @description: 他店舗商品ID管理 存新数据
     * @return: java.lang.Integer
     * @date: 2020/12/22 16:11
     */
    Integer insertData(@Param("product_id") String productId,
        @Param("client_id") String client_id,
        @Param("api_id") Integer tc203Id,
        @Param("renkei_product_id") String renkei_product_id,
        @Param("inventory_type") Integer inventory_type,
        @Param("ins_date") Date nowTime);

    /**
     * @param product_id : サンロジ商品ID
     * @param api_id ： 識別番号
     * @param client_id ： 店舗ID
     * @description: 根据商品Id， 店铺识别code， 店铺Id 获取他店舗商品ID管理表
     * @return: com.lemonico.common.bean.Mc106_produce_renkei
     * @date: 2020/12/22 17:00
     */
    Mc106_produce_renkei getDataById(@Param("product_id") String product_id,
        @Param("api_id") String api_id,
        @Param("client_id") String client_id);

    /**
     * @param idList : api_id 集合
     * @description: 根据api_id 获取到mc106的集合
     * @return: java.util.List<com.lemonico.common.bean.Mc106_produce_renkei>
     * @date: 2021/1/12 14:36
     */
    List<Mc106_produce_renkei> getDataByApiId(@Param("list") List<Integer> idList);


    List<Mc106_produce_renkei> getByApiId(@Param("apiId") Integer apiId);

    /**
     * @param : 樂天商品管理code
     * @description: 根据管理code 修改mc106表中对应的InventoryType
     * @return:
     * @date: 2021/1/12
     */
    Integer updateInventoryType(@Param("itemUrl") Integer itemUrl);

    /**
     * 連携商品マスタ取得
     * 
     * @param client_id 店舗ID
     * @param api_id API番号
     * @return List<Mc106_produce_renkei> 連携商品マスタリスト
     */
    List<Mc106_produce_renkei> getDataByClientIdAndApiId(@Param("client_id") String client_id,
        @Param("api_id") Integer api_id);

    /**
     * @param renkei_product_id : EC店舗商品ID
     * @param variant_id ： 外部連携ID
     * @param client_id ： 店舗ID
     * @description: 根据商品Id， 店铺识别code， 店铺Id 获取他店舗商品ID管理表
     * @return: com.lemonico.common.bean.Mc106_produce_renkei
     * @author: HZM
     * @date: 2021/6/2 17:00
     */
    Mc106_produce_renkei getDataByRenkeiId(@Param("renkei_product_id") String renkei_product_id,
        @Param("variant_id") String variant_id,
        @Param("client_id") String client_id);
}
