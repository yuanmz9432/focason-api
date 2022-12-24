package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.Mc100_product;
import com.lemonico.common.bean.Ms004_delivery;
import com.lemonico.common.bean.Ms016_macro;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.MacroSettingDao;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.service.MacroSettingService;
import java.lang.reflect.Field;
import java.util.*;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class MacroSettingServiceImpl implements MacroSettingService
{
    // ログ出力
    private final static Logger logger = LoggerFactory.getLogger(MacroSettingService.class);

    @Resource
    private MacroSettingDao macrosettingDao;

    @Resource
    private DeliveryDao deliveryDao;

    @Resource
    private ProductDao productDao;

    // マクロ条件件数
    private int conditions_cnt = 18;

    /**
     * 获取マクロ数据
     *
     * @param client_id
     * @return
     */
    @Override
    public JSONObject getMacroList(String client_id) {
        List<Ms016_macro> macroList = macrosettingDao.getMacroList(client_id);
        JSONArray jsonArray = new JSONArray();
        macroList.forEach(macro -> {
            JSONObject jsonObject = new JSONObject();
            // 管理ID
            jsonObject.put("id", macro.getId());
            // マクロ名
            jsonObject.put("macro_name", macro.getMacro_name());
            // イベント名
            jsonObject.put("event_name", macro.getEvent_name());
            // 优先级别
            jsonObject.put("priority", macro.getPriority());
            // マクロステータス
            jsonObject.put("macro_status", macro.getMacro_status());
            // 前端控制 是否更改flg
            jsonObject.put("inputFlg", false);
            jsonArray.add(jsonObject);
        });
        return CommonUtils.success(jsonArray);
    }

    /**
     * 获取单条マクロ数据
     * 
     * @param id
     * @return
     */
    @Override
    public JSONObject getMacroInfoById(String id) {
        List<Ms016_macro> MacroInfo = macrosettingDao.getMacroInfoById(id);
        return CommonUtils.success(MacroInfo);
    }

    @Override
    public Integer saveMacroInfo(JSONObject jsonObject) {
        Ms016_macro macro = new Ms016_macro();
        Integer id = jsonObject.getInteger("id");
        String client_id = jsonObject.getString("client_id");
        macro.setClient_id(client_id);
        macro.setMacro_name(jsonObject.getString("macro_name"));
        macro.setEvent_name(jsonObject.getString("event_name"));
        macro.setConditions_kube(jsonObject.getInteger("conditions_kube"));
        // 有效状态
        macro.setMacro_status(jsonObject.getInteger("macro_status"));
        macro.setAction_code(jsonObject.getInteger("action_code"));
        macro.setAction_content(jsonObject.getString("action_content"));
        macro.setStart_time(jsonObject.getDate("start_time"));
        macro.setEnd_time(jsonObject.getDate("end_time"));
        for (int i = 1; i <= conditions_cnt; i++) {
            // 存值标识
            String str = String.format("%02d", i);
            String item = "conditions_item" + str;
            String condition = "conditions" + str;
            String flg = "conditions_flg" + str;
            // 反射存值
            try {
                Field field1 = macro.getClass().getDeclaredField(item);
                Field field2 = macro.getClass().getDeclaredField(condition);
                Field field3 = macro.getClass().getDeclaredField(flg);
                field1.setAccessible(true);
                field2.setAccessible(true);
                field3.setAccessible(true);
                field1.set(macro, jsonObject.getInteger(item));
                field2.set(macro, jsonObject.getString(condition));
                field3.set(macro, jsonObject.getInteger(flg));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }

        // 如果有id 则为修改
        if (!StringTools.isNullOrEmpty(id)) {
            macro.setId(id);
            return macrosettingDao.updateMacroById(macro);
        }

        // 新规macro时默认给最大优先级
        Integer maxPriority = macrosettingDao.getMaxPriority(client_id);
        if (maxPriority == null) {
            macro.setPriority(1);
        } else {
            macro.setPriority(maxPriority + 1);
        }
        return macrosettingDao.insertMacroData(macro);
    }

    /**
     * @Description: 循环取出对应判断条件的比较方式
     *               @Date： 2021/3/25
     *               @Param：
     *               @return： conditionsFlg
     */
    public static HashMap<Integer, HashMap<String, Integer>> getConditionsFlg(Ms016_macro macro, int i) {
        HashMap<Integer, HashMap<String, Integer>> map = new HashMap<>();
        // 反射获取ms016对象的column数组
        Field[] declaredFields = macro.getClass().getDeclaredFields();
        String str = String.format("%02d", i);
        String conditions_item = "conditions_item" + str;
        String conditions_flg = "conditions_flg" + str;
        String conditions = "conditions" + str;
        // 初始化要接收值
        // 比较条件
        Integer item = 0;
        // 比较方式
        Integer flg = 0;
        // 标准值
        String condition = "";
        // 循环终止flg
        boolean loopFlg = false;
        for (int j = 0; j < declaredFields.length; j++) {
            declaredFields[j].setAccessible(true);
            String name = declaredFields[j].getName();
            Field field = declaredFields[j];
            // 比较column 与 传入的conditions_item 字段进行比教
            if (conditions_item.equals(name)) {
                try {
                    // get 获取 Macro对象中对应的值
                    item = (Integer) field.get(macro);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    item = 0;
                }
                continue;
            }

            // 購入者と配送先の名前と住所が異なる場合
            if (!StringTools.isNullOrEmpty(item) && item == 13) {
                break;
            }

            // 比较colunm 与 conditions 字段进行比教
            if (conditions.equals(name)) {
                try {
                    condition = (String) field.get(macro);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                continue;
            }
            // 比较colunm 与 conditions_flg 字段进行比教
            if (conditions_flg.equals(name)) {
                try {
                    flg = (Integer) field.get(macro);
                    loopFlg = true;
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                continue;
            }
            // 需要的字段找完后跳出
            if (loopFlg) {
                break;
            }
        }
        // 若三要素缺少任何一个 return
        if ((!StringTools.isNullOrEmpty(item) || !StringTools.isNullOrEmpty(condition)
            || !StringTools.isNullOrEmpty(flg)) || (!StringTools.isNullOrEmpty(item) && item == 13)) {
            HashMap<String, Integer> calculation = new HashMap<>();
            calculation.put(condition, flg);
            map.put(item, calculation);
        }
        return map;
    }

    /**
     * @Description: 根据店铺设定条件 判断出库订单是否满足
     *               @Date： 2021/3/25
     *               @Param： JSONObject tw200
     *               @Param： List<Ms016> Macro list
     *               @return： JSONObject
     */
    public JSONObject setValueByCondition(JSONObject tw200, String client_id) {
        // 获取店铺设定
        List<Ms016_macro> macros = macrosettingDao.getMacroList(client_id);
        // 如果店铺未设定macro功能 直接return
        if (macros.size() == 0) {
            return tw200;
        }
        // 一个店铺有多个设定
        for (Ms016_macro ms016 : macros) {
            // 判断是否生效
            if (ms016.getMacro_status() != 0) {
                // 无效
                logger.info("【マクロ】無効");
                continue;
            }
            // 开始时间
            Date startTime = ms016.getStart_time();
            // 结束时间
            Date endTime = ms016.getEnd_time();
            if (!StringTools.isNullOrEmpty(startTime) && !StringTools.isNullOrEmpty(endTime)) {
                // 若设置了生效时间 需要判断当前时间是否在区间内
                Date nowTime = new Date();
                if (nowTime.before(startTime) || nowTime.after(endTime)) {
                    // 开始时间之前或者结束时间之后 跳出本次循环
                    logger.error("当前时间={} 生效区间={} ----- {} 目前无效", nowTime, startTime, endTime);
                    continue;
                }
            }
            // 单个 or 多个
            Integer conditions_kube = ms016.getConditions_kube();
            // 执行比较方法 返回结果集
            List<Boolean> booleans = comparatorTo(tw200, ms016);
            if (booleans.size() == 0) {
                continue;
            }

            // 要执行的自动方式
            Integer action_code = ms016.getAction_code();
            // 自动执行的内容
            String action_content = ms016.getAction_content();
            // 返回的判断结果集
            if (booleans.size() == 0) {
                // 没有产生结果集 匹配失败
                logger.info("【マクロ】マッピング失敗");
                continue;
            }
            // 全部满足/单个满足 执行action
            if ((conditions_kube == 1 && !booleans.contains(false))
                || (conditions_kube == 2 && booleans.contains(true))) {
                logger.info("【マクロ】条件満足のため、自動処理起動");
                executeAction(tw200, action_code, action_content, ms016.getClient_id());
                // 其他 （全部不满足）
            }
            // 多个action 根据有限级顺序执行
        }
        return tw200;
    }

    /**
     * @Description: 选择比较方式 文本/数值
     *               @Date： 2021/3/25
     * @Param：
     * @return：
     */
    public List<Boolean> comparatorTo(JSONObject tw200, Ms016_macro macro) {
        // 比较结果集
        ArrayList<Boolean> result = new ArrayList<>();
        // boolean 比较结果
        boolean res;
        for (int i = 1; i <= conditions_cnt; i++) {
            // 获取比较前三要素
            HashMap<Integer, HashMap<String, Integer>> map = getConditionsFlg(macro, i);
            if (map.size() == 1) {
                // 取得 比较条件 key
                Integer item = map.keySet().iterator().next();
                // 比较类型 和 比较方式map
                HashMap<String, Integer> calculation = map.get(item);
                // 比较标准值
                String standard = calculation.keySet().iterator().next();
                // 比较方式 :< /> /= /!= /<= />=/ 包含 / 不包含 /相同 /不同
                Integer method = calculation.get(standard);
                // 判断是否符合标准值设定
                res = switchBySubject(tw200, item, standard, method);
                // 添加到结果集中
                result.add(res);
            } else {
                // 如果没有继续找到要匹配到比较条件 直接跳出
                break;
            }
        }
        return result;
    }

    /**
     * @Description: //TODO 根据科目选择 取出需要对应值
     *               @Date： 2021/3/26
     * @Param：condition (每一次需要比较的科目)再根据设定的问题传入不同的比较标准值
     *                  @Param： tw200元数据
     * @return：
     */
    public boolean switchBySubject(JSONObject tw200, Integer item, String standard, Integer method) {
        // 店铺设定比较科目 或比较条件 为空的话 无比较意义
        if (StringTools.isNullOrEmpty(item)) {
            // 无比较科目
            return false;
        }
        // 商品信息
        JSONArray items = tw200.getJSONArray("items");
        JSONObject sender = tw200.getJSONObject("sender");
        switch (item) {
            // お届け先郵便番号
            case Constants.ITEM_1:
                // 标准值和 需要比较的值都需要替换掉"-"
                String zipCode = sender.getString("postcode").replace("-", "");
                return comparator(zipCode, standard.replace("-", ""), method);
            // お届け先住所
            case Constants.ITEM_2:
                String address = sender.getString("prefecture") + sender.getString("address1");
                if (!StringTools.isNullOrEmpty(sender.getString("address2"))) {
                    address += sender.getString("address2");
                }
                address = address.replaceAll(" ", "");

                if (method == 21) {
                    // 需要判断配送者地址是否包含数字
                    List<String> list = Arrays.asList("０", "１", "２", "３", "４", "５", "６", "７", "８", "９", "0", "1", "2",
                        "3", "4", "5", "6", "7", "8", "9");
                    int count = (int) list.stream().filter(address::contains).count();
                    boolean resultFlg = false;
                    // 不包含数字 需要改为确认等待
                    if (count == 0) {
                        resultFlg = true;
                        Integer shipment_status = tw200.getInteger("shipment_status");
                        if (StringTools.isNullOrEmpty(shipment_status) || shipment_status != 9) {
                            tw200.put("shipment_status", 1);
                            tw200.put("status_message", Constants.DOES_NOT_INCLUDE_HOUSE_NUMBER);
                        }
                    }
                    return resultFlg;
                } else {
                    return comparator(address, standard, method);
                }
                // お届け先名前
            case Constants.ITEM_3:
                String name = sender.getString("surname").replaceAll(" ", "");
                return comparator(name, standard, method);
            // 商品名
            case Constants.ITEM_4:
                // 多个商品依次比较 只要其中一种商品匹配就是true
                // 否定条件设定2次时，全部满足则返回
                if (method == Constants.METHOD_2 || method == Constants.METHOD_4 || method == Constants.METHOD_18) {
                    return specialComparison(items, method, standard, Constants.ITEM_4);
                }
                for (int i = 0; i < items.size(); i++) {
                    String productName = items.getJSONObject(i).getString("name");
                    boolean comparator = comparator(productName, standard, method);
                    if (comparator) {
                        return true;
                    }
                }
                // 都没匹配到 false
                return false;
            // 商品コード
            case Constants.ITEM_5:
                // 否定条件设定2次时，全部满足则返回
                if (method == Constants.METHOD_2 || method == Constants.METHOD_4 || method == Constants.METHOD_18) {
                    return specialComparison(items, method, standard, Constants.ITEM_5);
                }
                for (int i = 0; i < items.size(); i++) {
                    String productName = items.getJSONObject(i).getString("code");
                    boolean comparator = comparator(productName, standard, method);
                    if (comparator) {
                        return true;
                    }
                }
                // 都没匹配到 false
                return false;
            // 配送方法
            case Constants.ITEM_6:
                String delivery_method = tw200.getString("delivery_carrier");
                Ms004_delivery delivery = deliveryDao.getDeliveryById(delivery_method);
                String param = delivery.getDelivery_nm() + " " + delivery.getDelivery_method_name();
                return comparator(param.replaceAll(" ", ""), standard.replaceAll(" ", ""), method);
            // 支払方法
            case Constants.ITEM_7:
                String payment_id = tw200.getString("payment_method");
                String payment_method = "";
                if (!StringTools.isNullOrEmpty(payment_id)) {
                    payment_method = deliveryDao.getPayById(Integer.valueOf(payment_id));
                }
                return comparator(payment_method, standard, method);
            // 備考欄
            case Constants.ITEM_8:
                String memo = tw200.getString("memo");
                return comparator(memo, standard, method);
            // 合計金額
            case Constants.ITEM_9:
                Integer price = tw200.getInteger("total_amount");
                return comparator(price, standard, method);
            // 合計購入数
            case Constants.ITEM_10:
                int total = 0;
                // 计算出库依赖的商品总数
                for (int i = 0; i < items.size(); i++) {
                    String product_plan_cnt = items.getJSONObject(i).getString("product_plan_cnt");
                    total += Integer.parseInt(product_plan_cnt);
                }
                return comparator(total, standard, method);
            // 定期購入回数
            case Constants.ITEM_11:
                Integer buy_cnt = tw200.getInteger("buy_cnt");
                return comparator(buy_cnt, standard, method);
            // 商品金額（合計）
            case Constants.ITEM_12:
                Integer product_price = tw200.getInteger("subtotal_amount");
                return comparator(product_price, standard, method);
            // 購入者と配送先の名前と住所が異なる場合
            case Constants.ITEM_13:
                // 購入者(注文者) 名前と住所
                String buy_cust = "";
                // 名前
                if (!StringTools.isNullOrEmpty(sender.getString("order_family_name"))) {
                    buy_cust += sender.getString("order_family_name");
                }
                if (!StringTools.isNullOrEmpty(sender.getString("order_first_name"))) {
                    buy_cust += sender.getString("order_first_name");
                }
                // 住所
                if (!StringTools.isNullOrEmpty(sender.getString("order_todoufuken"))) {
                    buy_cust += sender.getString("order_todoufuken");
                }
                if (!StringTools.isNullOrEmpty(sender.getString("order_address1"))) {
                    buy_cust += sender.getString("order_address1");
                }
                if (!StringTools.isNullOrEmpty(sender.getString("order_address2"))) {
                    buy_cust += sender.getString("order_address2");
                }
                buy_cust = buy_cust.replaceAll(" |　", "");

                // 配送先 名前と住所
                String delivery_cust = "";
                // 名前
                if (!StringTools.isNullOrEmpty(sender.getString("surname"))) {
                    delivery_cust += sender.getString("surname");
                }
                // 住所
                if (!StringTools.isNullOrEmpty(sender.getString("prefecture"))) {
                    delivery_cust += sender.getString("prefecture");
                }
                if (!StringTools.isNullOrEmpty(sender.getString("address1"))) {
                    delivery_cust += sender.getString("address1");
                }
                if (!StringTools.isNullOrEmpty(sender.getString("address2"))) {
                    delivery_cust += sender.getString("address2");
                }
                delivery_cust = delivery_cust.replaceAll(" |　", "");

                if (delivery_cust.equals(buy_cust)) {
                    return false;
                } else {
                    return true;
                }
                // 依赖主
            case Constants.ITEM_14:
                String sponsor_id = tw200.getString("sponsor_id");
                return comparator(sponsor_id, standard, method);
            // 手数料
            case Constants.ITEM_15:
                Integer handling_charge = tw200.getInteger("handling_charge");
                return comparator(handling_charge, standard, method);
            // 送料
            case Constants.ITEM_16:
                Integer delivery_charge = tw200.getInteger("delivery_charge");
                return comparator(delivery_charge, standard, method);
            // 割引金額
            case Constants.ITEM_17:
                Integer discount_amount = tw200.getInteger("discount_amount");
                return comparator(discount_amount, standard, method);
            // 商品オプション
            case Constants.ITEM_18:
                // 多个商品依次比较 只要其中一种商品匹配就是true
                // 否定条件设定2次时，全部满足则返回
                if (method == Constants.METHOD_2 || method == Constants.METHOD_4 || method == Constants.METHOD_18) {
                    return specialComparison(items, method, standard, Constants.ITEM_18);
                }
                for (int i = 0; i < items.size(); i++) {
                    String options = items.getJSONObject(i).getString("options");
                    boolean comparator = comparator(options, standard, method);
                    if (comparator) {
                        return true;
                    }
                }
                // 都没匹配到 false
                return false;
            default:
                // 默认操作
                break;
        }
        return false;
    }

    /**
     * @Description: //TODO 判断是那种比较类型 实际比较方法
     *               @Date： 2021/3/25
     * @Param：obj 要比较的值
     * @Param：standard 比较标准值
     * @Param：type 比较方式
     * @Param：obj 要比较的值
     *            @return： boolean
     */
    public boolean comparator(Object obj, String standard, Integer method) {
        // 为空和不为空判断 19:空欄 20:空欄できない
        if (method == Constants.METHOD_19) {
            return StringTools.isNullOrEmpty(obj);
        } else if (method == Constants.METHOD_20) {
            return !StringTools.isNullOrEmpty(obj);
        }
        if (StringTools.isNullOrEmpty(obj)) {
            return false;
        }
        // 确定比较值类型 文本
        if (obj instanceof String) {
            switch (method) {
                // 含む
                case Constants.METHOD_1:
                    return ((String) obj).contains(standard);
                // 含まない
                case Constants.METHOD_2:
                    return !((String) obj).contains(standard);
                // 同じ
                case Constants.METHOD_3:
                case Constants.METHOD_17:
                    return obj.equals(standard);
                // と同じではない
                case Constants.METHOD_4:
                case Constants.METHOD_18:
                    return !obj.equals(standard);
                // から始まる
                case Constants.METHOD_5:
                    return ((String) obj).startsWith(standard);
                // から始まらない
                case Constants.METHOD_6:
                    return ((String) obj).endsWith(standard);
            }
        } else {
            // 数值类型
            switch (method) {
                // < より小さい
                case Constants.METHOD_11:
                    return (Integer) obj < Integer.parseInt(standard);
                // > より大きい
                case Constants.METHOD_12:
                    return (Integer) obj > Integer.parseInt(standard);
                // = 等しい
                case Constants.METHOD_13:
                    return (Integer) obj == Integer.parseInt(standard);
                // >= 以上
                case Constants.METHOD_14:
                    return (Integer) obj >= Integer.parseInt(standard);
                // <= 以下
                case Constants.METHOD_15:
                    return (Integer) obj <= Integer.parseInt(standard);
                // != 等しくない
                case Constants.METHOD_16:
                    return (Integer) obj != Integer.parseInt(standard);
            }
        }
        return false;
    }

    /**
     * @Description: 根据action_code 执行修改元数据中的相应数据
     *               @Date： 2021/3/29
     *               @Param：
     *               @return： JSONObject
     */
    public JSONObject executeAction(JSONObject tw200, Integer action_Code, String action_content, String client_id) {
        // 商品信息
        JSONArray items = tw200.getJSONArray("items");
        if (StringTools.isNullOrEmpty(action_Code)) {
            logger.warn("action自动执行失败：没有acton_ode");
            return tw200;
        }
        switch (action_Code) {
            // 配送方法を変更する 直接修改tw200数据中的配送方法对应的 delivery_cd
            case Constants.ACTION_1:
                tw200.put("delivery_carrier", action_content);
                break;
            // 支払方法を変更する
            case Constants.ACTION_2:
                tw200.put("payment_method", action_content);
                // 如果是代金引换，【支払総計 (税込)】 = 【合計 (税込)】
                if ("2".equals(action_content)) {
                    tw200.put("total_for_cash_on_delivery", tw200.getInteger("total_amount"));
                }
                break;
            // 明細書を追加する
            case Constants.ACTION_3:
                // 分隔明细书追加的商品id，价格，数量
                List<String> products = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(action_content);
                // 是否计算总价
                String isCalculateTotal = products.get(products.size() - 1);
                Integer macro_total = 0;
                for (int i = 0; i < products.size() - 1; i++) {
                    String[] str = products.get(i).split("-");
                    // 创建要放入items列表中的数据
                    // 检索到了要自动追加的商品
                    Mc100_product mc100 = addItem(str[0], client_id);
                    if (mc100 == null) {
                        continue;
                    }

                    // 排除依赖和自动处理重复商品
                    int product_cnt = 0;
                    boolean productFlg = false;
                    for (int j = 0; j < items.size(); j++) {
                        if (mc100.getProduct_id().equals(items.getJSONObject(j).getString("product_id"))) {
                            product_cnt = items.getJSONObject(j).getInteger("product_plan_cnt");
                            items.getJSONObject(j).put("product_plan_cnt", product_cnt + Integer.parseInt(str[2]));
                            productFlg = true;
                            break;
                        }
                    }

                    // 要追加的商品
                    JSONObject add = new JSONObject();
                    Integer bundled_flg = mc100.getBundled_flg();
                    // set子商品信息
                    add.put("warehouse_cd", mc100.getWarehouse_cd());
                    add.put("client_id", client_id);
                    add.put("code", mc100.getCode());
                    add.put("name", mc100.getName());
                    add.put("product_id", mc100.getProduct_id());
                    add.put("is_reduced_tax", mc100.getIs_reduced_tax());
                    add.put("product_plan_cnt", str[2]);
                    add.put("bundled_flg", bundled_flg);
                    String unit_price = bundled_flg == 0 && "1".equals(isCalculateTotal) ? str[1] : "0";
                    add.put("unit_price", unit_price);
                    add.put("set_flg", mc100.getSet_flg());
                    add.put("set_sub_id", mc100.getSet_sub_id());
                    // 如果满足macro设定，product_sub_id为明细书中追加的商品标识，为1，否则为0，体现在TW201表
                    add.put("product_sub_id", 1);
                    add.put("tax_flag", "税込");
                    // macro 商品明细追加商品区分获取
                    Integer kubun = 0;
                    if (!StringTools.isNullOrEmpty(mc100.getKubun())) {
                        kubun = mc100.getKubun();
                    }
                    add.put("kubun", kubun);
                    // 如果是set商品 需要追加子商品array
                    JSONArray array;
                    if (mc100.getSet_flg() == 1) {
                        // 根据set_sub_id 获取set子商品信息
                        List<Mc100_product> productSetList =
                            productDao.getProductSetList(mc100.getSet_sub_id(), client_id);
                        array = JSONArray.parseArray(JSON.toJSONString(productSetList));
                        add.put("mc103_product_sets", array);
                    }
                    Integer total = Integer.parseInt(str[2]) * Integer.parseInt(unit_price);
                    add.put("price", String.valueOf(total));
                    macro_total += total;
                    // set
                    // 将追加的商品信息放到items中
                    if (productFlg) {
                        continue;
                    }
                    items.add(add);
                }
                // 计算总价
                if ("1".equals(isCalculateTotal)) {
                    tw200.put("subtotal_amount", tw200.getInteger("subtotal_amount") + macro_total);
                    tw200.put("total_amount", tw200.getInteger("total_amount") + macro_total);
                    tw200.put("total_with_normal_tax", tw200.getInteger("total_with_normal_tax") + macro_total);
                }
                return tw200;
            // 同梱物お指定する
            case Constants.ACTION_4:
                // 分隔明细书追加的商品id，数量
                List<String> bundleds = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(action_content);
                // 多个同捆物追加
                for (String bundled : bundleds) {
                    String[] str = bundled.split("-");
                    // 创建要放入items列表中的数据
                    // 检索到了要自动追加的商品
                    Mc100_product product = addItem(str[0], client_id);
                    if (StringTools.isNullOrEmpty(product) || product.getBundled_flg() != 1) {
                        continue;
                    }

                    // 排除依赖和自动处理重复商品
                    int product_cnt = 0;
                    boolean productFlg = false;
                    for (int j = 0; j < items.size(); j++) {
                        if (items.getJSONObject(j).getInteger("bundled_flg") != 1) {
                            continue;
                        }
                        if (product.getProduct_id().equals(items.getJSONObject(j).getString("product_id"))) {
                            product_cnt = items.getJSONObject(j).getInteger("product_plan_cnt");
                            items.getJSONObject(j).put("product_plan_cnt", product_cnt + Integer.parseInt(str[1]));
                            productFlg = true;
                            break;
                        }
                    }

                    if (productFlg) {
                        continue;
                    }

                    // 要追加的商品
                    JSONObject addBundled = new JSONObject();
                    addBundled.put("client_id", client_id);
                    addBundled.put("code", product.getCode());
                    addBundled.put("name", product.getName());
                    addBundled.put("product_id", product.getProduct_id());
                    addBundled.put("is_reduced_tax", product.getIs_reduced_tax());
                    addBundled.put("bundled_flg", product.getBundled_flg());
                    addBundled.put("set_flg", product.getSet_flg());
                    // 引当数
                    addBundled.put("product_plan_cnt", str[1]);
                    // 商品区分
                    addBundled.put("kubun", product.getKubun());
                    items.add(addBundled);
                }
                return tw200;
            // 作業指示書 特記事項に追記する todo 如果出库时已经指定满 追加内容写到bikou8
            case Constants.ACTION_5:
                String instructionsList = tw200.getString("delivery_instructions");
                if (!StringTools.isNullOrEmpty(instructionsList)) {
                    List<String> splitToList =
                        Splitter.on(",").omitEmptyStrings().trimResults().splitToList(instructionsList);
                    // 要追加的出荷特记事项 action_content;
                    if (splitToList.size() <= 8) {
                        tw200.put("delivery_instructions", instructionsList + "," + action_content);
                    } else {
                        logger.warn("出荷特技事项已满，无法继续追加！！！！");
                    }
                } else {
                    // 未指定
                    tw200.put("delivery_instructions", action_content);
                }
                break;
            // 納品書 明細メッセージに追記する （覆盖）
            case Constants.ACTION_6:
                tw200.put("message", action_content);
                break;
            // 配送希望：割れ物注意に追加する
            case Constants.ACTION_7:
                tw200.getJSONObject("delivery_options").put("fragile_item", action_content);
                break;
            // 配送希望：不在時宅配ボックスに追加する
            case Constants.ACTION_8:
                tw200.getJSONObject("delivery_options").put("box_delivery", action_content);
                break;
            // 緩衝材を指定する todo 缓冲材和包装设定生效规则：如果出库时指定了明细单位，不改变设定；如果没有指定或是注文单位，设定生效
            case Constants.ACTION_9:
                String cushioning_unit = tw200.getString("cushioning_unit");
                if (!"2".equals(cushioning_unit)) {
                    if (!StringTools.isNullOrEmpty(action_content)) {
                        tw200.put("cushioning_unit", "1");
                        tw200.put("cushioning_type", action_content);
                    } else {
                        tw200.put("cushioning_unit", "0");
                    }
                }
                break;
            // ギフトラッピングを指定する
            case Constants.ACTION_10:
                String gift_wrapping_unit = tw200.getString("gift_wrapping_unit");
                if (!"2".equals(gift_wrapping_unit)) {
                    if (!StringTools.isNullOrEmpty(action_content)) {
                        tw200.put("gift_wrapping_unit", "1");
                        tw200.put("gift_wrapping_type", action_content);
                    } else {
                        tw200.put("gift_wrapping_unit", "0");
                    }
                }
                break;
            // 確認待ちに変更する, 変更理由
            case Constants.ACTION_11:
                Integer shipment_status = tw200.getInteger("shipment_status");
                if (!StringTools.isNullOrEmpty(shipment_status) && shipment_status == 9) {
                    break;
                }
                tw200.put("shipment_status", 1);
                tw200.put("status_message", action_content);
                break;
            // 送り状特記事項にする, 追加内容
            case Constants.ACTION_12:
                String order_name = "";
                JSONObject sender = tw200.getJSONObject("sender");
                if (!StringTools.isNullOrEmpty(sender.getString("order_family_name"))) {
                    order_name += sender.getString("order_family_name");
                }
                if (!StringTools.isNullOrEmpty(sender.getString("order_first_name"))) {
                    order_name += sender.getString("order_first_name");
                }

                if (action_content.contains("#{order_name}")) {
                    action_content = action_content.replace("#{order_name}", order_name);
                }
                tw200.put("invoice_special_notes", action_content);
                break;
        }
        return tw200;
    }

    /**
     * @Description: //TODO 批量删除macro设定
     *               @Date： 2021/3/26
     * @Param：ids , client_id
     * @return：
     */
    @Override
    public Integer delMacro(String client_id, String ids) {
        String[] split = ids.split(",");
        List<String> idList = Arrays.asList(split);
        return macrosettingDao.delMacro(idList, client_id);
    }

    /**
     * @Description: //TODO 根据设定的明细书 ，同捆物追加内容 获取商品信息 拼装item
     *               @Date： 2021/4/1
     * @Param：
     * @return：
     */
    public Mc100_product addItem(String product_id, String client_id) {
        Mc100_product product = productDao.getProductById(product_id, client_id);
        if (!StringTools.isNullOrEmpty(product)) {
            return product;
        }
        return null;
    }

    /**
     * @Param:
     * @description: 检验macro优先顺序是否重复
     * @return: Integer
     * @date: 2021/04/06
     */
    @Override
    public boolean checkMacroPriorityExists(Integer priority, String client_id) {
        Integer i = macrosettingDao.checkMacroPriorityExists(priority, client_id);
        if (i != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Description: //TODO macro信息修改
     *               @Date： 2021/4/6
     *               @Param：
     *               @return： Integer
     */
    @Override
    public JSONObject updateMacroInfo(String client_id, Integer priority, Integer macro_status, String id,
        Integer old_priority, String repeat_id) {
        try {
            // 如果顺位重复，则将重复顺位修改为修改的原来的顺位
            if (!StringTools.isNullOrEmpty(repeat_id)) {
                macrosettingDao.updateRepeatMacroInfo(client_id, repeat_id, old_priority);
            }
            macrosettingDao.updateMacroInfo(client_id, priority, macro_status, id);
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error("macro信息改修失败, 店铺Id={} macroId={}", client_id, id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: //检验macro名称是否重复
     *               @Date： 2021/4/6
     *               @Param：
     *               @return： boolean
     */
    @Override
    public boolean nameCheck(String macro_name, String client_id) {
        Integer i = macrosettingDao.nameCheck(macro_name, client_id);
        if (i != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * @Description: //macro条件选择两次时否定形式需要全部满足时返回true
     *               @Date： 2021/9/27
     *               @Param：
     *               @return： boolean
     */
    public boolean specialComparison(JSONArray items, Integer method, String standard, Integer type) {
        int contain_flg = 0;
        int same_flg = 0;
        String productInfo = "";
        for (int i = 0; i < items.size(); i++) {
            if (type == Constants.ITEM_4) {
                // 商品名
                productInfo = items.getJSONObject(i).getString("name");
            } else if (type == Constants.ITEM_18) {
                // 商品オプション
                productInfo = items.getJSONObject(i).getString("options");
            } else {
                // 商品コード
                productInfo = items.getJSONObject(i).getString("code");
            }
            // 含まない 和 と同じではない 时，只有全部不满足的情况下才满足条件
            // 含まない
            if (method == Constants.METHOD_2) {
                boolean contain = comparator(productInfo, standard, Constants.METHOD_1);
                if (contain) {
                    contain_flg = 1;
                }
                // と同じではない
            } else if (method == Constants.METHOD_4 || method == Constants.METHOD_18) {
                boolean same = comparator(productInfo, standard, Constants.METHOD_17);
                if (same) {
                    same_flg = 1;
                }
            }

        }
        // 含まない 只有全部不满足时才返回TRUE
        if (method == Constants.METHOD_2 && contain_flg == 0) {
            return true;
        }
        // と同じではない
        if ((method == Constants.METHOD_4 || method == Constants.METHOD_18) && same_flg == 0) {
            return true;
        }
        // 没有匹配则返回false
        return false;
    }
}
