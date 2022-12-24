package com.lemonico.wms.dao;



import com.lemonico.common.bean.Mc100_product;
import com.lemonico.common.bean.Mc102_product_img;
import com.lemonico.common.bean.Ms010_product_size;
import com.lemonico.common.bean.Mw405_product_location;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ProductResultDao
 * @description: 仓库侧商品处理
 * @date: 2020/06/19
 **/
@Mapper
public interface ProductResultDao
{

    /**
     * @Param:
     * @description: 设定仓库侧商品size
     * @return:
     * @date: 2020/06/24
     */
    public Integer setProductSize(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("weight") Double weight,
        @Param("size_cd") String size_cd);

    /**
     * @Param:
     * @description: 获取商品货架信息
     * @return:
     * @date: 2020/06/25
     */
    public List<Mw405_product_location> getProductLocation(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Param:
     * @description: 移动商品所在货架
     * @return:
     * @date: 2020/06/25
     */
    public Integer moveProductLocation(@Param("client_id") String client_id, @Param("product_id") String product_id,
        @Param("location_id") String location_id, @Param("stock_cnt") Integer stock_cnt);

    String getSizeIdByName(String sizeName);

    /**
     * @param product_id : 商品Id
     * @param productTotal : 实际入库数
     * @param notDelivery : 不可配送数
     * @param locationId : 货架Id
     * @param client_id : 店铺Id
     * @param date : 当前时间
     * @param loginNm : 操作者
     * @description: 将商品放到货架
     * @return: java.lang.Integer
     * @date: 2021/10/20 13:11
     */
    Integer insertProductLocation(@Param("product_id") String product_id, @Param("productTotal") Integer productTotal,
        @Param("not_delivery") int notDelivery, @Param("location_id") String locationId,
        @Param("status") int status, @Param("bestbefore_date") Date bestbefore_date,
        @Param("client_id") String client_id, @Param("date") Date date,
        @Param("loginNm") String loginNm);

    /**
     * @description: 获取locationId
     * @return: java.lang.String
     * @date: 2020/06/29
     */
    String getLocationId(@Param("warehouse_cd") String warehouse_cd,
        @Param("wh_location_nm") String wh_location_nm,
        @Param("lot_no") String lot_no);

    /**
     * @Param: locationId
     * @param: productId
     * @param: client_id
     * @description: 查询该商品有没有在该货架上面
     * @return: com.lemonico.common.bean.Mw405_product_location
     * @date: 2020/06/30
     */
    Mw405_product_location getProductLocationInfo(@Param("locationId") String locationId,
        @Param("client_id") String client_id,
        @Param("productId") String productId);

    /**
     * @Param:
     * @description: 货架移动新规数据
     * @return:
     * @date: 2020/07/08
     */
    public Integer createLocationProduct(@Param("client_id") String client_id, @Param("product_id") String product_id,
        @Param("location_id") String location_id, @Param("stock_cnt") Integer stock_cnt);

    /**
     * @param locationId : 货架Id
     * @param product_id : 商品Id
     * @param client_id : 店铺Id
     * @param stock_cnt : 在库数
     * @param not_delivery : 不可配送数
     * @param loginNm : 操作者
     * @param date : 当前日期
     * @description: 根据Id修改该货架的引当数
     * @return: java.lang.Integer
     * @date: 2021/10/20 13:23
     */
    Integer updateReserveCntById(@Param("location_id") String locationId, @Param("product_id") String product_id,
        @Param("client_id") String client_id, @Param("stock_cnt") Integer stock_cnt,
        @Param("not_delivery") int not_delivery, @Param("loginNm") String loginNm, @Param("date") Date date);

    /**
     * @Param: client_id : 店舗ID
     * @param: warehouse_cd : 倉庫コード
     * @param: product_id : 商品ID
     * @param: detailStatus : ステータス
     * @description: 获取引当数大于在库数的货架Id
     * @return: java.util.List<com.lemonico.common.bean.Tw212_shipment_location_detail>
     * @date: 2020/8/4
     */
    List<Mw405_product_location> getLocationExceptionInfo(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("location_id") String locationId);

    /**
     * @Param: product_id
     * @param: weight
     * @param: size_cd
     * @Param: client_id
     * @description: 更改商品表的重量和s ize
     * @return: java.lang.Integer
     * @date: 2020/8/22
     */
    Integer updateWeightSizeByProductId(@Param("product_id") String product_id,
        @Param("weight") Double weight,
        @Param("size_cd") String size_cd,
        @Param("client_id") String client_id);

    /**
     * @description: 获取所有sizeName
     * @return: java.util.List<java.lang.String>
     * @date: 2020/11/23
     */
    List<Ms010_product_size> getSizeNameList();

    /**
     * @Param: client_id : 店铺Id
     * @param: warehouse_cd ： 仓库Id
     * @description: 仓库侧商品一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/24
     */
    List<Mc100_product> getItemList(String client_id, String warehouse_cd);

    /**
     * @param: client_id
     * @param: product_id
     * @description: 获取商品图片路径
     * @return:
     * @date: 2021/4/1
     */
    List<Mc102_product_img> getImagePath(@Param("client_id") String client_id,
        @Param("product_id") String product_id);
}
