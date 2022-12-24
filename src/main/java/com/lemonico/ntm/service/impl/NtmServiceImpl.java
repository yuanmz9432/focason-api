package com.lemonico.ntm.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.csvreader.CsvReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.BizLogiResEnum;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.ntm.bean.CheckAddressRequest;
import com.lemonico.ntm.bean.CheckAddressResponse;
import com.lemonico.ntm.bean.NtmCsvOrder;
import com.lemonico.ntm.dao.*;
import com.lemonico.ntm.service.NtmService;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.impl.OrderServiceImpl;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: NtmServiceImpl
 * @date: 2021/4/15
 **/
@Service
public class NtmServiceImpl implements NtmService
{

    private final static Logger logger = LoggerFactory.getLogger(NtmServiceImpl.class);

    @Resource
    private NtmShipmentDao ntmShipmentDao;

    @Resource
    private DeliveryFareDao deliveryFareDao;

    @Resource
    private OrderHistoryDao orderHistoryDao;

    @Resource
    private NtmProductMasterDao ntmProductMasterDao;

    @Resource
    private NtmAreaMasterDao ntmAreaMasterDao;

    @Resource
    private NtmYubinChangeHistoryDao ntmYubinChangeHistoryDao;

    @Resource
    private ProductDao productDao;

    @Resource
    private SettingDao settingDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderDetailDao orderDetailDao;

    @Resource
    private OrderServiceImpl orderService;

    @Resource
    private ProductSettingService productSettingService;
    @Resource
    private PathProps pathProps;

    @Resource
    private DeliveryDao deliveryDao;

    @Value("${bizApiHost}")
    private String bizApiHost;

    @Value("${bizAuthCustomId}")
    private String bizAuthCustomId;

    @Value("${bizAuthCustomPwd}")
    private String bizAuthCustomPwd;

    /**
     * @description ntm top页数据
     * @return: JSONObject
     * @date 2021/4/15
     **/
    @Override
    public JSONObject getTopData(String clientId) {

        HashMap<String, Integer> enterpriseData = getEnterpriseData(clientId);
        HashMap<String, Integer> personData = getPersonData(clientId);

        JSONObject personBean = new JSONObject();
        personBean.put("day", personData.get("todayCnt") + "/" + personData.get("todayTotalCnt"));
        personBean.put("tomorrow", personData.get("tomorrowCnt") + "/" + personData.get("tomorrowTotalCnt"));

        JSONObject resBean = new JSONObject();
        resBean.put("enterprise", enterpriseData);
        resBean.put("person", personBean);
        return resBean;
    }

    /**
     * @description 通过ntm的配送運賃数据
     * @param list 更新が必要です配送運賃
     * @return: boolean
     * @date 2021/6/11
     **/
    @Override
    public boolean syncDeliveryFare(List<Tw216_delivery_fare> list) {
        try {
            deliveryFareDao.truncateDeliveryFare();
            deliveryFareDao.insertDeliveryFareBatch(list);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * @description ntm个人受注csv导入
     * @param file 上传的CSV文件
     * @param client_id 店铺ID
     * @param wharehouse_cd 仓库ID
     * @param status 出庫依頼設定
     * @param request HttpRequest
     * @return JSONObject
     * @date 2021/6/16
     **/
    @Override
    @Transactional(rollbackFor = {
        BaseException.class, Error.class
    })
    public JSONObject importOrderCsv(MultipartFile file, String client_id, String wharehouse_cd, String status,
        HttpServletRequest request) {

        JSONObject resultJson = new JSONObject();

        // エラー情報を格納するリスト
        List<String> totalErrList = new ArrayList<String>();
        String encoding = "Shift-JIS";
        List<List<String>> errorFileData = Lists.newArrayList();
        // 错误的待过滤的数据NO
        List<String> filterNtmOrderSerialNum = Lists.newArrayList();

        try {
            // 保存上传的csv文件
            saveImportedNtmOrderFile(client_id, file);

            // CSV文件格式及字段校验
            JSONObject fieldFormatCheckRes =
                checkCsvFieldFormat(client_id, file, encoding, totalErrList, errorFileData, filterNtmOrderSerialNum);
            totalErrList = fieldFormatCheckRes.getObject("totalErrList", List.class);
            List<NtmCsvOrder> ntmCsvOrderList = fieldFormatCheckRes.getObject("ntmCsvOrderList", List.class);

            // 字段有效性校验及处理
            JSONObject fieldConvertAndValidityRes = checkCsvFieldConvertAndValidity(client_id, ntmCsvOrderList,
                totalErrList, errorFileData, filterNtmOrderSerialNum);
            totalErrList = fieldConvertAndValidityRes.getObject("totalErrList", List.class);
            List<NtmCsvOrder> conformOrderList = fieldConvertAndValidityRes.getObject("conformOrderList", List.class);
            List<NtmCsvOrder> emptyProductOrderList =
                fieldConvertAndValidityRes.getObject("emptyProductOrder", List.class);

            // 查询该CSV中包含的所有商品List
            List<Mc100_product> allProductList = getProductListByConformOrder(client_id, conformOrderList);
            if ((null == allProductList || (null != allProductList && allProductList.size() == 0))
                && (null != conformOrderList && conformOrderList.size() > 0)) {
                logger.error("インポートされた製品コードは存在しません", client_id);
                totalErrList.add("インポートされた製品コードは存在しません");
                errorFileData.add(Lists.newArrayList(
                    "", "", "", "インポートされた製品コードは存在しません"));
            }

            if (conformOrderList != null && conformOrderList.size() > 0) {

                // 过滤掉又错误的行数据
                List<String> tmpFilterList = filterNtmOrderSerialNum.stream().distinct().collect(Collectors.toList());
                for (int i = 0; i < conformOrderList.size(); i++) {
                    if (tmpFilterList.contains(conformOrderList.get(i).getSerialNum())) {
                        conformOrderList.remove(i);
                    }
                }

                // 入库（order/order_detail/shipment）
                // 构造订单Bean、订单详情Bean
                List<Tc200_order> tc200List =
                    constructOrderData(client_id, wharehouse_cd, status, conformOrderList, allProductList);

                orderService.batchInsertOrderAndOrderDetail(tc200List);

                // 选择出庫依頼する时走出库逻辑
                if (!Strings.isNullOrEmpty(status) && status.equals("1")) {
                    JSONObject orderNoJson = new JSONObject();
                    JSONArray list = new JSONArray();
                    for (Tc200_order tc200 : tc200List) {
                        list.add(tc200.getPurchase_order_no());
                    }
                    orderNoJson.put("list", list);
                    orderService.createShipments(orderNoJson, client_id, request);
                }

                // 生成した受注履歴beanに情報を格納
                generateOrderHistory(client_id, tc200List, tc200List.size() + emptyProductOrderList.size(),
                    tc200List.size(), emptyProductOrderList.size());
            }

            // 错误信息去重 并排序
            List<String> collect = totalErrList.stream().distinct().sorted().collect(Collectors.toList());
            // 给前端返回错误信息集合
            resultJson.put("errlist", collect);

        } catch (Exception e) {
            logger.error(e.getMessage());
            resultJson.put("errlist", Lists.newArrayList("csvの形式不正です。"));
        }
        resultJson.put("header", Lists.newArrayList("通し番号", "支店コード", "カタログ（翻訳マスタで変換エラーになったものだけ）", "エラー内容"));
        resultJson.put("parameterList", Lists.newArrayList(0, 1, 2, 3));
        resultJson.put("downloadCsvData", errorFileData);

        return CommonUtils.success(resultJson);
    }

    /**
     * @description 保存导入的ntm订单文件
     * @param clientId 店铺ID
     * @param file 上传的csv文件
     * @return: null
     * @date 2021/6/25
     **/
    private void saveImportedNtmOrderFile(String clientId, MultipartFile file) {
        String filePathName = pathProps.getNtmOrder() + "/" + clientId + "_" + System.currentTimeMillis() + ".csv";

        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(filePathName);
            fileOutputStream.write(file.getBytes());
        } catch (Exception var1) {
            logger.info("ntm注文csvファイルの保存に失敗しました。エラーメッセージ：" + var1.getMessage());
            var1.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
                logger.info("ntm注文csvファイルを保存するパスは次のとおりです：" + filePathName);
            } catch (IOException var2) {
                logger.info("ntm order csvファイルを保存した後、ファイルを閉じることができませんでした。エラーメッセージ：" + var2.getMessage());
            }
        }

    }

