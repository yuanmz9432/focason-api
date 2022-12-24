package com.lemonico.wms.service;



import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;

/**
 * @className: WarehousingResultService
 * @description: 仓库侧入库处理
 * @date: 2020/06/17 14:00
 **/
public interface WarehousingResultService
{

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID
     * @description: 根据入库依赖Id查询未入库检品的商品
     * @return: JSONObject
     * @date: 2021/07/05
     */
    JSONObject getWarehouseInfoUncheck(JSONObject jsonObject);

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID
     * @description: 根据入库依赖Id查询商品
     * @return: JSONObject
     * @date: 2020/06/17
     */
    JSONObject getWarehouseInfoById(JSONObject jsonObject);

    /**
     * @Param: jsonObject: client_id : 店舗ID, id : 入庫依頼ID, status : 入庫ステータス
     * @description: 根据入库依赖修改入库状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/17
     */
    JSONObject updateStatusById(JSONObject jsonObject, HttpServletRequest servletRequest);

    /**
     * @param: jsonObject: product_id,name,code,barcode,quantity
     * @description: 仓库侧入库商品PDF生成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/24
     */
    JSONObject createWarehouseInfoPDF(JSONObject jsonObject);

    /**
     * @param: jsonObject : client_id 店舗ID, id 入庫依頼ID, items 几乎商品表的所有数据
     * @description: 添加数据到在库管理表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/03
     */
    JSONObject insertStock(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 查出该仓库所有的货架
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    JSONObject getLocationNameByWarehouseId(String warehouse_cd, String searchName, String location_id);

    /**
     * @Param: warehouse_cd : 倉庫コード
     * @description: 检查货架是否存在
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/06/15
     */
    JSONObject locationNameCheck(String warehouse_cd, String name);

    /**
     * @Param: id : 入库依赖Id, client_id: 店铺Id, warehouse_id： 仓库Id
     * @description: 根据入库依赖Id查询商品异常的货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/14
     */
    JSONObject getLocationException(JSONObject jsonObject);

    /**
     * @param warehouse_id : 仓库Id
     * @param client_id ： 店铺Id
     * @description: 入庫ステータス件数
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/30 15:53
     */
    JSONObject getWarehousingStatusCount(String warehouse_id, String client_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param product_cnt : 入库数
     * @description: 判断包含该商品出库依赖明细的状态是否可以修改
     * @return: void
     * @date: 2021/6/26 14:06
     */
    Boolean judgeReserveStatus(String warehouse_cd, String client_id, String product_id, int product_cnt,
        HttpServletRequest request);

    /**
     * @param jsonObject : client_id,warehouse_cd,code,lot_not,locationName,bestbefore_date,number,info
     * @param request : 请求
     * @description: 仓库侧商品新规入库
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/28 16:01
     */
    JSONObject directWarehouse(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @param jsonObject : warehouse_cd: 仓库ID client_id: 店铺ID id: 入库依赖ID items: 入库商品明细 warehousing_date: 入库时间
     * @param request : 请求
     * @description: 添加部分选择的入库明细到在库
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/7/28 13:12
     */
    JSONObject insertSectionStock(JSONObject jsonObject, HttpServletRequest request);
}
