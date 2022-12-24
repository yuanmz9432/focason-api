package com.lemonico.common.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms004_delivery;
import com.lemonico.common.bean.Ms006_delivery_time;
import com.lemonico.common.bean.Ms007_setting;
import com.lemonico.common.dao.DeliveryDao;
import com.lemonico.common.service.DeliveryService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.StringTools;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @program: sunlogi
 * @description: 運送業者マスタ
 * @create: 2020-07-09 13:25
 **/
@Service
public class DeliveryServiceImpl implements DeliveryService
{

    @Resource
    private DeliveryDao deliveryDao;

    /**
     * @Description: 荷送人コード
     * @Param: 配送業者CD
     * @return: java.lang.Integer
     * @Date: 2020/10/14
     */
    @Override
    public JSONObject updateDCByDcode(JSONObject jsonObject) {
        try {
            deliveryDao.updateDCByDcode(jsonObject);
        } catch (Exception e) {
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success();
    }

    /**
     * @Param: delivery_method : 配送便指定 1:ポスト便 2:宅配便
     * @description: 運送業者マスタ
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/6
     */
    @Override
    public JSONObject getDeliveryInfo(String delivery_method) {
        List<Ms004_delivery> deliveryInfo = deliveryDao.getDeliveryInfo(delivery_method);
        return CommonUtils.success(deliveryInfo);
    }

    /**
     * @param: delivery_cd : 配送業者CD
     * @description: 根据配送業者CD获取運送業者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    @Override
    public JSONObject getDeliveryById(String delivery_cd) {
        Ms004_delivery ms004Delivery = deliveryDao.getDeliveryById(delivery_cd);
        return CommonUtils.success(ms004Delivery);
    }

    /**
     * @Param: delivery_nm : 配送業者名称
     * @description: 根据配送業者名称获取運送会社时间带
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    @Override
    public JSONObject getDeliveryTime(String delivery_nm, Integer delivery_time_id, Integer kubu) {
        List<Ms006_delivery_time> deliveryTime = deliveryDao.getDeliveryTime(delivery_nm, delivery_time_id, null, kubu);
        return CommonUtils.success(deliveryTime);
    }

    /**
     * @description: 配送業者名称数量获取
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/11
     */
    @Override
    public JSONObject getDeliveryType() {
        List<String> deliveryInfo = deliveryDao.getDeliveryType();
        return CommonUtils.success(deliveryInfo);
    }

    /**
     * @Param:
     * @description: 数据更新前删除该店铺所有数据再新规Ms007_setting
     * @return: Integer
     * @date: 2020/11/16
     */
    @Override
    public Integer delConvertedData(String client_id) {

        return deliveryDao.delConvertedData(client_id);
    }

    /**
     * @Param:
     * @description: 查询Ms007_setting客户取入连携设定数据
     * @return: List
     * @date: 2020/11/16
     */
    @Override
    public List<Ms007_setting> getConvertedDataAll(String client_id, Integer kubun) {

        return deliveryDao.getConvertedDataAll(client_id, kubun);
    }

    /**
     * @Param:
     * @description: 新规Ms007_setting客户取入连携设定数据
     * @return:
     * @date: 2020/11/16
     */
    @Override
    @Transactional
    public Integer insertConvertedData(JSONObject jsonObject, HttpServletRequest httpServletRequest) {
        String client_id = jsonObject.getString("client_id");
        // 新规前先删除该店铺数据
        delConvertedData(client_id);
        JSONArray jsonArray = jsonObject.getJSONArray("items");
        for (int i = 0; i < jsonArray.size(); i++) {
            JSONObject json = jsonArray.getJSONObject(i);
            Date date = DateUtils.getDate();
            String login_nm = null;
            if (!StringTools.isNullOrEmpty(httpServletRequest)) {
                login_nm = CommonUtils.getToken("login_nm", httpServletRequest);
            }
            Integer kubun = json.getInteger("kubun");
            String mapping_value = json.getString("mapping_value");
            String converted_id = json.getString("converted_id");
            Ms007_setting ms007 = new Ms007_setting();
            ms007.setClient_id(client_id);
            ms007.setKubun(kubun);
            ms007.setMapping_value(mapping_value);
            ms007.setConverted_id(converted_id);
            String converted_value = null;
            if (kubun == 1) {
                Ms004_delivery ms004 = deliveryDao.getDeliveryById(converted_id);
                converted_value = ms004.getDelivery_nm() + " " + ms004.getDelivery_method_name();
            } else if (kubun == 2) {
                converted_value = json.getString("converted_value");
            } else if (kubun == 3) {
                if (!StringTools.isNullOrEmpty(converted_id)) {
                    converted_value = deliveryDao.getPayById(CommonUtils.toInteger(converted_id));
                }
            }
            ms007.setConverted_value(converted_value);
            ms007.setUpd_usr(login_nm);
            ms007.setIns_usr(login_nm);
            ms007.setUpd_date(date);
            ms007.setIns_date(date);
            deliveryDao.insertConvertedData(ms007);
        }
        return 1;
    }
}