    /**
     * @description 生成受注履历信息
     * @param clientId 店铺ID
     * @param tc200List 受注list
     * @param totalCnt 取込件数
     * @param successCnt 成功件数
     * @param failCnt 失敗件数
     * @return: void
     * @date 2021/6/28
     **/
    private void generateOrderHistory(String clientId, List<Tc200_order> tc200List, int totalCnt, int successCnt,
        int failCnt) {
        Tc202_order_history order_history = new Tc202_order_history();
        String historyId = tc200List.get(0).getHistory_id();
        order_history.setHistory_id(Integer.valueOf(historyId));
        Timestamp nowDate = new Timestamp(DateUtils.getDate().getTime());
        order_history.setImport_datetime(nowDate);
        order_history.setIns_date(nowDate);
        order_history.setClient_id(clientId);
        // 取込件数
        order_history.setTotal_cnt(totalCnt);
        // 成功件数
        order_history.setSuccess_cnt(successCnt);
        // 失敗件数
        order_history.setFailure_cnt(failCnt);
        order_history.setBiko01("1");
        orderHistoryDao.insertOrderHistory(order_history);
    }

    /**
     * @description 构造订单相关Bean
     * @param clientId 店铺ID
     * @param warehouseCd 仓库ID
     * @param status 是否出库依赖，1：出库依赖，0：不出库
     * @param ntmCsvOrderList 符合条件的csv数据
     * @param allProductList 商品list
     * @return null
     * @date 2021/6/17
     **/
    private List<Tc200_order> constructOrderData(String clientId, String warehouseCd, String status,
        List<NtmCsvOrder> ntmCsvOrderList, List<Mc100_product> allProductList) {

        List<Tc200_order> orderList = Lists.newArrayList();

        // 获取最大受注取込履歴ID
        Integer historyId = getMaxHistoryId();

        for (int i = 0; i < ntmCsvOrderList.size(); i++) {

            if (null == ntmCsvOrderList.get(i))
                continue;
            NtmCsvOrder ntmCsvOrder = ntmCsvOrderList.get(i);
            Tc200_order order = new Tc200_order();

            order.setWarehouse_cd(warehouseCd);
            order.setClient_id(clientId);

            String orderNo = getOrderNo(i + 1);
            order.setPurchase_order_no(orderNo);
            // 用序列号作为 外部受注番号
            order.setOuter_order_no(ntmCsvOrder.getSerialNum());
            // 配送先形態个人
            order.setForm(2);

            // 受注取込履歴ID
            order.setHistory_id(Integer.toString(historyId));
            // 外部注文ステータス
            order.setOuter_order_status(0);
            order.setOrder_datetime(DateUtils.getDate());

            // 切割CSV的郵便番号 order_zip_code1、order_zip_code2
            List<String> yubinList =
                Splitter.on("-").omitEmptyStrings().trimResults().splitToList(ntmCsvOrder.getYubin());
            order.setOrder_zip_code1(Optional.ofNullable(yubinList).map(value -> value.get(0)).orElse(""));
            order.setOrder_zip_code2(Optional.ofNullable(yubinList).map(value -> value.get(1)).orElse(""));

            // 注文者住所信息
            order.setOrder_todoufuken(ntmCsvOrder.getPrefectures());
            order.setOrder_address1(ntmCsvOrder.getAddressCity());
            order.setOrder_address2(ntmCsvOrder.getAddressTown() + ntmCsvOrder.getAddressBuilding());

            // 支店名称
            String areaName = Optional.ofNullable(ntmCsvOrder)
                .map(item -> Optional.of(item.getNtmAreaMaster()).map(v -> v.getArea_name()).orElse("")).orElse("");
            order.setOrder_family_name(areaName);
            order.setOrder_first_name(ntmCsvOrder.getName());

            // 切割CSV的TEL
            List<String> tel = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(ntmCsvOrder.getTel());
            order.setOrder_phone_number1(Optional.ofNullable(tel).map(value -> value.get(0)).orElse(""));
            order.setOrder_phone_number2(Optional.ofNullable(tel).map(value -> value.get(1)).orElse(""));
            order.setOrder_phone_number3(Optional.ofNullable(tel).map(value -> value.get(2)).orElse(""));

            // 配送先郵便番号
            order.setReceiver_zip_code1(Optional.ofNullable(yubinList).map(value -> value.get(0)).orElse(""));
            order.setReceiver_zip_code2(Optional.ofNullable(yubinList).map(value -> value.get(1)).orElse(""));

            // 配送先住所都道府県
            order.setReceiver_todoufuken(ntmCsvOrder.getPrefectures());
            order.setReceiver_address1(ntmCsvOrder.getAddressCity());
            order.setReceiver_address2(ntmCsvOrder.getAddressTown() + ntmCsvOrder.getAddressBuilding());
            // 配送先姓
            order.setReceiver_family_name(ntmCsvOrder.getName());
            order.setReceiver_first_name("");
            order.setReceiver_phone_number1(Optional.ofNullable(tel).map(value -> value.get(0)).orElse(""));
            order.setReceiver_phone_number2(Optional.ofNullable(tel).map(value -> value.get(1)).orElse(""));
            order.setReceiver_phone_number3(Optional.ofNullable(tel).map(value -> value.get(2)).orElse(""));

            // 设置依赖主：根据法人/个人状态自动选择依赖主，法人：1、個人：2
            Ms012_sponsor_master sponsor = settingDao.getSponsorByForm(clientId, 2);
            order.setSponsor_id(null != sponsor.getSponsor_id() ? sponsor.getSponsor_id() : null);

            // 取込日時
            order.setImport_datetime(new Timestamp(System.currentTimeMillis()));

            // 'DM便不可'字段为1时，配送方式是佐川急便(2), 其他情况配送方式为ヤマトネコポス(1)
            List<Mc107_ntm_product_master> ntmProductMasterList = ntmCsvOrder.getNtmProductMasterList();
            Integer dmFlg =
                Optional.ofNullable(ntmProductMasterList.get(0)).map(value -> value.getDm_flag()).orElse(null);

            if (null != dmFlg && dmFlg == 1) {
                Ms004_delivery sagawaDelivery = deliveryDao.findDeliveryByName("佐川急便", "飛脚即配便");
                order.setDelivery_method(
                    Optional.ofNullable(sagawaDelivery).map(Ms004_delivery::getDelivery_method).orElse(""));
                order.setDelivery_company(
                    Optional.ofNullable(sagawaDelivery).map(Ms004_delivery::getDelivery_cd).orElse(""));
            } else {
                Ms004_delivery yamatoDelivery = deliveryDao.findDeliveryByName("ヤマト運輸", "ネコポス");
                order.setDelivery_method(
                    Optional.ofNullable(yamatoDelivery).map(Ms004_delivery::getDelivery_method).orElse(""));
                order.setDelivery_company(
                    Optional.ofNullable(yamatoDelivery).map(Ms004_delivery::getDelivery_cd).orElse(""));
            }

            // 明細同梱設定(0:同梱する 1:同梱しない)
            String deliveryNoteType = Optional.ofNullable(ntmCsvOrder).map(NtmCsvOrder::getProductSetting)
                .map(Mc105_product_setting::getDelivery_note_type)
                .orElse("");
            if (deliveryNoteType.equals("1")) {
                order.setDetail_bundled("同梱しない");
            } else {
                order.setDetail_bundled("同梱する");
            }

            // 明細書金額印字
            Integer priceOnDeliveryNote = Optional.ofNullable(ntmCsvOrder).map(NtmCsvOrder::getProductSetting)
                .map(Mc105_product_setting::getPrice_on_delivery_note)
                .orElse(null);
            if (null != priceOnDeliveryNote) {
                order.setDetail_price_print(String.valueOf(priceOnDeliveryNote));
            }

            // 明細書メッセージ
            order.setDetail_message(
                Optional.ofNullable(ntmCsvOrder).map(NtmCsvOrder::getSponsorMaster)
                    .map(Ms012_sponsor_master::getDetail_message)
                    .orElse(""));

            // 支店コード -> 備考1
            order.setBikou8(ntmCsvOrder.getBranchCode());
            order.setDel_flg(0);
            // 依赖主设定
            // order.setSponsor_id(Optional.ofNullable(ntmCsvOrder).map(value ->
            // value.getSponsorMaster().getSponsor_id()).orElse(null));
            order.setBikou_flg(0);
            order.setOrder_flag(0);

            // 构造受注详情list
            List<Tc201_order_detail> orderDetailList = constructOrderDetailData(ntmCsvOrder, order, allProductList);
            order.setTc201_order_detail_list(orderDetailList);

            // 商品金額(税込): product_price_excluding_tax
            // 根据mc100_product的税区分字段(tax_flag 或者店铺默认的税区分设置)。
            // 如果是税入的话不用计算，直接就是商品的单价, 即 product_price_excluding_tax = 商品单价
            // 如果是税抜的话，product_price_excluding_tax = 单价*消费税+单价
            // (消费税根据mc100_product的is_reduced_tax确定，is_reduced_tax=0时消费税是10%，is_reduced_tax是1时消费税是8%)
            JSONObject orderPriceWithTax = getOrderPriceWithTax(order);
            order.setProduct_price_excluding_tax(orderPriceWithTax.getInteger("totalPrice"));

            // 消費税合計: tax_total
            // product_price_excluding_tax/(100+消费税)，即product_price_excluding_tax/(100+10) 或
            // product_price_excluding_tax/(100+8)
            order.setTax_total(orderPriceWithTax.getDouble("totalTax").intValue());

            // billing_total = product_price_excluding_tax + tax_total + cash_on_delivery_fee + other_fee +
            // delivery_total
            order.setBilling_total(orderPriceWithTax.getInteger("billingTotal"));

            orderList.add(order);
        }

        return orderList;
    }

