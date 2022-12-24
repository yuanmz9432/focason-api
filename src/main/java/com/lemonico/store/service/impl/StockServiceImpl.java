package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.dao.StockDao;
import com.lemonico.store.service.StockService;
import com.lemonico.wms.dao.StocksResultDao;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @className: ProductServiceImpl
 * @description: Product ServiceImpl实现类
 * @date: 2020/05/11
 **/
@Service
public class StockServiceImpl implements StockService
{

    @Resource
    private StockDao stockDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ClientDao clientDao;
    @Resource
    private StocksResultDao stocksResultDao;

    /**
     * @Description: 在库一览
     * @Param:
     * @return: List
     * @Date: 2020/05/28
     */
    @Override
    public JSONObject getStockList(String client_id, String[] product_id, String search, String tags_id,
        Integer currentPage, Integer pageSize, Integer stock_search) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 0);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDay = DateUtils.getNowTime(dateFormat.format(calendar.getTime()) + " 00:00:00");
        Date nowTime = DateUtils.getNowTime(null);
        int type = 2;

        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        List<Mc100_product> stockList = stockDao.getStockList(client_id, product_id, search, tags_id, stock_search);
        PageInfo<Mc100_product> pageInfo;

        // 获取总件数
        long totalCnt = stockList.size();

        JSONObject resultJson = new JSONObject();
        if (totalCnt == 0L) {
            resultJson.put("result_data", stockList);
            resultJson.put("total", totalCnt);
            return resultJson;
        }

        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(stockList);
            totalCnt = pageInfo.getTotal();
        }

        List<String> productIds =
            stockList.stream().map(Mc100_product::getProduct_id).distinct().collect(Collectors.toList());
        List<Tw301_stock_history> stockHistoryList =
            stocksResultDao.getShipmentNumSum(client_id, productIds, firstDay, nowTime, type);

        for (Mc100_product product : stockList) {
            String productId = product.getProduct_id();
            // 获取在庫履歷的信息
            int quantity = 0;
            for (Tw301_stock_history shipmentNumSum : stockHistoryList) {
                if (!StringTools.isNullOrEmpty(shipmentNumSum) && productId.equals(shipmentNumSum.getProduct_id())) {
                    quantity += shipmentNumSum.getQuantity();
                }
            }
            product.setShipmentNum(quantity);

            // 获取商品图片
            List<Mc102_product_img> productImgs = productDao.getProductImg(client_id, productId);
            if (!StringTools.isNullOrEmpty(productImgs) && productImgs.size() > 0) {
                product.setMc102_product_imgList(productImgs);
            }
        }

        resultJson.put("result_data", stockList);
        resultJson.put("total", totalCnt);

        return resultJson;
    }

    /**
     * @Description: 在库补充设定
     * @Param:
     * @return: Integer
     * @Date: 2020/05/28
     */
    @Override
    public Integer updateReplenishCount(String client_id, String product_id, Integer replenish_cnt) {
        return stockDao.updateReplenishCount(client_id, product_id, replenish_cnt);
    }

    /**
     * @Description: 在库履历
     * @param: startTime ： 日期搜索 起始时间
     * @param: endTime ： 日期搜索 结束时间
     * @return: JSONObject
     * @Date: 2020/05/29
     */
    @Override
    public JSONObject getStockHistoryList(String client_id, String warehouse_cd, String proudct_id,
        String search, String tags_id, String startTime, String endTime,
        String typeFlg, Integer currentPage, Integer pageSize) {
        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        Integer type = null;
        if (!StringTools.isNullOrEmpty(typeFlg)) {
            type = Integer.valueOf(typeFlg);
        }
        Date start = DateUtils.stringToDate(startTime);
        // 検索日付を加工（23:59:59）にセットする @Add wang 2021/4/20
        Date dateEnd = CommonUtils.getDateEnd(endTime);

        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            PageHelper.startPage(currentPage, pageSize);
        }
        List<Tw301_stock_history> stockHistoryList =
            stockDao.getStockHistoryList(client_id, warehouse_cd, proudct_id, search, tags_id, start, dateEnd, type);
        PageInfo<Tw301_stock_history> pageInfo = null;

        // 获取总件数
        long totalCnt = stockHistoryList.size();
        if ((!StringTools.isNullOrEmpty(currentPage) && currentPage > 0)
            && (!StringTools.isNullOrEmpty(pageSize) && pageSize > 0)) {
            pageInfo = new PageInfo<>(stockHistoryList);
            totalCnt = pageInfo.getTotal();
        }

        JSONObject resultJson = new JSONObject();

        // 数据为空返回
        if (StringTools.isNullOrEmpty(stockHistoryList) || stockHistoryList.isEmpty()) {
            resultJson.put("result_data", stockHistoryList);
            resultJson.put("total", totalCnt);
            return resultJson;
        }

        // 找出所有数据店铺id
        List<String> clientIdList =
            stockHistoryList.stream().map(Tw301_stock_history::getClient_id).distinct().collect(Collectors.toList());

        Map<String, String> clientMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(clientIdList) && !clientIdList.isEmpty()) {
            // 獲取多個店鋪信息
            List<Ms201_client> clientInfoList = clientDao.getClientInfoList(clientIdList);
            clientMap = clientInfoList.stream()
                .collect(Collectors.toMap(Ms201_client::getClient_id, Ms201_client::getClient_nm));
        }

        // 设置店铺名称和商品图片
        for (Tw301_stock_history history : stockHistoryList) {
            history.setClient_name(clientMap.get(history.getClient_id()));

            List<Mc102_product_img> productImg =
                productDao.getProductImg(history.getClient_id(), history.getProduct_id());
            // String product_img = null;
            List<String> productImgSrcList = new ArrayList();
            if (!StringTools.isNullOrEmpty(productImg) && productImg.size() != 0) {
                // product_img = productImg.get(0).getProduct_img();
                productImg.forEach(img -> {
                    productImgSrcList.add(img.getProduct_img());
                });
            }
            history.setProduct_img(productImgSrcList);
        }

        resultJson.put("result_data", stockHistoryList);
        resultJson.put("total", totalCnt);
        return resultJson;
    }

    /**
     * @Description: 获取在库商品的tags和tags_id
     * @Param:
     * @return: List
     * @Date: 2020/05/29
     */
    @Override
    public List<Mc101_product_tag> getStockTags(String client_id) {
        return stockDao.getStockTags(client_id);
    }

}
