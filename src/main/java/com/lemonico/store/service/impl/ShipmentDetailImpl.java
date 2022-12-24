package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Mc103_product_set;
import com.lemonico.common.bean.Mc105_product_setting;
import com.lemonico.common.bean.Tw201_shipment_detail;
import com.lemonico.common.bean.Tw300_stock;
import com.lemonico.common.dao.ProductSettingDao;
import com.lemonico.common.service.CustomerHistoryService;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.ProductDao;
import com.lemonico.store.dao.ShipmentDetailDao;
import com.lemonico.store.dao.ShipmentsDao;
import com.lemonico.store.dao.StockDao;
import com.lemonico.store.service.ShipmentDetailService;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: sunlogic
 * @description: 出庫明細
 * @create: 2020-05-13 10:34
 **/
@Service
public class ShipmentDetailImpl implements ShipmentDetailService
{

    @Resource
    private ShipmentDetailDao shipmentDetailDao;
    @Resource
    private ShipmentsDao shipmentsDao;
    @Resource
    private StockDao stockDao;
    @Resource
    private ProductDao productDao;
    @Resource
    private ProductSettingDao productSettingDao;
    @Resource
    private CustomerHistoryService customerHistoryService;

    /**
     * @Description: 出庫明細LIST
     * @Param:
     * @return:
     * @Date: 2020/8/3
     */
    @Override
    public List<Tw201_shipment_detail> getShipmentDetailList(String client_id, String shipment_plan_id) {
        return shipmentDetailDao.getShipmentDetailList(client_id, shipment_plan_id, false);
    }