    /**
     * @description 构造订单详情
     * @param ntmCsvOrder 上传的csv行数据
     * @param order 构造的受注Bean
     * @param productList 所有商品list
     * @return: List
     * @date 2021/6/18
     **/
    private List<Tc201_order_detail> constructOrderDetailData(NtmCsvOrder ntmCsvOrder, Tc200_order order,
        List<Mc100_product> allProductList) {

        List<Tc201_order_detail> orderDetailList = Lists.newArrayList();

        // 切分 支店コード 的前缀
        // 如果前缀是5240，添加同捆物9988, 如果前缀是其他，添加同捆物9987
        String branchCode = ntmCsvOrder.getBranchCode();
        List<String> branchCodeList = Splitter.on("-").omitEmptyStrings().trimResults().splitToList(branchCode);
        String branchCodePrefix = branchCodeList.get(0);

        List<Mc107_ntm_product_master> productMasterList = ntmCsvOrder.getNtmProductMasterList();

        int subNo = 1;

        for (int i = 0; i < productMasterList.size(); i++) {
            Mc107_ntm_product_master productMaster = productMasterList.get(i);

            // 将mc107表中查出的商品id合并成 去重、不为空 的 list
            List<String> tmpCsvProductIdList = Lists.newArrayList();
            Optional.ofNullable(productMaster.getProduct1()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct2()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct3()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct4()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct5()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct6()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct7()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct8()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct9()).ifPresent(item -> tmpCsvProductIdList.add(item));
            Optional.ofNullable(productMaster.getProduct10()).ifPresent(item -> tmpCsvProductIdList.add(item));
            List<String> csvProductIdList =
                tmpCsvProductIdList.stream().filter(value -> !value.isEmpty()).distinct().collect(Collectors.toList());
            // 第一次遍历时才追加 同捆物，防止重复
            if (i == 0) {
                if ("5240".equals(branchCodePrefix)) {
                    csvProductIdList.add("9988");
                } else {
                    csvProductIdList.add("9987");
                }
            }

            // 根据这条csv数据，筛选出这条csv数据对应的商品list
            List<Mc100_product> csvProductList = allProductList.stream()
                .map(allproduct -> csvProductIdList.stream()
                    .filter(csvProductId -> Objects.equals(allproduct.getCode(), csvProductId)).findFirst()
                    .map(csvProductId -> {
                        return allproduct;
                    }).orElse(null))
                .filter(Objects::nonNull).distinct().collect(Collectors.toList());

            if (null != csvProductList && csvProductList.size() > 0) {
                for (int j = 0; j < csvProductList.size(); j++) {
                    Mc100_product tmpProduct = csvProductList.get(j);
                    Tc201_order_detail bean = new Tc201_order_detail();

                    // orderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
                    bean.setOrder_detail_no(order.getPurchase_order_no() + "-" + String.format("%03d", subNo++));
                    bean.setPurchase_order_no(order.getPurchase_order_no());
                    bean.setProduct_id(tmpProduct.getProduct_id());
                    bean.setProduct_code(tmpProduct.getCode());
                    bean.setProduct_name(tmpProduct.getName());
                    bean.setUnit_price(tmpProduct.getPrice());
                    // 个人的受注的商品数，统一为1个
                    bean.setNumber(1);
                    bean.setProduct_total_price(tmpProduct.getPrice());
                    bean.setDel_flg(0);
                    bean.setIs_reduced_tax(tmpProduct.getIs_reduced_tax());
                    bean.setTax_flag(tmpProduct.getTax_flag());

                    // 如果是同捆物则添加标识
                    if (null != tmpProduct.getCode()
                        && ("9988".equals(tmpProduct.getCode()) || "9987".equals(tmpProduct.getCode()))) {
                        bean.setBundled_flg(1);
                    } else {
                        bean.setBundled_flg(0);
                    }

                    orderDetailList.add(bean);
                }
            }
        }

        return orderDetailList;
    }

