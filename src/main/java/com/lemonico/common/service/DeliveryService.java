package com.lemonico.common.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms007_setting;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogi
 * @description: 運送業者マスタ
 * @create: 2020-07-09 13:19
 **/
public interface DeliveryService
{

    /**
     * @Description: 荷送人コード
     * @Param: 配送業者CD
     * @return: java.lang.Integer
     * @Date: 2020/10/14
     */
    public JSONObject updateDCByDcode(JSONObject jsonObject);

    /**
     * @Param: delivery_method : 配送便指定 1:ポスト便 2:宅配便
     * @description: 運送業者マスタ
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/6
     */
    JSONObject getDeliveryInfo(String delivery_method);

    /**
     * @param: delivery_cd : 配送業者CD
     * @description: 根据配送業者CD获取運送業者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    JSONObject getDeliveryById(String delivery_cd);

    /**
     * @Param: delivery_nm : 配送業者名称
     * @description: 根据配送業者名称获取運送会社时间带
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    JSONObject getDeliveryTime(String delivery_nm, Integer delivery_time_id, Integer kubu);

    /**
     * @description: 配送業者名称数量获取
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/11
     */
    JSONObject getDeliveryType();

    /**
     * @Param:
     * @description: 数据更新前删除该店铺所有数据再新规Ms007_setting
     * @return: Integer
     * @date: 2020/11/16
     */
    Integer delConvertedData(@Param("client_id") String client_id);

    /**
     * @Param:
     * @description: 查询Ms007_setting客户取入连携设定数据
     * @return: List
     * @date: 2020/11/16
     */
    List<Ms007_setting> getConvertedDataAll(@Param("client_id") String client_id, @Param("kubun") Integer kubun);

    /**
     * @Param:
     * @description: 新规Ms007_setting客户取入连携设定数据
     * @return:
     * @date: 2020/11/16
     */
    Integer insertConvertedData(JSONObject json, HttpServletRequest httpServletRequest);
}
