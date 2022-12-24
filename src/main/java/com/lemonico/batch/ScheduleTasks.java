package com.lemonico.batch;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.google.common.base.Splitter;
import com.lemonico.batch.bean.*;
import com.lemonico.batch.dao.CheckDataDao;
import com.lemonico.batch.utils.Excel;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.service.CommonService;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.ShipmentDetailDao;
import com.lemonico.store.dao.StockDao;
import com.lemonico.store.service.impl.ShipmentsImpl;
import com.lemonico.wms.dao.ShipmentDao;
import com.lemonico.wms.dao.StocksResultDao;
import com.lemonico.wms.service.ShipmentService;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ScheduleTasks
{
    private final static Logger logger = LoggerFactory.getLogger(ScheduleTasks.class);
    /**
     * 二重引用符からカンマを外すルール
     */
    private final static String REMOVE_COMMA_IN_DOUBLE_QUOTE_RULE = "(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    @Resource
    private StockDao stockDao;
    @Resource
    private StocksResultDao stocksResultDao;
    @Resource
    private ShipmentDao shipmentDao;
    @Resource
    private ClientDao clientDao;
    @Resource
    private ShipmentService shipmentService;
    @Resource
    private ShipmentDetailDao shipmentDetailDao;
    @Resource
    private ProductSettingService productSettingService;
    @Resource
    private CheckDataDao checkDataDao;
    @Resource
    private CommonService commonService;
    @Resource
    private PathProps pathProps;
    @Resource
    private OrderDao orderDao;

    /**
     * 異常データ検索処理
     */
    public void searchErrorData() {
        String activeProfile = null;
        try {
            activeProfile = SpringContextUtil.getActiveProfile();
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            logger.error("------------------- 获取当前程序运行环境失败 -----------------------");
        }
        // 获取到环境信息不为空 并且不是本番环境不需要检查数据
        if (!StringTools.isNullOrEmpty(activeProfile) && !Constants.PRO_ENVIRONMENT.equals(activeProfile)) {
            logger.info("++++++++++++++++++ 当前运行环境为 {} 不需要检查错误数据并发送邮件 ++++++++++++++++++", activeProfile);
            return;
        }
        // 在库表和货架表在库数不一致
        List<Tw300Mw405InconsistentInventory> inconsistentInventories = new ArrayList<>();
        try {
            inconsistentInventories = checkDataDao.getTw300Mw405InconsistentInventory();
        } catch (Exception e) {
            logger.error("在库表和货架表在库数不一致查询失败");
            logger.error(BaseException.print(e));
        }

        // 在库表的实际在库，理论在库，依赖中，不可配送在库数对不上
        List<Tw300_stock> numberInStock = new ArrayList<>();
        try {
            numberInStock = checkDataDao.getWrongNumberInStock();
        } catch (Exception e) {
            logger.error("在库表的实际在库，理论在库，依赖中，不可配送在库数对不上查询失败");
            logger.error(BaseException.print(e));
        }

        // 实际在库，依赖中，不可配送在库数出现负数
        List<Tw300_stock> inventoryTable = new ArrayList<>();
        try {
            inventoryTable = checkDataDao.getInventoryTable();
        } catch (Exception e) {
            logger.error("实际在库，依赖中，不可配送在库数出现负数查询失败");
            logger.error(BaseException.print(e));
        }

        // 没有出荷作业开始，但是tw212状态为出荷作业中的数据
        List<Tw200_shipment> noWorkStarted = new ArrayList<>();
        try {
            noWorkStarted = checkDataDao.getNoWorkStarted();
        } catch (Exception e) {
            logger.error("没有出荷作业开始，但是tw212状态为出荷作业中的数据查询失败");
            logger.error(BaseException.print(e));
        }

        // 出荷作业中，货架履历表没有数据
        List<Tw200_shipment> noDataInTheJob = new ArrayList<>();
        try {
            noDataInTheJob = checkDataDao.getNoDataInTheJob();
        } catch (Exception e) {
            logger.error("出荷作业中，货架履历表没有数据查询失败");
            logger.error(BaseException.print(e));
        }

        // 出库货架详细表里面的引当数总和和相对应的货架上面的依赖数不一致
        List<Mw405ErrorData> mw405ErrorDataList = new ArrayList<>();
        try {
            mw405ErrorDataList = checkDataDao.getMw405ErrorDataList();
        } catch (Exception e) {
            logger.error("出库货架详细表里面的引当数总和和相对应的货架上面的依赖数不一致查询失败");
            logger.error(BaseException.print(e));
        }

        // 还在出库的商品被删除
        List<AbnormalProducts> abnormalProducts = new ArrayList<>();
        try {
            abnormalProducts = checkDataDao.getAbnormalProducts();
        } catch (Exception e) {
            logger.error("还在出库的商品被删除 查询失败");
            logger.error(BaseException.print(e));
        }

        List<Tw200Tw201DifferentDelFlg> tw200Tw201DifferentDelFlgs = new ArrayList<>();
        try {
            tw200Tw201DifferentDelFlgs = checkDataDao.getTw200Tw201DifferentDelFlgs();
        } catch (Exception e) {
            logger.error("查询出库依赖和出库依赖明细的删除状态不一致失败");
            logger.error(BaseException.print(e));
        }
        List<Tw300ErrorRequestCnt> errorRequestCnts = new ArrayList<>();
        try {
            errorRequestCnts = checkDataDao.getErrorRequestCnts();
        } catch (Exception e) {
            logger.error("查询在库表依赖中数和出库详细表统计出来的依赖数不一致失败");
            logger.error(BaseException.print(e));
        }

        List<Tw301_stock_history> stockHistories = new ArrayList<>();

        try {
            stockHistories = checkDataDao.getStockHistories();
        } catch (Exception e) {
            logger.error("查询前一天的在库履历失败");
            logger.error(BaseException.print(e));
        }

        List<Tw301_stock_history> shipmentErrorHistory = new ArrayList<>();

        try {
            if (!StringTools.isNullOrEmpty(stockHistories) && !stockHistories.isEmpty()) {
                Map<String, List<Tw301_stock_history>> historyMap =
                    stockHistories.stream().collect(Collectors.groupingBy(
                        x -> x.getPlan_id() + "_" + x.getClient_id() + "_" + x.getProduct_id() + "_" + x.getType()));

                for (Map.Entry<String, List<Tw301_stock_history>> entry : historyMap.entrySet()) {
                    String key = entry.getKey();
                    List<Tw301_stock_history> histories = entry.getValue();
                    if (histories.size() == 1) {
                        continue;
                    }
                    List<String> list = Splitter.on("_").splitToList(key);
                    String type = list.get(3);
                    if ("1".equals(type)) {
                        shipmentErrorHistory.addAll(histories);
                        continue;
                    }
                    String shipmentId = list.get(0);
                    String clientId = list.get(1);
                    String productId = list.get(2);
                    // 获取出库详细信息
                    List<Tw201_shipment_detail> shipmentDetails =
                        shipmentDetailDao.getShipmentDetailsById(shipmentId, clientId, productId);
                    if (!StringTools.isNullOrEmpty(shipmentDetails) && !shipmentDetails.isEmpty()) {
                        List<Integer> setSubIdList = shipmentDetails.stream().map(Tw201_shipment_detail::getSet_sub_id)
                            .collect(Collectors.toList());
                        if (setSubIdList.size() != histories.size()) {
                            shipmentErrorHistory.addAll(histories);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            logger.error("获取在库履历错误数据失败");
        }

        String filePath = pathProps.getRoot() + pathProps.getStore() + "errorData.xls";
        Excel.createCheckDataExcel(inconsistentInventories, numberInStock, inventoryTable, noWorkStarted,
            noDataInTheJob, mw405ErrorDataList, abnormalProducts, tw200Tw201DifferentDelFlgs, errorRequestCnts,
            shipmentErrorHistory, filePath);

        MailBean mailBean = new MailBean();
        mailBean.setSubject("【SunLogi】ERROR DATA");
        mailBean.setContent("1");
        mailBean.setFilePath(filePath);
        commonService.sendMail(mailBean);

        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * S3に伝票番号をアップロード
     */
    public void uploadDataToS3() {
        // a获取所有出荷完了有传票番号且未上传过的tw200数据
        List<Tw200_shipment> list = orderDao.getShipmentInfoByIdentifier("IAMS3", null);
        String commaRule = "," + REMOVE_COMMA_IN_DOUBLE_QUOTE_RULE;

        // a如果有数据未上传则需要继续执行
        if (list.size() > 0) {
            String csvHeader = "お客様管理番号,受注番号,送り状番号,出荷状況";
            // a获取所有tc207中关于s3的设定信息
            List<Tc207_order_s3> s3List = orderDao.getS3SettingAll();
            for (Tc207_order_s3 tc207 : s3List) {
                List<String[]> data = new ArrayList<>();
                // csv文件数据生成
                for (Tw200_shipment tw200 : list) {
                    if (tc207.getClient_id().equals(tw200.getClient_id())) {
                        String[] csvData = new String[csvHeader.split(commaRule, -1).length];
                        csvData[0] = tw200.getShipment_plan_id();
                        csvData[1] = tw200.getOrder_no();
                        csvData[2] = tw200.getDelivery_tracking_nm();
                        csvData[3] = "出荷済み";
                        data.add(csvData);
                    }
                }
                // a如果没有数据则跳出当前循环，查找下一个207设定中有没有匹配的数据
                if (data.size() == 0) {
                    continue;
                }
                // a文件路径
                String filePath = pathProps.getOrder();
                // a文件名称
                String fileName = "order_tracking_";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
                Date date = new Date();
                String dateTime = simpleDateFormat.format(date);
                fileName += dateTime + ".csv";

                // a生成csv文件
                CsvUtils.write(filePath + fileName, csvHeader, data, true);
                // a判断用户保存数据是否以/结尾，如果不是需要加上/符号（上传文件不加/符号s3服务器不识别）
                String upload_folder = tc207.getUpload_folder();
                if (!"/".equals(upload_folder.charAt(upload_folder.length() - 1) + "")) {
                    upload_folder += "/";
                }
                // a上传到S3服务器
                // a首先添加验证信息
                AmazonS3 client = getCredentials(tc207.getPassword1(), tc207.getPassword2());
                // === aファイルから直接アップロードする場合 ===
                // aアップロードするファイル
                File file = new File(filePath + fileName);
                // aファイルをアップロード
                client.putObject(
                    // aアップロード先バケット名
                    tc207.getBucket(),
                    // aアップロード後のキー名
                    upload_folder + fileName,
                    // aファイルの実体
                    file);

                // === InputStreamからアップロードする場合 ===
                try (FileInputStream input = new FileInputStream(file)) {
                    // aメタ情報を生成
                    ObjectMetadata metaData = new ObjectMetadata();
                    metaData.setContentLength(file.length());
                    // aリクエストを生成
                    PutObjectRequest request = new PutObjectRequest(
                        // aアップロード先バケット名
                        tc207.getBucket(),
                        // aアップロード後のキー名
                        upload_folder + fileName,
                        // InputStream
                        input,
                        // aメタ情報
                        metaData);
                    // aアップロード
                    client.putObject(request);
                    // a上传完成之后更改对应的finish_flg
                    for (String[] tem : data) {
                        orderDao.updateFinishFlag(tem[0]);
                    }
                    // a上传完成之后删除本地文件
                    file.delete();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * S3認証情報取得
     *
     * @param accessKey アクセスキー
     * @param accessSecretKey シークレットキー
     * @return {@link AmazonS3}
     */
    private AmazonS3 getCredentials(String accessKey, String accessSecretKey) {
        AWSCredentials credentials = new BasicAWSCredentials(
            // aアクセスキー
            accessKey,
            // aシークレットキー
            accessSecretKey);
        return AmazonS3ClientBuilder.standard()
            // a認証情報を設定
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            // aリージョンを AP_NORTHEAST_1(東京) に設定
            .withRegion(Regions.AP_NORTHEAST_1).build();
    }

    /**
     * 領収書と納品書生成
     */
    public void createReceiptAndDeliveryPDF() {

        // 获取所有具有生成领取书pdf功能的店铺
        String functionId = "5";
        List<String> clientIdList = clientDao.getClientIdByFunctionId(functionId);
        if (clientIdList.size() == 0) {
            logger.info("領収書を生成する機能を利用している店舗は0件");
            return;
        }
        // 获取到状态围为出库依赖和出荷完了的数据
        List<Tw200_shipment> specificShipment = shipmentDao.getSpecificShipment(clientIdList);

        // 纳品书为空 或者 领取书为空 出库依赖信息
        List<Tw200_shipment> deliverShipmentList = specificShipment.stream()
            .filter(
                x -> StringTools.isNullOrEmpty(x.getDelivery_url()) || StringTools.isNullOrEmpty(x.getReceipt_url()))
            .collect(Collectors.toList());

        ArrayList<Tw200_shipment> shipments = new ArrayList<>();
        for (Tw200_shipment shipment : deliverShipmentList) {
            String shipment_plan_id = shipment.getShipment_plan_id();
            String client_id = shipment.getClient_id();
            String warehouse_cd = shipment.getWarehouse_cd();

            // 获取到 领取书 和 纳品书的url
            String deliveryUrl = shipment.getDelivery_url();
            String receiptUrl = shipment.getReceipt_url();
            if (StringTools.isNullOrEmpty(shipment.getDelivery_url())) {
                String pdfName = randomNum() + ".pdf";
                String pdfPath = pathProps.getTemporary() + "/" + pdfName;
                // 如果纳品书为空，则生成纳品书pdf 并获取到url
                JSONObject json = new JSONObject();
                json.put("client_id", client_id);
                json.put("warehouse_cd", warehouse_cd);
                json.put("shipment_plan_id", shipment_plan_id);
                json.put("relativePath", pdfPath);
                try {
                    shipmentService.createProductDetailPDF(json);
                    deliveryUrl = pdfName;
                } catch (Exception e) {
                    logger.error("出库依赖Id={},店铺Id={}, 通过BATCH生成纳品书失败", shipment_plan_id, client_id);
                    logger.error(BaseException.print(e));
                }
            }
            if (shipment.getShipment_status() == 8 && StringTools.isNullOrEmpty(shipment.getReceipt_url())) {
                List<Tw201_shipment_detail> shipmentDetails =
                    shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, false);
                if (shipmentDetails.isEmpty()) {
                    logger.error("出库依赖Id={} 没有出荷明细信息, 仓库Id={} 店铺Id={}", shipment_plan_id, warehouse_cd, client_id);
                    return;
                }
                JSONObject json = new JSONObject();
                JSONArray items = new JSONArray();
                for (Tw201_shipment_detail detail : shipmentDetails) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("tax_flag", detail.getTax_flag());
                    jsonObject.put("unit_price", detail.getUnit_price());
                    jsonObject.put("is_reduced_tax", detail.getIs_reduced_tax());
                    jsonObject.put("product_plan_cnt", detail.getProduct_plan_cnt());
                    jsonObject.put("set_sub_id", detail.getSet_sub_id());
                    jsonObject.put("set_cnt", detail.getSet_cnt());
                    items.add(jsonObject);
                }
                json.put("items", items);

                Mc105_product_setting productSetting = productSettingService.getProductSetting(client_id, null);
                Integer tax = productSetting.getTax();
                Integer accordion = productSetting.getAccordion();
                ShipmentsImpl.calculateProductTax(json, tax, accordion);

                String totalWithReducedTaxPrice = json.getString("totalWithReducedTaxPrice");
                String totalWithNormalTaxPrice = json.getString("totalWithNormalTaxPrice");

                // 计算出うち、消費税額
                int taxPrice = (!StringTools.isNullOrEmpty(totalWithNormalTaxPrice)
                    ? Integer.parseInt(totalWithNormalTaxPrice)
                    : 0)
                    + (!StringTools.isNullOrEmpty(totalWithReducedTaxPrice) ? Integer.parseInt(totalWithReducedTaxPrice)
                        : 0);
                shipment.setTotal_with_normal_tax(taxPrice);
                String pdfName = randomNum() + ".pdf";
                String pdfPath = pathProps.getTemporary() + "/" + pdfName;
                // 如果为出荷完了状态 并且领取书为空，生成领取书pdf 并获取url
                try {
                    PdfTools.receiptPdf(pathProps.getRoot() + pdfPath, shipment,
                        pathProps.getRoot() + pathProps.getImage() + "image_2021_07_20T06_04_38_469Z.png");
                    receiptUrl = pdfName;
                } catch (Exception e) {
                    logger.error("店铺Id={},出库依赖Id={} 生成领取书报错", client_id, shipment_plan_id);
                    logger.error(BaseException.print(e));
                }
            }
            Tw200_shipment tw200Shipment = new Tw200_shipment();
            tw200Shipment.setShipment_plan_id(shipment_plan_id);
            tw200Shipment.setClient_id(client_id);
            tw200Shipment.setWarehouse_cd(warehouse_cd);
            tw200Shipment.setDelivery_url(deliveryUrl);
            tw200Shipment.setReceipt_url(receiptUrl);
            shipments.add(tw200Shipment);
        }
        if (shipments.size() != 0) {
            // 批量变更出库依赖中的纳品书url 和 领取书url
            try {
                shipmentDao.updateDeliveryUrl(shipments);
            } catch (Exception e) {
                logger.error("通过BATCH批量变更出库依赖中的纳品书URL及领取书URL失败");
                logger.error("失败的出库依赖Id={}",
                    shipments.stream().map(Tw200_shipment::getShipment_plan_id).collect(Collectors.toList()));
                logger.error(BaseException.print(e));
            }
        }
    }

    /**
     * 配送不可数更新
     */
    @Transactional
    public void updateNotDeliveryCount() {
        logger.info("BATCH开始执行,不可配送数的变更");
        Date date = DateUtils.getDate();
        // 更改货架上面的不可配送数
        String updUsr = Constants.SUNLOGI;
        // 获取到已经过期商品货架信息
        List<Mw405_product_location> productLocations = stocksResultDao.getUnavailableProductLocation(date);
        if (StringTools.isNullOrEmpty(productLocations) || productLocations.isEmpty()) {
            logger.warn("所有的店铺没有过期的货架信息");
            return;
        }

        List<String> idList =
            productLocations.stream().map(x -> x.getLocation_id() + "_" + x.getClient_id() + "_" + x.getProduct_id())
                .distinct().collect(Collectors.toList());

        try {
            // 修改货架的出荷状态为不可出库并修改不可配送数
            stocksResultDao.updateLocationStatus(productLocations, date, updUsr);
        } catch (Exception e) {
            logger.error("修改货架的出荷状态为不可出库并修改不可配送数失败, 货架Id、店铺Id、商品Id={}", idList);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 获取到所有不可用的商品货架信息
        List<Mw405_product_location> mw405ProductLocations = stocksResultDao.getAllCannotProductLocation();

        if (StringTools.isNullOrEmpty(mw405ProductLocations) || mw405ProductLocations.isEmpty()) {
            logger.error("没有任何不可出库的商品信息");
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        List<Mw405_product_location> productLocationList =
            mw405ProductLocations.stream().map(x -> new Mw405_product_location(x.getClient_id(), x.getProduct_id()))
                .distinct().collect(Collectors.toList());

        // 查询到商品对应的在库信息
        List<Tw300_stock> tw300StockList = stockDao.getStockListByLocation(productLocationList);

        if (StringTools.isNullOrEmpty(tw300StockList) || tw300StockList.isEmpty()) {
            logger.error("货架Id、店铺Id、商品Id={} 没有任何在库信息，属于错误数据请确认", idList);
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        Map<String, Tw300_stock> stockMap =
            tw300StockList.stream().collect(Collectors.toMap(x -> x.getClient_id() + "_" + x.getProduct_id(), o -> o));

        Map<String, List<Mw405_product_location>> productMap = mw405ProductLocations.stream()
            .collect(Collectors.groupingBy(x -> x.getClient_id() + "_" + x.getProduct_id()));

        ArrayList<Tw300_stock> tw300Stocks = new ArrayList<>();
        for (Map.Entry<String, List<Mw405_product_location>> entry : productMap.entrySet()) {
            String key = entry.getKey();
            if (!stockMap.containsKey(key)) {
                continue;
            }

            Tw300_stock stock = stockMap.get(key);
            // 实际在库数
            Integer inventoryCnt = stock.getInventory_cnt();
            List<Mw405_product_location> locationList = entry.getValue();
            // 在库表的不可配送数 = 商品货架的 不可配送数总和
            int tw300NotDelivery = locationList.stream().mapToInt(Mw405_product_location::getNot_delivery).sum();
            int requestingCnt = stock.getRequesting_cnt() < 0 ? 0 : stock.getRequesting_cnt();
            // 在库表的理论在库数 = 实际在库数 - 依赖中数 - 不可配送数
            int tw300Available_cnt = inventoryCnt - requestingCnt - tw300NotDelivery;
            stock.setAvailable_cnt(tw300Available_cnt);
            stock.setNot_delivery(tw300NotDelivery);
            stock.setUpd_usr(updUsr);
            stock.setUpd_date(date);
            tw300Stocks.add(stock);
        }

        if (!tw300Stocks.isEmpty()) {
            // 更改多条在库信息的不可配送数和理论在库数
            try {
                stockDao.updateNotDelivery(tw300Stocks);
            } catch (Exception e) {
                List<String> stockList =
                    tw300Stocks.stream().map(Tw300_stock::getStock_id).collect(Collectors.toList());
                logger.error("修改多条在库信息的不可配送数和理论在库数失败, 在库Id={}", stockList);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        logger.info("不可配送数执行完成，执行的货架Id、店铺id、商品Id包含: {}", idList);
    }

    /**
     * 生成随机数
     */
    private String randomNum() {
        StringBuilder val = new StringBuilder();
        java.util.Random random = new java.util.Random();
        for (int i = 0; i < 32; i++) {// 定义随机数位数
            // 输出字母还是数字
            String charOrNum = random.nextInt(2) % 2 == 0 ? "char" : "num";
            // 字符串
            if ("char".equalsIgnoreCase(charOrNum)) {
                // 取得大写字母还是小写字母
                int choice = random.nextInt(2) % 2 == 0 ? 65 : 97;
                val.append((char) (choice + random.nextInt(26)));
            } else { // 数字
                val.append(random.nextInt(10));
            }
        }

        return System.currentTimeMillis() + val.toString();
    }

}