    /**
     * @Description: 出庫明細新规
     * @Param: Tw201_shipment_detail
     * @return: Integer
     * @Date: 2020/5/13
     */
    @Override
    @Transactional
    public Integer setShipmentDetail(JSONObject jsonParam, boolean insertFlg, HttpServletRequest servletRequest) {
        Date nowTime = DateUtils.getDate();
        String login_nm = null;
        if (!StringTools.isNullOrEmpty(servletRequest)) {
            login_nm = CommonUtils.getToken("login_nm", servletRequest);
        }
        Integer result = 0;
        int shipments_status = 3; // 出荷待ち
        String cushioningType = jsonParam.getString("cushioning_type");
        String giftWrappingType = jsonParam.getString("gift_wrapping_type");
        JSONArray items = jsonParam.getJSONArray("items");
        JSONObject detailJson = jsonParam;
        detailJson.remove(detailJson.getJSONArray("items"));

        // 获取店铺设定的税込・税抜信息
        Mc105_product_setting productSetting =
            productSettingDao.getProductSetting(jsonParam.getString("client_id"), null);

        String warehouse_cd = jsonParam.getString("warehouse_cd");
        String client_id = jsonParam.getString("client_id");
        String shipment_plan_id = jsonParam.getString("shipment_plan_id");
        int product_plan_total = 0;
        int total_price = 0;

        // set子商品拆分
        items = this.setProductSplice(items);

        String tmp_set_id = "";
        // 商品区分 flg 如果有商品为 之前未登录 需要变为true
        boolean kubunFlg = false;
        for (int i = 0; i < items.size(); i++) {
            JSONObject itemJson = items.getJSONObject(i);
            Integer product_plan_cnt = CommonUtils.toInteger(itemJson.getString("product_plan_cnt"));
            if (StringTools.isNullOrEmpty(itemJson.get("bundled_flg")) || itemJson.getInteger("bundled_flg") != 1) {
                product_plan_total += product_plan_cnt;
            }

            String tmp_sub_id = itemJson.getString("set_sub_id");
            String sub_id =
                (!StringTools.isNullOrEmpty(tmp_sub_id) && Integer.parseInt(tmp_sub_id) > 0) ? tmp_sub_id : "";
            if (!StringTools.isNullOrEmpty(sub_id) && tmp_set_id.indexOf(sub_id) == -1) {
                tmp_set_id += sub_id + ",";
                total_price += CommonUtils.toInteger(itemJson.getString("price"));
            } else if (StringTools.isNullOrEmpty(sub_id)) {
                total_price += CommonUtils.toInteger(itemJson.getString("price"));
            }

            String product_id = itemJson.getString("product_id");
            JSONObject stockJson = new JSONObject();
            stockJson.put("warehouse_cd", warehouse_cd);
            stockJson.put("client_id", client_id);
            stockJson.put("product_id", product_id);
            Tw300_stock stock = stockDao.getStockInfoById(stockJson);
            int requesting_cnt = 0, available_cnt = 0;
            if (StringTools.isNullOrEmpty(stock)) {
                // 如果该商品没有在库，则设定出库状态为：引当待ち
                shipments_status = 2;
                itemJson.put("reserve_status", 0);
                itemJson.put("reserve_cnt", 0);
            } else {
                // 在库表理论在库数小于本次依赖数的情况，该商品和该订单均为引当等待
                int reserveCnt = 0;
                // 编辑时查看该商品的引当数
                if (!insertFlg) {
                    // 引当数，此时均为普通商品
                    List<Tw201_shipment_detail> shipmentDetailById =
                        shipmentDetailDao.getShipmentDetailById(warehouse_cd, shipment_plan_id, product_id, false);
                    if (!StringTools.isNullOrEmpty(shipmentDetailById) && shipmentDetailById.size() > 0) {
                        for (Tw201_shipment_detail detail : shipmentDetailById) {
                            Integer setSubId = detail.getSet_sub_id();
                            String set_sub_id =
                                (!StringTools.isNullOrEmpty(setSubId) && setSubId > 0) ? String.valueOf(setSubId) : "";
                            if (!set_sub_id.equals(sub_id)) {
                                continue;
                            }
                            reserveCnt += detail.getReserve_cnt();
                        }
                    }
                }

                // String reserveCnt = itemJson.getString("reserve_cnt");
                // 进行比较的默认值为理论在库数
                int standardValue = stock.getAvailable_cnt();
                if (reserveCnt > 0) {
                    // 引当数不为空 则证明为编辑
                    // 在库数 - 该商品的所有引当数(排除本出库依赖) 将其赋予比较值
                    standardValue = ShipmentsImpl.getReserveCnt(client_id, reserveCnt, product_id, shipmentDetailDao,
                        stock.getInventory_cnt(), stock.getNot_delivery(), "");
                    // standardValue = standardValue + reserveCnt;
                }

                if (standardValue < itemJson.getInteger("product_plan_cnt")) {
                    shipments_status = 2;
                    itemJson.put("reserve_status", 0);
                    if (standardValue > 0) {
                        itemJson.put("reserve_cnt", standardValue);
                    } else {
                        itemJson.put("reserve_cnt", 0);
                    }
                } else {
                    itemJson.put("reserve_status", 1);
                    itemJson.put("reserve_cnt", itemJson.getInteger("product_plan_cnt"));
                }

                // 计算出庫依頼中数
                requesting_cnt = stock.getRequesting_cnt() + product_plan_cnt;
                // 计算理論在庫数
                available_cnt = stock.getAvailable_cnt() - product_plan_cnt;
                // 更新在库表中的依頼中数，理論在庫数
                stockDao.updateStockRequestingCnt(client_id, product_id, requesting_cnt, available_cnt, login_nm,
                    nowTime);
            }

            detailJson.put("items", itemJson);

            result = changeShipmentDetail(detailJson, cushioningType, giftWrappingType, login_nm, nowTime, insertFlg,
                productSetting);

            String kubun = itemJson.getString("kubun");
            // 商品区分 如果商品区分为9 则证明之前没有登录过
            if (!StringTools.isNullOrEmpty(kubun) && "9".equals(kubun)) {
                kubunFlg = true;
            }
        }

        if (jsonParam.getInteger("shipment_status") != 3) {
            shipments_status = jsonParam.getInteger("shipment_status");
        }

        // 出库依赖状态变更
        String status_message = jsonParam.getString("status_message");
        if (!StringTools.isNullOrEmpty(jsonParam.getString("shopify"))) {
            if ("0".equals(jsonParam.getString("shopify"))) {
                shipments_status = 1;
                status_message = "取込の配送方法の名称が指定できなかったため、手動で配送方法を指定してください。";
            }
        }
        // 如果配送会社 或者配送方法为空 则修改为确认等待状态
        if (StringTools.isNullOrEmpty(jsonParam.getString("delivery_method"))
            || StringTools.isNullOrEmpty(jsonParam.getString("delivery_carrier"))) {
            shipments_status = 1;
            status_message = "取込の配送方法の名称が指定できなかったため、手動で配送方法を指定してください。";
        }

        String operation_cd = "";
        String[] plan_id = {
            shipment_plan_id
        };

        // 出庫依頼開始(出荷済み)
        String resurgence_flg = jsonParam.getString("resurgence_flg");
        // 复活时作业履历文言修改
        if (!StringTools.isNullOrEmpty(resurgence_flg) && "1".equals(resurgence_flg)) {
            operation_cd = "05";
        } else {
            operation_cd = "03";
        }
        if (shipments_status == 2) {
            // 出庫依頼開始(引当済み)
            if (!StringTools.isNullOrEmpty(resurgence_flg) && "1".equals(resurgence_flg)) {
                operation_cd = "04";
            } else {
                operation_cd = "02";
            }
        }

        if ("1".equals(jsonParam.getString("suspend"))) {
            shipments_status = 6;
            // 06 出庫依頼変更(出庫保留)
            operation_cd = "06";
        }

        // 支付方法
        String payment_method = jsonParam.getString("payment_method");
        // 代金引换总额
        String totalForCashOnDelivery = jsonParam.getString("total_for_cash_on_delivery");
        boolean paymentFlg = false;
        if (!StringTools.isNullOrEmpty(payment_method) && "2".equals(payment_method)) {
            // 代金引换
            if (StringTools.isNullOrEmpty(totalForCashOnDelivery) || "0".equals(totalForCashOnDelivery)) {
                // 如果代金引换总额 为空 或者 为0 需要变为确认等待状态
                shipments_status = 1;
                status_message = Constants.THE_AMOUNT_OF_CASH_WITHDRAWAL_IS_NULL;
                operation_cd = "01";
                paymentFlg = true;
            }
        }

        if (kubunFlg && shipments_status != 9) {
            // 如果为true 则证明受注csv中含有之前不存在的商品 需要将 出库状态改为确认等待
            shipments_status = 1;
            status_message = Constants.NON_EXISTENT_GOODS;
            if (paymentFlg) {
                status_message += "  " + Constants.THE_AMOUNT_OF_CASH_WITHDRAWAL_IS_NULL;
            }
            operation_cd = "01";
        }
        // 确认等待
        if (shipments_status == 1) {
            operation_cd = "12";
        }

        // 出荷保留
        if (shipments_status == 6) {
            operation_cd = "06";
        }

        if (!StringTools.isNullOrEmpty(status_message)) {
            jsonParam.put("status_message", status_message);
        }

        // 顧客別作業履歴新增
        customerHistoryService.insertCustomerHistory(servletRequest, plan_id, operation_cd, null);

        shipmentsDao.setShipmentStatus(warehouse_cd, shipment_plan_id, shipments_status, status_message,
            product_plan_total, total_price, login_nm, nowTime);

        return result;
    }

