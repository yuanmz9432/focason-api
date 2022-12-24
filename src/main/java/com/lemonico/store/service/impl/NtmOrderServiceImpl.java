package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.beust.jcommander.internal.Lists;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.LcResourceAlreadyExistsException;
import com.lemonico.core.exception.LcResourceNotFoundException;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.ExcelUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.*;
import com.lemonico.store.service.NtmOrderService;
import com.lemonico.wms.service.ShipmentService;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: NtmOrderServiceImpl
 * @description: NTM 受注接口实现类
 * @date: 2021/3/31 13:31
 **/
@Service
public class NtmOrderServiceImpl implements NtmOrderService
{

    @Resource
    private ProductDao productDao;

    @Resource
    private LoginDao loginDao;

    @Resource
    private OrderHistoryDao orderHistoryDao;

    @Resource
    private OrderDao orderDao;

    @Resource
    private OrderDetailDao orderDetailDao;

    @Resource
    private ClientDao clientDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private OrderServiceImpl orderServiceImpl;

    @Resource
    private ShipmentService shipmentService;

    @Resource
    private SettingDao settingDao;

    private final static Logger logger = LoggerFactory.getLogger(NtmOrderServiceImpl.class);


    private static final HashMap<String, Integer> map;

    static {
        // 因为sheet1 都是没有规律的，所以提前定义好map， value 为坐标(x)
        // *因为都是在下标第3列，所以统一省略。
        map = new HashMap<>(35);
        // データ支給日
        map.put("dataSupplyDate", 8);
        // 最短納品日
        map.put("shortestDeliveryDate", 10);
        // ツール1品番
        map.put("productCode1", 12);
        // ツール2品番
        map.put("productCode2", 13);
        // ツール3品番
        map.put("productCode3", 14);
        // ツール4品番
        map.put("productCode4", 15);
        // ツール5品番
        map.put("productCode5", 16);
        // ツール6品番
        map.put("productCode6", 17);
        // ツール7品番
        map.put("productCode7", 18);
        // ツール8品番
        map.put("productCode8", 19);
        // ツール9品番
        map.put("productCode9", 20);
        // ツール10品番
        map.put("productCode10", 21);
        // WEB受注取込開始ID
        map.put("orderNo", 25);
        // 個口数判定基準
        map.put("criteria", 28);
        // (合計時)1口あたり個口数基準数量
        map.put("baseQuantity", 29);
        // (別時)ツール1個口数基準数量
        map.put("numberOfUnits1", 30);
        // (別時)ツール2個口数基準数量
        map.put("numberOfUnits2", 31);
        // (別時)ツール3個口数基準数量
        map.put("numberOfUnits3", 32);
        // (別時)ツール4個口数基準数量
        map.put("numberOfUnits4", 33);
        // (別時)ツール5個口数基準数量
        map.put("numberOfUnits5", 34);
        // (別時)ツール6個口数基準数量
        map.put("numberOfUnits6", 35);
        // (別時)ツール7個口数基準数量
        map.put("numberOfUnits7", 36);
        // (別時)ツール8個口数基準数量
        map.put("numberOfUnits8", 37);
        // (別時)ツール9個口数基準数量
        map.put("numberOfUnits9", 38);
        // (別時)ツール10個口数基準数量
        map.put("numberOfUnits10", 39);
    }

