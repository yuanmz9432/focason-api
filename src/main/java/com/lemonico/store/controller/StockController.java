package com.lemonico.store.controller;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc101_product_tag;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.store.service.StockService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在庫管理コントローラー
 *
 * @since 1.0.0
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Api(tags = "在庫管理")
public class StockController
{

    private final StockService stockService;

    /**
     * @Description: 在庫一覧
     * @Param:
     * @return: JSONObject
     * @Date: 2020/05/28
     */
    @ApiOperation(value = "在庫一覧", notes = "在庫一覧")
    @GetMapping(value = "/store/stock/list/{client_id}")
    public JSONObject getStockList(@PathVariable("client_id") String client_id, String[] product_id, String search,
        String tags_id, Integer currentPage, Integer pageSize, Integer stock_search) {

        JSONObject list =
            stockService.getStockList(client_id, product_id, search, tags_id, currentPage, pageSize, stock_search);
        return CommonUtils.success(list);
    }

    /**
     * @Description: 在庫補充設定
     * @Param:
     * @return: JSONObject
     * @Date: 2020/05/28
     */
    @ApiOperation(value = "在庫補充設定", notes = "在庫補充設定")
    @PutMapping(value = "/store/stock/replenish/{client_id}")
    public JSONObject updateReplenishCount(@PathVariable("client_id") String client_id, String product_id,
        Integer replenish_cnt) {
        stockService.updateReplenishCount(client_id, product_id, replenish_cnt);
        return CommonUtils.success();
    }

    /**
     * @Description: 在庫履歴
     * @Param:
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: JSONObject
     * @Date: 2020/05/29
     */
    @ApiOperation(value = "在庫履歴", notes = "在庫履歴")
    @GetMapping(value = "/stock/history/{client_id}")
    public JSONObject getStockHistoryList(@PathVariable("client_id") String client_id, String warehouse_cd,
        String product_id, String search,
        String tags_id, String startTime, String endTime, String type, Integer currentPage, Integer pageSize) {
        JSONObject historyJson = stockService.getStockHistoryList(client_id, warehouse_cd, product_id,
            search, tags_id, startTime, endTime, type, currentPage, pageSize);

        return CommonUtils.success(historyJson);
    }

    /**
     * @Description:在庫商品tagsとtags_idを取得する
     * @Param:
     * @return:
     * @Date: 2020/6/5
     */
    @ApiOperation(value = "在庫tagsを取得する", notes = "在庫tagsを取得する")
    @GetMapping(value = "/stock/tag/{client_id}")
    public JSONObject updateProductNote(@PathVariable("client_id") String client_id) {
        List<Mc101_product_tag> list = stockService.getStockTags(client_id);
        return CommonUtils.success(list);
    }

}
