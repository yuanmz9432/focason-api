package com.lemonico.ntm.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.CommonFunctionDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.ntm.dao.ReportDao;
import com.lemonico.ntm.dao.StockDayDao;
import com.lemonico.ntm.service.StockDayService;
import com.lemonico.store.dao.OrderApiDao;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.dao.StockDao;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @className: StockDayServiceImpl
 * @description: NTM调用接口实现类
 * @date: 2021/3/9 16:23
 **/
@Service
public class StockDayServiceImpl implements StockDayService
{

    private final static Logger logger = LoggerFactory.getLogger(StockDayServiceImpl.class);

    @Resource
    private StockDayDao stockDayDao;

    @Resource
    private ProductDao productDao;

    @Resource
    private StockDao stockDao;

    @Resource
    private CommonFunctionDao commonFunctionDao;

    @Resource
    private ReportDao reportDao;

    @Resource
    private OrderApiDao orderApiDao;

    @Autowired
    private DeliveryDao deliveryDao;

    /**
     * @description: 获取月次在库表的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/10 14:36
     */
    @Override
    public JSONObject getCsvData(String type, HttpServletRequest request) {
        // 获取当前年份和月份
        String nowMonth = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDateTime.now());
        // 获取到现在的日期 用来判断是上半月还是下半月
        int day = LocalDate.now().getDayOfMonth();
        // 默认为下半个月 前月度 应为本月15号到上个月16号
        // 上半个月 获取上个月的时间
        String lastTime = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.now().minusMonths(1));
        // 本次查询 开始日期 (包含上个月最后一天)
        String startDate = lastTime + "16";
        // 当前月的起始日期
        String endDate = nowMonth + "15";
        // 本次查询 结束日期
        String maxDate = nowMonth + day;

        String clientId = CommonUtils.getToken("client_id", request);
        String warehouseId = commonFunctionDao.getWarehouseIdByClientId(clientId);
        // 将来
        // if ("1".equals(type)) {
        // String nextMonth = DateTimeFormatter.ofPattern("yyyyMM").format(LocalDate.now().plusMonths(1));
        // //当月16~下月15
        // startDate = nowMonth + "16";
        // endDate = nextMonth + "15";
        // }
        logger.info("开始时间:{},结束时间:{}", startDate, endDate);
        List<Tw304_stock_day> allData = stockDayDao.getAllData(startDate, endDate, warehouseId, clientId);
        if (allData.size() == 0) {
            return CommonUtils.success();
        }
        // 根据在庫処理日进行分组 key：在庫処理日 value：処理日当天的所有数据
        Map<String, List<Tw304_stock_day>> dataMap =
            allData.stream().collect(Collectors.groupingBy(Tw304_stock_day::getStock_date));
        // 获取前月度最后一天的所有数据
        List<Tw304_stock_day> lastMonthData = dataMap.get(startDate);
        Map<String, Integer> lastMap = new HashMap<>();
        if (lastMonthData != null && lastMonthData.size() != 0) {
            // 根据商品Id 进行分组， key：商品Id value：在库数
            lastMap = lastMonthData.stream()
                .collect(Collectors.toMap(Tw304_stock_day::getProduct_id, Tw304_stock_day::getInventory_cnt));
        }
        // 获取到前一天的时期 用来当作key 判断dataMap中其中是否含有（防止空指针） 如果没有 取最近一天的在库数据
        String atLastKey = nowMonth + LocalDate.now().minusDays(1).getDayOfMonth();
        boolean keyBool = dataMap.containsKey(atLastKey);
        if (!keyBool) {
            // 获取map中的最大key值 当作最近一天
            Object[] keyArray = dataMap.keySet().toArray();
            Arrays.sort(keyArray);
            if (keyArray.length == 1) {
                atLastKey = keyArray[0].toString();
            } else {
                atLastKey = keyArray[keyArray.length - 1].toString();
            }
        }
        // 获取当月度的所有数据
        List<Tw304_stock_day> nowMonthData = dataMap.get(atLastKey);
        // dataMap.get();
        // 当月度的数据根据商品Id 进行分组， key：商品Id value：在库数
        Map<String, Integer> nowMap = nowMonthData.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
            .collect(Collectors.toMap(Tw304_stock_day::getProduct_id, Tw304_stock_day::getInventory_cnt));
        // 提取出所有的商品Id并去重
        List<String> productIdList =
            nowMonthData.stream().map(Tw304_stock_day::getProduct_id).distinct().collect(Collectors.toList());
        // 获取到商品信息 根据商品Id 进行分组 key: 商品Id value: 商品对象
        List<Mc100_product> productList = productDao.getProductListById(productIdList, warehouseId, clientId);
        Map<String, Mc100_product> productMap =
            productList.stream().collect(Collectors.toMap(Mc100_product::getProduct_id, o -> o));
        // 获取到当前月的出库数和入库数
        List<Tw301_stock_history> historyList = stockDao.getHistoryList(clientId, startDate, endDate);
        // 根据商品Id进行分组
        Map<String, List<Tw301_stock_history>> historyMap = historyList.stream()
            .collect(Collectors.groupingBy(Tw301_stock_history::getProduct_id));
        // 查询到所有商品的出库信息
        int shipment_type = 2;
        List<Tw301_stock_history> shipmentHistory = stockDao.getShipmentHistory(clientId, productIdList, shipment_type);
        Map<String, List<Tw301_stock_history>> shipmentDateMap = shipmentHistory.stream()
            .collect(Collectors.groupingBy(Tw301_stock_history::getProduct_id));

        JSONArray jsonArray = new JSONArray();
        // 拼接生成excel所需要的数据
        Map<String, Integer> finalLastMap = lastMap;
        productIdList.forEach(productId -> {
            JSONObject jsonObject = new JSONObject();
            // 获取到该商品本月的出库及入库信息
            List<Tw301_stock_history> stockHistories = historyMap.get(productId);
            // 总入库数
            int warehouseNum = 0;
            // 总出库数
            int shipmentSum = 0;
            if (!StringTools.isNullOrEmpty(stockHistories)) {
                for (int i = 1; i < 3; i++) {
                    int finalI = i;
                    // finalI 1: 入库 2: 出库
                    int sum = stockHistories.stream().filter(x -> x.getType() == finalI)
                        .mapToInt(Tw301_stock_history::getQuantity).sum();
                    if (i == 1) {
                        warehouseNum = sum;
                    } else {
                        shipmentSum = sum;
                    }
                }
            }
            List<Tw301_stock_history> shipmentHistories = shipmentDateMap.get(productId);
            // 最后出库日
            String lastShipmentDate = "";
            if (shipmentHistories != null && shipmentHistories.size() != 0) {
                // 取最后一次出库的日期
                Date ins_date = shipmentHistories.stream()
                    .max(Comparator.comparing(Tw301_stock_history::getIns_date)).get().getIns_date();
                if (!StringTools.isNullOrEmpty(ins_date)) {
                    lastShipmentDate = DateTimeFormatter.ofPattern("yyyy/MM/dd")
                        .format(LocalDateTime.ofInstant(ins_date.toInstant(), ZoneId.systemDefault()));
                }
            }
            // 获取到商品信息
            Mc100_product product = productMap.get(productId);
            if (product == null) {
                return;
            }
            // 商品Id
            jsonObject.put("productId", productId);
            // 品名コード
            jsonObject.put("code", product.getCode());
            // 品名
            jsonObject.put("name", product.getName());
            // 簿価
            jsonObject.put("ntm_price", product.getNtm_price());
            // 振替単価
            jsonObject.put("ntm_sale", product.getPrice());
            // 前月度在庫数
            jsonObject.put("lastStockNum", finalLastMap.get(productId));
            // 当月度入庫数(翌15日的 当月入库数和当月出库数 为空)
            jsonObject.put("warehouseNum", ("1".equals(type)) ? "" : warehouseNum);
            // 当月度出庫数(翌15日的 当月入库数和当月出库数 为空)
            jsonObject.put("shipmentSum", ("1".equals(type)) ? "" : shipmentSum);
            // 当月度在庫数(翌15日的 当月度在庫数 等于 前月度在庫数)
            jsonObject.put("stockNum", ("1".equals(type)) ? finalLastMap.get(productId) : nowMap.get(productId));
            // 最終出荷日
            jsonObject.put("lastShipmentDate", lastShipmentDate);
            // 商品类别区分
            jsonObject.put("product_type", product.getProduct_type());
            // 商品種類CD
            jsonObject.put("product_cd", product.getProduct_cd());
            // ソート 排序
            jsonObject.put("sort_no", product.getSort_no());
            jsonArray.add(jsonObject);
        });
        return CommonUtils.success(jsonArray);
    }

    /**
     * @description: 获取周次在库表的数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/12 12:46
     */
    @Override
    public Map<String, List<JSONObject>> getWeeksCsvData(String type, HttpServletRequest request) {
        // 获取当前时间
        LocalDate now = LocalDate.now();
        // 最新
        if ("1".equals(type)) {
            now = LocalDate.now().plusDays(7);
        }
        String client_id = CommonUtils.getToken("client_id", request);
        String warehouse_id = commonFunctionDao.getWarehouseIdByClientId(client_id);
        // 获取去年的对应时间
        LocalDate minus = now.minusYears(1);
        // 获取到现在到去年的今天这个时间段内所有的周三日期
        List<String> localDates = specificDate(minus, now.minusDays(1));
        // 查询出每周三的在库信息
        List<Tw304_stock_day> dataByStockDate = stockDayDao.getDataByStockDate(localDates, warehouse_id, client_id);
        Map<String, List<Tw304_stock_day>> stockDayMap = new LinkedHashMap<>();
        List<String> productIdList = new ArrayList<>();
        if (!StringTools.isNullOrEmpty(dataByStockDate) && dataByStockDate.size() != 0) {
            // 根据商品Id 进行分组 key：productId value：tw304集合
            stockDayMap = dataByStockDate.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
                .collect(
                    Collectors.groupingBy(Tw304_stock_day::getProduct_id, LinkedHashMap::new, Collectors.toList()));
            // 取出所有的商品ID 并去重
            productIdList =
                dataByStockDate.stream().distinct().map(Tw304_stock_day::getProduct_id).collect(Collectors.toList());
        }
        // 获取到商品信息 根据商品Id 进行分组 key: 商品Id value: 商品对象
        Map<String, Mc100_product> productMap = new HashMap<>();
        if (productIdList.size() != 0) {
            List<Mc100_product> productList = productDao.getProductListById(productIdList, warehouse_id, client_id);
            productMap = productList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getProduct_id()))
                .collect(Collectors.toMap(Mc100_product::getProduct_id, o -> o));
        }
        // Tw304 转JSONObject
        Map<String, List<JSONObject>> stockMap = new LinkedHashMap<>();
        Set<String> keys = stockDayMap.keySet();
        for (String key : keys) {
            List<JSONObject> itemLists = new ArrayList<>();
            List<Tw304_stock_day> tw304_stock_days = stockDayMap.get(key);
            for (int i = 0; i < tw304_stock_days.size(); i++) {
                Tw304_stock_day tw304 = tw304_stock_days.get(i);
                JSONObject item = (JSONObject) JSONObject.toJSON(tw304);
                itemLists.add(item);
            }
            stockMap.put(key, itemLists);
        }
        // 遍历在库表数据 拼接json
        Map<String, Mc100_product> finalProductMap = productMap;

        String lastWednesDay = localDates.get(localDates.size() - 1);
        LocalDate lastWednesDayDate = LocalDate.parse(lastWednesDay, DateTimeFormatter.ofPattern("yyyyMMdd"));
        List<Map<String, String>> totalOfShipmentsOnASpecifiedDateList = getTotalOfShipmentsOnASpecifiedDate2(null,
            warehouse_id, client_id,
            LocalDate.parse(localDates.get(localDates.size() - 4), DateTimeFormatter.ofPattern("yyyyMMdd")).toString(),
            LocalDate.parse(localDates.get(localDates.size() - 1), DateTimeFormatter.ofPattern("yyyyMMdd")).toString());
        List<Tw300_stock> notDeliveryOfProductList = getNotDeliveryOfProduct(productIdList, warehouse_id, client_id);

        stockMap.forEach((key, value) -> {
            Mc100_product productInfo = finalProductMap.get(key);
            value.forEach(item -> {
                item.put("name", productInfo.getName());
                item.put("code", productInfo.getCode());
                // 簿価
                item.put("ntm_price", productInfo.getNtm_price());
                // 振替単価
                item.put("ntm_sale", productInfo.getPrice());
                // item.put("price", productInfo.getPrice() + "");
                // 商品类别区分
                item.put("product_type", productInfo.getProduct_type());
                // 商品種類CD
                item.put("product_cd", productInfo.getProduct_cd());
                // ソート 排序
                item.put("sort_no", productInfo.getSort_no());
                // 増刷アラート部数（直前4週出荷数）
                Map<String, String> totalOfShipmentsOnASpecifiedDate = totalOfShipmentsOnASpecifiedDateList.stream()
                    .filter(v1 -> key.equals(v1.get("product_id"))).findFirst().orElse(null);
                item.put("reprint_num",
                    null == totalOfShipmentsOnASpecifiedDate ? "0" : totalOfShipmentsOnASpecifiedDate.get("cnt"));
                // 予備在庫数
                item.put("reserve_num",
                    Optional.ofNullable(notDeliveryOfProductList)
                        .map(productList -> productList.stream().filter(v1 -> key.equals(v1.getProduct_id()))
                            .findFirst().map(v2 -> v2.getNot_delivery()).orElse(0))
                        .orElse(0));
                // 備考(ピッキングメモ)
                Integer eccubeShowFlg = productInfo.getEccube_show_flg();
                String memo = Strings.isNullOrEmpty(productInfo.getBikou()) ? "" : productInfo.getBikou();
                if (eccubeShowFlg != null && eccubeShowFlg == 2) {
                    memo = memo + "【WEB非表示】";
                }
                item.put("memo", memo);
            });
        });
        return stockMap;
    }

    /**
     * @description 获取指定日期的商品出库数
     * @param productIdList 商品ID
     * @param warehouseCd 仓库CD
     * @param clientId 店铺ID
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @return List
     * @date 2021/8/4
     **/
    private List<Map<String, String>> getTotalOfShipmentsOnASpecifiedDate(List<String> productIdList,
        String warehouseCd, String clientId, String startDate, String endDate) {
        List<Map<String, String>> totalOfShipmentsNumWithTime =
            stockDayDao.getTotalOfShipmentsNumWithTime(startDate, endDate, clientId, warehouseCd);
        return totalOfShipmentsNumWithTime;
    }

    private List<Map<String, String>> getTotalOfShipmentsOnASpecifiedDate2(List<String> productIdList,
        String warehouseCd, String clientId, String startDate, String endDate) {
        List<Map<String, String>> totalOfShipmentsNumWithTime =
            stockDayDao.getTotalOfShipmentsNumWithTime2(startDate, endDate, clientId, warehouseCd);
        return totalOfShipmentsNumWithTime;
    }

    /**
     * @description 获取商品的不可配送数
     * @param productIdList 商品ID
     * @param warehouseCd 仓库CD
     * @param clientId 店铺ID
     * @return List
     * @date 2021/8/4
     **/
    private List<Tw300_stock> getNotDeliveryOfProduct(List<String> productIdList, String warehouseCd, String clientId) {
        List<Tw300_stock> stockCntByProductList =
            stockDao.getStockCntByProductList(clientId, warehouseCd, productIdList);
        return stockCntByProductList;
    }

    /**
     * @param shipment_plan_date : 出庫予定日
     * @param type : 1: ECCUBE受注&一斉発送 2: 一斉注文
     * @description: 获取日次出荷数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/12 14:42
     */
    @Override
    public JSONObject getStockDayData(String shipment_plan_date, String type, String form, HttpServletRequest request) {
        String client_id = CommonUtils.getToken("client_id", request);
        // 根据店铺ID获取仓库ID
        String warehouseId = commonFunctionDao.getWarehouseIdByClientId(client_id);

        String startDate = shipment_plan_date + " 00:00:00";
        String endDate = shipment_plan_date + " 23:59:59";
        Date start = DateUtils.getNowTime(startDate);
        Date end = DateUtils.getNowTime(endDate);
        // 获取ECCUBE识别子
        Tc203_order_client orderClient = orderApiDao.getOrderClientInfo(client_id, Constants.ECCUBE, null);
        String identification = "";
        if (!StringTools.isNullOrEmpty(orderClient)) {
            identification = orderClient.getIdentification();
        }
        int typeParam = 1;
        if (!StringTools.isNullOrEmpty(type)) {
            typeParam = Integer.parseInt(type);
        }
        // 出库状态 集合
        List<Integer> shipmentStatusList = Arrays.asList(8);
        List<Tw200_shipment> shipmentsList =
            reportDao.getShipmentList(client_id, warehouseId, null, start, end, Integer.valueOf(form),
                typeParam, identification, shipmentStatusList);
        // 获取所有配送方法
        List<Ms004_delivery> allDeliveryList = deliveryDao.getDeliveryAll();
        JSONArray jsonArray = new JSONArray();
        shipmentsList.forEach(x -> {
            String deliveryTrackingNm = x.getDelivery_tracking_nm();
            // 送り状番号
            String invoiceNumber = "";
            if (!StringTools.isNullOrEmpty(deliveryTrackingNm)) {
                invoiceNumber = deliveryTrackingNm;
            }
            Timestamp shipmentPlanDate = x.getShipment_plan_date();
            String shipment_plan = "";
            if (!StringTools.isNullOrEmpty(shipmentPlanDate)) {
                shipment_plan = shipmentPlanDate.toString().substring(0, 10);
            }
            int boxes = 1;
            if (!StringTools.isNullOrEmpty(x.getBoxes())) {
                boxes = x.getBoxes();
            }
            List<Tw201_shipment_detail> tw201_shipment_detail = x.getTw201_shipment_detail();
            String finalInvoiceNumber = invoiceNumber;
            String shipment_plan1 = shipment_plan;
            int finalBoxes = boxes;
            tw201_shipment_detail.forEach(detail -> {
                Mc100_product product = detail.getMc100_product().get(0);
                JSONObject jsonObject = new JSONObject();
                // 出荷日
                jsonObject.put("shipment_plan_date", shipment_plan1);
                // 受注NO
                jsonObject.put("orderNo", x.getOrder_no());
                // 請求先
                jsonObject.put("orderMail", x.getOrder_mail());
                // 請求先名
                jsonObject.put("orderFamilyName", x.getOrder_family_name());
                // 納品先名
                jsonObject.put("surname", x.getSurname());
                // 品名コード
                jsonObject.put("code", product.getCode());
                // 品名
                jsonObject.put("productName", product.getName());
                // 数量
                jsonObject.put("productNum", detail.getProduct_plan_cnt());
                // 問番
                jsonObject.put("invoiceNumber", finalInvoiceNumber);
                // 個口数
                jsonObject.put("boxes", finalBoxes);
                // 配送先住所都道府県
                jsonObject.put("todoufuken", (null == x.getPrefecture()) ? "" : x.getPrefecture());
                // 配送方式CD
                jsonObject.put("deliveryCarrier", (null == x.getDelivery_carrier()) ? "" : x.getDelivery_carrier());
                // 配送方式名称
                jsonObject.put("deliveryNm",
                    (null == x.getDelivery_carrier()) ? ""
                        : allDeliveryList.stream()
                            .filter(v -> v != null && Objects.equals(x.getDelivery_carrier(), v.getDelivery_cd()))
                            .findFirst().get().getDelivery_nm());
                // 支店code
                jsonObject.put("branchCode", (null == x.getBikou7()) ? "" : x.getBikou7());

                jsonArray.add(jsonObject);
            });
        });
        return CommonUtils.success(jsonArray);
    }

    /**
     * @param startDate : 起始日期
     * @param endDate : 结束日期
     * @description: 计算固定时间段内所有周三的日期
     * @return: java.util.List<java.lang.String>
     * @date: 2021/3/16 16:12
     */
    public static List<String> specificDate(LocalDate startDate, LocalDate endDate) {
        List<String> list = new ArrayList<>();
        long days = ChronoUnit.DAYS.between(startDate, endDate);
        logger.info("起始时间{} 结束时间{}, 总共{}天", startDate, endDate, days);
        // 计算有多少, 指定星期几
        Calendar startCalender = GregorianCalendar.from(startDate.atStartOfDay(ZoneId.systemDefault()));
        for (int i = 0; i < days; i++) {
            startCalender.add(Calendar.DATE, 1);
            if (startCalender.get(Calendar.DAY_OF_WEEK) == Calendar.WEDNESDAY) {
                LocalDate localDate =
                    LocalDateTime.ofInstant(startCalender.toInstant(), ZoneOffset.systemDefault()).toLocalDate();
                String date = DateTimeFormatter.ofPattern("yyyyMMdd").format(localDate);
                list.add(date);
            }
        }
        return list;
    }
}