    /**
     * @param client_id : 店铺Id
     * @param warehouse_cd : 仓库Id
     * @param file : 上传的excel文件
     * @param shipmentStatus : 是否出库的flg， 1：出庫依頼する 2：出庫依頼しない
     * @param request : 请求
     * @description: NTM受注Excel取込
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/3/23 14:02
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public JSONObject importOrderExcel(MultipartFile file, String client_id, String warehouse_cd,
        String shipmentStatus, String eccubeFLg, HttpServletRequest request) {
        // 存取错误信息
        ArrayList<String> errList = new ArrayList<>();
        // 获得所有的都道府 经过去重的集合
        List<String> todoufukenList = loginDao.getTodoufukenList();

        // 存取excel读取的数据
        ArrayList<Nt143_excel_order> excelOrders = new ArrayList<>();
        // 存取商品code
        ArrayList<String> productCodeList = new ArrayList<>();
        InputStream inputStream = null;
        XSSFWorkbook workbook = null;
        HashMap<String, String> paramMap = new HashMap<>(35);
        // 個口数判定基準
        String criteria = "";
        // (合計時)1口あたり個口数基準数量
        String baseQuantity = "";
        try {
            inputStream = file.getInputStream();
            workbook = new XSSFWorkbook(inputStream);
            // 获取sheet1 的数据
            XSSFSheet sheet1 = workbook.getSheetAt(0);
            StringBuilder errEmpty = new StringBuilder();
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String key = entry.getKey();
                Integer value = entry.getValue();
                String param = ExcelUtils.getCellValue(sheet1.getRow(value).getCell(3));
                if (StringTools.isNullOrEmpty(param)) {
                    if ("productCode1".equals(key)) {
                        errEmpty.append("ツール1品番、空にすることはできません。");
                    } else if ("orderNo".equals(key)) {
                        errEmpty.append("WEB受注取込開始ID、空にすることはできません。");
                    } else if ("criteria".equals(key)) {
                        errEmpty.append("個口数判定基準、空にすることはできません。");
                    }
                }
                if ("shortestDeliveryDate".equals(key)) {
                    if (Strings.isNullOrEmpty(param)) {
                        errEmpty.append("最短納品日は、「YYYY/MM/DD」の形式を守ってご入力ください。");
                    } else {
                        try {
                            LocalDate.parse(param, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                        } catch (Exception e) {
                            errEmpty.append("最短納品日は、「YYYY/MM/DD」の形式を守ってご入力ください。");
                        }
                    }
                } else if ("dataSupplyDate".equals(key)) {
                    if (Strings.isNullOrEmpty(param)) {
                        errEmpty.append("データ支給日は、「YYYY/MM/DD」の形式を守ってご入力ください。");
                    } else {
                        try {
                            LocalDate.parse(param, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
                        } catch (Exception e) {
                            errEmpty.append("データ支給日は、「YYYY/MM/DD」の形式を守ってご入力ください。");
                        }
                    }
                }
                // 如果为WEB受注取込開始ID 需要去掉最后「00」通し番号 *如果长度小于等于二则取原始值
                if ("orderNo".equals(key) && !StringTools.isNullOrEmpty(param)) {
                    if (param.length() > 2) {
                        param = param.substring(0, param.length() - 2);
                    }
                }
                // key不为空 key中包含productCode 并且从excel中读取的值不为空 存入到商品code集合中
                if (!StringTools.isNullOrEmpty(key) && key.contains("productCode")
                    && !StringTools.isNullOrEmpty(param)) {
                    productCodeList.add(param);
                }
                paramMap.put(key, param);
            }
            // 個口数判定基準
            criteria = paramMap.get("criteria");
            // (合計時)1口あたり個口数基準数量
            baseQuantity = paramMap.get("baseQuantity");
            // (別時)ツール1個口数基準数量
            String numberOfUnits1 = paramMap.get("numberOfUnits1");

            // '(合計時)1口あたり個口数基準数量' 字段长度、半角校验 Integer.MAX_VALUE 最大长度为10
            if (StringTools.isIncludeHalfWidth(baseQuantity) || !StringTools.isInteger(baseQuantity)
                || (null != baseQuantity && baseQuantity.length() > 9)) {
                errList.add("1口あたり個口数基準数量は、「半角数字」のみの9桁以内でご入力ください。");
            }

            if (!StringTools.isNullOrEmpty(criteria)) {
                if ("アイテム合計".equals(criteria) && StringTools.isNullOrEmpty(baseQuantity)) {
                    errEmpty.append("(合計時)1口あたり個口数基準数量、空にすることはできません。");
                }
                if ("アイテムごと".equals(criteria) && StringTools.isNullOrEmpty(numberOfUnits1)) {
                    errEmpty.append("(別時)ツール1個口数基準数量、空にすることはできません。");
                }
            }
            if (errEmpty.length() != 0) {
                errList.add(errEmpty.toString());
            }

            // 保存每一行的错误信息
            StringBuilder rowError = new StringBuilder();
            // 保存都道府错误信息
            StringBuilder todoufukenError = new StringBuilder();
            XSSFSheet sheet2 = workbook.getSheetAt(1);
            Iterator<Row> rowIterator = sheet2.rowIterator();
            int index = 0;
            Boolean existHeader = false;
            Row row;
            while (rowIterator.hasNext()) {
                index++;
                row = rowIterator.next();
                // 第一行为空白， 第二行为title 所以直接 continue
                if (index <= 2) {
                    if ("通しNo".equals(ExcelUtils.getCellValue(row.getCell(0)))) {
                        existHeader = true;
                        continue;
                    }
                } else if (index == 3 && !existHeader) {
                    errList.add("sheet2 Excelにはタイトルがありません。");
                }
                // 如果某行的第一列 通しNo为空，则证明这条数据为空 *应对表格下面多条空数据
                if (StringTools.isNullOrEmpty(ExcelUtils.getCellValue(row.getCell(0)))) {
                    continue;
                }
                // 根据反射获取实体类的所以字段
                Nt143_excel_order excelOrder = new Nt143_excel_order();
                Field[] declaredFields = excelOrder.getClass().getDeclaredFields();
                int boxes = 1;
                for (int i = 0; i < declaredFields.length; i++) {
                    // 设置可访问性 默认为false
                    declaredFields[i].setAccessible(true);
                    // 字段名称
                    String fieldName = declaredFields[i].getName();
                    // 字段
                    Field field = declaredFields[i];
                    if ("boxes".equals(fieldName)) {
                        continue;
                    }
                    // 读取相对应的excel单元格的数据
                    String value = ExcelUtils.getCellValue(row.getCell(i));
                    if ("commonNo".equals(fieldName)) {
                        // 拼接 受注番号 ： WEB受注取込開始ID + 通しNo
                        value = paramMap.get("orderNo") + completionParameters(value);
                    }
                    // 郵便番号不能为空
                    if ("zip".equals(fieldName) && StringTools.isNullOrEmpty(value)) {
                        rowError.append("郵便番号空にすることはできません。");
                    }
                    // 住所1
                    if ("address".equals(fieldName)) {
                        if (StringTools.isNullOrEmpty(value)) {
                            rowError.append("都道府県空にすることはできません。");
                        } else {
                            if (StringTools.isNullOrEmpty(getPrefectures(value, todoufukenList))) {
                                todoufukenError.append("都道府県が存在しない。");
                            }
                        }
                    }
                    // 名前１（部課名等）
                    if ("name1".equals(fieldName) && StringTools.isNullOrEmpty(value)) {
                        rowError.append("名前１（部課名等）空にすることはできません。");
                    }
                    // 字段长度、半角校验
                    if ("commonNo".equals(fieldName)
                        && (StringTools.isIncludeHalfWidth(value) || value.length() > 32)) {
                        rowError.append("注文番号は、「-_,半角英数」のみの32文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("email".equals(fieldName) && (StringTools.isIncludeHalfWidth(value) || value.length() > 100)) {
                        rowError.append("請求先支店コード(メールアドレス)には「半角英数」のみの100文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("shopName".equals(fieldName) && value.length() > 50) {
                        rowError.append("請求先支店名(月報表示用)には、50文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("productNum1".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール1には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum2".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール2には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum3".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール3には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum4".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール4には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum5".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール5には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum6".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール6には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum7".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール7には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum8".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール9には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum9".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール9には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("productNum10".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value)
                            || (!Strings.isNullOrEmpty(value) && !StringTools.isInteger(value))
                            || value.length() > 9)) {
                        rowError.append("ツール10には、「半角数字」のみを使用し、9文字以内でご入力ください。");
                    }
                    if ("bundledFlg".equals(fieldName) && (!Strings.isNullOrEmpty(value) && !value.equals("同梱あり"))) {
                        rowError.append("同梱有無には、「同梱あり」でご入力ください。また、空にすることはできません。");
                    }
                    if ("bundledCommonNo".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value) || value.length() > 9)) {
                        rowError.append("同梱先遠しNoには、「半角数字」のみを使用し、9桁以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("zip".equals(fieldName) && !Strings.isNullOrEmpty(value)) {
                        if ((value.indexOf("-") < 0 && value.length() != 7)
                            || (value.indexOf("-") > 0 && value.length() != 8)) {
                            rowError.append("郵便番号は、「000-0000」の形式を守ってご入力ください。");
                        }
                    }
                    if ("address".equals(fieldName) && !Strings.isNullOrEmpty(value) && value.length() > 50) {
                        rowError.append("住所1は、50文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("address1".equals(fieldName) && !Strings.isNullOrEmpty(value) && value.length() > 50) {
                        rowError.append("住所2は、50文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("address2".equals(fieldName) && !Strings.isNullOrEmpty(value) && value.length() > 50) {
                        rowError.append("住所３（会社名等）は、50文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("name1".equals(fieldName) && !Strings.isNullOrEmpty(value) && value.length() > 50) {
                        rowError.append("名前１（部課名等）は、50文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("name2".equals(fieldName) && !Strings.isNullOrEmpty(value) && value.length() > 50) {
                        rowError.append("名前２（担当者名等）は、50文字以内でご入力ください。また、空にすることはできません。");
                    }
                    if ("phone".equals(fieldName) && !Strings.isNullOrEmpty(value)
                        && (StringTools.isIncludeHalfWidth(value) || value.length() > 60)) {
                        rowError.append("電話番号は、「-,半角数字」のみで、60文字以内でご入力ください。また、空にすることはできません。");
                    }
                    try {
                        field.set(excelOrder, value);
                    } catch (IllegalAccessException e) {
                        logger.error("Nt143_excel_order通过反射插入数据失败");
                        logger.error(BaseException.print(e));
                        throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                    }
                }
                // 个口数默认为1
                excelOrder.setBoxes(boxes);
                excelOrders.add(excelOrder);
                if (rowError.length() > 0) {
                    rowError.insert(0, index + "行目: ");
                    String errorString = rowError.toString();
                    errList.add(errorString.substring(0, errorString.length()));
                    rowError.delete(0, rowError.length());
                }
                if (todoufukenError.length() > 0) {
                    todoufukenError.insert(0, index + "行目: ");
                    errList.add(todoufukenError.toString());
                    todoufukenError.delete(0, todoufukenError.length());
                }
            }
        } catch (IOException e) {
            logger.error("读取excel失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            if (null != workbook) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (null != inputStream) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (errList.size() != 0) {
            logger.error("excel数据不全, 错误信息为: {}", errList);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errlist", errList);
            return CommonUtils.success(jsonObject);
        }
        // 取出不含有同梱先遠しNo的受注信息 转为map结构， key：通しNo value：Nt143_excel_order
        LinkedHashMap<String, Nt143_excel_order> excelOrderLinkedHashMap = new LinkedHashMap<>();
        excelOrders.stream().forEach(
            value -> {
                if (StringTools.isNullOrEmpty(value.getBundledCommonNo())) {
                    excelOrderLinkedHashMap.put(value.getCommonNo(), value);
                }
            });
        // 取出含有同梱あり(并且 同梱先遠しNo 不为空的数据)的受注信息 根据同梱先遠しNo 分组为map结构 key：同梱先遠しNo value：List<Nt143_excel_order>
        Map<String, List<Nt143_excel_order>> bundledListMap = excelOrders.stream()
            .filter(x -> "同梱あり".equals(x.getBundledFlg()) && !Strings.isNullOrEmpty(x.getBundledCommonNo()))
            .collect(Collectors.groupingBy(Nt143_excel_order::getBundledCommonNo));
        // 获取到WEB受注取込開始ID
        String orderNo = paramMap.get("orderNo");
        List<String> bundledErrList = Lists.newArrayList();
        bundledListMap.forEach((key, value) -> {
            // 拼接key值并判断是否存在
            String orderKey = orderNo + completionParameters(key);
            if (excelOrderLinkedHashMap.containsKey(orderKey)) {
                // 根据key值获取到 不含有同捆的对象
                Nt143_excel_order excelOrder = excelOrderLinkedHashMap.get(orderKey);
                // 通过反射获取到不含同捆对象的字段信息
                Field[] fields = excelOrder.getClass().getDeclaredFields();
                // 遍历含有同梱あり的受注信息 因为value 为list
                for (Nt143_excel_order order : value) {
                    // 反射获取到含有同梱あり对象的字段信息
                    Field[] declaredFields = order.getClass().getDeclaredFields();
                    for (Field declaredField : declaredFields) {
                        declaredField.setAccessible(true);
                        String name = declaredField.getName();
                        // 如果字段名称为空或者不等于 productNum 跳出本次循环
                        if (StringTools.isNullOrEmpty(name) || !name.contains("productNum")) {
                            continue;
                        }
                        String productNum = null;
                        try {
                            // 获取到该字段的数据
                            productNum = (String) declaredField.get(order);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                        // 数据为空 跳出本次循环
                        if (StringTools.isNullOrEmpty(productNum)) {
                            continue;
                        }
                        // 遍历不含有同梱あり对象的字段信息
                        for (Field field : fields) {
                            field.setAccessible(true);
                            String fieldName = field.getName();
                            // 如果名称不符 跳出本次循环
                            if (!name.equals(fieldName)) {
                                continue;
                            }
                            String num = null;
                            try {
                                num = (String) field.get(excelOrder);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                            // 计算出该商品的总数量
                            int totalNum = 0;
                            if (!StringTools.isNullOrEmpty(num)) {
                                totalNum = Integer.parseInt(num);
                            }
                            if (!StringTools.isNullOrEmpty(productNum)) {
                                totalNum += Integer.parseInt(productNum);
                            }

                            try {
                                field.set(excelOrder, String.valueOf(totalNum));
                                break;
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
                excelOrderLinkedHashMap.put(orderKey, excelOrder);
            }
        });
        // 根据多个商品code获取多个商品信息 转为map key为code value为Mc100_product
        List<Mc100_product> productList = productDao.getProductInfoByCodeList(productCodeList, client_id);
        Map<String, Mc100_product> productMap = new HashMap<>();
        if (!StringTools.isNullOrEmpty(productList) && productList.size() != 0) {
            productMap = productList.stream().filter(x -> !StringTools.isNullOrEmpty(x.getCode()))
                .collect(Collectors.toMap(Mc100_product::getCode, o -> o));
        } else {
            throw new LcResourceNotFoundException("製品");
        }
        String loginNm = CommonUtils.getToken("login_nm", request);

        if (excelOrderLinkedHashMap.size() > 0) {
            setOrderData(excelOrderLinkedHashMap, productMap, paramMap, client_id, warehouse_cd,
                loginNm, shipmentStatus, eccubeFLg, todoufukenList, request);
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("errlist", Lists.newArrayList("Excelデータエラー"));
            return CommonUtils.success(jsonObject);
        }

        logger.info("excel受注读取完成");
        return CommonUtils.success();
    }

    /**
     * Map元素反转
     * 
     * @param excelOrderMap
     * @return excelOrderMap
     */
    private LinkedHashMap<String, Nt143_excel_order> reverse(LinkedHashMap<String, Nt143_excel_order> excelOrderMap) {
        ListIterator<Map.Entry<String, Nt143_excel_order>> i =
            new ArrayList<Map.Entry<String, Nt143_excel_order>>(excelOrderMap.entrySet())
                .listIterator(excelOrderMap.size());
        LinkedHashMap<String, Nt143_excel_order> res = new LinkedHashMap<>();
        while (i.hasPrevious()) {
            Map.Entry<String, Nt143_excel_order> entry = i.previous();
            res.put(entry.getKey(), entry.getValue());
        }
        return res;
    }