    /**
     * set子商品拆分
     * 
     * @param items
     * @return
     */
    @Override
    public JSONArray setProductSplice(JSONArray items) {
        int items_len = items.size();
        JSONArray new_items = new JSONArray();
        for (int i = 0; i < items_len; i++) {
            JSONObject itemJson = items.getJSONObject(i);
            // aセット商品
            Integer set_flg = itemJson.getInteger("set_flg");
            String set_sub_id = itemJson.getString("set_sub_id");
            int product_plan_cnt = itemJson.getInteger("product_plan_cnt");
            Integer old_plan_cnt = itemJson.getInteger("old_plan_cnt");
            if (StringTools.isNullOrEmpty(set_flg) || set_flg != 1) {
                new_items.add(itemJson);
                continue;
            }

            JSONArray product_sets = itemJson.getJSONArray("mc103_product_sets");
            for (int set = 0; set < product_sets.size(); set++) {
                JSONObject setJson = product_sets.getJSONObject(set);
                setJson.put("code", itemJson.getString("code"));
                setJson.put("name", itemJson.getString("name"));
                setJson.put("barcode", itemJson.getString("barcode"));
                setJson.put("set_sub_id", set_sub_id);
                setJson.put("unit_price", itemJson.getString("unit_price"));
                setJson.put("price", itemJson.getString("price"));

                // セット数
                int set_cnt = setJson.getInteger("product_cnt");
                setJson.put("set_cnt", product_plan_cnt);
                setJson.put("product_plan_cnt", set_cnt * product_plan_cnt);
                if (StringTools.isNullOrEmpty(old_plan_cnt)) {
                    old_plan_cnt = 0;
                }
                setJson.put("old_plan_cnt", set_cnt * old_plan_cnt);
                setJson.put("kubun", Constants.SET_PRODUCT);
                setJson.put("tax_flag", itemJson.getString("tax_flag"));
                setJson.put("is_reduced_tax", itemJson.getString("is_reduced_tax"));
                new_items.add(setJson);
            }
        }
        return new_items;
    }

