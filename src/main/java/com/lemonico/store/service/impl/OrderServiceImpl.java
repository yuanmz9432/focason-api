package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.beust.jcommander.internal.Lists;
import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Maps;
import com.google.common.primitives.Ints;
import com.lemonico.api.APICommonUtils;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.ProductSettingService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.BizLogiResEnum;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.ntm.bean.CheckAddressRequest;
import com.lemonico.ntm.bean.CheckAddressResponse;
import com.lemonico.product.service.ProductService;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.*;
import com.lemonico.wms.service.ShipmentService;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.*;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * 受注依頼管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class OrderServiceImpl implements OrderService
{
    private final static Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);
    private final ShipmentService shipmentService;
    private final SettingService settingService;
    private final ShipmentsService shipmentsService;
    private final ShipmentDetailService shipmentDetailService;
    private final ProductService productService;
    private final OrderApiService apiService;
    private final MacroSettingServiceImpl macroSettingService;
    private final ProductSettingService productSettingService;
    private final OrderDao orderDao;
    private final OrderDetailDao orderDetailDao;
    private final OrderHistoryDao orderHistoryDao;
    private final ClientDao clientDao;
    private final StockDao stockDao;
    private final ProductDao productDao;
    private final DeliveryTakesDaysDao deliveryTakesDaysDao;
    private final ShipmentsDao shipmentsDao;
    private final ShipmentDetailDao shipmentDetailDao;
    private final SettingDao settingDao;
    private final DeliveryDao deliveryDao;
    private final OrderApiDao orderApiDao;
    private final OrderErrorDao orderErrorDao;
    private final OrderCancelDao orderCancelDao;
    private final SponsorDao sponsorDao;
    private final APICommonUtils apiCommonUtils;
    private final PathProps pathProps;

    @Value("${bizApiHost}")
    private String bizApiHost;
    @Value("${bizAuthCustomId}")
    private String bizAuthCustomId;
    @Value("${bizAuthCustomPwd}")
    private String bizAuthCustomPwd;
    /**
     * 二重引用符からカンマを外すルール
     */
    private final static String REMOVE_COMMA_IN_DOUBLE_QUOTE_RULE = "(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)";
    /**
     * 日本の都道府県
     */
    private final static String[] PREFECTURES = {
        "北海道",
        "青森県",
        "岩手県",
        "宮城県",
        "秋田県",
        "山形県",
        "福島県",
        "茨城県",
        "栃木県",
        "群馬県",
        "埼玉県",
        "千葉県",
        "東京都",
        "神奈川県",
        "新潟県",
        "富山県",
        "石川県",
        "福井県",
        "山梨県",
        "長野県",
        "岐阜県",
        "静岡県",
        "愛知県",
        "三重県",
        "滋賀県",
        "京都府",
        "大阪府",
        "兵庫県",
        "奈良県",
        "和歌山県",
        "鳥取県",
        "島根県",
        "岡山県",
        "広島県",
        "山口県",
        "徳島県",
        "香川県",
        "愛媛県",
        "高知県",
        "福岡県",
        "佐賀県",
        "長崎県",
        "熊本県",
        "大分県",
        "宮崎県",
        "鹿児島県",
        "沖縄県"
    };
    /**
     * 異常情報リスト
     */
    List<String> totalErrList = new ArrayList<>();
    /**
     * 異常フィールド
     */
    List<String> errField = new ArrayList<>();
    /**
     * 異常日付
     */
    List<String> errDate = new ArrayList<>();
    /**
     * 異常長さ
     */
    List<String> errLength = new ArrayList<>();
    /**
     * 異常配送会社
     */
    List<String> errDelivery = new ArrayList<>();
    /**
     * 異常配送時間帯
     */
    List<String> errDeliveryTime = new ArrayList<>();
    /**
     * 異常支払方法
     */
    List<String> errPayment = new ArrayList<>();
    /**
     * CSVヘッダー
     */
    HashMap<String, String> csvHeaderMap = new HashMap<>(193);

    /**
     * 最新受注番号を取得
     *
     * @return 最新の受注番号
     * @Param なし
     */
    @Override
    public String getLastPurchaseOrderNo() {
        return orderDao.getLastPurchaseOrderNo();
    }

    /**
     * 新規受注を登録
     *
     * @return Integer
     * @Param なし
     */
    @Override
    public Integer setOrder(Tc200_order tc200_order) throws SQLException {
        try {
            return orderDao.insertOrder(tc200_order);
        } catch (Exception e) {
            e.printStackTrace();
            throw new SQLException("受注管理テーブルへのデータ追加に失敗しました。");
        }
    }

    /**
     * 批量插入受注订单和详情
     *
     * @param list 受注订单list
     * @date 2021/6/22
     **/
    @Override
    public void batchInsertOrderAndOrderDetail(List<Tc200_order> list) throws SQLException {
        orderDao.bulkInsertOrder((ArrayList<Tc200_order>) list);
        List<Tc201_order_detail> tc201List = Lists.newArrayList();

        Optional.ofNullable(list).ifPresent(
            orders -> {
                orders.forEach(
                    order -> {
                        tc201List.addAll(order.getTc201_order_detail_list());
                    });
            });

        if (tc201List.size() > 0) {
            orderDetailDao.bulkInsertOrderDetail((ArrayList<Tc201_order_detail>) tc201List);
        }
    }

    /**
     * 店舗情報の取得
     *
     * @param client_id 店舗ID
     * @return 店舗情報
     * @date 2020/8/19
     */
    @Override
    public JSONObject getStoreInfo(String client_id) {
        List<String> warehouseIdList = orderDao.getWarehouseIdListByClientId(client_id);
        // a根据仓库Id获取仓库信息
        List<Mw400_warehouse> warehouseNameList = orderDao.getWarehouseInfoByWarehouseId(warehouseIdList);
        // a根据店舗ID获取店铺信息
        Ms201_client clientInfo = clientDao.getClientInfo(client_id);
        List<String> templateList = orderDao.getApiTemplates();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("warehouse", warehouseNameList);
        jsonObject.put("client", clientInfo);
        jsonObject.put("templates", templateList);
        // a初期化
        JSONArray jsonArray2 = new JSONArray();
        jsonObject.put("ctemplates", jsonArray2);
        return CommonUtils.success(jsonObject);
    }

    /**
     * 受注CSVアップロード
     *
     * @param file 対象CSVファイル
     * @param client_id 店舗ID
     * @param warehouse_cd 倉庫ID
     * @param status 出荷依頼要否
     * @param request HTTPリクエスト
     * @param template_cd テンプレートコード
     * @param company_id EC店舗ID
     * @param s3_flag S3にアップロード要否
     * @return アップロード結果
     */
    @Override
    public JSONObject importOrderCsv(MultipartFile file,
        String client_id,
        String warehouse_cd,
        String status,
        HttpServletRequest request,
        Integer template_cd,
        String company_id,
        Boolean s3_flag) {

        JSONArray errCsvJson = new JSONArray();
        // 报错受注失败的错误数据key值
        List<Integer> parameterList = new ArrayList<>();

        totalErrList.clear();
        boolean insertFlg = false;
        // 保存未登录商品的Id
        ArrayList<Integer> unlistedProductIdList = new ArrayList<>();

        // 保存商品没有登录的受注番号
        ArrayList<String> noProductOrderList = new ArrayList<>();

        // 记录不能出库的受注番号 因为商品没有在库
        ArrayList<String> orderNoList = new ArrayList<>();

        // a获取客户模板的模板
        // a获取当前店铺选择的模板并重写csv文件的header
        Tc204_order_template orderTemplate = orderDao.getClientTemplate(client_id, company_id, template_cd).get(0);
        String encoding = orderTemplate.getEncoding();
        String identification = orderTemplate.getIdentification();
        if ("Shift-JIS".equals(encoding)) {
            encoding = "Windows-31J";
        }
        // 获取分隔符
        String splitIdent = ",";
        String delimiter = orderTemplate.getDelimiter();
        if ("タブ".equals(delimiter)) {
            splitIdent = "\t";
        }
        String commaRule = "," + REMOVE_COMMA_IN_DOUBLE_QUOTE_RULE;
        String splitRule = splitIdent + REMOVE_COMMA_IN_DOUBLE_QUOTE_RULE;

        // 保存用户上传的csvheader
        List<String> csvHeaderList = new ArrayList<>();

        // 受注番号list
        ArrayList<String> purchaseOrderNoList = new ArrayList<>();
        try {
            // ファイルが空の場合、処理せず
            if (!file.isEmpty()) {
                // Fileストリーム
                InputStream stream = file.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(stream);
                // 验证编码格式
                // if (!CommonUtil.determineEncoding(bufferedInputStream, new String[]{encoding})){
                // JSONObject resultJson = new JSONObject();
                // totalErrList.add("ご指定のCSVファイルが、取り扱いできる形式（SHIFT-JIS）ではありません。");
                // // 错误信息去重 并排序
                // List<String> collect = totalErrList.stream().distinct().sorted().collect(Collectors.toList());
                // // 给前端返回错误信息集合
                // resultJson.put("errlist", collect);
                // resultJson.put("unlistedProductId", noProductOrderList);
                // return CommonUtil.successJson(resultJson);
                // }
                // Fileをロード
                Reader reader = new InputStreamReader(bufferedInputStream, encoding);
                // a文件名加上时间戳防止重名
                String filePath = pathProps.getOrder() + "/" + client_id + "_" + System.currentTimeMillis() + ".csv";
                // a输出的CSV文件
                File outFile = new File(filePath);

                BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
                CsvWriter csvWriter = new CsvWriter(writer, ',');

                // a获取当前店铺选择的模板并重写csv文件的header
                String tempalte = orderTemplate.getData();

                // 将客户设定绑定的值 转为map形式 key：原版header value：模板header
                List<String> headerList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(tempalte);
                for (String value : headerList) {
                    String[] segmentation = value.split("=");
                    csvHeaderMap.put(segmentation[0], segmentation[1]);
                }

                String constant = orderTemplate.getConstant();
                CsvReader csvReader = new CsvReader(reader);

                int num = 0;
                // a固定值索引集合
                // List<Integer> list = new ArrayList<Integer>();
                Map<String, Integer> map = new HashMap<String, Integer>();
                int length = 0;
                // 进行header匹配的次数
                int checkTemp = 0;
                int con = 0;
                // 获取DB中保存的模板title
                String template = orderTemplate.getTemplate();
                String[] params = null;
                int headerCount = 0;

                csvHeaderList =
                    Splitter.on(splitIdent).trimResults().omitEmptyStrings().splitToList(template.replaceAll("\"", ""));
                while (csvReader.readRecord()) {
                    // System.err.println("第" + num + "次");
                    con++;
                    // tmp为上传实际header
                    // sunlogi为公司模板数据库中的字段
                    // tempalte为客户自定义模板用"="相连
                    String tmp = csvReader.getRawRecord();
                    // 验证模板是否匹配
                    if (con == 1) {
                        List<String> snapData =
                            Splitter.on(splitIdent).omitEmptyStrings().trimResults().splitToList(tmp);
                        ArrayList<String> headers = new ArrayList<>();
                        snapData.forEach(data -> {
                            if (!StringTools.isNullOrEmpty(data) && data.startsWith("\"") && data.endsWith("\"")) {
                                headers.add(data.substring(1, data.length() - 1));
                            } else {
                                headers.add(data);
                            }
                        });
                        tmp = Joiner.on(splitIdent).join(headers);

                        if (!StringTools.isNullOrEmpty(tmp)) {
                            if (!tmp.equals(template)) {
                                totalErrList.add("タイトル行がご指定のCSVテンプレートと異なりますのでご確認ください");
                                // throw new JsonException(ErrorEnum.E_40152);
                            }
                        }
                    }
                    String[] tmpArr = tmp.split(splitRule, -1);
                    // a判断是否当前行为CSV的header并重写header
                    if (num == 0) {

                        // a解析客户自定义模板数据
                        String[] temp = tempalte.split(commaRule, -1);
                        length = temp.length - 1;
                        // 进行header匹配的次数
                        // a通过模板给临时文件重写header信息
                        params = tmp.split(splitRule, -1);
                        // String[] newHeader = new String[length];
                        List<String> newHeader = new ArrayList<>();
                        for (int i = 0; i < length; i++) {
                            if (StringTools.isNullOrEmpty(temp[i])) {
                                continue;
                            }
                            String[] tempArr = temp[i].split("=");

                            for (String param : params) {
                                // 多对一重写头部
                                if (param.equals(tempArr[0])) {
                                    // newHeader去重写入
                                    if (!newHeader.contains(tempArr[1])) {
                                        // newHeader[p] = tempArr[1];
                                        newHeader.add(tempArr[1]);
                                    }
                                    checkTemp++;
                                    break;
                                }
                            }
                        }
                        tmp = StringUtils.join(newHeader, ",");

                        // a记录需要重写固定值的索引
                        if (!StringTools.isNullOrEmpty(constant)) {
                            String[] value = constant.split(commaRule);
                            for (String s : value) {
                                boolean flag = true;
                                String[] cons = s.split("=");
                                for (String item : temp) {
                                    if (StringTools.isNullOrEmpty(item)) {
                                        break;
                                    }
                                    String[] tempArr = item.split("=");
                                    if (tempArr.length > 0 && cons[0].equals(tempArr[1])) {
                                        flag = false;
                                        break;
                                    }
                                }

                                // 如果当前上传的csv header中没有匹配固定值的字段，则在header后追加
                                if (flag) {
                                    // 重新获取csv的header
                                    tmpArr = tmp.split(splitRule, -1);
                                    // 添加没有匹配的固定值header
                                    List<String> list = new ArrayList<>();
                                    for (String item : tmpArr) {
                                        if (!StringTools.isNullOrEmpty(item)) {
                                            list.add(item);
                                        }
                                    }
                                    list.add(cons[0]);
                                    String[] t = new String[list.size()];
                                    list.toArray(t);
                                    // 重写header
                                    tmp = StringUtils.join(t, ",");
                                    tmpArr = tmp.split(",", -1);
                                    // 记录固定值索引
                                    map.put(cons[0], list.size() - 1);
                                }
                            }
                        }

                        // 记录header的长度
                        headerCount = tmp.split(commaRule, -1).length;
                    }

                    // 重写数据，有重复绑定时，加载到对应的字段
                    String[] newData = new String[headerCount - map.size()];

                    if (num > 0) {
                        // a解析客户自定义模板数据 获取到header
                        String newTemplate = tempalte.substring(0, tempalte.length() - 1);
                        // 排除掉为空的数据
                        String[] temp = newTemplate.split(commaRule, -1);
                        int dataLength = temp.length;
                        // 存放每个不重复的绑定名
                        String[] saveData = new String[dataLength];
                        // 不重复绑定的存放下标
                        int saveSub = -1;
                        for (String s : temp) {
                            if (StringTools.isNullOrEmpty(s)) {
                                continue;
                            }
                            // 绑定关系数组
                            String[] tempArr = s.split("=");
                            // 多对一时，通过绑定名称拼接字段
                            for (int j = 0; j < params.length; j++) {
                                if (params[j].equals(tempArr[0])) {
                                    Integer sameSub = null;
                                    // 判断是否已经保存关联名称，有则取出对应下标，拼接对应值
                                    // tmpArr为读取的上传csv文件每行内容
                                    for (int k = 0; k < saveData.length; k++) {
                                        if (!StringTools.isNullOrEmpty(saveData[k]) && saveData[k].equals(tempArr[1])) {
                                            sameSub = k;
                                            break;
                                        }
                                    }

                                    // 初始化数据为空
                                    String newDataSub = "";
                                    // 绑定的header值
                                    String saveDataSub = tempArr[1];
                                    // 如果数值不为空， 赋值
                                    if (!StringTools.isNullOrEmpty(tmpArr[j])) {
                                        newDataSub = tmpArr[j];
                                    }

                                    // 若绑定值重复 拼接到原有值后面
                                    if (!StringTools.isNullOrEmpty(sameSub)) {
                                        if (StringTools.isInteger(newData[sameSub])
                                            && StringTools.isInteger(tmpArr[j])) {
                                            int total = CommonUtils.toInteger(newData[sameSub])
                                                + CommonUtils.toInteger(tmpArr[j]);
                                            newData[sameSub] = String.valueOf(total);
                                        } else {
                                            newData[sameSub] += tmpArr[j];
                                        }
                                    } else {
                                        saveSub++;
                                        newData[saveSub] = newDataSub;
                                        saveData[saveSub] = saveDataSub;
                                    }
                                    break;
                                }
                            }
                        }
                    }

                    // a固定值重写
                    if (num != 0 && constant != null && !"".equals(constant)) {
                        String[] value = constant.split(commaRule);
                        for (String s : value) {
                            String[] temp = s.split("=");
                            // 添加并重写数据

                            for (Entry<String, Integer> entry : map.entrySet()) {
                                if (temp[0].equals(entry.getKey())) {
                                    List<String> list = new ArrayList<>(Arrays.asList(newData));
                                    list.add(temp[1]);
                                    String[] t = new String[list.size()];
                                    list.toArray(t);

                                    // 重写数据
                                    tmp = StringUtils.join(t, ",");
                                    newData = tmp.split(commaRule, -1);
                                }
                            }
                        }
                    }

                    if (num > 0) {
                        tmp = StringUtils.join(newData, ",");
                    }
                    // a第一个参数表示要写入的字符串数组，每一个元素占一个单元格，第二个参数为true时表示写完数据后自动换行
                    csvWriter.writeRecord(tmp.split(commaRule, -1), true);
                    csvWriter.flush();// 刷新数据
                    num++;
                }

                if (length != checkTemp && num == 1) {
                    totalErrList.add("タイトル行をご入力ください");
                    insertFlg = true;
                }

                if (num == 1 && !insertFlg) {
                    totalErrList.add("1行目[受注番号：]：必須項目にご入力ください：受注番号、合計請求金額、配送先郵便番号1、配送先都道府県、配送先姓、商品名、単価、個数");
                    insertFlg = true;
                }

                csvWriter.close();
                // 使用utf-8(需要确认线上环境是否可用)解析header重写后的csv文件
                // 查看临时文件 顺序有无乱掉
                Reader isr = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);
                // 同一の注文番号として、取込単位
                Map<String, Collection<Rk144_csv_order>> maps = csvToMap(isr, client_id, template_cd, company_id);
                // 关闭并删除临时csv文件
                isr.close();

                // outFile.delete();
                // 初期化
                int subno = 1; // 受注番号の枝番
                int total = 0;
                try {
                    total = maps.size(); // 取込件数
                } catch (Exception e) {
                    logger.error("店舗ID = {},取込CSVの形式不正", client_id);
                    logger.error(BaseException.print(e));
                    totalErrList.add("csvの形式不正です。");
                    // return CommonUtil.errorJson(ErrorEnum.E_20007);
                }
                int successCnt = 0; // 成功件数
                int failureCnt = 0; // 失敗件数


                // 店舗情報を取得
                // Ms201_client ms201clients = clientDao.getClientInfo(client_id);
                // 受注に関する設定情報を取得(ms007_setting)
                List<Ms007_setting> ms007list = deliveryDao.getConvertedDataAll(client_id, null);
                // Map<String, String> ms007Maps = ms007list.stream()
                // .collect(Collectors.toMap(Ms007_setting::getMapping_value, Ms007_setting::getConverted_value));

                Map<String, List<Ms007_setting>> ms007Maps =
                    ms007list.stream().filter(x -> !StringTools.isNullOrEmpty(x.getMapping_value()))
                        .collect(Collectors.groupingBy(Ms007_setting::getMapping_value));


                // 获取到店铺设定的税率信息
                Mc105_product_setting productSetting = productSettingService.getProductSetting(client_id, null);
                int accordion = productSetting.getAccordion();

                // // 受注csv行数记录
                // int row = maps.size() + 1;
                // Mapから受注データを処理
                // System.err.println(maps.size());
                List<String> errlist;
                for (Entry<String, Collection<Rk144_csv_order>> entry : maps.entrySet()) {
                    errLength.clear();
                    errDate.clear();
                    errField.clear();
                    errDelivery.clear();
                    errDeliveryTime.clear();
                    errPayment.clear();
                    unlistedProductIdList.clear();
                    errlist = new ArrayList<String>();
                    try {
                        // logger.info("取込の受注番号：" + entry.getKey());
                        // System.err.println("重复の受注番号：" + entry.getKey());
                        String entryKey = entry.getKey();
                        String value = entryKey;
                        if (!StringTools.isNullOrEmpty(entryKey)) {
                            String substring = entryKey.substring(0, 1);
                            if ("\"".equals(substring)) {
                                value = entryKey.substring(1, (entryKey.length() - 1));
                            }
                        }
                        Integer resCnt = orderDao.getOuterOrderNo(value, client_id);
                        if (!StringTools.isNullOrEmpty(resCnt) && resCnt > 0) {
                            // totalErrList.add("[WARN]行目" +
                            // entry.getValue().stream().findFirst().get().getRow()
                            // + ",受注番号：" + entry.getKey() + "すでに登録済みの受注番号ですのでご確認ください。");
                            totalErrList.add(entry.getValue().stream().findFirst().get().getRow() + "行目[受注番号："
                                + entry.getKey() + "]：すでに登録済みの受注番号ですのでご確認ください");
                            failureCnt++;
                            getWrongOrderData(template, entry.getValue(), errCsvJson, parameterList);
                        } else {
                            // a受注データをTBLに追加
                            // a受注番号(EC店舗2文字-YYYYMMDDHHMM-00001)
                            // a如果是s3受注来的则受注番号需要满足IAMS3开头
                            String orderNo = getOrderNo(subno, identification);

                            purchaseOrderNoList.add(orderNo);
                            insertCsvData(orderNo, entry.getValue(), client_id, warehouse_cd,
                                status, request, unlistedProductIdList, orderNoList, errField,
                                errDelivery, errDeliveryTime, errPayment, errLength, errDate, errlist, ms007Maps,
                                accordion);
                            successCnt++;
                            // a受注番号増加
                            subno++;
                            boolean match = unlistedProductIdList.stream().anyMatch(x -> x.equals(1));
                            if (match) {
                                // noProductOrderList.add("【INFO】 注文番号(" + entry.getKey() + ")に関する商品情報(商品名)が未登録のため、"
                                // + "新規商品として、商品マスタに自動登録しました。");

                                noProductOrderList.add("[INFO] 注文番号(" + entry.getKey()
                                    + ")に関する商品情報(商品コード)が未登録のため、仮登録として、商品マスタに登録しましたが、商品情報を確認するため、「確認待ち」として出庫作業を止めて頂きます。");
                            }
                            boolean matchPay = unlistedProductIdList.stream().anyMatch(x -> x.equals(2));
                            if (matchPay) {
                                noProductOrderList.add("[INFO] 行目"
                                    + entry.getValue().stream().findFirst().get().getRow()
                                    + "ご指定の支払方法" + errPayment
                                    + "は設定されていないため、「指定なし」として取り込まれました。店舗情報の配送連携設定で、支払方法をご追加いただけますと、次回取り込みから支払方法が指定されます。");
                            }
                        }
                    } catch (Exception e) {
                        getWrongOrderData(template, entry.getValue(), errCsvJson, parameterList);
                        logger.error("受注取込NG 店舗ID：" + client_id + " CSV:" + filePath);
                        // if (errDate.size() != 0) {
                        // errlist.add("[ERROR]行目" +
                        // entry.getValue().stream().findFirst().get().getRow() + "[受注番号："
                        // + entry.getKey() + "]：「配達希望日」には、「出荷希望日」より後の日付をご指定ください");
                        // }
                        // if (errLength.size() != 0) {
                        // errlist.add("[ERROR]行目" +
                        // entry.getValue().stream().findFirst().get().getRow() + "[受注番号："
                        // + entry.getKey() + "]：" + errLength);
                        // }
                        if (errField.size() != 0) {
                            totalErrList.add(entry.getValue().stream().findFirst().get().getRow() + "行目" + "[受注番号："
                                + entry.getKey() + "]：必須項目にご入力ください：" + errField);
                        }
                        if (errDelivery.size() != 0) {
                            totalErrList.add(entry.getValue().stream().findFirst().get().getRow() + "行目" + errDelivery);
                        }
                        if (errDeliveryTime.size() != 0) {
                            totalErrList.add(entry.getValue().stream().findFirst().get().getRow() + "行目" + "[受注番号："
                                + entry.getKey() + "]：" + "ご指定の配送希望時間帯" + errDeliveryTime
                                + "を、店舗情報の配送連携設定でご追加ください。");
                        }
                        if (errlist.size() != 0) {
                            totalErrList.addAll(errlist);
                        }
                        if (totalErrList.size() == 0) {
                            totalErrList.add(entry.getValue().stream().findFirst().get().getRow() + "行目" + ",受注番号："
                                + entry.getKey() + "の取込エラーが発生しました。");
                        }
                        failureCnt++;
                        logger.error(BaseException.print(e));
                    }
                }
                if (!insertFlg) {
                    // 生成した受注履歴beanに情報を格納
                    Tc202_order_history order_history = new Tc202_order_history();
                    // a获取受注取込履歴ID
                    Integer historyId = getMaxHistoryId();
                    order_history.setHistory_id(historyId);
                    order_history.setImport_datetime(toTimestamp());
                    order_history.setIns_date(toTimestamp());
                    order_history.setClient_id(client_id);
                    order_history.setTotal_cnt(total);// 取込件数
                    order_history.setSuccess_cnt(successCnt);// 成功件数
                    order_history.setFailure_cnt(failureCnt);// 失敗件数
                    order_history.setBiko01("1");
                    try {
                        orderHistoryDao.insertOrderHistory(order_history);
                    } catch (Exception e) {
                        logger.error("店铺Id={} 履历Id={} 新规失败", client_id, historyId);
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }
                    // 更改受注表里的履历Id
                    try {
                        orderDao.updateHistoryId(client_id, historyId, purchaseOrderNoList);
                    } catch (Exception e) {
                        logger.error("店铺Id={} 履历Id={} 受注番号={} 修改履历Id失败", client_id, historyId, purchaseOrderNoList);
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }
                }
                logger.info("取込件数:" + total + " 成功件数：" + successCnt + " 失敗件数:" + failureCnt);
            } else {
                totalErrList.add("[ERROR]CSVファイルが空です。");
            }

            // a改变报错信息顺序(根据行号升序)
            List<String> errorTem = new ArrayList<>();
            for (int i = totalErrList.size() - 1; i >= 0; i--) {
                errorTem.add(totalErrList.get(i));
            }
            totalErrList = errorTem;

        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject resultJson = new JSONObject();
        // 错误信息去重 并排序
        List<String> collect = totalErrList.stream().distinct().sorted().collect(Collectors.toList());
        // 给前端返回错误信息集合
        resultJson.put("errlist", collect);
        resultJson.put("unlistedProductId", noProductOrderList);
        if (!errCsvJson.isEmpty()) {
            resultJson.put("downloadCsvData", errCsvJson);
            resultJson.put("header", csvHeaderList);
            resultJson.put("parameterList", parameterList);
        }
        return CommonUtils.success(resultJson);
    }


    private Integer getMaxHistoryId() {
        String lastOrderHistoryNo = orderHistoryDao.getLastOrderHistoryNo();
        int lastHistoryId = 0;
        if (!StringTools.isNullOrEmpty(lastOrderHistoryNo)) {
            lastHistoryId = Integer.parseInt(lastOrderHistoryNo);
        }
        String historyId = String.valueOf(lastHistoryId + 1);
        return Integer.parseInt(historyId);
    }

    private String getOrderNo(int num, String identification) {

        String tmp = Constants.DEFAULT_ORDER_NO;
        // 如果为空 则取默认值RK
        if (!StringTools.isNullOrEmpty(identification)) {
            tmp = identification;
        }
        // 受注
        return tmp + "-" + new SimpleDateFormat("yyyyMMddHHmmss-").format(new Date())
            + String.format("%05d", num);
    }

    @Transactional(rollbackFor = {
        BaseException.class, Error.class
    })
    public void insertCsvData(String orderNo, Collection<Rk144_csv_order> list, String clientId, String warehouseCd,
        String status, HttpServletRequest request, List<Integer> unlistedProductIdList, List<String> orderNoList,
        List<String> errField, List<String> errDelivery, List<String> errDeliveryTime,
        List<String> errPayment, List<String> errLength, List<String> errDate, List<String> errlist,
        Map<String, List<Ms007_setting>> ms007Maps, int accordion) {


        ArrayList<String> errNum = new ArrayList<>();
        // a初期化
        if (list.size() > 0) {
            // a受注管理(※Mapから第1要素)
            // 实体类数据双引号处理
            for (Rk144_csv_order data : list) {
                Arrays.stream(data.getClass().getDeclaredFields()).forEach(field -> {
                    try {
                        // 将私有属性变为可访问状态
                        field.setAccessible(true);
                        if (field.getType().equals(String.class)) {
                            String string = (String) field.get(data);
                            if (!StringTools.isNullOrEmpty(string)) {
                                String substring = string.substring(0, 1);
                                if ("\"".equals(substring)) {
                                    field.set(data, string.substring(1, (string.length() - 1)));
                                }
                            }
                        }
                    } catch (Exception e) {
                        logger.error("店舗ID = {}, 受注番号 = {}, 取込項目 = {},ダブルクォーテーション処理中エラー発生", clientId, orderNo,
                            field.getName());
                    }
                });
            }

            // 获取默认的依赖主
            List<Ms012_sponsor_master> sponsorDefalutList = sponsorDao.getSponsorList(clientId, true, null);

            Optional<Rk144_csv_order> firstOrder = list.stream().findFirst();
            Tc200_order tc200od;
            try {
                tc200od = setOrder(firstOrder.get(), orderNo, clientId, warehouseCd, errField, errDelivery,
                    errDeliveryTime, errPayment, errLength, errDate, unlistedProductIdList, errNum, errlist,
                    sponsorDefalutList,
                    ms007Maps, list, accordion);
            } catch (Exception e) {
                logger.error("店舗ID = {}, 受注番号 = {}, 受注管理表のデータ処理中にエラー発生", clientId, orderNo);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            try {
                // a检查注文者依頼是否为1
                if (tc200od.getOrder_flag() == 1) {
                    Ms012_sponsor_master ms012 = new Ms012_sponsor_master();
                    // a电话号码处理
                    if (tc200od.getOrder_phone_number2() == null || "".equals(tc200od.getOrder_phone_number2())) {
                        ms012.setPhone(tc200od.getOrder_phone_number1());
                    } else {
                        ms012.setPhone(tc200od.getOrder_phone_number1() + tc200od.getOrder_phone_number2()
                            + tc200od.getOrder_phone_number3());
                    }
                    ms012.setPrefecture(tc200od.getOrder_todoufuken());
                    // a数据为空字符串则入力null(为了后端抛出异常前端接收依赖主入力的错误信息)
                    if ((!"".equals(tc200od.getOrder_address1()) && tc200od.getOrder_address1() != null)
                        || (!"".equals(tc200od.getOrder_address2()) && tc200od.getOrder_address2() != null)) {
                        ms012.setAddress1(tc200od.getOrder_address1());
                        ms012.setAddress2(tc200od.getOrder_address2());
                    }
                    if ((!"".equals(tc200od.getOrder_family_name()) && tc200od.getOrder_family_name() != null)
                        || (!"".equals(tc200od.getOrder_first_name())) && tc200od.getOrder_first_name() != null) {
                        if (tc200od.getOrder_first_name() == null) {
                            tc200od.setOrder_first_name("");
                        }
                        ms012.setName(tc200od.getOrder_family_name() + tc200od.getOrder_first_name());
                    }
                    // a邮编处理
                    if ((!"".equals(tc200od.getOrder_zip_code2()) && tc200od.getOrder_zip_code2() != null)
                        || (!"".equals(tc200od.getOrder_zip_code1())) && tc200od.getOrder_zip_code1() != null) {
                        if (tc200od.getOrder_zip_code2() == null || "".equals(tc200od.getOrder_zip_code2())) {
                            ms012.setPostcode(tc200od.getOrder_zip_code1());
                        } else {
                            ms012.setPostcode(tc200od.getOrder_zip_code1() + tc200od.getOrder_zip_code2());
                        }
                    }
                    ms012.setCompany(tc200od.getOrder_company());
                    ms012.setDivision(tc200od.getOrder_division());
                    ms012.setDetail_logo(pathProps.getImage() + "white.jpg");
                    ms012.setContact(0);
                    ms012.setUtilize("99999");
                    ms012.setClient_id(clientId);

                    // 设置明细书印字
                    if (sponsorDefalutList.size() > 0) {
                        ms012.setDelivery_note_type(sponsorDefalutList.get(0).getDelivery_note_type());
                        ms012.setPrice_on_delivery_note(sponsorDefalutList.get(0).getPrice_on_delivery_note());
                    } else {
                        ms012.setDelivery_note_type(0);
                        ms012.setPrice_on_delivery_note(0);
                    }

                    // a检查依赖主信息是否存在
                    Ms012_sponsor_master checked;
                    checked = settingDao.checkSponsorExist(ms012);
                    // a如果存在依赖主则写入id，如果不存在则新规依赖主并写入id
                    if (!StringTools.isNullOrEmpty(checked)) {
                        tc200od.setSponsor_id(checked.getSponsor_id());
                    } else {
                        ms012.setEmail(tc200od.getOrder_mail());
                        ms012.setSponsor_id(String.valueOf(settingDao.getMaxSponsorId() + 1));
                        try {
                            settingDao.createDeliveryList(ms012);
                        } catch (Exception e) {
                            errlist.add(firstOrder.get().getRow() + "行目"
                                + "：注文者区分指定(1:有 0:無)の値が１の場合、注文者名前、注文者郵便番号、注文者住所の入力内容を再度ご確認ください。");
                        }
                        tc200od.setSponsor_id(String.valueOf(settingDao.getMaxSponsorId()));
                    }
                }
                // a受注管理の更新
                orderDao.insertOrder(tc200od);

            } catch (Exception e) {
                logger.error("店舗ID = {}, 受注番号 = {},受注管理表登録中にエラー発生", clientId, orderNo);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }

            // 受注明細(※MapのKey値に関連する全て明細を処理)
            logger.debug("----------------- Tc201_order_detail Strat -----------");
            int subNo = 1;
            // 判断同一个受注番号 里面是否商品重复， key：受注番号， value：商品Id
            // StringBuilder builder = new StringBuilder();
            // 存取商品Id 重复的行数

            // 记录受注明细的商品是否为同捆物
            ArrayList<Integer> bundledFlgList = new ArrayList<>();
            for (Rk144_csv_order var : list) {

                Tc201_order_detail tc201od;
                try {
                    tc201od = setOrderDetail(var, orderNo, subNo, unlistedProductIdList, clientId, request, orderNoList,
                        errNum, errlist, accordion, bundledFlgList);
                } catch (Exception e) {
                    logger.error("店舗ID = {}, 受注番号 = {},受注明細のデータ処理中にエラー発生", clientId, orderNo);
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
                // 如果第一次进来， 存入 受注番号，以及商品Id
                // if (map.size() == 0) {
                // map.put(orderNo, tc201od.getProduct_id());
                // arrayList.add(var.getRow() + "行目");
                // } else {
                // // 非第一次进来, 需要判断商品Id是否重复
                // for (Map.Entry<String, String> entry : map.entrySet()) {
                // // 根据这次的受注番号 获取到value
                // String value = entry.getValue();
                // if (tc201od.getProduct_id().equals(value)) {
                // arrayList.add(var.getRow() + "行目");
                // } else {
                // map.put(orderNo, tc201od.getProduct_id());
                // }
                // }
                // }
                try {
                    // a受注明細を更新
                    orderDetailDao.insertOrderDetail(tc201od);
                    subNo++;
                } catch (Exception e) {
                    logger.error("店舗ID = {}, 受注番号 = {},受注明細表の登録中にエラー発生", clientId, orderNo);
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
            if (errNum.size() != 0) {
                String join = Joiner.on("、").join(errNum);
                errlist
                    .add(firstOrder.get().getRow() + "行目[受注番号：" + tc200od.getOuter_order_no() + "]：8桁以内で、半角数字をご入力ください："
                        + join);
            }


            // 不存在普通商品 全部为同捆物
            if (!bundledFlgList.contains(0)) {
                errlist.add(firstOrder.get().getRow() + "行目[受注番号：" + tc200od.getOuter_order_no()
                    + "]：「同梱物」のみで受注登録することはできません。「通常商品」を1つ以上ご指定ください。");
            }
            // if (arrayList.size() != 1) {
            // String errLine = Joiner.on("、").join(arrayList);
            // errlist.add(errLine + "[受注番号：" + tc200od.getOuter_order_no() + "]：同一受注番号内で商品名が重複していますのでご確認ください");
            // }
            logger.debug("----------------- Tc201_order_detail End   -----------");

            if (errlist.size() != 0 || errDeliveryTime.size() != 0) {
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
            // 如果status = 1 ，选择出库
            if ("1".equals(status)) {
                // 如果含有不能出库的 受注Id
                selectIssue(orderNo, clientId, request);
            }
        }
    }

    /**
     * @Param: orderNo ： 受注番号
     * @description: csv上传后直接出库
     * @return: void
     * @date: 2020/8/20
     */
    // @Transactional(rollbackFor = { JsonException.class, Error.class })
    public void selectIssue(String orderNo, String clientId, HttpServletRequest request) {
        JSONObject jsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        array.add(orderNo);
        jsonObject.put("list", array);
        createShipments(jsonObject, clientId, request);
    }

    /**
     * @return 注入後の受注明細bean
     * @Description 楽天csvの内容を受注明細beanに注入
     * @Param Tc201_order_detail, String[]
     */
    // @Transactional(rollbackFor = { JsonException.class, Error.class })
    public Tc201_order_detail setOrderDetail(Rk144_csv_order var, String orderNo, int subNo,
        List<Integer> unlistedProductIdList, String clientId, HttpServletRequest request,
        List<String> orderNoList, ArrayList<String> errNum, List<String> errlist,
        int accordion, ArrayList<Integer> bundledFlgList) {
        String nullStr = "";
        String intStr = "";
        String length20Str = "";
        String length30Str = "";
        String length50Str = "";
        String length100Str = "";
        String length300Str = "";
        String length500Str = "";
        String codeAndNameNull = "";
        // 初期化
        Tc201_order_detail orderDetail = new Tc201_order_detail();

        // 受注明細番号(主キー)
        orderDetail.setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo));
        // a受注番号(主キー)
        orderDetail.setPurchase_order_no(orderNo);

        // 商品オプション
        String item082 = var.getItem082();
        if (StringTools.isNullOrEmpty(item082)) {
            // 商品オプション1
            String item173 = var.getItem173();
            item173 = !StringTools.isNullOrEmpty(item173) ? item173.trim() : "";
            if (!StringTools.isNullOrEmpty(item173) && item173.length() > 100) {
                length100Str += "商品オプション1、";
            }
            // 商品オプション値1
            String item174 = var.getItem174();
            item174 = !StringTools.isNullOrEmpty(item174) ? item174.trim() : "";
            if (!StringTools.isNullOrEmpty(item174) && item174.length() > 100) {
                length100Str += "商品オプション値1、";
            }
            // 商品オプション2
            String item175 = var.getItem175();
            item175 = !StringTools.isNullOrEmpty(item175) ? item175.trim() : "";
            if (!StringTools.isNullOrEmpty(item175) && item175.length() > 100) {
                length100Str += "商品オプション2、";
            }
            // 商品オプション値2
            String item176 = var.getItem176();
            item176 = !StringTools.isNullOrEmpty(item176) ? item176.trim() : "";
            if (!StringTools.isNullOrEmpty(item176) && item176.length() > 100) {
                length100Str += "商品オプション値2、";
            }
            // 商品オプション3
            String item177 = var.getItem177();
            item177 = !StringTools.isNullOrEmpty(item177) ? item177.trim() : "";
            if (!StringTools.isNullOrEmpty(item177) && item177.length() > 100) {
                length100Str += "商品オプション3、";
            }
            // 商品オプション値3
            String item178 = var.getItem178();
            item178 = !StringTools.isNullOrEmpty(item178) ? item178.trim() : "";
            if (!StringTools.isNullOrEmpty(item178) && item178.length() > 100) {
                length100Str += "商品オプション値3、";
            }
            // 商品オプション4
            String item179 = var.getItem179();
            item179 = !StringTools.isNullOrEmpty(item179) ? item179.trim() : "";
            if (!StringTools.isNullOrEmpty(item179) && item179.length() > 100) {
                length100Str += "商品オプション4、";
            }
            // 商品オプション値4
            String item180 = var.getItem180();
            item180 = !StringTools.isNullOrEmpty(item180) ? item180.trim() : "";
            if (!StringTools.isNullOrEmpty(item180) && item180.length() > 100) {
                length100Str += "商品オプション値4、";
            }
            String options = "";
            if (!StringTools.isNullOrEmpty(item173) || !StringTools.isNullOrEmpty(item174)) {
                options = item173 + ":" + item174 + ",";
            }
            if (!StringTools.isNullOrEmpty(item175) || !StringTools.isNullOrEmpty(item176)) {
                options += item175 + ":" + item176 + ",";
            }
            if (!StringTools.isNullOrEmpty(item177) || !StringTools.isNullOrEmpty(item178)) {
                options += item177 + ":" + item178 + ",";
            }
            if (!StringTools.isNullOrEmpty(item179) || !StringTools.isNullOrEmpty(item180)) {
                options += item179 + ":" + item180 + ",";
            }

            if (!StringTools.isNullOrEmpty(options)) {
                item082 = options.substring(0, options.length() - 1);
            }
        } else {
            if (item082.length() > 500) {
                length500Str += "商品オプション、";
            }
        }
        orderDetail.setProduct_option(item082);

        // 商品コード
        Mc100_product productInfoByCode;
        String item074 = var.getItem074();
        String item073 = var.getItem073();
        item074 = CommonUtils.conversionCharacter(item074);
        String item075 = var.getItem075();

        item074 = !StringTools.isNullOrEmpty(item074) ? item074.trim() : "";

        boolean kubunFlg = false;
        if (StringTools.isNullOrEmpty(item074)) {
            // 商品code为空 需要判断商品名 是否存在
            if (!StringTools.isNullOrEmpty(item073)) {
                if (item073.length() > 300) {
                    length300Str += "商品名、";
                } else {
                    List<Mc100_product> mc100Product = productDao.getProductInfoByName(item073, clientId);
                    if (!mc100Product.isEmpty()) {
                        // 如果根据商品名查找出来多个商品信息 取第一個值
                        Mc100_product product = mc100Product.get(0);

                        orderDetail.setProduct_id(product.getProduct_id());
                        orderDetail.setProduct_name(product.getName());
                        orderDetail.setProduct_code(product.getCode());
                        if (!StringTools.isNullOrEmpty(item075)) {
                            orderDetail.setProduct_barcode(item075);
                        }
                        if (product.getSet_flg() == 1 && !StringTools.isNullOrEmpty(product.getSet_sub_id())) {
                            orderDetail.setSet_sub_id(product.getSet_sub_id());
                        }

                        Integer bundledFlg = product.getBundled_flg();
                        if (StringTools.isNullOrEmpty(bundledFlg) || bundledFlg == 0) {
                            bundledFlg = 0;
                        }
                        orderDetail.setBundled_flg(bundledFlg);
                        // 商品区分 默认为0: 普通商品
                        int kubun = 0;
                        Integer productKubun = product.getKubun();
                        if (!StringTools.isNullOrEmpty(productKubun)) {
                            // 假登录商品
                            if (productKubun == 9) {
                                kubun = productKubun;
                                // 记录以前没有登录过的商品
                                unlistedProductIdList.add(1);
                            } else {
                                // 其他商品
                                kubun = productKubun;
                            }

                        }
                        orderDetail.setProduct_kubun(kubun);
                    } else {
                        // 假登录
                        kubunFlg = true;
                    }
                }
            } else {
                codeAndNameNull = "商品名";
            }
        } else {
            // 商品code 不为空
            // 商品对应表
            productInfoByCode = apiCommonUtils.fetchMc100Product(clientId, item074, item082);
            if (!StringTools.isNullOrEmpty(productInfoByCode)) {
                boolean regular = StringTools.regular(productInfoByCode.getCode(), "^[A-Za-z0-9-_]*$");
                if (!regular) {
                    errlist.add(var.getRow() + "行目[受注番号：" + var.getItem001() + "]：半角英数字とハイフン（-_）でご入力ください：商品コード");
                }
                orderDetail.setProduct_id(productInfoByCode.getProduct_id());
                orderDetail.setProduct_name(productInfoByCode.getName());
                orderDetail.setProduct_code(productInfoByCode.getCode());

                if (!StringTools.isNullOrEmpty(item075)) {
                    orderDetail.setProduct_barcode(item075);
                }
                if (!StringTools.isNullOrEmpty(productInfoByCode.getSet_sub_id())
                    && productInfoByCode.getSet_flg() == 1) {
                    orderDetail.setSet_sub_id(productInfoByCode.getSet_sub_id());
                }

                Integer bundledFlg = productInfoByCode.getBundled_flg();
                if (StringTools.isNullOrEmpty(bundledFlg) || bundledFlg == 0) {
                    bundledFlg = 0;
                }
                orderDetail.setBundled_flg(bundledFlg);
                // 仮登録 默认为0
                int kubun = 0;
                Integer productKubun = productInfoByCode.getKubun();
                if (!StringTools.isNullOrEmpty(productKubun)) {
                    // 假登录商品
                    if (productKubun == 9) {
                        kubun = productKubun;
                        // 记录以前没有登录过的商品
                        unlistedProductIdList.add(1);
                    } else {
                        // 其他商品
                        kubun = productKubun;
                    }
                }
                orderDetail.setProduct_kubun(kubun);
            } else {
                // 假登录
                kubunFlg = true;
            }
        }

        if (kubunFlg) {
            // 获取最大商品Id
            String productId = productService.createProductId(clientId);
            orderDetail.setProduct_id(productId);
            orderDetail.setProduct_name(item073);
            orderDetail.setProduct_code(item074);
            orderDetail.setSet_sub_id(null);
            orderDetail.setBundled_flg(0);
            // 记录以前没有登录过的商品Id
            unlistedProductIdList.add(1);
            // 将该商品自动登录
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("client_id", clientId);
            JSONArray array = new JSONArray();
            JSONObject items = new JSONObject();
            items.put("name", item073);
            items.put("bundled_flg", 0);
            items.put("is_reduced_tax", 0);
            items.put("code", item074);
            if (!StringTools.isNullOrEmpty(item075)) {
                items.put("barcode", item075);
            }
            String item076 = var.getItem076();
            if (!StringTools.isNullOrEmpty(item076)) {
                if ("${price}".equals(item076)) {
                    item076 = String.valueOf(parsePriceInt(var.getItem187()) / toInteger(var.getItem077()));
                }
                items.put("price", parsePriceInt(item076));
            } else {
                nullStr += "商品単価、";
            }
            items.put("tax_flag", 0);
            items.put("bundled_flg", 0);
            JSONArray tags = new JSONArray();
            JSONArray img = new JSONArray();
            items.put("tags", tags);
            items.put("img", img);

            // 因为该商品之前不存在 所以为仮登録 需要 kubun = 9
            items.put("kubun", 9);
            items.put("show_flg", 0);
            orderDetail.setProduct_kubun(9);
            array.add(items);
            jsonObject.put("items", array);
            // 登录商品
            try {
                productService.insertProductMain(jsonObject, request);
            } catch (Exception e) {
                logger.error("店舗ID = {}, 商品番号 = {}, 商品登録中にエラー発生", clientId, productId);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 商品コード
        if (!StringTools.isNullOrEmpty(item074)) {
            if (item074.length() > 100) {
                length100Str += "商品コード、";
            } else {
                orderDetail.setProduct_code(item074);
            }
        }
        // 管理バーコード
        if (!StringTools.isNullOrEmpty(item075)) {
            if (item075.length() > 20) {
                length20Str += "管理バーコード、";
            } else {
                orderDetail.setProduct_barcode(item075);
            }
        }
        // 単価
        if (!StringTools.isNullOrEmpty(var.getItem076())) {
            if (StringTools.isInteger(var.getItem076())) {
                orderDetail.setUnit_price(parsePriceInt(var.getItem076()));
                boolean regular = StringTools.regular(var.getItem076(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("単価");
                }
            } else if ("${price}".equals(var.getItem076())) {
                orderDetail.setUnit_price(parsePriceInt(var.getItem187()) / toInteger(var.getItem077()));
            } else {
                intStr += "商品単価、";
            }
        }
        // 個数
        String item077 = var.getItem077();
        if (!StringTools.isNullOrEmpty(item077)) {
            if (StringTools.isInteger(item077)) {
                Integer number = toInteger(item077);
                boolean regular = StringTools.regular(item077, "^[0-9]*$");
                if (!regular) {
                    errNum.add("個数");
                }
                if ((0 < number) && (number < 999999999)) {
                    orderDetail.setNumber(number);
                } else {
                    errlist.add(var.getRow() + "行目[受注番号：" + var.getItem001() + "]：1以上999999999以下の数値でご入力ください：個数");
                }
            } else {
                intStr += "商品個数、";
            }
        } else {
            nullStr += "商品個数、";
        }

        Map<String, Object> taxDivision = getTaxDivision(var);

        // 税区分(0:税込 1:税抜 2:非課税)
        int taxFlag = (int) taxDivision.get("taxFlag");
        // 軽減税率(0:10% 1:8%)
        double reduceRax = (double) taxDivision.get("reduceRax");

        // 商品計(個数 * 単価)验证过了 这里不需要再次验证
        String item076 = var.getItem076();
        if ("${price}".equals(item076)) {
            item076 = String.valueOf(orderDetail.getUnit_price());
        }
        if (StringTools.isInteger(item076) && StringTools.isInteger(var.getItem077())) {
            int unitPrice = parsePriceInt(item076);
            // 如果为税拔 需要计算税率
            if (taxFlag == 1) {
                unitPrice += CommonUtils.calculateTheNumbers((unitPrice * reduceRax), accordion);
            }
            orderDetail.setProduct_total_price(unitPrice * toInteger(item077));
        }
        String item186 = var.getItem186();
        int is_reduced_tax = 0;
        if (!StringTools.isNullOrEmpty(item186)) {
            is_reduced_tax = Integer.parseInt(item186);
        }
        orderDetail.setIs_reduced_tax(is_reduced_tax);
        // 如果为税拔 计算方式
        if (taxFlag == 1) {
            if (accordion == 0) {
                taxFlag = 10;
            }
            if (accordion == 1) {
                taxFlag = 11;
            }
            if (accordion == 2) {
                taxFlag = 12;
            }
        }
        if (taxFlag == 2) {
            taxFlag = 3;
        }
        orderDetail.setTax_flag(taxFlag);

        // 商品備考1
        String item155 = var.getItem155();
        if (!StringTools.isNullOrEmpty(item155)) {
            if (item155.length() > 100) {
                length100Str += "商品備考1、";
            } else {
                orderDetail.setBikou1(item155);
            }
        }
        // 商品備考2
        String item156 = var.getItem156();
        if (!StringTools.isNullOrEmpty(item156)) {
            if (item156.length() > 100) {
                length100Str += "商品備考2、";
            } else {
                orderDetail.setBikou2(item156);
            }
        }
        // 商品備考3
        String item157 = var.getItem157();
        if (!StringTools.isNullOrEmpty(item157)) {
            if (item157.length() > 100) {
                length100Str += "商品備考3、";
            } else {
                orderDetail.setBikou3(item157);
            }
        }
        // 商品備考4
        String item158 = var.getItem158();
        if (!StringTools.isNullOrEmpty(item158)) {
            if (item158.length() > 100) {
                length100Str += "商品備考4、";
            } else {
                orderDetail.setBikou4(item158);
            }
        }
        // 商品備考5
        String item159 = var.getItem159();
        if (!StringTools.isNullOrEmpty(item159)) {
            if (item159.length() > 100) {
                length100Str += "商品備考5、";
            } else {
                orderDetail.setBikou5(item159);
            }
        }
        // 商品備考6
        String item160 = var.getItem160();
        if (!StringTools.isNullOrEmpty(item160)) {
            if (item160.length() > 100) {
                length100Str += "商品備考6、";
            } else {
                orderDetail.setBikou6(item160);
            }
        }
        // 商品備考7
        String item161 = var.getItem161();
        if (!StringTools.isNullOrEmpty(item161)) {
            if (item161.length() > 100) {
                length100Str += "商品備考7、";
            } else {
                orderDetail.setBikou7(item161);
            }
        }
        // 商品備考8
        String item162 = var.getItem162();
        if (!StringTools.isNullOrEmpty(item162)) {
            if (item162.length() > 100) {
                length100Str += "商品備考8、";
            } else {
                orderDetail.setBikou8(item162);
            }
        }
        // 商品備考9
        String item163 = var.getItem163();
        if (!StringTools.isNullOrEmpty(item163)) {
            if (item163.length() > 100) {
                length100Str += "商品備考9、";
            } else {
                orderDetail.setBikou9(item163);
            }
        }
        // 商品備考10
        String item164 = var.getItem164();
        if (!StringTools.isNullOrEmpty(item164)) {
            if (item164.length() > 100) {
                length100Str += "商品備考10、";
            } else {
                orderDetail.setBikou10(item164);
            }
        }
        // ラッピングタイトル1
        String item085 = var.getItem085();
        if (!StringTools.isNullOrEmpty(item085)) {
            if (item085.length() > 50) {
                length50Str += "ラッピングタイトル1、";
            } else {
                orderDetail.setWrapping_title1(item085);
            }
        }
        // ラッピング名1
        String item086 = var.getItem086();
        if (!StringTools.isNullOrEmpty(item086)) {
            if (item086.length() > 50) {
                length50Str += "ラッピング名1、";
            } else {
                orderDetail.setWrapping_name1(item086);
            }
        }
        // ラッピング料金1
        if (!StringTools.isNullOrEmpty(var.getItem087())) {
            if (StringTools.isInteger(var.getItem087())) {
                orderDetail.setWrapping_price1(parsePriceInt(var.getItem087()));
                boolean regular = StringTools.regular(var.getItem087(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("ラッピング料金1");
                }
            } else {
                intStr += "ラッピング料金1、";
            }
        }
        // ラッピング税込別1
        if (!StringTools.isNullOrEmpty(var.getItem088())) {
            if (StringTools.isInteger(var.getItem088())) {
                orderDetail.setWrapping_tax1(parsePriceInt(var.getItem088()));
                boolean regular = StringTools.regular(var.getItem088(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("ラッピング税込別1");
                }
            } else {
                intStr += "ラッピング税込別1、";
            }
        }
        // ラッピング種類1
        String item089 = var.getItem089();
        if (!StringTools.isNullOrEmpty(item089)) {
            if (item089.length() > 50) {
                length50Str += "ラッピング種類1、";
            } else {
                orderDetail.setWrapping_type1(item089);
            }
        }
        // ラッピングタイトル2
        String item090 = var.getItem090();
        if (!StringTools.isNullOrEmpty(item090)) {
            if (item090.length() > 50) {
                length50Str += "ラッピングタイトル2、";
            } else {
                orderDetail.setWrapping_title2(item090);
            }
        }
        // ラッピング名2
        String item091 = var.getItem091();
        if (!StringTools.isNullOrEmpty(item091)) {
            if (item091.length() > 50) {
                length50Str += "ラッピング名2、";
            } else {
                orderDetail.setWrapping_name2(item091);
            }
        }
        // ラッピング料金2
        if (!StringTools.isNullOrEmpty(var.getItem092())) {
            if (StringTools.isInteger(var.getItem092())) {
                orderDetail.setWrapping_price2(parsePriceInt(var.getItem092()));
                boolean regular = StringTools.regular(var.getItem092(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("ラッピング料金2");
                }
            } else {
                intStr += "ラッピング料金2、";
            }
        }
        // ラッピング税込別2
        if (!StringTools.isNullOrEmpty(var.getItem093())) {
            if (StringTools.isInteger(var.getItem093())) {
                orderDetail.setWrapping_tax2(parsePriceInt(var.getItem093()));
                boolean regular = StringTools.regular(var.getItem093(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("ラッピング税込別2");
                }
            } else {
                intStr += "ラッピング税込別2、";
            }
        }
        // ラッピング種類2
        String item094 = var.getItem094();
        if (!StringTools.isNullOrEmpty(item094)) {
            if (item094.length() > 50) {
                length50Str += "ラッピング種類2、";
            } else {
                orderDetail.setWrapping_type2(item094);
            }
        }
        // 追加時間
        orderDetail.setIns_date(toTimestamp());

        String errorMess = "";

        if (!StringTools.isNullOrEmpty(length20Str)) {
            length20Str = length20Str.substring(0, length20Str.length() - 1);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + orderNo + "]：" + length20Str + "を20文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length30Str)) {
            length30Str = length30Str.substring(0, length30Str.length() - 1);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + orderNo + "]：" + length30Str + "を30文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length50Str)) {
            length50Str = length50Str.substring(0, length50Str.length() - 1);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + orderNo + "]：" + length50Str + "を50文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length100Str)) {
            length100Str = length100Str.substring(0, length100Str.length() - 1);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + orderNo + "]：" + length100Str + "を100文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length300Str)) {
            length300Str = length300Str.substring(0, length300Str.length() - 1);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + orderNo + "]：" + length300Str + "を300文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length500Str)) {
            length500Str = length500Str.substring(0, length500Str.length() - 1);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + orderNo + "]：" + length500Str + "を500文字以内でご入力ください");
        }
        // if (!StringTools.isNullOrEmpty(nullStr)) {
        // nullStr = nullStr.substring(0, nullStr.length() - 1);
        // errorMess += nullStr;
        // }
        if (!StringTools.isNullOrEmpty(nullStr)) {
            nullStr = nullStr.substring(0, nullStr.length() - 1);
            errlist.add(var.getRow() + "行目[受注番号：" + var.getItem001() + "]：" + nullStr + "を必須項目にご入力ください");
            // throw new JsonException(ErrorEnum.E_10001);

        }
        if (!StringTools.isNullOrEmpty(intStr)) {
            intStr = intStr.substring(0, intStr.length() - 1);
            errorMess += intStr + "の形式が正しくありません、数字のみご入力ください。";
        }
        if (!StringTools.isNullOrEmpty(errorMess)) {
            errlist.add(var.getRow() + "行目[受注番号：" + var.getItem001() + "]：" + errorMess);
        }
        if (!StringTools.isNullOrEmpty(codeAndNameNull)) {
            errlist.add(var.getRow() + "行目[受注番号：" + var.getItem001()
                + "]：商品コードと商品名、2項目のうちのどちらかは必須です。両方とも入力されていない場合は、受注取込みできません。ご確認ください。");
        }
        // 记录 受注明细的商品 是否为同捆物
        bundledFlgList.add(!StringTools.isNullOrEmpty(orderDetail.getBundled_flg()) ? orderDetail.getBundled_flg() : 0);
        return orderDetail;
    }

    /**
     * @param var : Rk144_csv_order
     * @description: 计算出 税区分 和 轻减税率
     * @return: java.util.Map<java.lang.String, java.lang.Object>
     * @date: 2021/7/16 15:49
     */
    private Map<String, Object> getTaxDivision(Rk144_csv_order var) {
        HashMap<String, Object> taxMap = new HashMap<>();
        // 税区分(0:税込 1:税抜 2:非課税)
        String item185 = var.getItem185();
        int taxFlag = 0;
        if (!StringTools.isNullOrEmpty(item185)) {
            taxFlag = Integer.parseInt(item185);
        }
        // 軽減税率(0:10% 1:8%)
        double reduceRax = 0.1;
        String item186 = var.getItem186();
        if (!StringTools.isNullOrEmpty(item186) && "1".equals(item186)) {
            reduceRax = 0.08;
        }

        taxMap.put("taxFlag", taxFlag);
        taxMap.put("reduceRax", reduceRax);
        return taxMap;
    }

    /**
     * @return 注入後の受注bean
     * @Description 楽天csvの内容を受注beanに注入
     * @Param OrderItemEntity, String[]
     */
    // @Transactional(rollbackFor = { JsonException.class, Error.class })
    public Tc200_order setOrder(Rk144_csv_order var, String orderNo, String clientId, String warehouseCd,
        List<String> errField, List<String> errDelivery, List<String> errDeliveryTime,
        List<String> errPayment, List<String> errLength, List<String> errDate, List<Integer> unlistedProductIdList,
        ArrayList<String> errNum, List<String> errlist, List<Ms012_sponsor_master> sponsorDefalutList,
        Map<String, List<Ms007_setting>> ms007Maps, Collection<Rk144_csv_order> list, int accordion) {

        String nullStr = "";
        String intStr = "";
        String boolStr = "";
        String length4Str = "";
        String length8Str = "";
        String length10Str = "";
        String length16Str = "";
        String length20Str = "";
        String length30Str = "";
        String length32Str = "";
        String length50Str = "";
        String length100Str = "";
        String length160Str = "";
        String length300Str = "";
        String length500Str = "";
        String errMail = "";
        String errZip = "";
        String errPhone = "";
        String errDates = "";
        String spronsorStr = "";
        // 初期化
        Tc200_order order = new Tc200_order();
        // 店舗ID(主キー)
        order.setClient_id(clientId);
        // 受注倉庫管理CD(主キー)
        order.setWarehouse_cd(warehouseCd);
        // 受注管理番号(主キー)
        order.setPurchase_order_no(orderNo);
        // 外部受注番号
        String item001 = var.getItem001();
        if (!StringTools.isNullOrEmpty(item001)) {
            order.setOuter_order_no(item001);
            if (item001.length() > 50) {
                length50Str += "受注番号、";
            } else {
                boolean regular = StringTools.regular(item001, "^[A-Za-z0-9_-]*$");
                if (!regular) {
                    errlist.add(var.getRow() + "行目[受注番号：" + item001
                        + "]：半角英数字とハイフン（-）・アンダーバー（_）ご入力ください：受注番号");
                }
            }
        } else {
            nullStr += "外部受注番号、";
        }
        // 外部注文ステータス
        order.setOuter_order_status(0);
        // 注文日時(Timestampにキャスト)
        String item005 = var.getItem005();
        Date item005Date = null;
        boolean bool = false;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (!StringTools.isNullOrEmpty(item005)) {
            item005 = CommonUtils.dateTransfer(item005);

            Timestamp timestamp = toTimestampWithNullValue(item005);
            if (!StringTools.isNullOrEmpty(timestamp)) {
                item005Date = new Date(timestamp.getTime());
            } else {
                item005Date = null;
                errDates = "注文日時、";
            }
        } else {
            bool = true;
        }
        if (bool) {
            Date date = new Date();
            String dateTime = simpleDateFormat.format(date);
            try {
                item005Date = simpleDateFormat.parse(dateTime);
            } catch (ParseException e) {
                item005Date = null;
                errDates = "注文日時、";
            }
        }
        order.setOrder_datetime(item005Date);
        // 注文種別
        String item018 = var.getItem018();
        if (!StringTools.isNullOrEmpty(item018)) {
            if (StringTools.isInteger(item018)) {
                order.setOrder_type(toInteger(item018));
                boolean regular = StringTools.regular(item018, "^[0-9]{0,8}$");
                if (!regular) {
                    errNum.add("注文種別");
                }
            } else {
                intStr += "注文種別、";
            }
        }

        // 如果csv里面为空 经过计算的商品金额合计
        int totalAmount = 0;

        // 计算出商品的合计 送料、手数料、割引金額
        // 送料
        int delivery_total = 0;
        // 手数料
        int handling_charge = 0;
        // 割引金額
        int other_fee = 0;
        for (Rk144_csv_order csvOrder : list) {
            // 通过商品code判断商品是否为同捆物，是同捆物则不计算价格
            Mc100_product productInfoByCode =
                apiCommonUtils.fetchMc100Product(clientId, csvOrder.getItem074(), csvOrder.getItem082());
            Integer productKubun = 0;
            if (!StringTools.isNullOrEmpty(productInfoByCode)) {
                productKubun = productInfoByCode.getKubun();
            }
            if (productKubun != 1) {
                // 手动计算 商品单价 * 商品个数
                String item076 = csvOrder.getItem076();
                if ("${price}".equals(item076)) {
                    // 如果商品单价为 ${price} 则需要计算单价 商品小计/个数
                    String item187 = csvOrder.getItem187();
                    if (StringTools.isNullOrEmpty(item187)) {
                        item187 = "0";
                    }
                    item076 = String.valueOf(parsePriceInt(item187) / toInteger(csvOrder.getItem077()));
                }


                if (StringTools.isInteger(item076) && StringTools.isInteger(csvOrder.getItem077())) {
                    String number = csvOrder.getItem077();
                    if (number.length() > 9) {
                        errlist.add(
                            csvOrder.getRow() + "行目[受注番号：" + csvOrder.getItem001() + "]：1以上999999999以下の数値でご入力ください：個数");
                    } else {
                        Map<String, Object> taxDivision = getTaxDivision(csvOrder);

                        // 税区分(0:税込 1:税抜 2:非課税)
                        int taxFlag = (int) taxDivision.get("taxFlag");
                        // 軽減税率(0:10% 1:8%)
                        double reduceRax = (double) taxDivision.get("reduceRax");

                        int unitPrice = parsePriceInt(item076);
                        // 如果为税拔 需要计算税率
                        if (taxFlag == 1) {
                            unitPrice += CommonUtils.calculateTheNumbers((unitPrice * reduceRax), accordion);
                        }
                        totalAmount += unitPrice * toInteger(csvOrder.getItem077());
                    }
                }
                // 商品送料合計
                delivery_total += parsePriceInt(csvOrder.getItem190());
                // 商品手数料
                handling_charge += parsePriceInt(csvOrder.getItem188());
                // 割引金額
                other_fee += parsePriceInt(csvOrder.getItem189());
            }
        }

        // 通过商品code判断商品是否为同捆物，是同捆物则不计算价格
        Mc100_product productInfoByCode =
            apiCommonUtils.fetchMc100Product(clientId, var.getItem074(), var.getItem082());
        Integer productKubun = 0;
        if (!StringTools.isNullOrEmpty(productInfoByCode)) {
            productKubun = productInfoByCode.getKubun();
        }
        // 商品金額(小計)
        if (!StringTools.isNullOrEmpty(var.getItem026()) && productKubun != 1) {
            if (StringTools.isInteger(var.getItem026())) {
                order.setProduct_price_excluding_tax(parsePriceInt(var.getItem026()));
                boolean regular = StringTools.regular(var.getItem026(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("商品金額(小計)");
                }
            } else {
                intStr += "商品金額(小計)、";
            }
        } else {
            order.setProduct_price_excluding_tax(totalAmount);
        }
        // 消費税合計
        if (!StringTools.isNullOrEmpty(var.getItem027()) && productKubun != 1) {
            if (StringTools.isInteger(var.getItem027())) {
                order.setTax_total(parsePriceInt(var.getItem027()));
                boolean regular = StringTools.regular(var.getItem027(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("消費税合計");
                }
            } else {
                intStr += "消費税合計、";
            }
        }

        // 送料合計
        if (!StringTools.isNullOrEmpty(var.getItem028()) && productKubun != 1) {
            if (StringTools.isInteger(var.getItem028())) {
                order.setDelivery_total(parsePriceInt(var.getItem028()));
                boolean regular = StringTools.regular(var.getItem028(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("送料合計");
                }
            } else {
                intStr += "送料合計、";
            }
        } else {
            order.setDelivery_total(delivery_total);
            var.setItem028(String.valueOf(delivery_total));
        }

        // 手数料
        if (!StringTools.isNullOrEmpty(var.getItem002()) && productKubun != 1) {
            if (StringTools.isInteger(var.getItem002())) {
                order.setHandling_charge(parsePriceInt(var.getItem002()));
                boolean regular = StringTools.regular(var.getItem002(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("手数料");
                }
            } else {
                intStr += "手数料、";
            }
        } else {
            order.setHandling_charge(handling_charge);
            var.setItem002(String.valueOf(handling_charge));
        }
        // 割引金額
        if (!StringTools.isNullOrEmpty(var.getItem003()) && productKubun != 1) {
            if (StringTools.isInteger(var.getItem003())) {
                boolean regular = StringTools.regular(var.getItem003(), "^[-\\+]?[0-9]{0,8}(\\.?\\d{0,2})$");
                if (!regular) {
                    errNum.add("割引金額");
                } else {
                    order.setOther_fee(Math.abs(parsePriceInt(var.getItem003())));
                }
            } else {
                intStr += "割引金額、";
            }
        } else {
            order.setOther_fee(other_fee);
            var.setItem003(String.valueOf(other_fee));
        }

        // 合計請求金額
        Integer billing_total = 0;
        if (productKubun != 1) {
            if (!StringTools.isNullOrEmpty(var.getItem030())) {
                if (StringTools.isInteger(var.getItem030())) {
                    billing_total = parsePriceInt(var.getItem030());
                    order.setBilling_total(billing_total);
                    boolean regular = StringTools.regular(var.getItem030(), "^[0-9]{0,8}(\\.?\\d{0,2})$");
                    if (!regular) {
                        errNum.add("合計請求金額");
                    }
                } else {
                    if ("#{SUM}".equals(var.getItem030())) {
                        String number1 = var.getItem026();
                        String number2 = var.getItem076();
                        String number3 = var.getItem077();
                        String number4 = var.getItem028();
                        String number5 = var.getItem002();
                        String number6 = var.getItem003();
                        // 商品金額(小計)
                        if (!StringTools.isNullOrEmpty(number1) && StringTools.isInteger(number1)) {
                            billing_total += parsePriceInt(number1);
                        } else {
                            billing_total += totalAmount;
                        }
                        // 送料合計
                        if (!StringTools.isNullOrEmpty(number4) && StringTools.isInteger(number4)) {
                            billing_total += parsePriceInt(number4);
                        }
                        // 手数料
                        if (!StringTools.isNullOrEmpty(number5) && StringTools.isInteger(number5)) {
                            billing_total += parsePriceInt(number5);
                        }
                        // 割引金額
                        if (!StringTools.isNullOrEmpty(number6) && StringTools.isInteger(number6)) {
                            billing_total = billing_total - parsePriceInt(number6);
                        }
                        order.setBilling_total(billing_total);
                    } else {
                        intStr += "合計請求金額、";
                    }
                }
            } else {
                nullStr += "合計請求金額、";
            }
        } else {
            order.setBilling_total(billing_total);
        }

        // 支付方法
        String payment = null;
        if (!StringTools.isNullOrEmpty(var.getItem013())) {
            String pay = var.getItem013();
            Integer kubun = 3; // 3代表支付方法
            payment = deliveryDao.getConverted_id(clientId, kubun, pay);
            if (StringTools.isNullOrEmpty(payment)) {
                unlistedProductIdList.add(2);
                errPayment.add(pay);
                order.setBikou10(pay);
            }
        }
        order.setPayment_method(payment);


        // 代金引換総計
        String cash_on_delivery_fee = "0";
        if (!StringTools.isNullOrEmpty(payment) && "2".equals(payment)) {
            if (!StringTools.isNullOrEmpty(var.getItem029())) {
                cash_on_delivery_fee = var.getItem029();
            } else {
                cash_on_delivery_fee = billing_total.toString();
            }
        }
        if (!StringTools.isNullOrEmpty(cash_on_delivery_fee)) {
            boolean regular = StringTools.regular(cash_on_delivery_fee, "^[0-9]{0,8}(\\.?\\d{0,2})$");
            if (!regular) {
                errNum.add("代金引換総計");
            }
        }
        if (!StringTools.isNullOrEmpty(cash_on_delivery_fee)) {
            if (StringTools.isInteger(cash_on_delivery_fee)) {
                order.setCash_on_delivery_fee(parsePriceInt(cash_on_delivery_fee));
            } else {
                intStr += "代金引換総計、";
            }
        }
        // a注文者依頼,1:需要把注文信息登录到依赖主マスタ，M0001:代表依赖主ID
        String item165 = var.getItem165();
        if (!StringTools.isNullOrEmpty(item165)) {
            if (!"1".equals(item165) && !"0".equals(item165) && !item165.contains("M")) {
                spronsorStr += "依頼主区分、";
            }

            if ("1".equals(item165)) {
                order.setOrder_flag(1);
            } else {
                order.setOrder_flag(0);
                if (item165.indexOf("M") == 0) {
                    // 判断依赖主ID是否存在，如果不存在则获取默认依赖主ID
                    String sponsor_id = toInteger(item165.substring(1)).toString();
                    List<Ms012_sponsor_master> sponsorList = sponsorDao.getSponsorList(clientId, false, sponsor_id);
                    if (sponsorList.size() > 0) {
                        order.setSponsor_id(sponsor_id);
                    } else {
                        sponsorList = sponsorDefalutList;
                        if (sponsorList.size() > 0) {
                            order.setSponsor_id(sponsorList.get(0).getSponsor_id());
                        }
                    }
                }
            }
        } else {
            order.setOrder_flag(0);
        }

        // a注文者会社名
        String item166 = var.getItem166();
        if (!StringTools.isNullOrEmpty(item166)) {
            if (item166.length() > 50) {
                length50Str += "注文者会社名、";
            } else {
                order.setOrder_company(var.getItem166());
            }
        }
        // a注文者部署
        String item167 = var.getItem167();
        if (!StringTools.isNullOrEmpty(item167)) {
            if (item167.length() > 30) {
                length30Str += "注文者部署、";
            } else {
                order.setOrder_division(var.getItem167());
            }
        }
        // 注文者郵便番号1
        String item036 = var.getItem036();
        if (!StringTools.isNullOrEmpty(item036)) {
            if (item036.length() > 8) {
                length8Str += "注文者郵便番号1、";
            } else {
                order.setOrder_zip_code1(var.getItem036().replaceAll("-", ""));
                boolean regular = StringTools.regular(var.getItem036(), "^[0-9-]*$");
                if (!regular) {
                    errZip += "注文者郵便番号1、";
                }
            }
        }
        // 注文者郵便番号2
        String item037 = var.getItem037();
        if (!StringTools.isNullOrEmpty(item037)) {
            if (item037.length() > 4) {
                length4Str += "注文者郵便番号2、";
            } else {
                order.setOrder_zip_code2(var.getItem037());
                boolean regular = StringTools.regular(var.getItem037(), "^[0-9]{0,8}$");
                if (!regular) {
                    errNum.add("注文者郵便番号2");
                }
            }
        }

        if (!StringTools.isNullOrEmpty(item036) && !StringTools.isNullOrEmpty(item037)) {
            String orderZip = (item036 + item037).replaceAll("-", "");
            if (orderZip.length() > 7) {
                errZip += "注文者郵便番号、";
            }
        }

        // a注文者住所都道府県
        if (var.getItem038() != null && !"".equals(var.getItem038()) && "1".equals(var.getItem039())) {
            if (var.getItem038().length() < 4) {
                order.setOrder_todoufuken(var.getItem038());
                // a注文者住所郡市区
                order.setOrder_address1(var.getItem039());
            } else {
                if ("神奈川県".equals(var.getItem038().substring(0, 4)) || "鹿児島県".equals(var.getItem038().substring(0, 4))
                    || "和歌山県".equals(var.getItem038().substring(0, 4))) {
                    order.setOrder_todoufuken(var.getItem038().substring(0, 4));
                    order.setOrder_address1(var.getItem038().substring(4));
                } else {
                    order.setOrder_todoufuken(var.getItem038().substring(0, 3));
                    order.setOrder_address1(var.getItem038().substring(3));
                }
            }
        } else {
            order.setOrder_todoufuken(var.getItem038());
            order.setOrder_address1(var.getItem039());
        }
        if (!StringTools.isNullOrEmpty(var.getItem038())) {
            // 获取到注文者都道府県
            String orderTodoufuken = order.getOrder_todoufuken();
            // 判断是该都道府県是否真实存在
            boolean contains = Arrays.asList(PREFECTURES).contains(orderTodoufuken);
            if (!contains) {
                errlist.add(var.getRow() + "行目[受注番号：" + order.getOuter_order_no() + "]：正しい都道府県で始まる住所をご入力ください：注文者都道府県");
                // throw new JsonException(ErrorEnum.E_10001);
            }
        }
        // 注文者詳細住所
        String item040 = var.getItem040();
        if (!StringTools.isNullOrEmpty(item040)) {
            if (item040.length() > 100) {
                length100Str += "注文者詳細住所、";
            } else {
                order.setOrder_address2(var.getItem040());
            }
        }
        // 注文者姓
        String item041 = var.getItem041();
        if (!StringTools.isNullOrEmpty(item041)) {
            if (item041.length() > 50) {
                length50Str += "注文者姓、";
            } else {
                order.setOrder_family_name(var.getItem041());
            }
        }
        // 注文者名
        String item042 = var.getItem042();
        if (!StringTools.isNullOrEmpty(item042)) {
            if (item042.length() > 50) {
                length50Str += "注文者名、";
            } else {
                order.setOrder_first_name(var.getItem042());
            }
        }
        // 注文者姓カナ
        String item043 = var.getItem043();
        if (!StringTools.isNullOrEmpty(item043)) {
            if (item043.length() > 32) {
                length32Str += "注文者姓カナ、";
            } else {
                order.setOrder_family_kana(var.getItem043());
            }
        }
        // 注文者名カナ
        String item044 = var.getItem044();
        if (!StringTools.isNullOrEmpty(item044)) {
            if (item044.length() > 32) {
                length32Str += "注文者名カナ、";
            } else {
                order.setOrder_first_kana(var.getItem044());
            }
        }
        // 注文者電話番号1
        String phone_regex = "^[0-9-+]*$";
        String item045 = CommonUtils.toTelNumber(var.getItem045());
        String item046 = CommonUtils.toTelNumber(var.getItem046());
        String item047 = CommonUtils.toTelNumber(var.getItem047());
        String phoneNo = item045 + item046 + item047;
        if (!StringTools.isNullOrEmpty(phoneNo)) {
            if (phoneNo.length() > 20) {
                length20Str += "注文者電話番号、";
            } else {
                // 注文者電話番号1
                if (!StringTools.isNullOrEmpty(item045)) {
                    order.setOrder_phone_number1(item045);
                    if (item045.matches(phone_regex) == false) {
                        errPhone += "注文者電話番号1、";
                    }
                }
                // 注文者電話番号2
                if (!StringTools.isNullOrEmpty(item046)) {
                    order.setOrder_phone_number2(item046);
                    if (item046.matches(phone_regex) == false) {
                        errPhone += "注文者電話番号2、";
                    }
                }
                // 注文者電話番号3
                if (!StringTools.isNullOrEmpty(item047)) {
                    order.setOrder_phone_number3(item047);
                    if (item047.matches(phone_regex) == false) {
                        errPhone += "注文者電話番号3、";
                    }
                }
            }
        }
        // 注文者メールアドレス
        String item048 = var.getItem048();
        if (!StringTools.isNullOrEmpty(item048)) {
            if (item048.length() > 100) {
                length100Str += "注文者メールアドレス、";
            } else {
                order.setOrder_mail(var.getItem048());
                boolean regular = StringTools.regular(var.getItem048(), "^[A-Za-z0-9-._@]*$");
                if (!regular) {
                    errMail += "注文者メールアドレス、";
                }
            }
        }
        // 注文者性別(男:0 女:1)
        if (!StringTools.isNullOrEmpty(var.getItem049())) {
            if (StringTools.isInteger(var.getItem049())) {
                if (!"0".equals(var.getItem049()) && !"1".equals(var.getItem049())) {
                    intStr += "注文者性別(男:0 女:1)、";
                }
                order.setOrder_gender(toInteger(var.getItem049()));
                boolean regular = StringTools.regular(var.getItem049(), "^[0-9]{0,8}$");
                if (!regular) {
                    errNum.add("注文者性別");
                }
            } else {
                intStr += "注文者性別(男:0 女:1)、";
            }
        }
        // a配送先会社名
        String item168 = var.getItem168();
        if (!StringTools.isNullOrEmpty(item168)) {
            if (item168.length() > 50) {
                length50Str += "配送先会社名、";
            } else {
                order.setReceiver_company(var.getItem168());
            }
        }
        // a配送先部署
        String item169 = var.getItem169();
        if (!StringTools.isNullOrEmpty(item169)) {
            if (item169.length() > 30) {
                length30Str += "配送先部署、";
            } else {
                order.setReceiver_division(var.getItem169());
            }
        }
        // 配送先郵便番号1 tips：邮编验证需要优化
        if (!StringTools.isNullOrEmpty(var.getItem059())) {
            if (var.getItem059().length() > 8) {
                length8Str += "配送先郵便番号1、";
            }
            boolean regular = StringTools.regular(var.getItem059(), "^[0-9-]*$");
            if (!regular) {
                errZip += "配送先郵便番号1、";
            }

        } else {
            nullStr += "配送先郵便番号1、";
        }
        order.setReceiver_zip_code1(var.getItem059().replaceAll("-", ""));
        // 配送先郵便番号2
        String item060 = var.getItem060();
        if (!StringTools.isNullOrEmpty(item060)) {
            if (item060.length() > 4) {
                length4Str += "配送先郵便番号2、";
            } else {
                order.setReceiver_zip_code2(item060);
                boolean regular = StringTools.regular(var.getItem060(), "^[0-9]{0,8}$");
                if (!regular) {
                    errNum.add("配送先郵便番号2");
                }
            }
        }

        if (!StringTools.isNullOrEmpty(var.getItem059()) && !StringTools.isNullOrEmpty(item060)) {
            String postcode = (var.getItem059() + item060).replaceAll("-", "");
            if (postcode.length() > 7) {
                errZip += "配送先郵便番号、";
            }
        }

        // 配送先都道府県
        if (var.getItem061() != null && !"".equals(var.getItem061()) && "1".equals(var.getItem062())) {
            if (var.getItem061().length() < 4) {
                order.setReceiver_todoufuken(var.getItem061());
                // 配送先住所郡市区
                order.setReceiver_address1(var.getItem062());
            } else {
                // 都道府县分割处理
                if ("神奈川県".equals(var.getItem061().substring(0, 4)) || "鹿児島県".equals(var.getItem061().substring(0, 4))
                    || "和歌山県".equals(var.getItem061().substring(0, 4))) {
                    order.setReceiver_todoufuken(var.getItem061().substring(0, 4));
                    order.setReceiver_address1(var.getItem061().substring(4));
                } else {
                    order.setReceiver_todoufuken(var.getItem061().substring(0, 3));
                    order.setReceiver_address1(var.getItem061().substring(3));
                }
            }
        } else {
            if (!StringTools.isNullOrEmpty(var.getItem061())) {
                order.setReceiver_todoufuken(var.getItem061());
            } else {
                nullStr += "配送先都道府県、";
            }
            if (!StringTools.isNullOrEmpty(var.getItem062())) {
                order.setReceiver_address1(var.getItem062());
            } else {
                nullStr += "配送先郡市区(固定値1:都道府県から分割)、";
            }
        }

        if (!StringTools.isNullOrEmpty(var.getItem061())) {
            // 获取到配送先都道府県
            String receiverTodoufuken = order.getReceiver_todoufuken();
            // 判断是该都道府県是否真实存在
            boolean contains = Arrays.asList(PREFECTURES).contains(receiverTodoufuken);
            if (!contains) {
                errlist.add(var.getRow() + "行目[受注番号：" + order.getOuter_order_no() + "]：正しい都道府県で始まる住所をご入力ください：配送先都道府県");
            }
        }

        // 備考印字(1:有 0：無)
        String item051 = var.getItem051();
        if (!StringTools.isNullOrEmpty(item051)) {
            if (!"0".equals(item051) && !"1".equals(item051)) {
                boolStr += "備考印字、";
            }
        }
        order.setBikou_flg(toInteger(var.getItem051()));
        // 明細同梱設定
        if ("1".equals(var.getItem054())) {
            order.setDetail_bundled("同梱する");
            // 不是注文者，并且同捆设定不为1、0、或者为空，Detail_bundled设为‘’
        } else if (order.getOrder_flag() == 0 && (StringTools.isNullOrEmpty(var.getItem054())
            || !"1".equals(var.getItem054()) && !"0".equals(var.getItem054()))) {
            order.setDetail_bundled("");
        } else {
            order.setDetail_bundled("同梱しない");
        }
        // 明細書金額印字
        String item053 = var.getItem053();
        if (!StringTools.isNullOrEmpty(item053)) {
            if (!"0".equals(item053) && !"1".equals(item053)) {
                boolStr += "明細書金額印字、";
            }
        }
        order.setDetail_price_print(var.getItem053());
        // 明細書メッセージ
        String item052 = var.getItem052();
        if (!StringTools.isNullOrEmpty(item052)) {
            if (item052.length() > 500) {
                length500Str += "明細書メッセージ、";
            }
            order.setDetail_message(item052);
        }
        // 出荷希望日
        boolean dateFlag = true;
        if (!StringTools.isNullOrEmpty(var.getItem055())) {
            Timestamp timestamp = toTimestampWithNullValue(var.getItem055());
            if (!StringTools.isNullOrEmpty(timestamp)) {
                order.setShipment_wish_date(new Date(timestamp.getTime()));
            } else {
                dateFlag = false;
                errDates = "出荷希望日、";
            }
        }

        // 配達担当者
        String item056 = var.getItem056();
        if (!StringTools.isNullOrEmpty(item056)) {
            if (item056.length() > 50) {
                length50Str += "配達担当者、";
            } else {
                order.setDeliveryman(var.getItem056());
            }
        }
        // 配送先性別
        if (!StringTools.isNullOrEmpty(var.getItem057())) {
            if (StringTools.isInteger(var.getItem057())) {
                order.setReceiver_gender(toInteger(var.getItem057()));
                boolean regular = StringTools.regular(var.getItem057(), "^[0-9]{0,8}$");
                if (!regular) {
                    errNum.add("配送先性別");
                }
            } else {
                intStr += "配送先性別、";
            }
        }
        // 配送先メールアドレス
        String item058 = var.getItem058();
        if (!StringTools.isNullOrEmpty(item058)) {
            if (item058.length() > 100) {
                length100Str += "配送先メールアドレス、";
            } else {
                order.setReceiver_mail(var.getItem058());
                boolean regular = StringTools.regular(var.getItem058(), "^[A-Za-z0-9-._@]*$");
                if (!regular) {
                    errMail += "配送先メールアドレス";
                }
            }
        }


        // 作業指示書備考1
        String item145 = var.getItem145();
        if (!StringTools.isNullOrEmpty(item145)) {
            if (item145.length() > 100) {
                length100Str += "作業指示書備考1、";
            } else {
                order.setBikou1(var.getItem145());
            }
        }
        // 作業指示書備考2
        String item146 = var.getItem146();
        if (!StringTools.isNullOrEmpty(item146)) {
            if (item146.length() > 100) {
                length100Str += "作業指示書備考2、";
            } else {
                order.setBikou2(var.getItem146());
            }
        }
        // 作業指示書備考3
        String item147 = var.getItem147();
        if (!StringTools.isNullOrEmpty(item147)) {
            if (item147.length() > 100) {
                length100Str += "作業指示書備考3、";
            } else {
                order.setBikou3(var.getItem147());
            }
        }
        // 作業指示書備考4
        String item148 = var.getItem148();
        if (!StringTools.isNullOrEmpty(item148)) {
            if (item148.length() > 100) {
                length100Str += "作業指示書備考4、";
            } else {
                order.setBikou4(var.getItem148());
            }
        }
        // 作業指示書備考5
        String item149 = var.getItem149();
        if (!StringTools.isNullOrEmpty(item149)) {
            if (item149.length() > 100) {
                length100Str += "作業指示書備考5、";
            } else {
                order.setBikou5(var.getItem149());
            }
        }
        // 作業指示書備考6
        String item150 = var.getItem150();
        if (!StringTools.isNullOrEmpty(item150)) {
            if (item150.length() > 100) {
                length100Str += "作業指示書備考6、";
            } else {
                order.setBikou6(var.getItem150());
            }
        }
        // 作業指示書備考7
        String item151 = var.getItem151();
        if (!StringTools.isNullOrEmpty(item151)) {
            if (item151.length() > 100) {
                length100Str += "作業指示書備考7、";
            } else {
                order.setBikou7(var.getItem151());
            }
        }
        // 作業指示書備考8
        String item152 = var.getItem152();
        if (!StringTools.isNullOrEmpty(item152)) {
            if (item152.length() > 100) {
                length100Str += "作業指示書備考8、";
            } else {
                order.setBikou8(var.getItem152());
            }
        }
        // 作業指示書備考9
        String item153 = var.getItem153();
        if (!StringTools.isNullOrEmpty(item153)) {
            if (item153.length() > 100) {
                length100Str += "作業指示書備考9、";
            } else {
                order.setBikou9(var.getItem153());
            }
        }
        // 作業指示書備考10
        String item154 = var.getItem154();
        if (!StringTools.isNullOrEmpty(item154)) {
            if (item154.length() > 100) {
                length100Str += "作業指示書備考10、";
            } else {
                order.setBikou10(var.getItem154());
            }
        }

        // a配達希望日
        if (!StringTools.isNullOrEmpty(var.getItem096())) {
            Timestamp timestamp = toTimestampWithNullValue(var.getItem096());
            if (!StringTools.isNullOrEmpty(timestamp)) {
                order.setDelivery_date(new Date(timestamp.getTime()));
            } else {
                dateFlag = false;
                errDates = "配達希望日、";
            }
        }

        // 判断配達希望日不能小于出荷希望日
        if (!StringTools.isNullOrEmpty(var.getItem055()) && !StringTools.isNullOrEmpty(var.getItem096()) && dateFlag) {
            if (toTimestampWithNullValue(var.getItem055()).getTime() > toTimestampWithNullValue(var.getItem096())
                .getTime()) {
                errlist.add(var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no()
                    + "]：「配達希望日」には、「出荷希望日」より後の日付をご指定ください");
            }
        }
        // aギフト配送希望
        String item100 = var.getItem100();
        if (!StringTools.isNullOrEmpty(item100)) {
            if (!"1".equals(item100) && !"0".equals(item100)) {
                boolStr += "ギフト配送希望、";
            }
        }
        order.setGift_wish(String.valueOf(toInteger(var.getItem100())));

        String convertedId = "";
        String method = "";
        // csv获取到配送方法
        Ms004_delivery ms004 = new Ms004_delivery();
        String item144 = var.getItem144();

        if (!StringTools.isNullOrEmpty(item144)) {
            // a如果csv 或者 固定值 设置了配送方法
            Integer kubun = 1; // 1代表配送方法
            // a从店舗側連携設定 里面获取有没有相对应的值， 没有直接报错
            convertedId = deliveryDao.getConverted_id(clientId, kubun, item144);
        }
        if (StringTools.isNullOrEmpty(convertedId)) {
            // 如果为空 查询店铺默认配送方法
            Ms201_client clientInfo = clientDao.getClientInfo(clientId);
            convertedId = clientInfo.getDelivery_method();
        }
        if (!StringTools.isNullOrEmpty(convertedId)) {
            // a查询出配送公司
            ms004 = deliveryDao.getDeliveryById(convertedId);
            method = ms004.getDelivery_method();
        } else {
            if (StringTools.isNullOrEmpty(item144)) {
                errlist.add(var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]： " +
                    "配送方法が設定されておりません。設定メニューから店舗情報の配送関連設定で、「デフォルト配送方法」または、「配送方法」のルールを設定いただけますと、受注CSVが取り込めます。");
            } else {
                errlist.add(var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]： 配送方法「" + item144
                    + "」が設定されておりません。設定メニューから店舗情報の配送関連設定で、「デフォルト配送方法」または、「配送方法」のルールを設定いただけますと、受注CSVが取り込めます。");
            }
        }
        // a配送会社
        order.setDelivery_company(convertedId);
        // a配送方法指定
        order.setDelivery_method(method);
        // 配達希望時間帯
        String deliveryTimeId = null;
        if (!StringTools.isNullOrEmpty(var.getItem095())) {
            String time_slot = var.getItem095();
            deliveryTimeId =
                CommonUtils.getDeliveryTimeId(time_slot, ms004.getDelivery_nm(), ms007Maps, deliveryDao, clientId);

            // 如果time为空 则说明都没有匹配到
            if (StringTools.isNullOrEmpty(deliveryTimeId)) {
                errDeliveryTime.add(time_slot);
            }
        }
        order.setDelivery_time_slot(deliveryTimeId);
        String item063 = var.getItem063();
        // a配送先詳細住所
        order.setReceiver_address2(item063);
        // a配送先姓
        if (!StringTools.isNullOrEmpty(var.getItem064())) {
            order.setReceiver_family_name(var.getItem064());
        } else {
            nullStr += "配送先姓、";
        }
        // 送り状特記事項
        if (!StringTools.isNullOrEmpty(var.getItem191())) {
            order.setInvoice_special_notes(var.getItem191());
        } else {
            order.setInvoice_special_notes("");
        }
        // a配送先名
        if (var.getItem065() == null) {
            var.setItem065("");
        }
        order.setReceiver_first_name(var.getItem065());
        // 配送先姓カナ
        String item066 = var.getItem066();
        if (!StringTools.isNullOrEmpty(item066)) {
            if (item066.length() > 32) {
                length32Str += "配送先姓カナ、";
            } else {
                order.setReceiver_family_kana(var.getItem066());
            }
        }
        // 配送先名カナ
        String item067 = var.getItem067();
        if (!StringTools.isNullOrEmpty(item067)) {
            if (item067.length() > 32) {
                length32Str += "配送先名カナ、";
            } else {
                order.setReceiver_first_kana(var.getItem067());
            }
        }

        // 配送先電話番号3
        String item068 = CommonUtils.toTelNumber(var.getItem068());
        String item069 = CommonUtils.toTelNumber(var.getItem069());
        String item070 = CommonUtils.toTelNumber(var.getItem070());
        String phoneNum = item068 + item069 + item070;
        if (!StringTools.isNullOrEmpty(phoneNum)) {
            if (phoneNum.length() > 20) {
                length20Str += "配送先電話番号、";
            } else {
                // 配送先電話番号1
                if (!StringTools.isNullOrEmpty(item068)) {
                    if (item068.length() > 20) {
                        length20Str += "配送先電話番号1、";
                    } else {
                        order.setReceiver_phone_number1(item068);
                        if (!StringTools.regular(item068, "^[0-9-+]*$")) {
                            errPhone += "配送先電話番号1、";
                        }
                    }
                }
                // 配送先電話番号2
                if (!StringTools.isNullOrEmpty(item069)) {
                    if (item069.length() > 20) {
                        length20Str += "配送先電話番号2、";
                    } else {
                        order.setReceiver_phone_number2(item069);
                        if (!StringTools.regular(item069, "^[0-9-+]*$")) {
                            errPhone += "配送先電話番号2、";
                        }
                    }
                }
                // 配送先電話番号3
                if (!StringTools.isNullOrEmpty(item070)) {
                    if (item070.length() > 20) {
                        length20Str += "配送先電話番号3、";
                    } else {
                        order.setReceiver_phone_number3(item070);
                        if (!StringTools.regular(item070, "^[0-9-+]*$")) {
                            errPhone += "配送先電話番号3、";
                        }
                    }
                }
            }
        }
        // 品名
        String item172 = var.getItem172();
        if (!StringTools.isNullOrEmpty(item172)) {
            if (item172.length() > 50) {
                length50Str += "品名、";
            } else {
                order.setLabel_note(item172);
            }
        }
        // 不在時宅配ボックス
        String item170 = var.getItem170();
        if (!StringTools.isNullOrEmpty(item170)) {
            if (!"1".equals(item170) && !"0".equals(item170)) {
                boolStr += "不在時宅配ボックス、";
            }
        }
        order.setBox_delivery(toInteger(var.getItem170()));
        // 割れ物注意
        String item171 = var.getItem171();
        if (!StringTools.isNullOrEmpty(item171)) {
            if (!"1".equals(item171) && !"0".equals(item171)) {
                boolStr += "割れ物注意、";
            }
        }
        order.setFragile_item(toInteger(var.getItem171()));
        // 定期購入ID
        String item181 = var.getItem181();
        if (!StringTools.isNullOrEmpty(item181)) {
            order.setBuy_id(item181);
        }
        // 定期購入回数
        String item182 = var.getItem182();
        if (!StringTools.isNullOrEmpty(item182)) {
            boolean regular = StringTools.regular(item182, "^[0-9]{0,8}$");
            if (!regular) {
                errNum.add("定期購入回数");
            }
            order.setBuy_cnt(toInteger(item182));
        }
        // 次回お届け予定日
        String item183 = var.getItem183();
        Date item183Date;
        if (!StringTools.isNullOrEmpty(item183)) {
            item183 = CommonUtils.dateTransfer(item183);

            Timestamp timestamp = toTimestampWithNullValue(item183);
            if (!StringTools.isNullOrEmpty(timestamp)) {
                item183Date = new Date(timestamp.getTime());
            } else {
                item183Date = null;
                errDates = "次回お届け予定日、";
            }
            order.setNext_delivery_date(item183Date);
        }
        // 購入者備考欄
        String item184 = var.getItem184();
        if (!StringTools.isNullOrEmpty(item184)) {
            if (item184.length() > 500) {
                length500Str += "購入者備考欄、";
            } else {
                order.setMemo(item184);
            }
        }
        // インポート時間
        order.setImport_datetime(toTimestamp());
        // 登録時間
        order.setIns_date(toTimestamp());
        // 1:法人／2:個人（受注自動読み込むの場合、formは固定に個人にする）
        order.setForm(2);
        // 結果を返す

        String errorMess = "";
        if (!StringTools.isNullOrEmpty(length16Str)) {
            length16Str = length16Str.substring(0, length16Str.length() - 1);
            // errlist.add("20文字（全角は16文字）以内でご入力ください：" + length16Str);
            errlist.add(var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length16Str
                + "を20文字（全角は16文字）以内でご入力ください");
            // throw new JsonException(ErrorEnum.E_10001);
        }
        if (!StringTools.isNullOrEmpty(length4Str)) {
            length4Str = length4Str.substring(0, length4Str.length() - 1);
            // errLength.add("4文字以内でご入力ください：" + length4Str);
            errlist
                .add(var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length4Str + "を4文字以内でご入力ください");
            // throw new JsonException(ErrorEnum.E_10001);
        }
        if (!StringTools.isNullOrEmpty(length8Str)) {
            length8Str = length8Str.substring(0, length8Str.length() - 1);
            errlist
                .add(var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length8Str + "を8文字以内でご入力ください");
            // errLength.add("8文字以内でご入力ください：" + length8Str);
            // throw new JsonException(ErrorEnum.E_10001);
        }
        if (!StringTools.isNullOrEmpty(length10Str)) {
            length10Str = length10Str.substring(0, length10Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length10Str + "を10文字以内でご入力ください");
            // errLength.add("10文字以内でご入力ください：" + length10Str);
            // throw new JsonException(ErrorEnum.E_10001);
        }
        if (!StringTools.isNullOrEmpty(length20Str)) {
            length20Str = length20Str.substring(0, length20Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length20Str + "を20文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length30Str)) {
            length30Str = length30Str.substring(0, length30Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length30Str + "を30文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length32Str)) {
            length32Str = length32Str.substring(0, length32Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length32Str + "を32文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length50Str)) {
            length50Str = length50Str.substring(0, length50Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length50Str + "を50文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length100Str)) {
            length100Str = length100Str.substring(0, length100Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length100Str + "を100文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length160Str)) {
            length160Str = length160Str.substring(0, length160Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length160Str + "を160文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length300Str)) {
            length300Str = length300Str.substring(0, length300Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length300Str + "を300文字以内でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(length500Str)) {
            length500Str = length500Str.substring(0, length500Str.length() - 1);
            errlist.add(
                var.getRow() + "行目" + "[受注番号：" + order.getOuter_order_no() + "]：" + length500Str + "を500文字以内でご入力ください");
        }
        // if (!StringTools.isNullOrEmpty(nullStr)) {
        // nullStr = nullStr.substring(0, nullStr.length() - 1);
        // errorMess += nullStr;
        // }
        String orderNum = StringTools.isNullOrEmpty(order.getOuter_order_no()) ? "" : order.getOuter_order_no();
        if (!StringTools.isNullOrEmpty(nullStr)) {
            errlist.add(var.getRow() + "行目[受注番号：" + orderNum + "]：" + nullStr + "を必須項目にご入力ください");
            // throw new JsonException(ErrorEnum.E_10001);
        }
        if (!StringTools.isNullOrEmpty(intStr)) {
            intStr = intStr.substring(0, intStr.length() - 1);
            errorMess += intStr + "の形式が正しくありません、数字のみご入力ください。";
        }
        if (!StringTools.isNullOrEmpty(boolStr)) {
            boolStr = boolStr.substring(0, boolStr.length() - 1);
            errlist.add(var.getRow() + "行目[受注番号：" + orderNum + "]：" + boolStr + "を0（無し）または、1（有り）をご入力ください");
        }
        if (!StringTools.isNullOrEmpty(spronsorStr)) {
            errlist
                .add(var.getRow() + "行目[受注番号：" + orderNum + "]：「依頼主区分」項目の入力値は固定値１か、頭文字「Mxxxxxx」(※依頼主ID)の文字しか入力できません");
        }
        if (!StringTools.isNullOrEmpty(errMail)) {
            errlist.add(var.getRow() + "行目[受注番号：" + orderNum + "]：半角英数字とハイフン（-）・ドット（.）・アンダーバー（_）・アットマーク（@）でご入力ください："
                + errMail);
        }
        if (!StringTools.isNullOrEmpty(errZip)) {
            errlist.add(var.getRow() + "行目[受注番号：" + orderNum
                + "]" + errZip + "：半角数字とハイフン（-）でご入力ください（例：123-0001 または 123 または1230001）");
        }
        if (!StringTools.isNullOrEmpty(errPhone)) {
            errlist.add(var.getRow() + "行目[受注番号：" + orderNum + "]：" + errPhone + "を半角数字とハイフン（-）とプラス（+）でご入力ください");
        }
        if (!StringTools.isNullOrEmpty(errDates)) {
            errlist.add(var.getRow() + "行目[受注番号：" + orderNum + "]：" + (errDates.substring(0, errDates.length() - 1))
                + "を日付形式が不正です。yyyy-MM-dd, yyyyMMdd, yyyy/MM/ddの形式で入力してください");
        }

        if (!StringTools.isNullOrEmpty(errorMess)) {
            errField.add(errorMess);
            // throw new JsonException(ErrorEnum.E_10001);
        }
        // if (errlist.size() != 0) {
        // throw new JsonException(ErrorEnum.E_10001);
        // }
        return order;
    }

    /**
     * @return 現在日付(Time)を返す
     */
    private Timestamp toTimestamp() {
        return toTimestamp("");
    }

    /**
     * @return Timestamp
     * @Description 値をDataに変換する処理
     * @Param String
     */
    public Timestamp toTimestamp(String str) {
        // 現在日時を取得
        Date date = new Date();
        // 値をDataに変換する処理
        if (!Strings.isNullOrEmpty(str)) {
            date = toDate(str, "yyyy/MM/dd");
            if (StringTools.isNullOrEmpty(date)) {
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 現在日時を取得
        // Timestamp型へ変換

        return new Timestamp(date.getTime());

    }

    public static Timestamp toTimestampWithNullValue(String str) {
        // 値をDataに変換する処理
        if (StringTools.isNullOrEmpty(str)) {
            return null;
        }

        // 时间格式，数组内容时间必须由长到短的顺序定义
        String[] formatArray = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd",
            "yyyyMMdd HH:mm:ss",
            "yyyyMMdd",
            "yyyy-M-d HH:mm:ss",
            "yyyy-M-d",
            "yyyy/MM/dd HH:mm:ss",
            "yyyy/MM/dd",
            "yyyy/M/d HH:mm:ss",
            "yyyy/M/d",
            "yyyyMd HH:mm:ss",
            "yyyyMd",
        };

        SimpleDateFormat dateFormat = new SimpleDateFormat();
        Date date = null;
        Timestamp timestamp;

        try {
            for (String format : formatArray) {
                dateFormat.applyPattern(format);
                dateFormat.setLenient(false);
                ParsePosition parsePosition = new ParsePosition(0);
                date = dateFormat.parse(str, parsePosition);
                if (!StringTools.isNullOrEmpty(date)) {
                    break;
                }
            }

            timestamp = new Timestamp(date.getTime());
        } catch (Exception e) {
            timestamp = null;
        }

        return timestamp;
    }

    /**
     * @return Date
     * @Description 値をDataに変換する処理
     * @Param String
     */
    private Date toDate(String str, String ymd) {
        // 現在日時を取得
        Date date;
        // フォーマット
        SimpleDateFormat sdf = new SimpleDateFormat(ymd);

        try {
            date = sdf.parse(str);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getDelivery_method(String method, String company) {
        String tem = null;
        if (!"1003".equals(company)) {
            tem = "宅配便";
        } else {
            if ("1".equals(method)) {
                tem = "ポスト便（宅配便スピード）";
            }
            if ("2".equals(method)) {
                tem = "宅配便";
            }
        }
        return tem;
    }

    private Integer getOtherFee(String str1, String str2) {
        int int1 = toInteger(str1);
        int int2 = toInteger(str2);
        return (int1 + int2);
    }

    /**
     * @return int
     * @Description 値を数字に変換する処理
     * @Param String
     */
    private Integer toInteger(String str) {
        String var = "0";
        if (!Strings.isNullOrEmpty(str)) {
            if ("男".equals(str)) {
                var = "0";
            } else if ("女".equals(str)) {
                var = "1";
            } else {
                var = str;
                if ('+' == (str.charAt(0))) {
                    var = var.substring(1);
                }
            }
        }
        return Ints.tryParse(var);
    }

    /**
     * @param str : 需要进行转化的值
     * @description: 金额转换为int 如果带小数点 取最小整数
     * @return: int
     * @date: 2021/11/30 13:30
     */
    private int parsePriceInt(String str) {
        int price = 0;
        if (StringTools.isNullOrEmpty(str)) {
            return price;
        }
        return (int) Double.parseDouble(str);
    }

    /**
     * csvをMapに変換する
     *
     * @param reader 文字ストリーム抽象クラス
     * @return Map<String, Collection < OrderItemEntity>>
     * @author wangquansheng
     * @date 2020-07-16
     */
    public Map<String, Collection<Rk144_csv_order>> csvToMap(Reader reader, String client_id, Integer template_cd,
        String company_id) {
        try {
            // CsvToBeanBuilder<Rk144_csv_order> csvToBeanBuilder = new
            // CsvToBeanBuilder<Rk144_csv_order>(reader);
            //
            // CsvToBean<Rk144_csv_order> csvToBean =
            // csvToBeanBuilder.withType(Rk144_csv_order.class).build();
            CsvToBean<Rk144_csv_order> csvToBean = new CsvToBeanBuilder<Rk144_csv_order>(reader)
                .withType(Rk144_csv_order.class).build();
            List<Rk144_csv_order> items = csvToBean.parse();

            // a同一の注文番号として、取込単位
            // Multimap<String, Rk144_csv_order> maps = LinkedListMultimap.create();
            // 使用LinkedListMultimap维持插入的顺序，以及键的插入顺序。
            LinkedListMultimap<String, Rk144_csv_order> maps =
                LinkedListMultimap.create(items.size() >= 8 ? items.size() * 2 : 16);
            int row = 2;
            for (Rk144_csv_order item : items) {
                // 插入数据行数，2开始
                item.setRow(row);
                maps.put(item.getItem001(), item);
                row++;
            }
            // a結果を返す
            return maps.asMap();
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseException.print(e));
            return null;
        }
    }

    @Override
    public JSONObject getOrderDateList(String client_id, Integer status, Integer del_flg, Integer page, Integer size,
        String column, String sort, String start_date, String end_date, List<String> checkList,
        String orderType, String search, String request_date_start, String request_date_end, String delivery_date_start,
        String delivery_date_end, String form, String identifier) {

        if (!hasNtmEccubeFunction("", client_id, "4")) {
            return getSunlogiOrderDateList(client_id, status, del_flg, page, size, column, sort, start_date, end_date);
        }

        // 处理模糊查询拼接%
        if (!StringTools.isNullOrEmpty(search)) {
            search = "%" + search + "%";
        }
        if (!StringTools.isNullOrEmpty(identifier)) {
            identifier = identifier + "%";
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        if (!StringTools.isNullOrEmpty(start_date)) {
            try {
                startDate = format.parse(start_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringTools.isNullOrEmpty(end_date)) {
            try {
                endDate = format.parse(end_date);
                // endDate处理
                endDate = CommonUtils.getDateEnd(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        Integer outer_order_status = null;
        if (!StringTools.isNullOrEmpty(status) && status != 1) {
            outer_order_status = 0;
        }
        // PageHelper.startPage(page, size);
        String requestStartDate =
            StringTools.isNullOrEmpty(request_date_start) ? null : "" + Timestamp.valueOf(request_date_start);
        String requestEndDate =
            StringTools.isNullOrEmpty(request_date_end) ? null : "" + Timestamp.valueOf(request_date_end);
        String deliveryDateStart =
            StringTools.isNullOrEmpty(delivery_date_start) ? null : "" + Timestamp.valueOf(delivery_date_start);
        String deliveryDateEnd =
            StringTools.isNullOrEmpty(delivery_date_end) ? null : "" + Timestamp.valueOf(delivery_date_end);
        List<Tc200_order> orderList = orderDao.getOrderList(client_id, outer_order_status, del_flg, column, sortType,
            startDate, endDate, orderType, search, requestStartDate, requestEndDate, deliveryDateStart, deliveryDateEnd,
            form, identifier);

        // 根据注文者都道府县获取配送时长List
        List<String> todoufukenList = orderList.stream().map(Tc200_order::getOrder_todoufuken)
            .filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<Mw410_delivery_takes_days> deliveryTakesDaysList =
            deliveryTakesDaysDao.getDeliveryTakesDaysListBySender(todoufukenList);

        // 获取到所有的配送公司信息
        List<Ms004_delivery> deliveryInfo = deliveryDao.getDeliveryInfo(null);

        // PageInfo<Tc200_order> pageInfo = new PageInfo<>(orderList);

        JSONObject resultJsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        String finalIdentifier = identifier;
        orderList.forEach(order -> {
            JSONObject orderJsonObject = new JSONObject();
            orderJsonObject.put("purchase_order_no", order.getPurchase_order_no());
            orderJsonObject.put("form", order.getForm());
            orderJsonObject.put("outer_order_no", order.getOuter_order_no());
            orderJsonObject.put("outer_order_status", order.getOuter_order_status());
            orderJsonObject.put("order_datetime", order.getOrder_datetime());
            // orderJsonObject.put("order_detail_no", order.getOrder_detail_no());
            orderJsonObject.put("receiver_family_name", order.getReceiver_family_name());
            orderJsonObject.put("receiver_first_name", order.getReceiver_first_name());
            orderJsonObject.put("receiver_zip_code1", order.getReceiver_zip_code1());
            orderJsonObject.put("receiver_zip_code2", order.getReceiver_zip_code2());

            orderJsonObject.put("receiver_todoufuken", order.getReceiver_todoufuken());
            orderJsonObject.put("receiver_address1", order.getReceiver_address1());
            orderJsonObject.put("receiver_address2", order.getReceiver_address2());

            orderJsonObject.put("receiver_phone_number1", order.getReceiver_phone_number1());
            orderJsonObject.put("receiver_phone_number2", order.getReceiver_phone_number2());
            orderJsonObject.put("receiver_phone_number3", order.getReceiver_phone_number3());
            orderJsonObject.put("receiver_mail", order.getReceiver_mail());

            orderJsonObject.put("shipment_plan_id", order.getShipment_plan_id());
            orderJsonObject.put("gift_wish", order.getGift_wish());

            orderJsonObject.put("delivery_company", order.getDelivery_company());
            orderJsonObject.put("delivery_method", order.getDelivery_method());
            orderJsonObject.put("delivery_time_slot", order.getDelivery_time_slot());
            String delivery_date = "";
            if (!StringTools.isNullOrEmpty(order.getDelivery_date())) {
                delivery_date = format.format(order.getDelivery_date());
            }
            orderJsonObject.put("delivery_date", delivery_date);

            // 配送方法构造
            StringBuilder delivery_carrier_nm = new StringBuilder();
            if (deliveryInfo.size() != 0) {
                final boolean[] bool = {
                    false
                };
                deliveryInfo.stream().filter(delivery -> {
                    String delivery_cd = delivery.getDelivery_cd();
                    if (delivery_cd.equals(order.getDelivery_company())) {
                        delivery_carrier_nm.append(delivery.getDelivery_nm()).append(" ")
                            .append(delivery.getDelivery_method_name());
                        orderJsonObject.put("delivery_method", delivery.getDelivery_method());
                        orderJsonObject.put("delivery_nm", delivery.getDelivery_nm());
                        bool[0] = true;
                    }
                    return bool[0];
                }).findAny();
            }
            orderJsonObject.put("delivery_carrier_nm", delivery_carrier_nm.toString());

            // 配達希望時間帯
            if (!StringTools.isNullOrEmpty(order.getDelivery_time_slot())) {
                List<Ms006_delivery_time> deliveryTimes = deliveryDao.getDeliveryTime(null,
                    Integer.valueOf(order.getDelivery_time_slot()), null, null);
                String delivery_time_name = "";
                if (deliveryTimes.size() != 0) {
                    delivery_time_name = deliveryTimes.get(0).getDelivery_time_name();
                }
                orderJsonObject.put("delivery_time_slot", delivery_time_name);
            }
            orderJsonObject.put("todoufuken", order.getOrder_todoufuken());

            // 支付方法
            String payment_method = "";
            if (!StringTools.isNullOrEmpty(order.getPayment_method())) {
                payment_method = deliveryDao.getPayById(Integer.parseInt(order.getPayment_method()));
            }
            orderJsonObject.put("payment_method", payment_method);

            // 注文者姓名
            String surname = Strings.isNullOrEmpty(order.getReceiver_first_name()) ? order.getReceiver_family_name()
                : order.getReceiver_family_name() + order.getReceiver_first_name();
            orderJsonObject.put("surname", surname);

            orderJsonObject.put("shipment_wish_date", order.getShipment_wish_date());
            orderJsonObject.put("cash_on_delivery_fee", order.getCash_on_delivery_fee());
            orderJsonObject.put("receiver_wish_method", order.getReceiver_wish_method());
            orderJsonObject.put("billing_total", order.getBilling_total());
            orderJsonObject.put("ins_date", order.getIns_date());
            orderJsonObject.put("upd_date", order.getUpd_date());
            orderJsonObject.put("bikou1", order.getBikou1());
            orderJsonObject.put("bikou7", order.getBikou7());

            // 查询商品名
            String purchase_order_no = order.getPurchase_order_no();
            List<Tc201_order_detail> orderDetails =
                orderDetailDao.getOrderDetailEntityListByPurchaseOrderNo(purchase_order_no, del_flg);
            if (!StringTools.isNullOrEmpty(orderDetails) && orderDetails.size() != 0) {
                List<String> nameList = orderDetails.stream()
                    .filter(x -> !StringTools.isNullOrEmpty(x.getProduct_name()))
                    .map(Tc201_order_detail::getProduct_name).collect(Collectors.toList());
                String name = Joiner.on("/").join(nameList);
                orderJsonObject.put("productName", name);
                int number = orderDetails.stream().mapToInt(Tc201_order_detail::getNumber).sum();

                Integer amount = orderDetails.stream().map(detail -> {
                    Integer num = Optional.ofNullable(detail.getNumber()).orElse(0);
                    return num;
                }).reduce(Integer::sum).orElse(0);
                String sender = Optional.ofNullable(order.getOrder_todoufuken()).orElse("");
                String receiver = Optional.ofNullable(order.getReceiver_todoufuken()).orElse("");
                String method = Optional.ofNullable(order.getDelivery_company()).orElse("");
                Integer days = getDeliveryDays(deliveryTakesDaysList, sender, receiver, method);
                if (null != days && days > 2) {
                    orderJsonObject.put("distance", "遠方");
                } else {
                    orderJsonObject.put("distance", "0");
                }
                orderJsonObject.put("sku", "SKU" + orderDetails.size());
                orderJsonObject.put("amount", "数量" + amount);

                if (!StringTools.isNullOrEmpty(checkList) && checkList.size() != 0) {
                    if (checkList.contains("0") && (null != days && days > 2)) {
                        array.add(orderJsonObject);
                    }
                    if (checkList.contains("1") && orderDetails.size() >= 17) {
                        array.add(orderJsonObject);
                    }
                    if (checkList.contains("2") && number >= 100) {
                        array.add(orderJsonObject);
                    }
                    if (checkList.contains("3") && number >= 50) {
                        array.add(orderJsonObject);
                    }
                } else {
                    array.add(orderJsonObject);
                }
            }

            // 商品数
            orderJsonObject.put("detail_num", orderDetails.size());
        });

        // XXX: 多表关联，不能用PageHelper插件，故手动分页
        if (array.size() > size) {
            JSONArray pageOrders = new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                // (page - 1) * size -> (page - 1) * size + size;
                if (i >= (page - 1) * size && i < (page - 1) * size + size) {
                    pageOrders.add(array.get(i));
                }
            }
            resultJsonObject.put("orders", pageOrders);
        } else {
            resultJsonObject.put("orders", array);
        }
        resultJsonObject.put("total", array.size());
        resultJsonObject.put("ntmFormClassifyCount", getNtmFormClassifyCount(client_id, array));
        return resultJsonObject;
    }

    @Override
    public JSONObject getSunlogiOrderDateList(String client_id, Integer status, Integer del_flg, Integer page,
        Integer size,
        String column, String sort, String start_date, String end_date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate = null;
        Date endDate = null;
        if (!StringTools.isNullOrEmpty(start_date)) {
            try {
                startDate = format.parse(start_date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (!StringTools.isNullOrEmpty(end_date)) {
            try {
                endDate = format.parse(end_date);
                // endDate处理
                endDate = CommonUtils.getDateEnd(endDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        Integer outer_order_status = null;
        if (!StringTools.isNullOrEmpty(status) && status != 1) {
            outer_order_status = 0;
        }
        PageHelper.startPage(page, size);
        List<Tc200_order> orderList = orderDao.getOrderList(client_id, outer_order_status, del_flg, column, sortType,
            startDate, endDate, null, null, null, null, null, null, null, null);
        PageInfo<Tc200_order> pageInfo = new PageInfo<>(orderList);

        JSONObject resultJsonObject = new JSONObject();
        JSONArray array = new JSONArray();
        orderList.forEach(order -> {
            JSONObject orderJsonObject = new JSONObject();
            orderJsonObject.put("purchase_order_no", order.getPurchase_order_no());
            orderJsonObject.put("outer_order_no", order.getOuter_order_no());
            orderJsonObject.put("outer_order_status", order.getOuter_order_status());
            orderJsonObject.put("order_datetime", order.getOrder_datetime());
            // orderJsonObject.put("order_detail_no", order.getOrder_detail_no());
            orderJsonObject.put("receiver_family_name", order.getReceiver_family_name());
            orderJsonObject.put("receiver_first_name", order.getReceiver_first_name());
            orderJsonObject.put("receiver_zip_code1", order.getReceiver_zip_code1());
            orderJsonObject.put("receiver_zip_code2", order.getReceiver_zip_code2());

            orderJsonObject.put("receiver_todoufuken", order.getReceiver_todoufuken());
            orderJsonObject.put("receiver_address1", order.getReceiver_address1());
            orderJsonObject.put("receiver_address2", order.getReceiver_address2());

            orderJsonObject.put("receiver_phone_number1", order.getReceiver_phone_number1());
            orderJsonObject.put("receiver_phone_number2", order.getReceiver_phone_number2());
            orderJsonObject.put("receiver_phone_number3", order.getReceiver_phone_number3());
            orderJsonObject.put("receiver_mail", order.getReceiver_mail());

            orderJsonObject.put("shipment_plan_id", order.getShipment_plan_id());
            orderJsonObject.put("gift_wish", order.getGift_wish());

            orderJsonObject.put("delivery_company", order.getDelivery_company());
            orderJsonObject.put("delivery_method", order.getDelivery_method());
            orderJsonObject.put("delivery_time_slot", order.getDelivery_time_slot());
            orderJsonObject.put("delivery_date", order.getDelivery_date());

            orderJsonObject.put("shipment_wish_date", order.getShipment_wish_date());
            orderJsonObject.put("cash_on_delivery_fee", order.getCash_on_delivery_fee());
            orderJsonObject.put("receiver_wish_method", order.getReceiver_wish_method());
            orderJsonObject.put("billing_total", order.getBilling_total());
            orderJsonObject.put("ins_date", order.getIns_date());
            orderJsonObject.put("upd_date", order.getUpd_date());
            orderJsonObject.put("bikou1", order.getBikou1());

            // 查询商品名
            String purchase_order_no = order.getPurchase_order_no();
            List<String> productName = orderDetailDao.getOrderDetailListByPurchaseOrderNo(purchase_order_no);
            String name = "";
            if (productName.size() > 0 && productName.get(0) != null) {
                name = Joiner.on("/").join(productName);
                orderJsonObject.put("productName", name);
            }
            array.add(orderJsonObject);
        });
        resultJsonObject.put("orders", array);
        resultJsonObject.put("total", pageInfo.getTotal());
        return resultJsonObject;
    }

    /**
     * @description 获取法人Excel/法人Eccube/个人数据
     * @param allShipmentList: 出库数据
     * @date 2021/11/17
     **/
    private JSONObject getNtmFormClassifyCount(String client_id, JSONArray allShipmentList) {
        // 获取ntm绑定eccube店铺识别子
        Tc203_order_client tc203 = orderApiDao.getOrderClientInfo(client_id, Constants.ECCUBE, null);
        String identification = "";
        if (!StringTools.isNullOrEmpty(tc203)) {
            identification = tc203.getIdentification();
        }
        JSONObject res = new JSONObject();
        long ntmCorporationExcelNum = allShipmentList.stream()
            .filter(v -> "1".equals(((JSONObject) v).getString("form"))
                && (((JSONObject) v).getString("purchase_order_no") != null
                    && ((JSONObject) v).getString("purchase_order_no").indexOf("NTM") != -1))
            .count();
        String finalIdentification = identification;
        long ntmCorporationEccubeNum = allShipmentList.stream()
            .filter(v -> "1".equals(((JSONObject) v).getString("form"))
                && (((JSONObject) v).getString("purchase_order_no") != null
                    && ((JSONObject) v).getString("purchase_order_no").indexOf(finalIdentification) != -1))
            .count();
        long ntmIndividualNum =
            allShipmentList.stream().filter(v -> "2".equals(((JSONObject) v).getString("form"))).count();
        res.put("ntmCorporationExcelNum", ntmCorporationExcelNum);
        res.put("ntmCorporationEccubeNum", ntmCorporationEccubeNum);
        res.put("ntmIndividualNum", ntmIndividualNum);
        res.put("identifier", identification);
        return res;
    }

    /**
     * @description 获取快递运送时长
     * @param mv410List 配送时长List
     * @param sender 注文者住所都道府県
     * @param receiver 配送先住所都道府県
     * @param method 配送方式
     * @return Integer
     * @date 2021/6/23
     **/
    private Integer getDeliveryDays(List<Mw410_delivery_takes_days> mv410List, String sender, String receiver,
        String method) {

        Objects.requireNonNull(mv410List);
        Objects.requireNonNull(method);
        Objects.requireNonNull(sender);
        Objects.requireNonNull(receiver);
        Integer days = null;

        switch (method) {
            // 9、7、8、10、11、26、6 ： 佐川急便
            case "9":
            case "7":
            case "8":
            case "10":
            case "11":
            case "26":
            case "6":
                days = mv410List.stream().filter(mv410 -> sender.equals(mv410.getSender()))
                    .filter(mv410 -> receiver.equals(mv410.getReceiver()))
                    .map(Mw410_delivery_takes_days::getSagawa_day).findFirst().orElse(null);
                break;
            // 1、5、4、3、2、23 ： ヤマト運輸
            case "1":
            case "5":
            case "4":
            case "3":
            case "2":
            case "23":
                days = null;
                break;
            default:
                days = null;
        }
        return days;
    }

    /**
     * @Param: jsonObject
     * @param: client_id
     * @param: request
     * @description: 受注出庫依頼の処理
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/20
     */
    @Override
    @Transactional(rollbackFor = {
        BaseException.class, Error.class
    })
    public JSONObject createShipments(JSONObject jsonObject, String client_id, HttpServletRequest request) {

        List<String> warehouseIdList = orderDao.getWarehouseIdListByClientId(client_id);
        String warehouseId = "";
        if (warehouseIdList == null || warehouseIdList.size() == 0) {
            throw new BaseException(ErrorCode.WAREHOUSE_NOT_EXISTED);
        }
        // ディフォルトで一番目の倉庫を取る？ TODO
        warehouseId = warehouseIdList.get(0);
        // 出庫依頼の注文番号を取得
        JSONArray list = jsonObject.getJSONArray("list");
        String shopify = jsonObject.getString("shopify_status");// 固定的にshopif_statusを取っているので、共通方法として、statusに変更した方がいいかな？
                                                                // TODO
        logger.info("出庫依頼処理開始(件数):" + list.size());
        try {
            for (int i = 0; i < list.size(); i++) {
                // 根据 受注番号 查询受注信息
                String purchaseOrderNo = list.getString(i);
                Tc200_order tc200Order = orderDao.getOrderInfoByPurchaseOrderNo(purchaseOrderNo);
                // 验证商品是否存在
                List<String> exceptionId = verifyProductExists(tc200Order, client_id);
                String shipment_plan_id = null;
                if (exceptionId.size() != 0) {
                    // 出庫依頼処理
                    shipment_plan_id =
                        this.getShipmentJson(tc200Order, purchaseOrderNo, client_id, warehouseId, request,
                            shopify);
                }
                // 更改受注状态
                orderDao.updateOuterOrderStatus(purchaseOrderNo, shipment_plan_id);
            }
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description 店铺仓库是否有ntm权限
     * @param warehouse_cd 倉庫CD
     * @param client_id 店舗ID
     * @param function_cd 功能CD
     * @return: boolean
     * @date 2021/7/14
     **/
    private boolean hasNtmEccubeFunction(String warehouse_cd, String client_id, String function_cd) {
        List<String> functionCdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(function_cd);
        List<Ms001_function> ms001FunctionList = clientDao.function_info(client_id, functionCdList, warehouse_cd);
        return Optional.ofNullable(ms001FunctionList).map(
            functionList -> {
                return functionList.stream().filter(value -> value.getFunction_cd().equals("4"))
                    .collect(Collectors.toList()).size() > 0;
            }).orElse(false);
    }

    /**
     * 校验Eccube数据合法性, 并更新tw200表得status_message字段
     */
    private void checkEccubeData(Tc200_order tc200, String client_id, String shipmentPlanId) {
        try {
            // 获取ntm绑定eccube店铺识别子
            Tc203_order_client tc203 = orderApiDao.getOrderClientInfo(client_id, Constants.ECCUBE, null);
            String identification = "";
            if (!StringTools.isNullOrEmpty(tc203)) {
                identification = tc203.getIdentification();
            }

            String finalIdentification = identification;
            Optional.ofNullable(tc200).ifPresent(bean -> {
                bean.getPurchase_order_no();
                // 如果 受注番号 包含ECCUBE识别子，则标识该数据来自ECCUBE
                if (bean.getPurchase_order_no() != null
                    && bean.getPurchase_order_no().indexOf(finalIdentification) != -1) {
                    String errorMsg = checkPhoneNumber(tc200) + checkYubinLegal(tc200);

                    if (!Strings.isNullOrEmpty(errorMsg)) {
                        shipmentService.asyncNtmEccubeStatusMessage(shipmentPlanId, 1, errorMsg);
                    }
                }
            });
        } catch (Exception e) {
            logger.info("Check Eccube Data Error, Exception Message: " + e.getMessage());
        }

    }

    /**
     * 校验电话号码
     */
    private String checkPhoneNumber(Tc200_order tc200) {

        String phoneNumber = Optional.of(tc200.getReceiver_phone_number1()).orElse("") +
            Optional.of(tc200.getReceiver_phone_number2()).orElse("") +
            Optional.of(tc200.getReceiver_phone_number3());
        return Strings.isNullOrEmpty(phoneNumber) ? "[電話番号]検証に失敗しました。" : "";
    }

    /**
     * @description 检查邮编和地址是否合法
     * @param tc200 受注信息
     * @return List
     * @date 2021/6/17
     **/
    private String checkYubinLegal(Tc200_order tc200) {

        String url = bizApiHost + Constants.BIZ_CHECKADDRESS;
        CheckAddressRequest request = initCheckAddressRequest(tc200);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();
        String errorMsg = "";

        String requestValue = "";
        try {
            requestValue = XmlUtil.bean2Xml(builder, request);
            logger.info("CheckAddress Request Param: " + requestValue);
            HashMap<String, String> params = Maps.newHashMap();
            params.put("value", requestValue);
            JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
            String responseValue = responseBody.getString("value");
            logger.info("CheckAddress Response Body: " + responseValue);
            CheckAddressResponse responseBean =
                (CheckAddressResponse) XmlUtil.xml2Bean(builder, responseValue, CheckAddressResponse.class);

            if (!BizLogiResEnum.S0_0001.getCode().equals(responseBean.getResultCode())) {
                errorMsg = "[郵便番号]、[住所情報] 検証に失敗しました。";
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errorMsg;
    }

    /**
     * 住所チェック請求初期化
     * <p>
     * ※NTM用
     * </p>
     *
     * @param tc200 受注データ
     * @return {@link CheckAddressRequest}
     */
    private CheckAddressRequest initCheckAddressRequest(Tc200_order tc200) {
        CheckAddressRequest request = new CheckAddressRequest();

        CheckAddressRequest.CustomerAuth customerAuth = request.new CustomerAuth();
        customerAuth.setCustomerId(bizAuthCustomId);
        customerAuth.setLoginPassword(bizAuthCustomPwd);
        request.setCustomerAuth(customerAuth);

        String address = tc200.getReceiver_todoufuken() + tc200.getReceiver_address1() + tc200.getReceiver_address2();
        String yubin = tc200.getReceiver_zip_code1() + tc200.getReceiver_zip_code2();
        request.setRequestYubin(yubin);
        request.setRequestAddress(address);

        return request;
    }

    /**
     * @Param: jsonObject : client_id,history_id
     * @description: 根据受注取込履歴ID 获取受注信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    @Override
    public JSONObject getOrderListByHistoryId(JSONObject jsonObject) {
        List<Tc200_order> orderListByHistoryId = orderDao.getOrderListByHistoryId(jsonObject);
        Tc202_order_history tc202OrderHistory = orderHistoryDao
            .getOrderHistoryInfoByHistoryId(jsonObject.getString("history_id"));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(tc202OrderHistory.getImport_datetime());
        // 根据履历Id 获取售出失败信息
        List<Tc207_order_error> orderErrors = orderErrorDao.getErrorInfoByHistoryId(jsonObject.getString("client_id"),
            jsonObject.getString("history_id"));
        JSONArray jsonArray = new JSONArray();
        orderListByHistoryId.forEach(orderHistory -> {
            JSONObject historyJson = new JSONObject();
            historyJson.put("outer_order_no", orderHistory.getOuter_order_no());
            // 成功
            historyJson.put("type", "成功");
            historyJson.put("shipment_plan_id", orderHistory.getShipment_plan_id());
            historyJson.put("biko01", orderHistory.getBikou1());
            jsonArray.add(historyJson);
        });
        orderErrors.forEach(orderError -> {
            JSONObject errorJson = new JSONObject();
            errorJson.put("outer_order_no", orderError.getOuter_order_no());
            errorJson.put("type", "成功");
            errorJson.put("shipment_plan_id", " ");
            errorJson.put("biko01", orderError.getError_msg());
            jsonArray.add(errorJson);
        });
        jsonObject.clear();
        jsonObject.put("orderListByHistoryId", jsonArray);
        jsonObject.put("date", date);
        return CommonUtils.success(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 更改店铺配送方法
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject changeDeliveryMethod(JSONObject jsonObject) {
        String description_setting = jsonObject.getString("description_setting");
        String client_id = jsonObject.getString("client_id");
        settingService.descriptionSetting(client_id, description_setting);
        orderDao.changeDeliveryMethod(jsonObject);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject client_id,shipment_plan_id
     * @description: 生成出库PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/21
     */
    @Override
    public JSONObject createShipmentPDF(JSONObject jsonObject, HttpServletRequest request) {
        String client_id = jsonObject.getString("client_id");
        String shipment_plan_id = jsonObject.getString("shipment_plan_id");
        Tw200_shipment tw200Shipments = shipmentsDao.getShipmentsDetail(client_id, shipment_plan_id);
        if (!StringTools.isNullOrEmpty(tw200Shipments)) {
            List<String> shipmentIdList = new ArrayList<>();
            shipmentIdList.add(shipment_plan_id);
            List<Tw201_shipment_detail> shipmentDetails =
                shipmentDetailDao.getShipmentProductList(client_id, shipmentIdList, false);
            tw200Shipments.setTw201_shipment_detail(shipmentDetails);
            JSONObject json = getShipmentJsonDate(jsonObject, tw200Shipments);
            Integer flg = 1;
            shipmentsService.getShipmentsPDF(json, request, flg, null, null);
        }
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject client_id,client_url,template
     * @description: 保存店铺受注关系表信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/9
     */
    @Override
    public JSONObject insertOrderClient(JSONObject jsonObject) {
        String apiName = jsonObject.getString("api_name");
        String clientId = jsonObject.getString("client_id");
        String template = jsonObject.getString("template");
        String identification = orderDao.getApiIdentification(template);
        // 根据apiName 和 client_id 查询以前是否存在
        // Tc203_order_client orderClient = apiService.getOrderClientInfoById(apiName, clientId);
        Integer maxId = getMaxId(clientId);

        boolean insertFlg = false;
        if (!StringTools.isNullOrEmpty(jsonObject.getString("id"))) {
            maxId = jsonObject.getInteger("id");
            insertFlg = true;
        }
        jsonObject.put("id", maxId);
        // APIの識別コードの修正 Modify by HZM 20201209
        DecimalFormat df = new DecimalFormat("000");
        String str2 = df.format(maxId);
        identification = identification + str2;
        jsonObject.put("identification", identification);
        if (StringTools.isNullOrEmpty(jsonObject.getString("expireDate"))) {
            jsonObject.put("expireDate", null);
        }
        if (insertFlg) {
            apiService.updateOrderClient(jsonObject);
        } else {
            apiService.insertOrderClient(jsonObject);
        }
        return CommonUtils.success(maxId);
    }

    private Integer getMaxId(String clientId) {
        Integer id = apiService.getMaxId(clientId);
        int maxId = 1;
        if (!StringTools.isNullOrEmpty(id)) {
            maxId = id + 1;
        }
        return maxId;
    }

    /**
     * @Param: client_id
     * @param: template
     * @Param: apiName
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    @Override
    public JSONObject getOrderClientInfo(String client_id, String template, String apiName) {
        return apiService.getOrderClientInfo(client_id, template, apiName);
    }

    /**
     * @Description: 受注取消
     * @Param: 受注番号
     * @return: Integer
     * @Date: 2020/11/12
     */
    @Override
    @Transactional
    public Integer orderShipmentsDelete(String[] purchase_order_no, HttpServletRequest httpServletRequest) {
        Date upd_date = DateUtils.getDate();
        String upd_usr = null;
        if (!StringTools.isNullOrEmpty(httpServletRequest)) {
            upd_usr = CommonUtils.getToken("login_nm", httpServletRequest);
        }
        List<String> order_no = new ArrayList<>(Arrays.asList(purchase_order_no));

        try {
            orderDao.orderShipmentsDelete(order_no, upd_date, upd_usr, null);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }

        try {
            orderDao.orderDetailDelete(order_no, upd_date, upd_usr, null);
        } catch (Exception e) {
            logger.debug(e.getMessage());
        }
        return 1;
    }

    /**
     * @Param: jsonObject
     * @param: tw200_shipment
     * @description: 出库json数据 做成
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/26
     */
    public static JSONObject getShipmentJsonDate(JSONObject jsonObject, Tw200_shipment tw200_shipment) {
        jsonObject.put("discount_amount", tw200_shipment.getDiscount_amount());
        jsonObject.put("handling_charge", tw200_shipment.getHandling_charge());
        jsonObject.put("cushioning_type", tw200_shipment.getCushioning_type());
        jsonObject.put("total_with_reduced_tax", tw200_shipment.getTotal_with_reduced_tax());
        jsonObject.put("shipment_status", tw200_shipment.getShipment_status());
        jsonObject.put("postcode", tw200_shipment.getPostcode());
        jsonObject.put("gift_wrapping_unit", tw200_shipment.getGift_wrapping_unit());
        jsonObject.put("tax", tw200_shipment.getTax());
        jsonObject.put("ins_date", tw200_shipment.getIns_usr());
        jsonObject.put("product_kind_plan_cnt", tw200_shipment.getProduct_kind_plan_cnt());
        jsonObject.put("delivery_charge", tw200_shipment.getDelivery_charge());
        jsonObject.put("delivery_carrier", tw200_shipment.getDelivery_carrier());
        jsonObject.put("price_on_delivery_note", tw200_shipment.getPrice_on_delivery_note());
        jsonObject.put("warehouse_cd", tw200_shipment.getWarehouse_cd());
        jsonObject.put("total_amount", tw200_shipment.getTotal_amount());
        jsonObject.put("phone", tw200_shipment.getPhone());
        jsonObject.put("identifier", tw200_shipment.getIdentifier());
        jsonObject.put("upd_date", tw200_shipment.getUpd_date());
        jsonObject.put("product_plan_total", tw200_shipment.getProduct_plan_total());
        jsonObject.put("fragile_item", tw200_shipment.getFragile_item());
        jsonObject.put("total_with_normal_tax", tw200_shipment.getTotal_with_normal_tax());
        jsonObject.put("order_no", tw200_shipment.getOrder_no());
        jsonObject.put("prefecture", tw200_shipment.getPrefecture());
        jsonObject.put("tax_for_cash_on_delivery", tw200_shipment.getTax_for_cash_on_delivery());
        jsonObject.put("delivery_note_type", tw200_shipment.getDelivery_note_type());
        jsonObject.put("cash_on_delivery", tw200_shipment.getCash_on_delivery());
        jsonObject.put("surname", tw200_shipment.getSurname());
        jsonObject.put("division", tw200_shipment.getDivision());
        jsonObject.put("delivery_method", tw200_shipment.getDelivery_method());
        jsonObject.put("form", tw200_shipment.getForm());
        jsonObject.put("company", tw200_shipment.getCompany());
        jsonObject.put("email", tw200_shipment.getEmail());
        jsonObject.put("total_for_cash_on_delivery", tw200_shipment.getTotal_for_cash_on_delivery());
        jsonObject.put("total_price", tw200_shipment.getTotal_price());
        jsonObject.put("address2", tw200_shipment.getAddress2());
        jsonObject.put("address1", tw200_shipment.getAddress1());
        jsonObject.put("ins_usr", tw200_shipment.getIns_usr());
        jsonObject.put("cushioning_unit", tw200_shipment.getCushioning_unit());
        jsonObject.put("message", tw200_shipment.getMessage());
        jsonObject.put("sponsor_id", tw200_shipment.getSponsor_id());
        jsonObject.put("subtotal_amount", tw200_shipment.getSubtotal_amount());
        jsonObject.put("bundled_items", tw200_shipment.getBundled_items());
        jsonObject.put("gift_sender_name", tw200_shipment.getGift_sender_name());
        jsonObject.put("label_note", tw200_shipment.getLabel_note());
        jsonObject.put("request_date", tw200_shipment.getRequest_date());
        jsonObject.put("box_delivery", tw200_shipment.getBox_delivery());
        jsonObject.put("upd_usr", tw200_shipment.getUpd_usr());
        jsonObject.put("gift_wrapping_type", tw200_shipment.getGift_wrapping_type());
        jsonObject.put("payment_method", tw200_shipment.getPayment_method());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        if (tw200_shipment.getDelivery_date() != null) {
            Date date = tw200_shipment.getDelivery_date();
            jsonObject.put("delivery_date", sdf.format(date));
        }
        String orderDatetime = "";
        if (tw200_shipment.getOrder_datetime() != null) {
            Timestamp order_datetime = tw200_shipment.getOrder_datetime();
            orderDatetime = sdf.format(order_datetime);
        }
        jsonObject.put("order_datetime", orderDatetime);
        jsonObject.put("bikou1", tw200_shipment.getBikou1());
        jsonObject.put("bikou2", tw200_shipment.getBikou2());
        jsonObject.put("bikou3", tw200_shipment.getBikou3());
        jsonObject.put("bikou4", tw200_shipment.getBikou4());
        jsonObject.put("bikou5", tw200_shipment.getBikou5());
        jsonObject.put("bikou6", tw200_shipment.getBikou6());
        jsonObject.put("bikou7", tw200_shipment.getBikou7());
        JSONArray jsonArray = new JSONArray();
        tw200_shipment.getTw201_shipment_detail().stream().forEach(tw201_shipment_detail -> {
            JSONObject json = new JSONObject();
            json.put("client_id", tw201_shipment_detail.getClient_id());
            json.put("warehouse_cd", tw201_shipment_detail.getWarehouse_cd());
            json.put("product_id", tw201_shipment_detail.getProduct_id());
            json.put("is_reduced_tax", tw201_shipment_detail.getIs_reduced_tax());
            json.put("name", tw201_shipment_detail.getMc100_product().get(0).getName());
            json.put("code", tw201_shipment_detail.getMc100_product().get(0).getCode());
            json.put("unit_price", tw201_shipment_detail.getUnit_price());
            json.put("product_plan_cnt", tw201_shipment_detail.getProduct_plan_cnt());
            json.put("price", tw201_shipment_detail.getPrice());
            json.put("cushioning_type", tw201_shipment_detail.getCushioning_type());
            json.put("tax_flag", tw201_shipment_detail.getTax_flag());
            json.put("set_sub_id", tw201_shipment_detail.getSet_sub_id());
            json.put("set_cnt", tw201_shipment_detail.getSet_cnt());
            json.put("kubun", tw201_shipment_detail.getKubun());
            jsonArray.add(json);
        });
        jsonObject.put("items", jsonArray);
        return jsonObject;
    }

    /**
     * @Param: tc200_order
     * @param: client_id
     * @description: 验证商品是否存在
     * @return: java.util.List<java.lang.String>
     * @date: 2020/8/19
     */
    public List<String> verifyProductExists(Tc200_order tc200_order, String client_id) {
        ArrayList<String> exceptionId = new ArrayList<>();
        List<String> productIdList = tc200_order.getTc201_order_detail_list().stream()
            .map(Tc201_order_detail::getProduct_id).collect(Collectors.toList());
        productIdList.stream().forEach(productId -> {
            Mc100_product product = productDao.getNameByProductId(productId, client_id);
            // 如果商品没有登录
            if (StringTools.isNullOrEmpty(product)) {
                exceptionId.add(productId);
            }
        });
        return exceptionId;
    }

    /**
     * @Param: tc200_order
     * @description: 拼接json
     * @return: void
     * @date: 2020/8/19
     */
    @Transactional(rollbackFor = {
        BaseException.class, Error.class
    })
    public String getShipmentJson(Tc200_order tc200_order, String purchaseOrderNo, String client_id, String warehouseId,
        HttpServletRequest request, String shopify_status) {
        Date deliveryDate = tc200_order.getDelivery_date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        JSONObject jsonObject = new JSONObject();
        // 注文番号 = 外部受注番号
        jsonObject.put("order_no", tc200_order.getOuter_order_no());
        // 出庫識別番号 = 受注番号
        jsonObject.put("identifier", purchaseOrderNo);
        // 出庫予定日
        Date shipmentPlanDate = tc200_order.getShipment_plan_date();
        String shipment_plan_date = null;
        if (!StringTools.isNullOrEmpty(shipmentPlanDate)) {
            shipment_plan_date = simpleDateFormat.format(shipmentPlanDate);
        }
        jsonObject.put("shipment_plan_date", shipment_plan_date);
        // 出荷希望日
        Date shipmentWishDate = tc200_order.getShipment_wish_date();
        String shipment_wish_date = null;
        if (!StringTools.isNullOrEmpty(shipmentWishDate)) {
            shipment_wish_date = simpleDateFormat.format(shipmentWishDate);
        }
        jsonObject.put("shipping_date", shipment_wish_date);
        // 納品書 割引額 = その他金額(割引)
        jsonObject.put("discount_amount", tc200_order.getOther_fee());
        // 代金引換消費税 默认值0
        jsonObject.put("tax_for_cash_on_delivery", "0");
        // 店舗ID
        jsonObject.put("client_id", client_id);
        // 支付方法
        String payment_method = tc200_order.getPayment_method();
        jsonObject.put("payment_method", tc200_order.getPayment_method());
        // 納品書 手数料 = 手数料
        jsonObject.put("handling_charge", tc200_order.getHandling_charge());
        // // 明細書の同梱設定 如果受注 明細同梱設定 为空
        // // 默认值：同梱する
        // int delivery_note_type = 1;
        // if (!StringTools.isNullOrEmpty(tc200_order.getDetail_bundled()) &&
        // "同梱しない".equals(tc200_order.getDetail_bundled())) {
        // delivery_note_type = 0;
        // }
        // jsonObject.put("delivery_note_type", delivery_note_type);
        // 代金引換指定 cash_on_delivery_fee > 0 ? 1 : 0
        // Integer cash_on_delivery_fee = tc200_order.getCash_on_delivery_fee() > 0 ? 1
        if (!StringTools.isNullOrEmpty(payment_method)) {
            // 如果为代金引换 为 1 否则为 0
            jsonObject.put("cash_on_delivery", "2".equals(payment_method) ? 1 : 0);
        }

        // jsonObject.put("cash_on_delivery", cash_on_delivery_fee);
        // 緩衝材種別 固定値：空
        jsonObject.put("cushioning_type", "");
        // 配送方法和配送公司
        String delivery_carrier = tc200_order.getDelivery_company();
        String delivery_method = tc200_order.getDelivery_method();
        // a配送便指定 = 配送方法
        jsonObject.put("delivery_method", delivery_method);
        // a配送会社 = 配送会社指定
        jsonObject.put("delivery_carrier", delivery_carrier);
        // a保留フラグ
        jsonObject.put("suspend", 0);
        // a代金引換総計 = 代引料
        if (!StringTools.isNullOrEmpty(tc200_order.getCash_on_delivery_fee())
            && tc200_order.getCash_on_delivery_fee() > 0) {
            jsonObject.put("total_for_cash_on_delivery", tc200_order.getCash_on_delivery_fee());
            jsonObject.put("cash_on_delivery", 1);
            jsonObject.put("tax_for_cash_on_delivery", 0);
        } else {
            jsonObject.put("total_for_cash_on_delivery", 0);
            jsonObject.put("tax_for_cash_on_delivery", 0);
        }
        // PDF判断 和DB无关 1 PDF生成， 0 插入数据库并生成PDF
        jsonObject.put("preview_flg", 0);
        // aギフト配送希望 为1的话 注文単位：１
        // a没有值的默认 为0
        int gift_wrapping_unit = 0;
        if (!StringTools.isNullOrEmpty(tc200_order.getGift_wish())) {
            if ("1".equals(tc200_order.getGift_wish())) {
                gift_wrapping_unit = 1;
            }
        }
        // ギフトラッピング単位 = 受注 ギフト配送希望
        jsonObject.put("gift_wrapping_unit", gift_wrapping_unit);
        // 税 = 消費税合計
        jsonObject.put("tax", tc200_order.getTax_total());
        // 緩衝材単位 固定値：０
        jsonObject.put("cushioning_unit", 0);
        // sponsor_default == 1
        Integer sponsor_default = 1;
        // 如果 受注 注文者ID(依頼主ID) 为空的时候
        // 默认值：店铺默认值
        // 注意：
        // 如果 受注 注文者ID(依頼主ID) 在依赖表里没有值时，也使用默认值
        String sponsorId = tc200_order.getSponsor_id();
        // 查询默认依赖主信息
        Ms012_sponsor_master sponsorInfo = settingDao.getSponsorDefaultInfo(client_id, sponsor_default);
        if (StringTools.isNullOrEmpty(sponsorId)) {
            sponsorId = sponsorInfo.getSponsor_id();
        } else {
            // 查询该依赖主Id 是否存在
            List<Ms012_sponsor_master> deliveryListOne = settingDao.getDeliveryListOne(client_id, sponsorId);
            if (deliveryListOne.size() == 0) {
                sponsorId = sponsorInfo.getSponsor_id();
            } else {
                sponsorInfo = deliveryListOne.get(0);
            }
        }
        jsonObject.put("sponsor_id", sponsorId);
        // 明細書メッセージ
        String message = tc200_order.getDetail_message();
        if (StringTools.isNullOrEmpty(message)) {
            message = sponsorInfo.getDetail_message();
        }
        jsonObject.put("message", message);
        // 商品合計金額 = 合計請求金額
        jsonObject.put("total_price", tc200_order.getBilling_total());
        // 配達希望時間帯
        jsonObject.put("delivery_time_slot", tc200_order.getDelivery_time_slot());
        // 同梱物 ※廃止 @Add wang 20021/04/1
        jsonObject.put("bundled_items", "");
        // 請求コード @Add wang 20021/04/1 start
        jsonObject.put("bill_barcode", tc200_order.getBill_barcode());
        jsonObject.put("total_with_normal_tax", tc200_order.getTotal_with_normal_tax());
        jsonObject.put("total_with_reduced_tax", tc200_order.getTotal_with_reduced_tax());
        // 請求コード @Add wang 20021/04/1 end
        // 如果 受注 ギフト配送希望 为1的话 ギフト贈り主氏名 = 注文者姓＋注文者名
        String gift_sender_name = "";
        if (!StringTools.isNullOrEmpty(tc200_order.getGift_wish())) {
            if ("1".equals(tc200_order.getGift_wish())) {
                if (!StringTools.isNullOrEmpty(tc200_order.getOrder_family_name())) {
                    gift_sender_name = tc200_order.getOrder_family_name();
                }
                if (!StringTools.isNullOrEmpty(tc200_order.getOrder_first_name())) {
                    gift_sender_name += tc200_order.getOrder_first_name();
                }
            }
        }
        jsonObject.put("gift_sender_name", gift_sender_name);

        // 明細書の同梱設定 如果受注 明細同梱設定 为空
        // 默认值：同梱する
        int delivery_note_type = 1;
        if (StringTools.isNullOrEmpty(tc200_order.getDetail_bundled())) {
            delivery_note_type = sponsorInfo.getDelivery_note_type();
        } else if (!StringTools.isNullOrEmpty(tc200_order.getDetail_bundled())
            && "同梱しない".equals(tc200_order.getDetail_bundled())) {
            delivery_note_type = 0;
        }
        jsonObject.put("delivery_note_type", delivery_note_type);

        if (tc200_order.getLabel_note() == null || "".equals(tc200_order.getLabel_note())) {
            // a品名 默认值：店铺侧 依赖主设置的品名
            jsonObject.put("label_note", sponsorInfo.getLabel_note());
        } else {
            jsonObject.put("label_note", tc200_order.getLabel_note());
        }

        String format = null;
        if (!StringTools.isNullOrEmpty(deliveryDate)) {
            format = simpleDateFormat.format(deliveryDate);
        }
        jsonObject.put("delivery_date", format);
        jsonObject.put("delivery_time_slot", tc200_order.getDelivery_time_slot());
        // 運送業者マスタ
        jsonObject.put("customer_delivery", 0);
        // 送料合計
        jsonObject.put("delivery_charge", tc200_order.getDelivery_total());
        // 如果 受注 明細書金額印字 为空
        // 参考 商品设定中的 设定值
        String detail_price_print = tc200_order.getDetail_price_print();
        if (StringTools.isNullOrEmpty(tc200_order.getDetail_price_print())) {
            detail_price_print = Optional.ofNullable(sponsorInfo.getPrice_on_delivery_note())
                .map(value -> String.valueOf(value)).orElse("0");
        }
        // 明細書への金額印字指定
        jsonObject.put("price_on_delivery_note", detail_price_print);
        jsonObject.put("warehouse_cd", warehouseId);

        // null
        JSONObject sender = new JSONObject();
        sender.put("delivery_id", 0);
        // 配送先郵便番号1
        // 配送先郵便番号2
        String postcode = "";
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_zip_code1())) {
            postcode = tc200_order.getReceiver_zip_code1();
        }
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_zip_code2())) {
            postcode += tc200_order.getReceiver_zip_code2();
        }

        String zip = CommonUtils.getZip(postcode);
        // 郵便番号
        sender.put("postcode", zip);
        // 都道府県 = 配送先住所都道府県
        sender.put("prefecture", tc200_order.getReceiver_todoufuken());
        sender.put("address1", tc200_order.getReceiver_address1());
        sender.put("address2", tc200_order.getReceiver_address2());
        sender.put("company", tc200_order.getReceiver_company());
        sender.put("division", tc200_order.getReceiver_division());
        String surname = tc200_order.getReceiver_family_name();
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_first_name())) {
            surname += tc200_order.getReceiver_first_name();
        }
        sender.put("surname", surname);
        // 配送先電話番号1
        // 配送先電話番号2
        // 配送先電話番号3
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_phone_number1())) {
            stringBuilder.append(tc200_order.getReceiver_phone_number1());
        }
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_phone_number2())) {
            stringBuilder.append(tc200_order.getReceiver_phone_number2());
        }
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_phone_number3())) {
            stringBuilder.append(tc200_order.getReceiver_phone_number3());
        }

        // 電話番号 TODO @Add wang 20200128 電話番号の処理追加
        sender.put("phone", checkPhoneToList(stringBuilder.toString()));

        // メールアドレス = 配送先メールアドレス
        sender.put("email", tc200_order.getReceiver_mail());
        // 配送先形態 2：個人(默认)
        int form = 2;
        if (!StringTools.isNullOrEmpty(tc200_order.getForm())) {
            form = tc200_order.getForm();
        }
        sender.put("form", form);
        // @注文情報追加 2021/04/16 wang edit Start
        sender.put("order_company", tc200_order.getOrder_company());
        sender.put("order_division", tc200_order.getOrder_division());
        sender.put("order_zip_code1", tc200_order.getOrder_zip_code1());
        sender.put("order_zip_code2", tc200_order.getOrder_zip_code2());
        sender.put("order_todoufuken", tc200_order.getOrder_todoufuken());
        sender.put("order_address1", tc200_order.getOrder_address1());
        sender.put("order_address2", tc200_order.getOrder_address2());
        String orderFamilyName = tc200_order.getOrder_family_name();
        String firstName = tc200_order.getOrder_first_name();
        if (!StringTools.isNullOrEmpty(orderFamilyName) && !StringTools.isNullOrEmpty(firstName)) {
            orderFamilyName += firstName;
        }
        sender.put("order_family_name", orderFamilyName);
        sender.put("order_first_name", "");
        sender.put("order_family_kana", tc200_order.getOrder_family_kana());
        sender.put("order_first_kana", tc200_order.getOrder_first_kana());
        sender.put("order_phone_number1", tc200_order.getOrder_phone_number1());
        sender.put("order_phone_number2", tc200_order.getOrder_phone_number2());
        sender.put("order_phone_number3", tc200_order.getOrder_phone_number3());
        sender.put("order_mail", tc200_order.getOrder_mail());
        sender.put("order_gender", tc200_order.getOrder_gender());
        jsonObject.put("sender", sender);

        JSONObject delivery_options = new JSONObject();
        // a不在時宅配ボックス 固定値：０
        delivery_options.put("box_delivery", tc200_order.getBox_delivery());
        // a割れ物注意 固定値：０
        delivery_options.put("fragile_item", tc200_order.getFragile_item());
        jsonObject.put("delivery_options", delivery_options);
        // 送り状特記事項
        jsonObject.put("invoice_special_notes", tc200_order.getInvoice_special_notes());

        /* 出庫明細 */
        JSONArray items = new JSONArray();
        ArrayList<Integer> canNotStock = new ArrayList<>();
        final Integer[] product_plan_total = {
            0
        };
        for (int i = 0; i < tc200_order.getTc201_order_detail_list().size(); i++) {
            Tc201_order_detail detail = tc200_order.getTc201_order_detail_list().get(i);
            JSONObject object = new JSONObject();
            object.put("warehouse_cd", warehouseId);
            object.put("client_id", client_id);
            object.put("product_id", detail.getProduct_id());
            JSONObject json = new JSONObject();
            // 获取该商品在库信息
            Tw300_stock stock = stockDao.getStockInfoById(object);
            if (!StringTools.isNullOrEmpty(stock)) {
                // 可以出库的数量 = 实际在库数 - 依赖中数
                int num = stock.getInventory_cnt() - stock.getRequesting_cnt();
                // 不能出库
                if (num < detail.getNumber()) {
                    canNotStock.add(1);
                }
                json.put("inventory_cnt", stock.getInventory_cnt());
                json.put("requesting_cnt", stock.getRequesting_cnt());
            } else {
                json.put("inventory_cnt", 0);
                json.put("requesting_cnt", 0);
                canNotStock.add(1);
            }
            json.put("client_id", client_id);
            json.put("warehouse_cd", warehouseId);
            json.put("product_id", detail.getProduct_id());
            // 軽減税率適用商品 固定値：0
            // json.put("is_reduced_tax", 0);
            // 軽減税率適用商品 DBから値を取得する(0:10% 1:8%) @Add wang 20210212
            json.put("is_reduced_tax", detail.getIs_reduced_tax());
            json.put("name", detail.getProduct_name());
            // 商品オプション追加 @Add wang 20210725
            json.put("options", detail.getProduct_option());
            json.put("barcode", detail.getProduct_barcode());
            // 在判断是否是同一商品时根据商品对应表进行替换
            // 商品对应表
            Mc100_product productInfoByCode = null;
            productInfoByCode =
                apiCommonUtils.fetchMc100Product(client_id, detail.getProduct_code(), detail.getProduct_option());
            if (!StringTools.isNullOrEmpty(productInfoByCode)) {
                json.put("code", productInfoByCode.getCode());
            } else {
                json.put("code", detail.getProduct_code());
            }
            // 商品単価 = 受注明细 単価
            json.put("unit_price", detail.getUnit_price());
            // 出庫依頼数 = 受注明细 個数
            json.put("product_plan_cnt", detail.getNumber());
            // 商品総額 = 受注明细 個数 * 単価
            json.put("price", detail.getProduct_total_price());
            // セット商品ID
            if (!StringTools.isNullOrEmpty(detail.getSet_sub_id())) {
                json.put("set_sub_id", detail.getSet_sub_id());
                json.put("set_flg", 1);
            } else {
                json.put("set_sub_id", null);
                json.put("set_flg", 0);
            }
            // 緩衝材種別 固定値：空
            json.put("cushioning_type", "");
            //
            json.put("gift_add_cnt", "");
            // 如果是set商品
            if (!StringTools.isNullOrEmpty(detail.getSet_sub_id()) && detail.getSet_sub_id() != 0) {
                JSONArray stock_json_array = new JSONArray();
                List<Mc100_product> list = new ArrayList<Mc100_product>();
                list = productDao.getSetProductLists(detail.getSet_sub_id(), client_id);
                for (Mc100_product mc100 : list) {
                    JSONObject stock_json = new JSONObject();
                    stock_json.put("client_id", client_id);
                    stock_json.put("product_id", mc100.getProduct_id());
                    stock_json.put("set_sub_id", mc100.getSet_sub_id());
                    stock_json.put("product_cnt", mc100.getProduct_cnt());
                    stock_json.put("name", mc100.getName());
                    stock_json.put("is_reduced_tax", detail.getIs_reduced_tax());
                    int available_cnt = -1;
                    int requesting_cnt = -1;
                    int inventory_cnt = -1;
                    if (!StringTools.isNullOrEmpty(mc100.getTw300_stock())) {
                        if (!StringTools.isNullOrEmpty(mc100.getTw300_stock().getAvailable_cnt())) {
                            available_cnt = mc100.getTw300_stock().getAvailable_cnt();
                        }
                        if (!StringTools.isNullOrEmpty(mc100.getTw300_stock().getRequesting_cnt())) {
                            requesting_cnt = mc100.getTw300_stock().getRequesting_cnt();
                        }
                        if (!StringTools.isNullOrEmpty(mc100.getTw300_stock().getInventory_cnt())) {
                            inventory_cnt = mc100.getTw300_stock().getInventory_cnt();
                        }
                    }
                    stock_json.put("available_cnt", available_cnt);
                    stock_json.put("requesting_cnt", requesting_cnt);
                    stock_json.put("inventory_cnt", inventory_cnt);
                    stock_json_array.add(stock_json);
                }
                json.put("mc103_product_sets", stock_json_array);
            }

            // a受注明細
            // aラッピング種類1
            // aラッピングタイトル1
            // aラッピング種類2
            // aラッピングタイトル2
            StringBuilder builder = new StringBuilder();
            if (!StringTools.isNullOrEmpty(detail.getWrapping_type1())) {
                builder.append(detail.getWrapping_type1());
            }
            if (!StringTools.isNullOrEmpty(detail.getWrapping_title1())) {
                builder.append("_").append(detail.getWrapping_title1());
            }
            if (!StringTools.isNullOrEmpty(detail.getWrapping_type2())) {
                builder.append("_").append(detail.getWrapping_type2());
            }
            if (!StringTools.isNullOrEmpty(detail.getWrapping_title2())) {
                builder.append("_").append(detail.getWrapping_title2());
            }
            json.put("gift_wrapping_note", builder.toString());

            // 同梱物処理 TODO @Add 20210128 wang
            if (!StringTools.isNullOrEmpty(detail.getBundled_flg())) {
                json.put("bundled_flg", detail.getBundled_flg());
            } else {
                json.put("bundled_flg", 0);
            }
            if (!StringTools.isNullOrEmpty(detail.getTax_flag())) {
                json.put("tax_flag", detail.getTax_flag());
            } else {
                json.put("tax_flag", 0);
            }
            // 商品区分
            if (!StringTools.isNullOrEmpty(productInfoByCode)) {
                json.put("kubun", productInfoByCode.getKubun());
            } else {
                json.put("kubun", detail.getProduct_kubun());
            }
            // オプション金額
            json.put("option_price", detail.getOption_price());
            // 依赖数据重复
            boolean productCheckFlg = false;

            if (!StringTools.isNullOrEmpty(items) && items.size() > 0) {
                for (int j = 0; j < items.size(); j++) {
                    JSONObject tmpJson = items.getJSONObject(j);
                    if (json.getString("code").equals(tmpJson.getString("code"))) {
                        // 商品价格
                        int unit_price = tmpJson.getInteger("unit_price");
                        tmpJson.put("unit_price", unit_price);
                        // 商品件数
                        int product_plan_cnt = detail.getNumber() + tmpJson.getInteger("product_plan_cnt");
                        tmpJson.put("product_plan_cnt", product_plan_cnt);

                        // 商品价格合计
                        int price = detail.getProduct_total_price() + tmpJson.getInteger("price");
                        tmpJson.put("price", price);

                        int requesting_cnt = json.getInteger("requesting_cnt") + product_plan_cnt;
                        tmpJson.put("requesting_cnt", requesting_cnt);

                        productCheckFlg = true;
                    }
                }
            }

            if (!productCheckFlg) {
                items.add(json);
            }
            product_plan_total[0] += detail.getNumber();
        }
        // 出庫依頼商品種類数
        jsonObject.put("product_kind_plan_cnt", items.size());
        // 出庫依頼商品数計
        jsonObject.put("product_plan_total", product_plan_total[0]);

        jsonObject.put("items", items);
        int shipment_status = 3;

        // 受注取込時に、受注情報の配送希望時間帯が配送関連設定に存在しない場合、「出庫確認待ち」にする TODO 入金待ち時
        String delivery_time_slot = tc200_order.getDelivery_time_slot();
        if (!StringTools.isNullOrEmpty(delivery_time_slot)) {
            Ms006_delivery_time deliveryTime = deliveryDao.getDeliveryTimeById(Integer.parseInt(delivery_time_slot));
            if (!StringTools.isNullOrEmpty(deliveryTime.getKubu()) && deliveryTime.getKubu() == 99999) {
                shipment_status = 1;
                jsonObject.put("status_message", "配送時間帯は正しく変換されず、配送会社へ配送時間を反映されない恐れがありますので、再度確認した上でご修正ください。");
            }
        }

        // 入金待ち
        if (!StringTools.isNullOrEmpty(tc200_order.getOrder_type()) && tc200_order.getOrder_type() == 0) {
            shipment_status = 9;
            jsonObject.put("status_message", "");
        }

        // 納品書 合計 = 合計請求金額
        jsonObject.put("total_amount", tc200_order.getBilling_total());
        // ギフトラッピングタイプ = 如果受注ギフト有值的话 固定値：ギフトあり
        String gift_wrapping_type = "";
        if (!StringTools.isNullOrEmpty(tc200_order.getGift_wish())) {
            gift_wrapping_type = "ギフトあり";
        }
        jsonObject.put("gift_wrapping_type", gift_wrapping_type);
        jsonObject.put("shipment_status", shipment_status);
        // 総計通常税率 固定値：０
        jsonObject.put("total_with_normal_tax", 0);
        // 総計軽減税率 固定値：０
        jsonObject.put("total_with_reduced_tax", 0);
        // 納品書 小計 = 商品税抜金額
        jsonObject.put("subtotal_amount", tc200_order.getProduct_price_excluding_tax());// 商品小计显示null值的原因所在
        // 備考(出荷指示特記事項)
        jsonObject.put("instructions_special_notes", tc200_order.getBikou1());
        // 備考1
        jsonObject.put("bikou1", tc200_order.getBikou2());
        // 備考2
        jsonObject.put("bikou2", tc200_order.getBikou3());
        // 備考3
        jsonObject.put("bikou3", tc200_order.getBikou4());
        // 備考4
        jsonObject.put("bikou4", tc200_order.getBikou5());
        // 備考5
        jsonObject.put("bikou5", tc200_order.getBikou6());
        // 備考6
        jsonObject.put("bikou6", tc200_order.getBikou7());
        // 備考7
        jsonObject.put("bikou7", tc200_order.getBikou8());
        // 備考9 (配送方法使用备考9)
        jsonObject.put("bikou9", tc200_order.getBikou9());
        // 備考10 (支払方法使用备考10)
        jsonObject.put("bikou10", tc200_order.getBikou10());
        // 備考内容显示flg
        Integer bikou_flg = 0;
        if (!StringTools.isNullOrEmpty(tc200_order.getBikou_flg())) {
            bikou_flg = tc200_order.getBikou_flg();
        }
        jsonObject.put("bikou_flg", bikou_flg);
        jsonObject.put("shopify", shopify_status);
        jsonObject.put("order_flg", "1");

        jsonObject.put("payment_id", tc200_order.getPayment_id());
        // 配送先名前フリガナ
        String surname_kana = tc200_order.getReceiver_family_kana();
        if (!StringTools.isNullOrEmpty(tc200_order.getReceiver_first_kana())) {
            surname_kana += " " + tc200_order.getReceiver_first_kana();
        }
        jsonObject.put("surname_kana", surname_kana);
        // @注文情報追加 2021/04/16 wang edit end
        jsonObject.put("order_datetime", tc200_order.getOrder_datetime());
        // 定期購入ID
        jsonObject.put("buy_id", tc200_order.getBuy_id());
        // 定期購入回数
        jsonObject.put("buy_cnt", tc200_order.getBuy_cnt());
        // 次回お届け予定日
        jsonObject.put("next_delivery_date", tc200_order.getNext_delivery_date());
        // 購入者備考欄
        jsonObject.put("memo", tc200_order.getMemo());
        // Qoo10関連注文番号
        jsonObject.put("related_order_no", tc200_order.getRelated_order_no());

        // 出庫管理IDを取得
        String shipment_plan_id = shipmentsService.setShipmentPlanId(client_id);
        jsonObject.put("shipment_plan_id", shipment_plan_id);

        // マクロ
        jsonObject = macroSettingService.setValueByCondition(jsonObject, client_id);

        int boxes = 1;
        if (!StringTools.isNullOrEmpty(tc200_order.getBoxes())) {
            boxes = tc200_order.getBoxes();
        }
        jsonObject.put("boxes", boxes);

        // 出庫管理表の書込を実行
        shipmentsService.setShipments(jsonObject, true, request);
        try {
            // 出庫明細表の書込を実行
            shipmentDetailService.setShipmentDetail(jsonObject, true, request);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 如果是NTM店铺且是Eccube数据，则校验数据合法性
        if (hasNtmEccubeFunction(warehouseId, client_id, "4")) {
            checkEccubeData(tc200_order, client_id, shipment_plan_id);
        }

        return shipment_plan_id;
    }

    private Integer getTaxPrice(double params, Mc105_product_setting mc105ProductSetting) {
        int price = 0;
        if (mc105ProductSetting.getTax() == 1) {
            switch (mc105ProductSetting.getAccordion()) {
                case 1:
                    price = (int) Math.ceil(params);
                    break;
                case 2:
                    price = (int) Math.round(params);
                    break;
                default:
                    price = (int) params;
                    break;
            }
        } else {
            price = (int) params;
        }
        return price;
    }

    /**
     * @Param:
     * @description: 新规店铺受注csv模板信息
     * @return: Tc204_order_template
     * @date: 2020/9/16
     */
    @Override
    public Integer createClientTemplate(JSONObject js) {
        String client_id = js.getString("client_id");
        // String company_id = js.getString("company_id");
        String constant = js.getString("constant");
        String template_nm = js.getString("template_nm");
        String encoding = js.getString("encoding");
        String delimiter = js.getString("delimiter");
        String data = js.getString("data");
        String template = js.getString("template");
        List<String> list = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(template);
        ArrayList<String> headerList = new ArrayList<>();
        list.forEach(param -> {
            if (!StringTools.isNullOrEmpty(param) && param.startsWith("\"") && param.endsWith("\"")) {
                headerList.add(param.substring(1, param.length() - 1));
            } else {
                headerList.add(param);
            }
        });
        template = Joiner.on(",").join(headerList);

        Tc204_order_template tc204 = new Tc204_order_template();
        tc204.setClient_id(client_id);
        tc204.setTemplate_nm(template_nm);
        tc204.setEncoding(encoding);
        tc204.setDelimiter(delimiter);
        tc204.setData(data);
        tc204.setTemplate(template);
        tc204.setConstant(constant);
        tc204.setIdentification(js.getString("identification"));
        return orderDao.createClientTemplate(tc204);
    }

    /**
     * @Param:
     * @description: 编集店铺受注csv模板信息
     * @return:
     * @date: 2020/9/16
     */
    @Override
    public Integer updateClientTemplate(JSONObject js) {
        Integer template_cd = js.getInteger("template_cd");
        String template_nm = js.getString("template_nm");
        String encoding = js.getString("encoding");
        String delimiter = js.getString("delimiter");
        String data = js.getString("data");
        String constant = js.getString("constant");
        Tc204_order_template tc204 = new Tc204_order_template();
        tc204.setTemplate_cd(template_cd);
        tc204.setTemplate_nm(template_nm);
        tc204.setEncoding(encoding);
        tc204.setDelimiter(delimiter);
        tc204.setData(data);
        tc204.setConstant(constant);
        tc204.setIdentification(js.getString("identification"));
        return orderDao.updateClientTemplate(tc204);
    }

    private File transferToFile(MultipartFile multipartFile) {
        // 选择用缓冲区来实现这个转换即使用java 创建的临时文件 使用 MultipartFile.transferto()方法 。
        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split(".");
            file = File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * @Param: jsonObject
     *         client_id,client_id,ftp_host,ftp_user,ftp_passwd,ftp_path,ftp_filename
     * @description: 保存店铺FTP信息
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2020/10/19
     */
    @Override
    public JSONObject insertFtpClient(JSONObject jsonObject) {
        String ftp_host = jsonObject.getString("ftp_host");
        String clientId = jsonObject.getString("client_id");
        Integer get_send_flg = jsonObject.getInteger("get_send_flg");
        // 根据apiName 和 client_id 查询以前是否存在
        Tc206_order_ftp orderFtpClient = apiService.getFtpClientInfoById(ftp_host, clientId, get_send_flg);

        Integer maxId = getMaxId(clientId);
        jsonObject.put("id", maxId);
        if (!StringTools.isNullOrEmpty(orderFtpClient)) {
            apiService.updateFtpClient(jsonObject);
        } else {
            apiService.insertFtpClient(jsonObject);
        }
        return CommonUtils.success();
    }

    @Override
    public JSONObject getFtpClientInfo(String client_id, Integer get_send_flag) {
        // TODO 自動生成されたメソッド・スタブ
        return apiService.getFtpClientInfo(client_id, get_send_flag);
    }

    /**
     * @Param:
     * @description: 获取S3文件列表
     * @return: list
     * @date: 2021/1/14
     */
    @Override
    public List<S3ObjectSummary> getS3FileList(String bucket, String password1, String password2, String folder) {
        // System.err.println("开始获取文件列表");
        AmazonS3 client = getCredentials(password1, password2);
        Pattern checkCsv = Pattern.compile("^.+\\.csv$");
        // System.err.println("验证完了");
        // aファイル一覧を取得
        ObjectListing objListing = client.listObjects(bucket); // バケット名を指定
        List<S3ObjectSummary> objList = objListing.getObjectSummaries();
        if (!StringTools.isNullOrEmpty(folder)) {
            Iterator<S3ObjectSummary> iterator = objList.iterator();
            while (iterator.hasNext()) {
                String folderCheck = iterator.next().getKey();
                // a如果接口返回的值不是csv文件则删掉
                if (!checkCsv.matcher(folderCheck).matches()) {
                    iterator.remove();
                    // a判断传过来的路径是否匹配list中的值，不匹配则删掉
                } else if (folderCheck.indexOf(folder) != 0) {
                    iterator.remove();
                }
            }
        }
        return objList;
    }

    /**
     * @Param:
     * @description: 获取S3所有文件夹
     * @return: list
     * @date: 2021/1/14
     */
    @Override
    public List<S3ObjectSummary> getS3Folder(String bucket, String password1, String password2) {
        AmazonS3 client = getCredentials(password1, password2);
        // aファイル一覧を取得
        ObjectListing objListing = client.listObjects(bucket); // バケット名を指定
        List<S3ObjectSummary> objList = objListing.getObjectSummaries();

        Iterator<S3ObjectSummary> iterator = objList.iterator();
        while (iterator.hasNext()) {
            String folderCheck = iterator.next().getKey();
            // a如果不是文件夹，则删掉
            if (folderCheck.split("/").length != 1) {
                iterator.remove();
            }
        }
        return objList;

    }

    /**
     * @Param:
     * @description: 下载读取S3指定文件
     * @return: list
     * @date: 2021/1/14
     */
    @Override
    public JSONObject s3CsvDownload(String bucket, String password1, String password2, String filePath,
        String client_id, String warehouse_cd, Integer template_cd, String shipmentStatus, String company_id,
        HttpServletRequest request) {
        AmazonS3 client = getCredentials(password1, password2);
        // a取得するファイルのバケット名とキー名(ファイルパス)を用意
        GetObjectRequest req = new GetObjectRequest(bucket, filePath);
        int index = filePath.split("/").length;
        // === aファイルに直接保存する場合 ===
        // a格納先のファイル
        File file = new File(pathProps.getOrder() + filePath.split("/")[index - 1]);
        // aオブジェクトの取得
        client.getObject(req, file);
        InputStream inputStream;
        JSONObject js = null;
        try {
            inputStream = new FileInputStream(file);
            MultipartFile multipartFile = new MockMultipartFile(file.getName(), inputStream);
            js = importOrderCsv(multipartFile, client_id, warehouse_cd, shipmentStatus, request, template_cd,
                company_id, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return js;
    }

    /**
     * @Param:
     * @description: S3验证
     * @return: list
     * @date: 2021/1/14
     */
    private AmazonS3 getCredentials(String password1, String password2) {
        AWSCredentials credentials = new BasicAWSCredentials(
            // aアクセスキー
            password1,
            // aシークレットキー
            password2);
        return AmazonS3ClientBuilder.standard()
            // a認証情報を設定
            .withCredentials(new AWSStaticCredentialsProvider(credentials))
            // aリージョンを AP_NORTHEAST_1(東京) に設定
            .withRegion(Regions.AP_NORTHEAST_1).build();
    }

    /**
     * @Param:
     * @description: 新规s3设定信息
     * @return:
     * @date: 2021/1/15
     */
    @Override
    public void insertS3Setting(String client_id, String bucket, String password1, String password2, String folder,
        String upload_folder) {
        Tc207_order_s3 tc207 = new Tc207_order_s3();
        tc207.setClient_id(client_id);
        tc207.setBucket(bucket);
        tc207.setPassword1(password1);
        tc207.setPassword2(password2);
        tc207.setFolder(folder);
        tc207.setIns_date(DateUtils.getNowTime(null));
        tc207.setUpd_date(DateUtils.getNowTime(null));
        tc207.setUpload_folder(upload_folder);
        orderDao.insertS3Setting(tc207);
    }

    /**
     * @Param:
     * @description: 更新s3设定信息
     * @return:
     * @date: 2021/1/15
     */
    @Override
    public void updateS3Setting(String client_id, String bucket, String password1, String password2, String folder,
        String upload_folder) {
        Tc207_order_s3 tc207 = new Tc207_order_s3();
        tc207.setClient_id(client_id);
        tc207.setBucket(bucket);
        tc207.setPassword1(password1);
        tc207.setPassword2(password2);
        tc207.setUpd_date(DateUtils.getNowTime(null));
        tc207.setFolder(folder);
        tc207.setUpload_folder(upload_folder);
        orderDao.updateS3Setting(tc207);
    }

    /*
     * @Description: 受注各个状态件数取得
     *
     * @Param: client_id
     *
     * @return: JSON
     *
     *
     * @Date: 2021/01/26
     */
    @Override
    public JSONObject orderCount(String client_id) {
        Integer[] orderCount = new Integer[4];
        // 获取未读的错误信息
        List<Tc207_order_error> errorMessage = orderDao.getOrderErrorMes(client_id, 0, null);
        orderCount[0] = errorMessage.size();
        // 获取取消履历
        List<Tc208_order_cancel> orderCancelMes = orderDao.getOrderCancelMes(client_id, 0, null);
        orderCount[2] = orderCancelMes.size();

        // 获取当日受注総計
        // Date nowTime = CommonUtil.getNowTime(null);
        // List<Tc200_order> orderList = orderDao.getOrderList(client_id, null, 0, null,
        // null, nowTime, nowTime, null, null, null, null, null, null, null ,null);

        // 获取到当日受注 没有出库的数据
        // List<Tc200_order> collect = orderList.stream().filter(x -> x.getOuter_order_status() ==
        // 0).collect(Collectors.toList());
        // int size = 0;
        // if (!StringTools.isNullOrEmpty(collect) && !collect.isEmpty()) {
        // size = collect.size();
        // }

        // 获取未出库的数据
        Integer orderListCount = orderDao.getOrderCount(client_id);
        orderCount[1] = orderListCount;
        // 所有未出库的数据 - 当日未出库的数据 + 当日受注総計
        // orderCount[3] = orderShipmentList.size() - size + orderList.size();
        return CommonUtils.success(orderCount);
    }

    /**
     * @Description: 获取受注时的错误信息
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/26
     */
    @Override
    public JSONObject orderErrorMessages(String client_id, Integer status, Integer page, Integer size, String sort) {
        if (StringTools.isNullOrEmpty(sort)) {
            sort = "desc";
        }
        PageHelper.startPage(page, size);
        List<Tc207_order_error> errorMessage = orderDao.getOrderErrorMes(client_id, status, sort);
        PageInfo<Tc207_order_error> pageInfo = new PageInfo<>(errorMessage);
        JSONObject resultJsonObject = new JSONObject();
        resultJsonObject.put("errorMessage", errorMessage);
        resultJsonObject.put("total", pageInfo.getTotal());

        return CommonUtils.success(resultJsonObject);
    }

    /**
     * @Description: 更新消息为已读状态
     * @Param: client_id
     * @return: JSON
     * @Date: 2021/01/27
     */
    @Override
    public JSONObject updOrderErrorMes(String client_id, Integer[] order_error_no) {
        try {
            orderDao.updOrderErrorMes(client_id, order_error_no);
        } catch (Exception e) {
            logger.error("");
            logger.error(BaseException.print(e));
        }
        return CommonUtils.success();
    }

    /**
     * @param client_id : 店舗ID
     * @param status ： 受注取消 确认状态
     * @param page ： 页数
     * @param size ： 每页显示行数
     * @param sort ： 排序方式
     * @description: 受注取消统计
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 9:55
     */
    @Override
    public JSONObject orderCancelMessages(String client_id, Integer status, Integer page, Integer size, String sort) {
        String sortType = null;
        if (!StringTools.isNullOrEmpty(sort)) {
            sortType = "descending".equals(sort) ? "DESC" : "ASC";
        }
        PageHelper.startPage(page, size);
        try {
            List<Tc208_order_cancel> cancelMes = orderDao.getOrderCancelMes(client_id, status, sortType);
            cancelMes.forEach(data -> {
                String shipment_plan_id = data.getShipment_plan_id();
                if (!StringTools.isNullOrEmpty(shipment_plan_id)) {
                    String substring = shipment_plan_id.substring(2);
                    boolean integerBool = StringTools.isInteger(substring);
                    if (!integerBool) {
                        data.setShipment_plan_id("-");
                        data.setShipment_status(shipment_plan_id);
                    } else {
                        List<Tw200_shipment> shipmentId = shipmentsDao.getShipmentInfoByShipmentId(shipment_plan_id);
                        Optional<Tw200_shipment> first = shipmentId.stream().findFirst();
                        boolean present = first.isPresent();
                        if (present) {
                            Integer del_flg = first.get().getDel_flg();
                            String value;
                            if (del_flg == 0) {
                                Integer shipment_status = first.get().getShipment_status();
                                switch (shipment_status) {
                                    case 1:
                                        value = "確認待ち";
                                        break;
                                    case 2:
                                        value = "引当待ち";
                                        break;
                                    case 3:
                                        value = "出荷待ち";
                                        break;
                                    case 4:
                                        value = "出荷作業中";
                                        break;
                                    case 5:
                                        value = "検品中";
                                        break;
                                    case 6:
                                        value = "出荷保留";
                                        break;
                                    case 7:
                                        value = "出荷検品済";
                                        break;
                                    case 8:
                                        value = "出荷済み";
                                        break;
                                    case 9:
                                        value = "入金待ち";
                                        break;
                                    case 11:
                                        value = "出庫承認失敗";
                                        break;
                                    case 41:
                                        value = "出庫承認待ち";
                                        break;
                                    case 42:
                                        value = "出庫承認完了";
                                        break;
                                    default:
                                        value = "";
                                        break;
                                }
                            } else {
                                value = "出庫依頼取消済み";
                            }

                            data.setShipment_status(value);
                        }
                    }

                }
            });
            PageInfo<Tc208_order_cancel> pageInfo = new PageInfo<>(cancelMes);
            JSONObject resultJsonObject = new JSONObject();
            resultJsonObject.put("cancelMes", cancelMes);
            resultJsonObject.put("total", pageInfo.getTotal());
            return CommonUtils.success(resultJsonObject);
        } catch (Exception e) {
            logger.error("店舗ID = {},受注キャンセルの取得失敗", client_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param client_id ：店舗ID
     * @param order_cancel_no ： 多个受注番号
     * @description: 更新受注取消为確認済
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/7 10:37
     */
    @Override
    public JSONObject updOrderCancelMes(String client_id, String[] order_cancel_no) {
        try {
            orderDao.updOrderCancelMes(client_id, order_cancel_no);
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error("店舗ID = {}, 受注キャンセルの更新失敗", client_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    private String checkPhoneToList(String number) {
        // 戻り値
        String result = "";
        // 置換前, 置換後
        Map<String, String> map = new HashMap<>();
        map.put("+81", "0");
        map.put("(", "");
        map.put(")", "");
        map.put("-", "");
        map.put(" ", "");
        map.put("　", "");
        if (!StringTools.isNullOrEmpty(number)) {
            //
            for (String key : map.keySet()) {
                number = number.replace(key, map.get(key));
            }
            result = checkTelephoneNumber(number, "^0\\d\\d{4}\\d{4}$");// 固定
            if (!StringTools.isNullOrEmpty(result) && !"--".equals(result)) {
                return result;
            }
            result = checkTelephoneNumber(number, "^\\(0\\d\\)\\d{4}\\d{4}$");// 固定
            if (!StringTools.isNullOrEmpty(result) && !"--".equals(result)) {
                return result;
            }
            result = checkTelephoneNumber(number, "^(070|080|090)\\d{4}\\d{4}$");// 携帯
            if (!StringTools.isNullOrEmpty(result) && !"--".equals(result)) {
                return result;
            }
            result = checkTelephoneNumber(number, "^050\\d{4}\\d{4}$");// 携帯IP
            if (!StringTools.isNullOrEmpty(result) && !"--".equals(result)) {
                return result;
            }
            result = checkTelephoneNumber(number, "^0120\\d{3}\\d{3}$");// 0120
            if (!StringTools.isNullOrEmpty(result) && !"--".equals(result)) {
                return result;
            }
            result = checkTelephoneNumber(number, "^[1-9][0-9].*");// 数字
            if (!StringTools.isNullOrEmpty(result) && !"--".equals(result)) {
                return result;
            }
        }
        return result;
    }

    private String checkTelephoneNumber(String number, String type) {// [50]
        Pattern pattern = Pattern.compile(type);
        Matcher matcher = pattern.matcher(number);
        String phone = "";
        if (matcher.find()) {
            String telStr = matcher.group();
            if (telStr != null) {
                int len = telStr.length();
                // 不正データがある場合、０埋め
                if (len <= 8) {
                    number = String.format("%10s", number).replace(" ", "0");
                    len = 10;
                }
                phone = number.substring(0, len - 8) + "-" + number.substring(len - 8).substring(0, 4) + "-"
                    + number.substring(len - 4);
            }
        }
        return phone;
    }

    /**
     * @Param:
     * @description: 获取api连携平台信息
     * @return:
     * @date: 2020/12/21
     */
    @Override
    public List<Ms013_api_template> getApiStoreInfo(String template) {
        return orderDao.getApiStoreInfo(template);
    }


    /**
     * @param shipment_plan_id : 出库依赖Id
     * @description: 根据出库依赖Id获取出库取消信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/18 16:25
     */
    @Override
    public JSONObject getCancelInfo(String shipment_plan_id) {
        List<Tc208_order_cancel> cancelInfo = orderCancelDao.getCancelInfo(shipment_plan_id);
        return CommonUtils.success(cancelInfo);
    }

    /**
     * @Param: jsonObject
     * @param: client_id
     * @param: outer_order_no
     * @param: warehouse_cd
     * @description: 入金待ちから入金済みの処理
     * @return: com.alibaba.fastjson.JSONObject
     * @author: HZM
     * @date: 2021/3/22
     */
    @Override
    public JSONObject upOrderStatus(String client_id, String outer_order_no, String warehouse_cd,
        String Shipment_plan_id, HttpServletRequest request) {
        orderDao.upOrderType(client_id, outer_order_no);
        if (!StringTools.isNullOrEmpty(Shipment_plan_id)) {
            String[] shipment_id = {
                Shipment_plan_id
            };

            String delivery_time_slot = shipmentsDao.getShipmentDeliveryTimeSlot(client_id, Shipment_plan_id);
            int shipment_status = 9;
            String status_message = "";
            if (!StringTools.isNullOrEmpty(delivery_time_slot)) {
                Ms006_delivery_time deliveryTime =
                    deliveryDao.getDeliveryTimeById(Integer.parseInt(delivery_time_slot));
                if (deliveryTime.getKubu() == 99999) {
                    shipment_status = 1;
                    status_message = "配送時間帯は正しく変換されず、配送会社へ配送時間を反映されない恐れがありますので、再度確認した上でご修正ください。";
                }
            }

            shipmentService.setShipmentStatus(request, warehouse_cd, shipment_status, shipment_id, null,
                status_message, null, true, "9", null);
        }

        return CommonUtils.success();
    }

    /**
     * @param api_name : api设定名称
     * @param client_id : 店铺Id
     * @param certificate : 证书
     * @param secretKey : 秘密键
     * @description: 上传yahoo的证书及秘密键
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/9/23 12:33
     */
    @Override
    public JSONObject uploadYahoo(String api_name, String client_id, MultipartFile certificate,
        MultipartFile secretKey) {
        String fileName = client_id + "-YH-" + System.currentTimeMillis();
        String certificatePath = pathProps.getRoot() + pathProps.getYahoo() + client_id + "/" + fileName + ".crt";
        CommonUtils.uploadFile(certificatePath, certificate);
        String secretKeyPath = pathProps.getRoot() + pathProps.getYahoo() + client_id + "/" + fileName + ".key";
        CommonUtils.uploadFile(secretKeyPath, secretKey);
        return CommonUtils.success();
    }

    /**
     * @param rk144CsvOrder : 受注を管理するEntityクラス
     * @description: 将实体类字段注解里面的column作为key 字段数据为value
     * @return: java.util.Map<java.lang.String, java.lang.String>
     * @date: 2021/5/10 11:03
     */
    public Map<String, String> getRk144OrderData(Rk144_csv_order rk144CsvOrder) {
        HashMap<String, String> map = new HashMap<>(193);
        Class<? extends Rk144_csv_order> clazz = rk144CsvOrder.getClass();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            boolean annotationPresent = field.isAnnotationPresent(CsvBindByName.class);
            // 判断是否含有注解
            if (annotationPresent) {
                CsvBindByName csvBindByName = field.getAnnotation(CsvBindByName.class);
                String column = csvBindByName.column();
                String value = "";
                try {
                    value = (String) field.get(rk144CsvOrder);
                } catch (IllegalAccessException e) {
                    logger.error("获取字段数据失败, 字段名为={}", field.getName());
                    logger.error(BaseException.print(e));
                }
                map.put(column, value);
            }
        }
        return map;
    }

    /**
     * @param header : 客户上传csv的header
     * @param csvOrderList : 错误的受注数据
     * @param errCsvJson : 保存错误数据
     * @param parameterList : 保存对应的key值
     * @description: 获取到受注错误数据
     * @return: void
     * @date: 2021/5/10 17:58
     */
    public void getWrongOrderData(String header, Collection<Rk144_csv_order> csvOrderList, JSONArray errCsvJson,
        List<Integer> parameterList) {

        List<String> titles = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(header);
        for (Rk144_csv_order csvOrder : csvOrderList) {
            Map<String, String> rk144OrderData = getRk144OrderData(csvOrder);
            int i = 0;
            JSONObject jsonObject = new JSONObject();
            for (String title : titles) {
                // 获取到模板的header
                String tempHeader = title;
                if (csvHeaderMap.containsKey(title)) {
                    tempHeader = csvHeaderMap.get(title);
                }
                String data = "";
                if (rk144OrderData.containsKey(tempHeader)) {
                    data = rk144OrderData.get(tempHeader);
                }
                jsonObject.put(String.valueOf(i), data);
                if (errCsvJson.isEmpty()) {
                    // 因为多条数据key都是相同的 所以只需要第一次进来保存key值
                    parameterList.add(i);
                }
                i++;
            }
            errCsvJson.add(jsonObject);
        }
    }
}
