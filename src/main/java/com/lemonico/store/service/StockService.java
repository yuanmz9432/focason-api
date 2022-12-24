package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc101_product_tag;
import java.util.List;

/**
 * @className: StockService
 * @description: StockService
 * @date: 2020/05/28
 **/
public interface StockService
{

    /**
     * @Description: 在庫一覧
     * @Param:
     * @return: List
     * @Date: 2020/05/28
     */
    JSONObject getStockList(String client_id, String[] product_id, String search, String tags_id,
        Integer currentPage, Integer pageSize, Integer stock_search);

    /**
     * @Description: 在庫補充設定
     * @Param:
     * @return: Integer
     * @Date: 2020/05/28
     */
    Integer updateReplenishCount(String client_id, String product_id, Integer replenish_cnt);

    /**
     * @Description: 在庫履歴
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: List
     * @Date: 2020/05/29
     */
    JSONObject getStockHistoryList(String client_id, String warehouse_cd,
        String proudct_id, String search, String tags_id,
        String startTime, String endTime, String type,
        Integer currentPage, Integer pageSize);

    /**
     * @Description: 在庫商品のtagsとtags_idを取得する
     * @Param:
     * @return: List
     * @Date: 2020/05/29
     */
    List<Mc101_product_tag> getStockTags(String client_id);
}