    /**
     * @Param * @param: jsonParam
     * @param: cushioningType
     * @param: giftWrappingType
     * @param: login_nm
     * @param: nowTime
     * @param: insertFlg
     * @description: 将商品信息 存到 出库依赖明细表
     * @return: void
     * @date: 2020/9/3
     */
    private Integer changeShipmentDetail(JSONObject jsonParam, String cushioningType, String giftWrappingType,
        String login_nm, Date nowTime, boolean insertFlg, Mc105_product_setting productSetting) {
        // 获取随机数
        Random random = new Random();

        String shipment_guid = jsonParam.getString("shipment_plan_id")
            + Long.valueOf(String.valueOf(CommonUtils.getRandomNum(100000000) + (System.currentTimeMillis() / 1000L)))
            + (1 * random.nextInt(100));

        JSONObject items = jsonParam.getJSONObject("items");
        Integer result = 0;

        // 出库依赖编辑时，同捆物新增
        // if (!StringTools.isNullOrEmpty(items.getInteger("bundled_add")) && items.getInteger("bundled_add") == 1) {
        // insertFlg = true;
        // }
        JSONObject detailJson = new JSONObject();
        String warehouse_cd = jsonParam.getString("warehouse_cd");
        String client_id = jsonParam.getString("client_id");
        String shipment_plan_id = jsonParam.getString("shipment_plan_id");
        String product_id = items.getString("product_id");
        String set_sub_id = items.getString("set_sub_id");
        Integer product_plan_cnt = CommonUtils.toInteger(items.getString("product_plan_cnt"));
        detailJson.put("shipment_guid", shipment_guid);
        detailJson.put("client_id", client_id);
        detailJson.put("warehouse_cd", warehouse_cd);
        detailJson.put("shipment_plan_id", shipment_plan_id);
        detailJson.put("product_id", product_id);
        detailJson.put("name", items.getString("name"));
        detailJson.put("code", items.getString("code"));
        detailJson.put("barcode", items.getString("barcode"));
        // 商品オプション追加 @Add wang 20210725
        detailJson.put("options", items.getString("options"));
        detailJson.put("is_reduced_tax", CommonUtils.toInteger(items.getString("is_reduced_tax")));
        String bundledFlg = items.getString("bundled_flg");
        if (StringTools.isNullOrEmpty(bundledFlg)) {
            bundledFlg = "0";
        }

        if (!StringTools.isNullOrEmpty(bundledFlg) && Integer.parseInt(bundledFlg) == 0) {
            if (jsonParam.getInteger("cushioning_unit") == 1) {
                detailJson.put("cushioning_type", cushioningType);
            } else if (jsonParam.getInteger("cushioning_unit") == 2) {
                detailJson.put("cushioning_type", items.getString("cushioning_type"));
            } else {
                detailJson.put("cushioning_type", null);
            }
            if (jsonParam.getInteger("gift_wrapping_unit") == 1) {
                detailJson.put("gift_wrapping_type", giftWrappingType);
            } else if (jsonParam.getInteger("gift_wrapping_unit") == 2) {
                detailJson.put("gift_wrapping_type", items.getString("gift_wrapping_type"));
            } else {
                detailJson.put("gift_wrapping_type", null);
            }
        } else {
            detailJson.put("cushioning_type", null);
            detailJson.put("gift_wrapping_type", null);
        }

        detailJson.put("product_plan_cnt", product_plan_cnt);
        detailJson.put("unit_price", CommonUtils.toInteger(items.getString("unit_price")));
        detailJson.put("price", CommonUtils.toInteger(items.getString("price")));
        String taxFlag = items.getString("tax_flag");
        if (!StringTools.isNullOrEmpty(taxFlag)) {
            int flg = 0;
            switch (taxFlag) {
                case "非課税":
                    flg = 3;
                    break;
                case "税抜":
                    if (!StringTools.isNullOrEmpty(productSetting)) {
                        Integer accordion = productSetting.getAccordion();
                        switch (accordion) {
                            case 0:
                                // 切り捨て
                                flg = 10;
                                break;
                            case 1:
                                // 切り上げ
                                flg = 11;
                                break;
                            case 2:
                                // 四捨五入
                                flg = 12;
                                break;
                            default:
                                break;
                        }
                    }
                    break;
                case "税込":
                    flg = 0;
                    break;
                default:
                    flg = Integer.parseInt(taxFlag);
                    break;
            }
            detailJson.put("tax_flag", flg);
        } else {
            detailJson.put("tax_flag", 0);
        }

        detailJson.put("ins_usr", login_nm);
        detailJson.put("ins_date", nowTime);
        detailJson.put("upd_usr", login_nm);
        detailJson.put("upd_date", nowTime);
        detailJson.put("set_sub_id", set_sub_id);
        detailJson.put("set_cnt", items.getInteger("set_cnt"));
        detailJson.put("gift_wrapping_note", jsonParam.getString("gift_wrapping_note"));
        detailJson.put("reserve_status", items.getInteger("reserve_status"));
        detailJson.put("reserve_cnt", items.getString("reserve_cnt"));
        Integer product_sub_id = items.getInteger("product_sub_id");
        // 如果item中没有product_sub_id为0，为1表示macro明细设定;
        detailJson.put("product_sub_id", product_sub_id != null ? 1 : 0);
        // 默认商品区分为普通商品
        int kubun = Constants.ORDINARY_PRODUCT;
        if (!StringTools.isNullOrEmpty(items.getString("kubun")) && "1".equals(items.getString("kubun"))) {
            // 同捆物
            kubun = Constants.BUNDLED;
        } else if (!StringTools.isNullOrEmpty(set_sub_id) && Integer.parseInt(set_sub_id) > 0) {
            // セット商品
            kubun = Constants.SET_PRODUCT;
        } else if (!StringTools.isNullOrEmpty(items.getString("kubun")) && "9".equals(items.getString("kubun"))) {
            // 假登录
            kubun = Constants.NOT_LOGGED_PRODUCT;
        }
        detailJson.put("kubun", kubun);
        // オプション金額
        int optionPrice = 0;
        if (!StringTools.isNullOrEmpty(items.getInteger("option_price"))) {
            optionPrice = items.getInteger("option_price");
        }
        detailJson.put("option_price", optionPrice);
        if (insertFlg) {
            result = shipmentDetailDao.insertShipmentDetail(detailJson);
        } else {
            List<Tw201_shipment_detail> details =
                shipmentDetailDao.getShipmentDetailById(warehouse_cd, shipment_plan_id, product_id, false);
            // 查询出库详细，如果商品商品id，set_sub_id都相同，则插入，否则更新
            boolean update_flg = false;
            int sub_id = (!StringTools.isNullOrEmpty(detailJson.getString("set_sub_id"))
                && Integer.parseInt(detailJson.getString("set_sub_id")) > 0) ? detailJson.getInteger("set_sub_id") : 0;
            for (int i = 0; i < details.size(); i++) {
                int id =
                    (!StringTools.isNullOrEmpty(details.get(i).getSet_sub_id()) && details.get(i).getSet_sub_id() > 0)
                        ? details.get(i).getSet_sub_id()
                        : 0;
                if (id == sub_id) {
                    update_flg = true;
                }
            }
            if (update_flg) {
                result = shipmentDetailDao.updateShipmentDetail(detailJson);
            } else {
                result = shipmentDetailDao.insertShipmentDetail(detailJson);
            }
        }
        return result;
    }