    /**
     *
     *
     * @param excelOrderMap : excel sheet2读取的数据 key：通しNo value：相对应行的数据
     * @param productMap : 商品map key：code value：商品对象
     * @param paramMap : excel sheet1读取的数据
     * @param client_id : 店铺Id
     * @param warehouse_cd : 仓库Id
     * @param loginNm : 用户名
     * @param shipmentStatus : 是否出库 1：出库 0：不出库
     * @param request : 请求
     * @description: 将数据添加到受注表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/4/6 11:29
     * @since
     */
    public JSONObject setOrderData(LinkedHashMap<String, Nt143_excel_order> excelOrderMap,
        Map<String, Mc100_product> productMap,
        HashMap<String, String> paramMap, String client_id,
        String warehouse_cd, String loginNm, String shipmentStatus,
        String eccubeFLg, List<String> todoufukenList, HttpServletRequest request) {

        // 反转元素，保证导入的顺序和list顺序一致（出荷作业list排序循序，先按request_date排，相同时再按shipment_plan_id排）
        excelOrderMap = reverse(excelOrderMap);

        // 初期化
        int subNo = 1;
        // 获取受注取込履歴ID
        Integer historyId = getMaxHistoryId();
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        ArrayList<Tc200_order> tc200_orders = new ArrayList<>();
        ArrayList<Tc201_order_detail> tc201_order_details = new ArrayList<>();
        // 获取所有的配送时间
        List<Ms006_delivery_time> deliveryTimeAll = deliveryDao.getDeliveryTimeAll();
        // 配送会社
        String convertedId = "";
        // 配送方法指定
        String method = "";
        Ms201_client clientInfo = clientDao.getClientInfo(client_id);
        convertedId = clientInfo.getDelivery_method();
        // csv获取到配送方法
        Ms004_delivery ms004 = new Ms004_delivery();
        if (!StringTools.isNullOrEmpty(convertedId)) {
            // a查询出配送公司
            ms004 = deliveryDao.getDeliveryById(convertedId);
            method = ms004.getDelivery_method();
        }
        Ms004_delivery finalMs00 = ms004;
        // 筛选出默认配送公司的配送时间带
        List<Ms006_delivery_time> deliveryTimeList = deliveryTimeAll.stream()
            .filter(x -> x.getDelivery_nm().equals(finalMs00.getDelivery_nm())).collect(Collectors.toList());

        // 保存所有的受注番号
        ArrayList<String> orderNoList = new ArrayList<>();
        for (Map.Entry<String, Nt143_excel_order> entry : excelOrderMap.entrySet()) {
            String orderNo = getNTMOrderNo(subNo, eccubeFLg);
            String outerOrderNo = entry.getKey();
            int resCnt = orderDao.getOuterOrderNo(outerOrderNo, client_id);
            if (resCnt > 0) {
                throw new LcResourceAlreadyExistsException("受注番号");
            }
            Nt143_excel_order excelOrder = entry.getValue();

            // 计算个口数
            Class<? extends Nt143_excel_order> aClass = excelOrder.getClass();
            Field[] declaredFields = aClass.getDeclaredFields();
            // 個口数判定基準
            String criteria = paramMap.get("criteria");
            // (合計時)1口あたり個口数基準数量
            String baseQuantity = paramMap.get("baseQuantity");
            int boxes = 0;
            // 总商品数
            int totalProductNum = 0;
            // 当前商品
            Mc100_product product = null;
            for (Field field : declaredFields) {
                field.setAccessible(true);
                String name = field.getName();

                if (StringTools.isNullOrEmpty(name) || !name.contains("productNum")) {
                    continue;
                }
                String index = name.substring("productNum".length());
                // 获取商品信息
                if (null == product) {
                    String key = "productCode" + index;
                    String productCode = paramMap.get(key);
                    product = productMap.get(productCode);
                }
                int productNum = 0;
                try {
                    String num = (String) field.get(excelOrder);
                    if (!StringTools.isNullOrEmpty(num)) {
                        productNum = Integer.parseInt(num);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                String numberOfUnit = paramMap.get("numberOfUnits" + index);
                if ("アイテムごと".equals(criteria) && !StringTools.isNullOrEmpty(numberOfUnit)) {
                    // 截取出当前商品的序号 拼接为key值 获取对应的個口数基準数量
                    // 用商品数量除以個口数基準数量 并将小数点进一
                    boxes += (int) Math.ceil(((double) productNum) / (Double.parseDouble(numberOfUnit)));
                }
                if ("アイテム合計".equals(criteria) && !StringTools.isNullOrEmpty(baseQuantity)) {
                    // 如果为アイテム合計，先将所有的商品数量加起来放在boxes，
                    boxes += productNum;
                }
                totalProductNum += productNum;
            }
            if ("アイテム合計".equals(criteria)) {
                // 商品数量总数除以あたり個口数基準数量 小数点进一
                boxes = (int) Math.ceil(((double) boxes) / (Double.parseDouble(baseQuantity)));
            }

            Tc200_order tc200Order = new Tc200_order();
            orderNoList.add(orderNo);
            // 个口数
            tc200Order.setBoxes(boxes);
            // 受注番号
            tc200Order.setPurchase_order_no(orderNo);
            // 倉庫管理番号
            tc200Order.setWarehouse_cd(warehouse_cd);
            // 顧客管理番号
            tc200Order.setClient_id(client_id);
            // 外部受注番号
            tc200Order.setOuter_order_no(outerOrderNo);
            // 受注取込履歴ID
            tc200Order.setHistory_id(historyId + "");
            // 外部注文ステータス
            tc200Order.setOuter_order_status(0);
            // 配送先形態 (NTM受注 全部为法人状态)
            tc200Order.setForm(1);
            // 注文日時
            String dataSupplyDate = paramMap.get("dataSupplyDate");
            Timestamp supplyTimestamp = null;
            if (!StringTools.isNullOrEmpty(dataSupplyDate)) {
                Date supplyDate = null;
                try {
                    supplyDate = format.parse(dataSupplyDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!StringTools.isNullOrEmpty(supplyDate)) {
                    supplyTimestamp = new Timestamp(supplyDate.getTime());
                }
            }
            tc200Order.setOrder_datetime(supplyTimestamp);
            // 支払方法
            tc200Order.setPayment_method("");
            // 注文種別, 入金待ち：0, 入金済み：1
            tc200Order.setOrder_type(1);
            // 会員情報
            tc200Order.setMember_info(excelOrder.getShopName());
            // 商品税抜金額(即 商品价格)
            tc200Order.setProduct_price_excluding_tax(
                Optional.ofNullable(product)
                    .map(value -> value.getPrice())
                    .orElse(0));
            // 消費税合計
            tc200Order.setTax_total(0);
            // 代引料
            tc200Order.setCash_on_delivery_fee(0);
            // その他金額
            tc200Order.setOther_fee(0);
            // 送料合計
            tc200Order.setDelivery_total(0);
            // 合計請求金額(商品个数*商品价格)
            int finalTotalProductNum = totalProductNum;
            tc200Order.setBilling_total(
                Optional.ofNullable(product)
                    .map(value -> value.getPrice() * finalTotalProductNum)
                    .orElse(0));
            // 郵便番号
            String zip1 = "";
            String zip2 = "";
            String zip = excelOrder.getZip();
            if (!StringTools.isNullOrEmpty(zip)) {
                if (zip.indexOf("-") > 0) {
                    List<String> list = Splitter.on("-").trimResults().omitEmptyStrings().splitToList(zip);
                    zip1 = list.get(0);
                    zip2 = list.get(1);
                } else if (zip.length() == 7) {
                    zip1 = zip.substring(0, 3);
                    zip2 = zip.substring(3);
                }
            }
            // 配送先郵便番号1
            tc200Order.setReceiver_zip_code1(zip1);
            // 注文者郵便番号1
            tc200Order.setOrder_zip_code1(zip1);
            // 配送先郵便番号2
            tc200Order.setReceiver_zip_code2(zip2);
            // 注文者郵便番号2
            tc200Order.setOrder_zip_code2(zip2);
            // 配送先住所都道府県
            String prefectures = getPrefectures(excelOrder.getAddress(), todoufukenList);
            tc200Order.setReceiver_todoufuken(prefectures);
            // 注文者住所都道府県
            tc200Order.setOrder_todoufuken(prefectures);
            // 配送先住所郡市区 和 配送先詳細住所 写在一起
            String address = excelOrder.getAddress();
            String residence = "";
            if (!StringTools.isNullOrEmpty(address) && (address.length() > prefectures.length())) {
                residence =
                    address.substring(prefectures.length()) + excelOrder.getAddress1() + excelOrder.getAddress2();
            }
            tc200Order.setReceiver_address1(residence);
            // 配送先姓
            tc200Order.setReceiver_family_name(excelOrder.getName2());
            // a配送会社
            tc200Order.setDelivery_company(convertedId);
            // a配送方法指定
            tc200Order.setDelivery_method(method);
            // 配送先会社名
            tc200Order.setReceiver_company(excelOrder.getName1());

            // 配送先名
            tc200Order.setReceiver_first_name("");
            // 電話番号
            String phone = excelOrder.getPhone();
            String[] phoneArray = getPhone(phone);
            // 配送先電話番号1
            tc200Order.setReceiver_phone_number1(phoneArray[0]);
            // 配送先電話番号2
            tc200Order.setReceiver_phone_number2(phoneArray[1]);
            // 配送先電話番号3
            tc200Order.setReceiver_phone_number3(phoneArray[2]);
            // 配送先メールアドレス
            tc200Order.setReceiver_mail(excelOrder.getEmail());
            // 注文者メールアドレス
            tc200Order.setOrder_mail(excelOrder.getEmail());
            // 注文者姓
            tc200Order.setOrder_family_name(excelOrder.getShopName());
            // 配達時間帯
            List<Ms006_delivery_time> collect = deliveryTimeList.stream()
                .filter(x -> x.getDelivery_time_name().equals(excelOrder.getDeliveryTimeZone()))
                .collect(Collectors.toList());
            if (collect.size() != 0) {
                tc200Order.setDelivery_time_slot(collect.get(0).getDelivery_time_id() + "");
            } else {
                throw new LcResourceNotFoundException("配送時間帯");
            }
            // 配達指定日
            String specifiedDeliveryDate = excelOrder.getSpecifiedDeliveryDate();

            Timestamp deliveryDate = OrderServiceImpl.toTimestampWithNullValue(specifiedDeliveryDate);
            tc200Order.setDelivery_date(deliveryDate);
            // 取込日時
            Date nowDate = DateUtils.getDate();
            if (!StringTools.isNullOrEmpty(nowDate)) {
                tc200Order.setImport_datetime(new Timestamp(nowDate.getTime()));
            }
            // 最短納品日
            String shortestDeliveryDate = paramMap.get("shortestDeliveryDate");
            Date shortDeliveryDate = null;
            if (!StringTools.isNullOrEmpty(shortestDeliveryDate)) {
                try {
                    shortDeliveryDate = format.parse(shortestDeliveryDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            // 出荷希望日
            tc200Order.setShipment_wish_date(shortDeliveryDate);
            // 出庫予定日
            tc200Order.setShipment_plan_date(shortDeliveryDate);
            // 作成者
            tc200Order.setIns_usr(loginNm);
            // 作成日時
            tc200Order.setIns_date(new Timestamp(nowDate.getTime()));
            // 更新者
            tc200Order.setUpd_usr(loginNm);
            // 更新rome時
            tc200Order.setUpd_date(new Timestamp(nowDate.getTime()));
            // 削除フラグ
            tc200Order.setDel_flg(0);
            List<String> excelOrderLableNoteList =
                Lists.newArrayList(excelOrder.getLabel_note1(), excelOrder.getLabel_note2(),
                    excelOrder.getLabel_note3(), excelOrder.getLabel_note4(), excelOrder.getLabel_note5());
            String labelNote = Joiner.on("@@@").join(excelOrderLableNoteList);
            // 品名1
            tc200Order.setLabel_note(labelNote);
            // 设置依赖主：根据法人/个人状态自动选择依赖主，法人：1、個人：2
            Ms012_sponsor_master sponsor = settingDao.getSponsorByForm(client_id, 1);
            tc200Order
                .setSponsor_id((sponsor != null && null != sponsor.getSponsor_id()) ? sponsor.getSponsor_id() : null);
            tc200_orders.add(tc200Order);
            // tc201 受注明细表插入数据
            try {
                setOrderDetailData(excelOrder, productMap, paramMap, orderNo, subNo, loginNm, tc201_order_details);
            } catch (Exception e) {
                throw new LcResourceNotFoundException("製品");
            }
            subNo++;
        }
        orderDao.bulkInsertOrder(tc200_orders);
        if (tc201_order_details.size() == 0) {

            throw new LcResourceNotFoundException("製品");
        }
        orderDetailDao.bulkInsertOrderDetail(tc201_order_details);

        if ("1".equals(shipmentStatus)) {
            createShipment(orderNoList, warehouse_cd, client_id, request);
            // 同步更新默认运费
            shipmentService.syncDefaultShipmentFreight(orderNoList, warehouse_cd);
        }

        // 生成した受注履歴beanに情報を格納
        Tc202_order_history order_history = new Tc202_order_history();
        order_history.setHistory_id(historyId);
        Timestamp nowDate = new Timestamp(DateUtils.getDate().getTime());
        order_history.setImport_datetime(nowDate);
        order_history.setIns_date(nowDate);
        order_history.setClient_id(client_id);
        // 取込件数
        order_history.setTotal_cnt(excelOrderMap.size());
        // 成功件数
        order_history.setSuccess_cnt(excelOrderMap.size());
        // 失敗件数
        order_history.setFailure_cnt(0);
        order_history.setBiko01("1");
        orderHistoryDao.insertOrderHistory(order_history);

        return null;
    }

    /**
     * @param excelOrder : excel sheet2读取的数据
     * @param productMap : 商品map key：code value：商品对象
     * @param paramMap : excel sheet1读取的数据
     * @param orderNo : 受注番号
     * @param subNo : 常量 (自增)
     * @param loginNm : 用户名
     * @param tc201_order_details : 存受注明细对象的集合
     * @description: 将数据插入到受注明细表里面
     * @return: void
     * @date: 2021/4/6 11:24
     */
    public void setOrderDetailData(Nt143_excel_order excelOrder, Map<String, Mc100_product> productMap,
        HashMap<String, String> paramMap, String orderNo, int subNo,
        String loginNm, ArrayList<Tc201_order_detail> tc201_order_details) {
        Timestamp nowTime = new Timestamp(DateUtils.getDate().getTime());
        Field[] declaredFields = excelOrder.getClass().getDeclaredFields();
        int i = 0;
        for (Field field : declaredFields) {
            i++;
            field.setAccessible(true);
            String name = field.getName();
            if (StringTools.isNullOrEmpty(name) || !name.contains("productNum")) {
                continue;
            }
            String productNum = "";
            try {
                productNum = (String) field.get(excelOrder);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (StringTools.isNullOrEmpty(productNum)) {
                continue;
            }
            String index = name.substring("productNum".length());
            String key = "productCode" + index;
            int numberOfProducts = Integer.parseInt(productNum);
            String productCode = paramMap.get(key);
            Mc100_product product = productMap.get(productCode);
            if (StringTools.isNullOrEmpty(product)) {
                throw new LcResourceNotFoundException("製品");
            }
            if (!StringTools.isNullOrEmpty(product)) {
                Tc201_order_detail orderDetail = new Tc201_order_detail();
                // 受注明細番号
                orderDetail
                    .setOrder_detail_no(orderNo + "-" + String.format("%03d", subNo) + "-" + String.format("%03d", i));
                // 受注番号
                orderDetail.setPurchase_order_no(orderNo);
                // 商品ID
                orderDetail.setProduct_id(product.getProduct_id());
                // 商品コード
                orderDetail.setProduct_code(product.getCode());
                // 商品名
                orderDetail.setProduct_name(product.getName());
                // 単価
                Integer price = product.getPrice();
                orderDetail.setUnit_price(price);
                // 個数
                orderDetail.setNumber(numberOfProducts);
                // 商品計 (個数 * 単価)
                int totalPrice = 0;
                if (!StringTools.isNullOrEmpty(price)) {
                    totalPrice = numberOfProducts * price;
                }
                orderDetail.setProduct_total_price(totalPrice);
                // 軽減税率適用商品
                orderDetail.setIs_reduced_tax(product.getIs_reduced_tax());
                // 作成者
                orderDetail.setIns_usr(loginNm);
                // 作成日時
                orderDetail.setIns_date(nowTime);
                // 更新者
                orderDetail.setUpd_usr(loginNm);
                // 更新日時
                orderDetail.setUpd_date(nowTime);
                // 削除フラグ
                orderDetail.setDel_flg(0);
                tc201_order_details.add(orderDetail);
            }
        }
    }

    /**
     * @param orderNoList : 受注番号的集合
     * @param warehouse_cd : 仓库ID
     * @param client_id : 店铺ID
     * @param request : 请求
     * @description: 自动出库
     * @return: void
     * @date: 2021/4/6 17:28
     */
    public void createShipment(ArrayList<String> orderNoList, String warehouse_cd, String client_id,
        HttpServletRequest request) {
        List<Tc200_order> tc200Orders = orderDao.getOrderInfoByOrderNo(orderNoList);
        for (Tc200_order tc200Order : tc200Orders) {
            String purchaseOrderNo = tc200Order.getPurchase_order_no();
            String shipmentId = orderServiceImpl.getShipmentJson(tc200Order, purchaseOrderNo,
                client_id, warehouse_cd, request, null);
            // 更改受注状态
            orderDao.updateOuterOrderStatus(purchaseOrderNo, shipmentId);
        }
    }

    /**
     * @param num : 常量
     * @description: 计算出受注番号
     * @return: java.lang.String
     * @date: 2021/3/31 13:51
     */
    private String getNTMOrderNo(int num, String eccubeFLg) {
        String mark = Constants.ECCUBE_NTME;
        if ("2".equals(eccubeFLg)) {
            mark = Constants.ECCUBE_NTMN;
        }
        // 受注
        return mark + "-" + new SimpleDateFormat("yyyyMMddHHmmss-").format(new Date())
            + String.format("%05d", num);
    }

    /**
     * @description: 获取最大受注履历ID
     * @return: java.lang.Integer
     * @date: 2021/4/1 9:43
     */
    private Integer getMaxHistoryId() {
        String lastOrderHistoryNo = orderHistoryDao.getLastOrderHistoryNo();
        int lastHistoryId = 0;
        if (!StringTools.isNullOrEmpty(lastOrderHistoryNo)) {
            lastHistoryId = Integer.parseInt(lastOrderHistoryNo);
        }
        String historyId = String.valueOf(lastHistoryId + 1);
        return Integer.parseInt(historyId);
    }

    /**
     * @param param : 需要判断的字符串
     * @param prefecturesList : 都道府集合
     * @description: 获取正确的都道府
     * @return: java.lang.String
     * @date: 2021/4/2 14:52
     */
    private static String getPrefectures(String param, List<String> prefecturesList) {
        String prefectures = "";
        if (StringTools.isNullOrEmpty(param)) {
            return prefectures;
        }
        prefectures = param.substring(0, 3);
        if (!prefecturesList.contains(prefectures)) {
            prefectures = param.substring(0, 4);
            if (!prefecturesList.contains(prefectures)) {
                prefectures = "";
            }
        }
        return prefectures;
    }

    /**
     * @param phone : 需要分割的电话番号
     * @description: 分割电话番号
     * @return: java.lang.String[]
     * @date: 2021/4/2 15:23
     */
    private static String[] getPhone(String phone) {
        String[] phoneArray = {
            "", "", ""
        };
        if (StringTools.isNullOrEmpty(phone)) {
            return phoneArray;
        }
        if (phone.contains("-")) {
            String[] phones = phone.split("-");
            int index = 0;
            for (String param : phones) {
                phoneArray[index] = param;
                index++;
            }
        } else if (phone.length() > 10) {
            phoneArray[0] = phone.substring(0, 3);
            phoneArray[1] = phone.substring(3, 7);
            phoneArray[2] = phone.substring(7);
        }
        return phoneArray;
    }

    /**
     * @param param : 数据
     * @description: 补全参数
     * @return: java.lang.String
     * @date: 2021/4/22 16:24
     */
    public static String completionParameters(String param) {
        return StringUtils.leftPad(param, 2, "0");
    }

}
