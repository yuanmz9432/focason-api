package com.lemonico.wms.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mw405_product_location;
import com.lemonico.common.bean.Tw302_stock_management;
import com.lemonico.common.bean.Tw303_stock_detail;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: StocksResultService
 * @description: 在库service层
 * @date: 2020/07/06
 **/
public interface StocksResultService
{

    /**
     * @param warehouse_cd : 仓库Id
     * @param page : 页数
     * @param size : 条数
     * @param column : 排序的字段
     * @param sort : 排序的方式
     * @param location_id : 货架Id
     * @param search : 搜索关键字
     * @description: 获取货架location信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/22 13:25
     */
    public JSONObject getLocationList(String warehouse_cd, int page, int size, String column, String sort,
        String location_id, String search);

    /**
     * @Param:
     * @description: 货架info设定
     * @return: Integer
     * @date: 2020/07/07
     */
    public Integer setLocationInfo(String warehouse_cd, String location_id, String info);

    /**
     * @Param:
     * @description: 获取单个货架中的商品信息
     * @return: JSONObject
     * @date: 2020/07/07
     */
    public JSONObject getLocationProduct(String warehouse_cd, String location_id, int page,
        int size, String column, String sort, String client_id);

    /**
     * @Param:
     * @description: 获取商品的货架信息
     * @return: List
     * @date: 2020/07/17
     */
    public List<Mw405_product_location> getLocationInfo(String client_id, String product_id);

    /**
     * @Param:
     * @description: 盘点开始时新规登录盘点表及明细表
     * @return: Integer
     * @date: 2020/07/10
     */
    public Integer createProductCheckAll(JSONObject jsonObject);

    /**
     * @Param:
     * @description: 获取盘点明细表数据(tw303_stock_detail)
     * @return: List
     * @date: 2020/07/10
     */
    public List<Tw303_stock_detail> getStockDetail(String client_id, Integer manage_id);

    /**
     * @Param:
     * @description: 更新盘点在库实际数(tw303_stock_detail)
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer updateStockDetailCount(String product_id, Integer count);

    /**
     * @Param:
     * @description: 变更盘点状态
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer changeStockCheckState(Integer manage_id, Integer state);

    /**
     * @Param:
     * @description: 获取盘点管理信息
     * @return: List
     * @date: 2020/07/13
     */
    public List<Tw302_stock_management> getStockCheckManageInfo(Integer state);

    /**
     * @Param:
     * @description: 盘点结束后更新理论在库数和実在庫数
     * @return: Integer
     * @date: 2020/07/14
     */
    public Integer updateStockCount(String client_id, String product_id, Integer inventory_cnt);

    /**
     * @Param:
     * @description: 检测是否有盘点未完成
     * @return: Integer
     * @date: 2020/07/13
     */
    public boolean stockCheckExist(String client_id);

    /**
     * @Param:
     * @description: 统计作业中数
     * @return: Integer
     * @date: 2020/07/13
     */
    public Integer getCheckingCount(String warehouse_cd);

    /**
     * @Param:
     * @description: 新规货架
     * @return: JSONObject
     * @date: 2020/07/15
     */
    public JSONObject createNewLocation(JSONObject jsonObject);

    /**
     * @Param: jsonObject : 在库明细表的所有字段
     * @description: 生成在库商品明细PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    JSONObject getStockProductDetail(JSONObject jsonObject);

    /**
     * @Param: jsonObject : locationName: 货架名称, name： 商品名称, code: 商品code，
     *         product_id: 商品Id，stock_cnt： 在库数
     * @description: 生成在库货架上商品明细PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/15
     */
    JSONObject getStockLocationProduct(JSONObject jsonObject);

    /**
     * @Param: jsonObject : stockInfo {product_id, (stock_cnt), wh_location_nm,
     *         client_id}
     * @description: 生成在该货架是否有该商品的PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/16
     */
    JSONObject getStockProductCount(JSONObject jsonObject);

    /**
     * @Param:
     * @description: 检验货架名是否重复
     * @return: String
     * @date: 2020/07/17
     */
    public boolean checkLocationNameExists(String wh_location_nm, String lot_no, String warehouse_cd);

    /**
     * @Param:
     * @description: 检验仓库货架优先顺序是否重复
     * @return: String
     * @date: 2020/08/18
     */
    public boolean checkLocationPriorityExists(Integer priority, String warehouse_cd);

    /**
     * @Param:
     * @description: 货架信息修改
     * @return: Integer
     * @date: 2020/07/22
     */
    public JSONObject updateLocationInfo(String warehouse_cd, JSONObject jsonObject, HttpServletRequest request);


    /**
     * @Param:
     * @description: 货架信息修改
     * @return: Integer
     * @date: 2020/07/22
     */
    public JSONObject localCsvUpload(HttpServletRequest req, String warehouse_cd, MultipartFile file);

    /**
     * @param warehouse_cd : 仓库Id
     * @param page : 页码
     * @param size : 件数
     * @param column : 需要排序的 字段名称
     * @param sort : 排序的方式
     * @param search : 搜索内容
     * @description: 获取在库一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/25 9:39
     */
    JSONObject getStockList(String warehouse_cd, int page, int size, String column, String sort, String search,
        String stock_flg, String client_id);

    /**
     * @param jsonObject
     * @description: 在库数调整
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/26 9:39
     */
    JSONObject changeStockCnt(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @param jsonObject
     * @param request
     * @description: 商品货架移动
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/26 18:09
     */
    JSONObject locationMove(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @param warehouse_cd : 仓库Id
     * @param page : 页码
     * @param size : 件数
     * @param column : 需要排序的 字段名称
     * @param sort : 排序的方式
     * @param client_id : 店铺Id
     * @param stock_flg : 是否在库
     * @description: 获取商品在库一览
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/8/31 13:35
     */
    JSONObject getProductStockList(String warehouse_cd, int page, int size, String column, String sort, int stock_flg,
        String client_id, String search);

    /**
     * @param warehouse_cd : 仓库Id
     * @param product_id : 商品Id
     * @param client_id : 店铺Id
     * @description: 获取商品对应的货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/1 13:05
     */
    JSONObject getProductLocationInfo(String warehouse_cd, String product_id, String client_id);

    /**
     * @param warehouse_cd : 仓库Id
     * @param locationNm : 货架名称
     * @description: 根据或货架名称获取货架信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/10 1:17
     */
    JSONObject getLocationInfoByName(String warehouse_cd, String locationNm);

    /**
     * @param jsonObject : client_id（店铺Id） location_id（货架Id） product_id（商品Id） bestbefore_date（过期时间） status（出荷不可）
     * @param request : 请求
     * @description: 修改货架明细的 过期时间 和 出荷不可
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/10/22 13:57
     */
    JSONObject updateLocationDetail(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @param warehouse_cd : 仓库Id
     * @param client_id : 店铺Id
     * @param stock_flg : 在庫数flg
     * @param search : 检索条件
     * @param column : 需要排序的字段名称
     * @param sort : 排序的方式
     * @description: 在库一览csv下载获取数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/11/22 16:28
     */
    JSONObject getStockListCsvData(String warehouse_cd, String client_id, int stock_flg, String search, String column,
        String sort);
}