    /**
     * @Param: items
     * @description: 验证 セット商品
     * @return: void
     * @date: 2020/9/3
     */
    @Override
    public void verificationSetProduct(JSONArray items) {
        for (int i = 0; i < items.size(); i++) {
            JSONObject jsonObject = items.getJSONObject(i);
            // 查询该商品是否为 セット商品
            List<Mc103_product_set> setProductInfoList = productDao.getSetProductInfoList(jsonObject);
            if (setProductInfoList.size() != 0) {
                for (Mc103_product_set productSet : setProductInfoList) {
                    JSONObject json = new JSONObject();
                    json.put("client_id", jsonObject.getString("client_id"));
                    json.put("warehouse_cd", jsonObject.getString("warehouse_cd"));
                    json.put("product_id", productSet.getProduct_id());

                    json.put("product_plan_cnt", jsonObject.getString("product_plan_cnt"));
                    json.put("is_reduced_tax", jsonObject.getString("is_reduced_tax"));
                    json.put("cushioning_type", jsonObject.getString("cushioning_type"));
                    json.put("gift_wrapping_type", jsonObject.getString("gift_wrapping_type"));
                    json.put("unit_price", jsonObject.getString("unit_price"));
                    json.put("price", jsonObject.getString("price"));
                    json.put("set_flg", 1);
                    json.put("set_sub_id", null);
                    items.add(json);
                }
                items.remove(i);
                i = i - 1;
            }
        }
    }

    /**
     * @Description: 出庫明細テーブル删除
     * @Param: 顧客CD, 出庫依頼ID
     * @return: Integer
     * @Date: 2020/5/14
     */
    @Override
    @Transactional
    public Integer deleteShipmentDetail(String client_id, String shipment_plan_id, Date upd_date, String upd_usr) {
        return shipmentDetailDao.deleteShipmentDetail(client_id, shipment_plan_id, upd_date, upd_usr);
    }
}