    /**
     * @description 受注番号
     * @param num 序号
     * @return: String
     * @date 2021/6/18
     **/
    private String getOrderNo(int num) {
        // 受注
        String orderNo = Constants.NTM_ORDER_NO + "-" + new SimpleDateFormat("yyyyMMddHHmmss-").format(new Date())
            + String.format("%05d", num);
        return orderNo;
    }

    /**
     * @description 校验上传的Csv文件
     * @param client_id 店铺ID
     * @param file 上传的csv文件
     * @param encoding csv文件编码格式
     * @param totalErrList エラー情報を格納するリスト
     * @return: JSONObject
     * @date 2021/6/17
     **/
    private JSONObject checkCsvFieldFormat(String client_id, MultipartFile file, String encoding,
        List<String> totalErrList, List<List<String>> errorFileData, List<String> filterNtmOrderSerialNum) {

        // 错误字段
        List<String> errField = Lists.newArrayList();
        // CSV转为JavaBean
        List<NtmCsvOrder> ntmCsvOrderList = Lists.newArrayList();
        // 必须项错误
        Map<Integer, List<String>> requiredItemErr = Maps.newHashMap();
        Map<Integer, String> lineSerial = Maps.newHashMap();

        if (!file.isEmpty()) {

            try {
                // Fileストリーム
                InputStream stream = file.getInputStream();
                // Fileをロード
                Reader reader = new InputStreamReader(stream, encoding);

                CsvReader csvReader = new CsvReader(reader);

                // 校验Csv表头
                int con = 0; // 进行header匹配的次数
                while (csvReader.readRecord()) {
                    con++;
                    // tmp为上传实际header
                    String tmp = csvReader.getRawRecord();
                    // 校验标题行
                    if (con == 1) {
                        List<String> snapData = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tmp);
                        ArrayList<String> headers = new ArrayList<>();
                        snapData.forEach(data -> {
                            if (!StringTools.isNullOrEmpty(data) && data.startsWith("\"") && data.endsWith("\"")) {
                                headers.add(data.substring(1, data.length() - 1));
                            } else {
                                headers.add(data);
                            }
                        });
                        tmp = Joiner.on(",").join(headers);

                        // 校验header是否合法
                        if (!StringTools.isNullOrEmpty(tmp)) {
                            String standardHeader =
                                "通し番号,支店コード,カタログ1,カタログ2,カタログ3,カタログ4,カタログ5,カタログ6,カタログ7,カタログ8,カタログ9,カタログ10,姓名,郵便番号,都道府県,住所市区町村名,住所町名番地,住所ビル名,TEL";
                            if (!standardHeader.equals(tmp)) {
                                totalErrList.add(
                                    "1行目：必須項目にご入力ください：通し番号、支店コード、カタログ1、カタログ2、カタログ3、カタログ4、カタログ5、カタログ6、カタログ7、カタログ8、カタログ9、カタログ10、姓名、郵便番号、都道府県、住所市区町村名、住所町名番地、住所ビル名、TEL");
                                errorFileData.add(Lists.newArrayList(
                                    "", "", "",
                                    "1行目：必須項目にご入力ください：通し番号、支店コード、カタログ1、カタログ2、カタログ3、カタログ4、カタログ5、カタログ6、カタログ7、カタログ8、カタログ9、カタログ10、姓名、郵便番号、都道府県、住所市区町村名、住所町名番地、住所ビル名、TEL"));
                            }
                        }
                    }
                }

                // 校验csv->bean转换
                try {
                    InputStream newStream = file.getInputStream();
                    Reader isr = new InputStreamReader(newStream, encoding);
                    ntmCsvOrderList = csv2List(isr);
                    isr.close();
                    // 为null时抛出异常，即csv->bean转换失败
                    ntmCsvOrderList.size();
                } catch (Exception e) {
                    logger.error("店舗ID = {},取込CSVの形式不正", client_id);
                    totalErrList.add("csvの形式不正です。");
                    errorFileData.add(Lists.newArrayList(
                        "", "", "", "csvの形式不正です。"));
                }

                // 校验必需项是否缺失
                Optional.ofNullable(ntmCsvOrderList).ifPresent(
                    ntmCsvOrders -> {
                        for (int i = 0; i < ntmCsvOrders.size(); i++) {
                            NtmCsvOrder item = ntmCsvOrders.get(i);
                            List<String> tmpItemErr = Lists.newArrayList();

                            if (Strings.isNullOrEmpty(item.getSerialNum())) {
                                tmpItemErr.add("[通し番号]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            if (Strings.isNullOrEmpty(item.getBranchCode())) {
                                tmpItemErr.add("[支店コード]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            if (Strings.isNullOrEmpty(item.getName())) {
                                tmpItemErr.add("[姓名]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            if (Strings.isNullOrEmpty(item.getYubin())) {
                                tmpItemErr.add("[郵便番号]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            if (Strings.isNullOrEmpty(item.getPrefectures())) {
                                tmpItemErr.add("[都道府県]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            if (Strings.isNullOrEmpty(item.getAddressCity())) {
                                tmpItemErr.add("[住所市区町村名]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            if (Strings.isNullOrEmpty(item.getAddressTown())) {
                                tmpItemErr.add("[住所町名番地]");
                                filterNtmOrderSerialNum.add(item.getSerialNum());
                            }
                            requiredItemErr.put(i + 1, tmpItemErr);
                            if (Strings.isNullOrEmpty(item.getSerialNum())) {
                                lineSerial.put(i + 1, "-");
                            } else {
                                lineSerial.put(i + 1, item.getSerialNum());
                            }
                        }
                    });
                for (Map.Entry<Integer, List<String>> entry : requiredItemErr.entrySet()) {
                    Integer row = entry.getKey();
                    List<String> tmpItem = entry.getValue();
                    if (null != tmpItem && tmpItem.size() > 0) {
                        String tmpItemStr = Joiner.on("、").join(tmpItem);
                        totalErrList.add(row + "行目 " + tmpItemStr + " 必須項目にご入力ください");
                        errorFileData.add(Lists.newArrayList(
                            lineSerial.get(row), "", "", tmpItemStr + " 必須項目にご入力ください"));
                    }
                }

                // 外部受注番号重复校验
                if (null != ntmCsvOrderList) {
                    List<String> csvAllSerialNumList =
                        ntmCsvOrderList.stream().map(NtmCsvOrder::getSerialNum).collect(Collectors.toList());
                    List<String> repeatedSerialNumList = csvAllSerialNumList.stream().collect(Collectors
                        .collectingAndThen(Collectors.groupingBy(Function.identity(), Collectors.counting()), map -> {
                            map.values().removeIf(size -> size == 1);
                            List<String> tempList = Lists.newArrayList(map.keySet());
                            return tempList;
                        }));
                    if (null != repeatedSerialNumList && repeatedSerialNumList.size() > 0) {
                        totalErrList.add("ファイル内の「通し番号」が重複しています");
                        errorFileData.add(Lists.newArrayList(
                            "", "", "", "ファイル内の「通し番号」が重複しています"));
                    }
                    ntmCsvOrderList.stream().forEach(v -> {
                        if (repeatedSerialNumList.contains(v.getSerialNum())) {
                            filterNtmOrderSerialNum.add(v.getSerialNum());
                        }
                    });
                }


            } catch (Exception e) {
                // e.printStackTrace();
                logger.error("店舗ID = {},取込CSV解析错误", client_id);
            }

        } else {
            totalErrList.add("[ERROR]CSVファイルが空です。");
            errorFileData.add(Lists.newArrayList(
                "", "", "", "[ERROR]CSVファイルが空です。"));
        }

        JSONObject checkRes = new JSONObject();
        checkRes.put("ntmCsvOrderList", ntmCsvOrderList);
        checkRes.put("totalErrList", totalErrList);

        return checkRes;
    }

    /**
     * @description CSV字段转换 及 有效性校验
     * @param null:
     * @return: JSONObject
     * @date 2021/6/21
     **/
    private JSONObject checkCsvFieldConvertAndValidity(String client_id, List<NtmCsvOrder> ntmCsvOrderList,
        List<String> totalErrList, List<List<String>> errorFileData, List<String> filterNtmOrderSerialNum) {

        List<Mc109_ntm_yubin_change_history> yubinChangeHistoryList = getAllYubinChangeHistory();

        // 邮编转换
        Optional.ofNullable(ntmCsvOrderList).ifPresent(
            ntmCsvOrders -> {
                ntmCsvOrders.stream().filter(Objects::nonNull).forEach(
                    item -> {
                        // 根据当前csv的邮编，过滤出Mc109记录（有记录则说明需要转换，没有则不转换）
                        Mc109_ntm_yubin_change_history change = yubinChangeHistoryList.stream()
                            .filter(
                                yubinChangeHistory -> Objects.equals(item.getYubin(), yubinChangeHistory.getBefore()))
                            .findFirst()
                            .map(yubinBean -> {
                                return yubinBean;
                            })
                            .orElse(null);

                        // 邮编替换
                        Optional.ofNullable(change).ifPresent(
                            value -> {
                                item.setYubin(value.getAfter());
                            });
                    });
            });

        // 邮编地址校验
        totalErrList = checkYubinLegal(ntmCsvOrderList, totalErrList, errorFileData, filterNtmOrderSerialNum);

        // 去除特殊字符（' "）
        ntmCsvOrderList = specialCharacterHandling(ntmCsvOrderList);

        // csv不可重复导入校验
        JSONObject repeatJson =
            repeatImportVerification(client_id, ntmCsvOrderList, errorFileData, filterNtmOrderSerialNum);
        ntmCsvOrderList = repeatJson.getObject("noRepeatCsvOrderList", List.class);
        List<String> errRepeatList = repeatJson.getObject("errRepeat", List.class);
        totalErrList.addAll(errRepeatList);

        // 根据csv、mc107构造有效bean
        JSONObject conformOrderJson =
            constructConformOrder(client_id, ntmCsvOrderList, errorFileData, filterNtmOrderSerialNum);
        List<NtmCsvOrder> conformOrderList = conformOrderJson.getObject("conformOrder", List.class);
        List<NtmCsvOrder> emptyProductOrder = conformOrderJson.getObject("emptyProductOrder", List.class);
        if (null != emptyProductOrder && emptyProductOrder.size() > 0) {
            totalErrList.add("翻訳表に登録されていない商品があります。");
        }

        // 校验翻译master表code是否真实存在在mc107表
        if (!checkProductMasterCodeExists(conformOrderList, client_id)) {
            totalErrList.add("翻訳表で指定された商品コードに該当する商品が、商品マスタにありません。");
            errorFileData.add(Lists.newArrayList(
                "", "", "", "翻訳表で指定された商品コードに該当する商品が、商品マスタにありません。"));
        }

        // 校验字段最大长度
        for (int i = 0; i < conformOrderList.size(); i++) {
            NtmCsvOrder bean = conformOrderList.get(i);
            if (bean.getSerialNum().length() > 32) {
                totalErrList.add(i + 2 + "行目[受注番号：" + bean.getSerialNum() + "]： 通し番号は32文字以内でご入力ください。");
                errorFileData.add(Lists.newArrayList(
                    bean.getSerialNum(), "", "", "通し番号は32文字以内でご入力ください。"));
                filterNtmOrderSerialNum.add(bean.getSerialNum());
            }
            if (bean.getBranchCode().length() > 100) {
                totalErrList.add(i + 2 + "行目[受注番号：" + bean.getSerialNum() + "]： 支店コードは100文字以内でご入力ください。");
                errorFileData.add(Lists.newArrayList(
                    bean.getSerialNum(), "", "", "支店コードは100文字以内でご入力ください。"));
                filterNtmOrderSerialNum.add(bean.getSerialNum());
            }
            if (bean.getYubin().length() > 20) {
                totalErrList.add(i + 2 + "行目[受注番号：" + bean.getSerialNum() + "]： 郵便番号は20文字以内でご入力ください。");
                errorFileData.add(Lists.newArrayList(
                    bean.getSerialNum(), "", "", "郵便番号は20文字以内でご入力ください。"));
                filterNtmOrderSerialNum.add(bean.getSerialNum());
            }
            if (bean.getTel().length() > 60) {
                totalErrList.add(i + 2 + "行目[受注番号：" + bean.getSerialNum() + "]： TELは60文字以内でご入力ください。");
                errorFileData.add(Lists.newArrayList(
                    bean.getSerialNum(), "", "", "TELは60文字以内でご入力ください。"));
                filterNtmOrderSerialNum.add(bean.getSerialNum());
            }
            if (bean.getName().length() > 50) {
                totalErrList.add(i + 2 + "行目[受注番号：" + bean.getSerialNum() + "]： 姓名は50文字以内でご入力ください。");
                errorFileData.add(Lists.newArrayList(
                    bean.getSerialNum(), "", "", " 姓名は50文字以内でご入力ください。"));
                filterNtmOrderSerialNum.add(bean.getSerialNum());
            }
        }

        JSONObject checkRes = new JSONObject();
        checkRes.put("totalErrList", totalErrList);
        checkRes.put("conformOrderList", conformOrderList);
        checkRes.put("emptyProductOrder", emptyProductOrder);

        return checkRes;
    }

    /**
     * @description 校验翻译master表中的code是否存在（在Mc107表是否真实存在）
     * @param conformOrderList 有效order数据
     * @param clientId 店铺ID
     * @return boolean true: 存在， false: 不存在
     * @date 2021/7/16
     **/
    private boolean checkProductMasterCodeExists(List<NtmCsvOrder> conformOrderList, String clientId) {

        List<Mc100_product> productList = Lists.newArrayList();
        List<String> conformCsvProductCodeList = Lists.newArrayList();

        conformCsvProductCodeList = Optional.ofNullable(conformOrderList).map(
            orderList -> orderList.stream().map(conformList -> conformList.getNtmProductMasterList())
                .flatMap(Collection::stream)
                .flatMap(list -> Stream.of(list.getProduct1(), list.getProduct2(), list.getProduct3(),
                    list.getProduct4(), list.getProduct5(), list.getProduct6(), list.getProduct7(), list.getProduct8(),
                    list.getProduct9(), list.getProduct10()))
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList())

        ).orElse(Lists.newArrayList());

        productList = productDao.getProductInfoByCodeList((ArrayList<String>) conformCsvProductCodeList, clientId);

        if (productList.size() < conformCsvProductCodeList.size()) {
            List<String> existProductCodeList =
                productList.stream().map(value -> value.getCode()).collect(Collectors.toList());
            List<String> nonExistProductCodeList =
                conformCsvProductCodeList.stream().filter(value -> !existProductCodeList.contains(value))
                    .filter(Objects::nonNull).collect(Collectors.toList());
            logger.info("以下code: {" + Joiner.on(",").join(nonExistProductCodeList) + "}, 在Mc100表中不存在");
            return false;
        }

        return true;
    }

    /**
     * @description 特殊字符处理
     * @param list csv转换的bean list
     * @return: List
     * @date 2021/6/28
     **/
    private List<NtmCsvOrder> specialCharacterHandling(List<NtmCsvOrder> list) {

        List<NtmCsvOrder> csvOrderList = list;

        Optional.ofNullable(list).ifPresent(
            conformOrders -> {
                conformOrders.stream().forEach(
                    item -> {
                        if (null != item.getSerialNum()) {
                            item.setSerialNum(item.getSerialNum().trim().replaceAll("'", ""));
                            item.setSerialNum(item.getSerialNum().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getBranchCode()) {
                            item.setBranchCode(item.getBranchCode().trim().replaceAll("'", ""));
                            item.setBranchCode(item.getBranchCode().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog1()) {
                            item.setCatalog1(item.getCatalog1().trim().replaceAll("'", ""));
                            item.setCatalog1(item.getCatalog1().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog2()) {
                            item.setCatalog2(item.getCatalog2().trim().replaceAll("'", ""));
                            item.setCatalog2(item.getCatalog2().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog3()) {
                            item.setCatalog3(item.getCatalog3().trim().replaceAll("'", ""));
                            item.setCatalog3(item.getCatalog3().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog4()) {
                            item.setCatalog4(item.getCatalog4().trim().replaceAll("'", ""));
                            item.setCatalog4(item.getCatalog4().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog5()) {
                            item.setCatalog5(item.getCatalog5().trim().replaceAll("'", ""));
                            item.setCatalog5(item.getCatalog5().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog6()) {
                            item.setCatalog6(item.getCatalog6().trim().replaceAll("'", ""));
                            item.setCatalog6(item.getCatalog6().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog7()) {
                            item.setCatalog7(item.getCatalog7().trim().replaceAll("'", ""));
                            item.setCatalog7(item.getCatalog7().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog8()) {
                            item.setCatalog8(item.getCatalog8().trim().replaceAll("'", ""));
                            item.setCatalog8(item.getCatalog8().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog9()) {
                            item.setCatalog9(item.getCatalog9().trim().replaceAll("'", ""));
                            item.setCatalog9(item.getCatalog9().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getCatalog10()) {
                            item.setCatalog10(item.getCatalog10().trim().replaceAll("'", ""));
                            item.setCatalog10(item.getCatalog10().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getName()) {
                            item.setName(item.getName().trim().replaceAll("'", ""));
                            item.setName(item.getName().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getYubin()) {
                            item.setYubin(item.getYubin().trim().replaceAll("'", ""));
                            item.setYubin(item.getYubin().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getPrefectures()) {
                            item.setPrefectures(item.getPrefectures().trim().replaceAll("'", ""));
                            item.setPrefectures(item.getPrefectures().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getAddressCity()) {
                            item.setAddressCity(item.getAddressCity().trim().replaceAll("'", ""));
                            item.setAddressCity(item.getAddressCity().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getAddressTown()) {
                            item.setAddressTown(item.getAddressTown().trim().replaceAll("'", ""));
                            item.setAddressTown(item.getAddressTown().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getAddressBuilding()) {
                            item.setAddressBuilding(item.getAddressBuilding().trim().replaceAll("'", ""));
                            item.setAddressBuilding(item.getAddressBuilding().trim().replaceAll("\"", ""));
                        }
                        if (null != item.getTel()) {
                            item.setTel(item.getTel().trim().replaceAll("'", ""));
                            item.setTel(item.getTel().trim().replaceAll("\"", ""));
                        }
                    });
            });
        return csvOrderList;
    }

    /**
     * @description csv导入重复校验
     * @param list csv转换的bean list
     * @return: JSONObject
     * @date 2021/6/28
     **/
    private JSONObject repeatImportVerification(String clientId, List<NtmCsvOrder> list,
        List<List<String>> errorFileData, List<String> filterNtmOrderSerialNum) {
        Objects.requireNonNull(list);

        List<String> errRepeat = Lists.newArrayList();
        List<NtmCsvOrder> noRepeatCsvOrderList = list;

        List<String> orderNoList =
            noRepeatCsvOrderList.stream().map(NtmCsvOrder::getSerialNum).collect(Collectors.toList());
        List<String> existOuterOrderNo = orderDao.getOuterOrderNoListBySpecificNo(clientId, orderNoList);

        for (int i = 0; i < noRepeatCsvOrderList.size(); i++) {
            NtmCsvOrder ntmCsvOrder = noRepeatCsvOrderList.get(i);
            boolean existOrder =
                existOuterOrderNo.stream().filter(no -> no.equals(ntmCsvOrder.getSerialNum())).findAny().isPresent();
            if (existOrder) {
                errRepeat.add(i + 2 + "行目[受注番号：" + ntmCsvOrder.getSerialNum() + "]： すでに登録済みの受注番号ですのでご確認ください");
                errorFileData.add(Lists.newArrayList(
                    ntmCsvOrder.getSerialNum(), "", "", "すでに登録済みの受注番号ですのでご確認ください"));
                filterNtmOrderSerialNum.add(ntmCsvOrder.getSerialNum());
            }
        }

        JSONObject res = new JSONObject();
        res.put("errRepeat", errRepeat);
        res.put("noRepeatCsvOrderList", noRepeatCsvOrderList);
        return res;
    }

    /**
     * @description 根据符合条件的csv数据的商品code，查询商品list
     * @param clientId 店铺ID
     * @param conformOrderList 根据符合条件的csv数据list
     * @return List
     * @date 2021/6/17
     **/
    private List<Mc100_product> getProductListByConformOrder(String clientId, List<NtmCsvOrder> conformOrderList) {

        List<Mc100_product> productList = Lists.newArrayList();
        // 上传的受注list的所有商品code
        List<String> productCodeList = Lists.newArrayList();

        productCodeList = conformOrderList.stream()
            .map(conformList -> conformList.getNtmProductMasterList())
            .flatMap(Collection::stream)
            .flatMap(list -> Stream.of(list.getProduct1(), list.getProduct2(), list.getProduct3(), list.getProduct4(),
                list.getProduct5(), list.getProduct6(), list.getProduct7(), list.getProduct8(), list.getProduct9(),
                list.getProduct10()))
            .filter(Objects::nonNull)
            .distinct()
            .collect(Collectors.toList());

        // 追加 同捆物ID
        productCodeList.add("9988");
        productCodeList.add("9987");

        productList = productDao.getProductInfoByCodeList((ArrayList<String>) productCodeList, clientId);
        return productList;
    }

    /**
     * @description 通过Mc107表确认csv中的有效数据
     * @param clientId 店铺ID
     * @param ntmCsvOrderList csv中读取的bean list
     * @return JSONObject
     * @date 2021/6/17
     **/
    private JSONObject constructConformOrder(String clientId, List<NtmCsvOrder> ntmCsvOrderList,
        List<List<String>> errorFileData, List<String> filterNtmOrderSerialNum) {
        // 有效的受注List
        List<NtmCsvOrder> conformOrder = Lists.newArrayList();
        // 空商品的受注List
        List<NtmCsvOrder> emptyProductOrder = Lists.newArrayList();

        Ms012_sponsor_master sponsorMaster = getSponsorMasterByClientId(clientId);
        Mc105_product_setting productSetting = productSettingService.getProductSetting(clientId, null);

        Optional.ofNullable(ntmCsvOrderList).ifPresent(
            ntmCsvOrders -> {
                ntmCsvOrders.stream().filter(Objects::nonNull).forEach(
                    item -> {

                        List<String> nameList = Lists.newArrayList();
                        nameList.add(item.getCatalog1());
                        nameList.add(item.getCatalog2());
                        nameList.add(item.getCatalog3());
                        nameList.add(item.getCatalog4());
                        nameList.add(item.getCatalog5());
                        nameList.add(item.getCatalog6());
                        nameList.add(item.getCatalog7());
                        nameList.add(item.getCatalog8());
                        nameList.add(item.getCatalog9());
                        nameList.add(item.getCatalog10());

                        nameList = nameList.stream().filter(value -> !Strings.isNullOrEmpty(value))
                            .collect(Collectors.toList());
                        LocalDate date = LocalDate.now();
                        List<Mc107_ntm_product_master> mc107List =
                            ntmProductMasterDao.getNtmProductMasterListByName(nameList, date.toString());

                        if (null != mc107List && mc107List.size() > 0 && nameList.size() <= mc107List.size()) {
                            NtmCsvOrder bean = item;
                            bean.setNtmProductMasterList(mc107List);
                            // 依赖主信息设置
                            bean.setSponsorMaster(sponsorMaster);
                            // 店铺商品设置
                            bean.setProductSetting(productSetting);

                            // 地区master信息设置
                            Mc108_ntm_area_master areaMaster =
                                ntmAreaMasterDao.getNtmAreaMasterByCode(bean.getBranchCode());
                            bean.setNtmAreaMaster(areaMaster);

                            conformOrder.add(bean);
                        } else {
                            emptyProductOrder.add(item);
                            logger.info("受注订单：" + item.getSerialNum() + " Mc107中不存在以下商品 "
                                + Joiner.on(",").join(nameList) + "的商品");
                            filterNtmOrderSerialNum.add(item.getSerialNum());

                            List<String> existNameList =
                                mc107List.stream().map(Mc107_ntm_product_master::getName).collect(Collectors.toList());
                            nameList.stream().forEach(v1 -> {
                                if (!existNameList.contains(v1)) {
                                    errorFileData.add(Lists.newArrayList(
                                        item.getSerialNum(), item.getBranchCode(), v1, "翻訳表に登録されていない商品があります。"));
                                }
                            });
                        }

                    });
            });

        JSONObject res = new JSONObject();
        res.put("conformOrder", conformOrder);
        res.put("emptyProductOrder", emptyProductOrder);

        return res;
    }

    /**
     * @description 检查邮编和地址是否合法
     * @param list csv bean数据
     * @return List
     * @date 2021/6/17
     **/
    private List<String> checkYubinLegal(List<NtmCsvOrder> list, List<String> totalErrList,
        List<List<String>> errorFileData, List<String> filterNtmOrderSerialNum) {

        String url = bizApiHost + Constants.BIZ_CHECKADDRESS;
        List<CheckAddressRequest> requestList = initCheckAddressRequest(list);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();

        for (int i = 0; i < requestList.size(); i++) {
            String requestValue = "";
            try {
                requestValue = XmlUtil.bean2Xml(builder, requestList.get(i));
                logger.info("CheckAddress Request Param: " + requestValue);
                HashMap<String, String> params = Maps.newHashMap();
                params.put("value", requestValue);
                JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
                String responseValue = responseBody.getString("value");
                logger.info("CheckAddress Response Body: " + responseValue);
                CheckAddressResponse responseBean =
                    (CheckAddressResponse) XmlUtil.xml2Bean(builder, responseValue, CheckAddressResponse.class);

                if (!BizLogiResEnum.S0_0001.getCode().equals(responseBean.getResultCode())) {
                    totalErrList.add((i + 2) + "行目" + "[郵便番号]、[住所情報] 検証に失敗しました");
                    errorFileData.add(Lists.newArrayList(
                        list.get(i).getSerialNum(), "", "", "[郵便番号]、[住所情報] 検証に失敗しました。"));
                    filterNtmOrderSerialNum.add(list.get(i).getSerialNum());
                }

            } catch (JsonProcessingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return totalErrList;
    }

    /**
     * @description 构造请求参数
     * @param ntmCsvOrderList csv bean数据
     * @return List
     * @date 2021/6/17
     **/
    private List<CheckAddressRequest> initCheckAddressRequest(List<NtmCsvOrder> ntmCsvOrderList) {
        List<CheckAddressRequest> requestList = Lists.newArrayList();

        Optional.ofNullable(ntmCsvOrderList).ifPresent(
            ntmCsvOrders -> {
                ntmCsvOrders.stream().filter(Objects::nonNull).forEach(
                    item -> {
                        CheckAddressRequest request = new CheckAddressRequest();

                        CheckAddressRequest.CustomerAuth customerAuth = request.new CustomerAuth();
                        customerAuth.setCustomerId(bizAuthCustomId);
                        customerAuth.setLoginPassword(bizAuthCustomPwd);
                        request.setCustomerAuth(customerAuth);

                        String address = item.getPrefectures() + item.getAddressCity() + item.getAddressTown()
                            + item.getAddressBuilding();
                        // String yubin = item.getYubin().replace("-", "");
                        String yubin =
                            Optional.ofNullable(item.getYubin()).map(value -> value.replace("-", "")).orElse("");
                        request.setRequestYubin(yubin);
                        request.setRequestAddress(address);

                        requestList.add(request);
                    });
            });
        return requestList;
    }

    /**
     * @description csv字段映射到java bean
     * @param reader csv文件流
     * @return: List
     * @date 2021/6/16
     **/
    public List<NtmCsvOrder> csv2List(Reader reader) {

        try {
            HeaderColumnNameMappingStrategy<NtmCsvOrder> strategy = new HeaderColumnNameMappingStrategy<>();
            strategy.setType(NtmCsvOrder.class);

            CsvToBean<NtmCsvOrder> csvToBean = new CsvToBeanBuilder<NtmCsvOrder>(reader)
                .withMappingStrategy(strategy)
                .build();
            List<NtmCsvOrder> items = csvToBean.parse();

            return items;

        } catch (RuntimeException var1) {
            // logger.error(JsonException.getTrace(var1));
            return null;
        } catch (Exception e) {
            // logger.error(JsonException.getTrace(e));
            return null;
        }
    }

    /**
     * @description 获取最大受注履历ID
     * @return: Integer
     * @date 2021/6/16
     **/
    private Integer getMaxHistoryId() {
        String lastOrderHistoryNo = orderHistoryDao.getLastOrderHistoryNo();
        Integer lastHistoryId = 0;
        if (!StringTools.isNullOrEmpty(lastOrderHistoryNo)) {
            lastHistoryId = Integer.valueOf(lastOrderHistoryNo);
        }
        String historyId = String.valueOf(lastHistoryId + 1);
        return Integer.parseInt(historyId);
    }

    /**
     * @description 获取依赖主信息
     * @param clientId 店铺ID
     * @return: Ms012_sponsor_master
     * @date 2021/6/18
     **/
    private Ms012_sponsor_master getSponsorMasterByClientId(String clientId) {
        Ms012_sponsor_master sponsorDefaultInfo = settingDao.getSponsorDefaultInfo(clientId, 1);
        return sponsorDefaultInfo;
    }

    /**
     * @description 获取所有 郵便番号変更履歴
     * @return: List
     * @date 2021/6/21
     **/
    private List<Mc109_ntm_yubin_change_history> getAllYubinChangeHistory() {
        List<Mc109_ntm_yubin_change_history> allYubinChangeHistoryList =
            ntmYubinChangeHistoryDao.getAllYubinChangeHistory();
        return allYubinChangeHistoryList;
    }

    /**
     * @description 计算受注订单金额
     * @param order 受注订单
     * @param productSetting 设置信息
     * @return: JSONObject
     * @date 2021/6/22
     **/
    private JSONObject getOrderPriceWithTax(Tc200_order order, Mc105_product_setting productSetting) {
        if (null == order || null == productSetting) {
            return null;
        }

        List<Integer> priceList = Lists.newArrayList();
        List<Double> taxList = Lists.newArrayList();

        List<Tc201_order_detail> orderDetail = order.getTc201_order_detail_list();

        Optional.ofNullable(order).map(Tc200_order::getTc201_order_detail_list).ifPresent(
            details -> {
                details.stream().filter(Objects::nonNull).forEach(
                    detail -> {

                        Integer price = 0;
                        double tax = 0;
                        if (productSetting.getTax() == 1) { // 税抜
                            switch (productSetting.getAccordion()) {
                                case 1:// 8%
                                    price = detail.getUnit_price() * 8;
                                    tax = getTax(detail.getUnit_price(), 8);
                                    break;
                                case 0:// 10%
                                    price = detail.getUnit_price() * 10;
                                    tax = getTax(detail.getUnit_price(), 10);
                                    break;
                            }
                        } else { // 税入
                            price = (int) detail.getUnit_price();
                            switch (productSetting.getAccordion()) {
                                case 1:// 8%
                                    tax = getTax(detail.getUnit_price(), 8);
                                    break;
                                case 0:// 10%
                                    tax = getTax(detail.getUnit_price(), 10);
                                    break;
                            }
                        }

                        priceList.add(price);
                        taxList.add(tax);
                    });
            });

        Integer totalPrice = priceList.stream().reduce(Integer::sum).orElse(0);
        Double totalTax = taxList.stream().reduce(Double::sum).orElse(Double.valueOf(0));

        Integer billingTotal = totalPrice;

        JSONObject price = new JSONObject();
        price.put("totalPrice", totalPrice);
        price.put("totalTax", totalTax);
        price.put("billingTotal", billingTotal);
        return price;
    }

    /**
     * @description 根据商品master计算受注订单金额
     * @param order 受注订单
     * @return: JSONObject
     * @date 2021/6/22
     **/
    private JSONObject getOrderPriceWithTax(Tc200_order order) {
        List<Integer> priceList = Lists.newArrayList();
        List<Double> taxList = Lists.newArrayList();

        List<Tc201_order_detail> orderDetail = order.getTc201_order_detail_list();

        Optional.ofNullable(order).map(Tc200_order::getTc201_order_detail_list).ifPresent(
            details -> {
                details.stream().filter(Objects::nonNull).forEach(
                    detail -> {

                        Integer price = 0;
                        double tax = 0;
                        if (detail.getTax_flag() == 1) { // 税抜
                            switch (detail.getIs_reduced_tax()) {
                                case 1:// 8%
                                    price = detail.getUnit_price() * 8;
                                    tax = getTax(detail.getUnit_price(), 8);
                                    break;
                                case 0:// 10%
                                    price = detail.getUnit_price() * 10;
                                    tax = getTax(detail.getUnit_price(), 10);
                                    break;
                            }
                        } else if (detail.getTax_flag() == 0) { // 税入
                            price = (int) detail.getUnit_price();
                            switch (detail.getIs_reduced_tax()) {
                                case 1:// 8%
                                    tax = getTax(detail.getUnit_price(), 8);
                                    break;
                                case 0:// 10%
                                    tax = getTax(detail.getUnit_price(), 10);
                                    break;
                            }
                        } else { // 非課税
                            price = (int) detail.getUnit_price();
                            tax = 0;
                        }

                        priceList.add(price);
                        taxList.add(tax);
                    });
            });

        Integer totalPrice = priceList.stream().reduce(Integer::sum).orElse(0);
        Double totalTax = taxList.stream().reduce(Double::sum).orElse(Double.valueOf(0));

        Integer billingTotal = totalPrice;

        JSONObject price = new JSONObject();
        price.put("totalPrice", totalPrice);
        price.put("totalTax", totalTax);
        price.put("billingTotal", billingTotal);
        return price;
    }

    /**
     * @param tax : 消费税
     * @param reduced : 消费税率
     * @description: 计算消费税金额
     * @return: double
     */
    private static double getTax(int tax, int reduced) {
        double param = tax * reduced;
        return param / (100 + reduced);
    }

    /**
     * @description 获取法人数据
     * @param clientId 店铺id
     * @return: HashMap
     * @date 2021/5/27
     **/
    private HashMap<String, Integer> getEnterpriseData(String clientId) {
        return ntmShipmentDao.getNtmTopEnterpriseCntByStatus(clientId,
            Constants.CONFIRMATION_WAIT,
            Constants.ENTRANCE_WAITING,
            Constants.DELIVERY_WAIT,
            Constants.INRESERVE,
            Constants.DURINGWORK,
            Constants.SHIPPED);
    }

    /**
     * @description 获取个人数据
     * @param clientId 店铺id
     * @return: HashMap
     * @date 2021/5/27
     **/
    private HashMap<String, Integer> getPersonData(String clientId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyy-MM-dd");
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(calendar.DATE, 1);
        String todayStartDate = sdf.format(new Date());
        String todayEndDate = sdf.format(calendar.getTime());
        String tomorrowStartDate = todayEndDate;

        calendar.setTime(new Date());
        calendar.add(calendar.DATE, 2);
        String tomorrowEndDate = sdf.format(calendar.getTime());

        List<Integer> statusList = new ArrayList();
        statusList.add(Constants.SHIPPED);
        statusList.add(Constants.DERINGINSPECTION); // 検品中
        statusList.add(Constants.INSPECTED); // 検品済み

        return ntmShipmentDao.getNtmTopPersonCntByStatus(clientId, todayStartDate, todayEndDate, tomorrowStartDate,
            tomorrowEndDate, statusList);
    }
}
