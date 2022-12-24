package com.lemonico.common.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.Ms004_delivery;
import com.lemonico.common.bean.Ms006_delivery_time;
import com.lemonico.common.bean.Ms007_setting;
import com.lemonico.common.bean.Ms014_payment;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogi
 * @description: 運送業者マスタ
 * @create: 2020-07-09 13:19
 **/
public interface DeliveryDao
{

    /**
     * @Description: 荷送人コード
     * @Param: 配送業者CD
     * @return: java.lang.Integer
     * @Date: 2020/10/14
     */
    public Integer updateDCByDcode(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: delivery_method : 配送便指定 1:ポスト便 2:宅配便
     * @description: 運送業者マスタ
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/6
     */
    List<Ms004_delivery> getDeliveryInfo(@Param("delivery_method") String delivery_method);

    /**
     * @param: delivery_cd : 配送業者CD
     * @description: 根据配送業者CD获取運送業者信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    Ms004_delivery getDeliveryById(@Param("delivery_cd") String delivery_cd);

    /**
     * @Param: delivery_nm : 配送業者名称
     * @description: 根据配送業者名称获取運送会社时间带
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/10
     */
    List<Ms006_delivery_time> getDeliveryTime(@Param("delivery_nm") String delivery_nm,
        @Param("delivery_time_id") Integer delivery_time_id,
        @Param("delivery_time_name") String delivery_time_name,
        @Param("kubu") Integer kubu);

    /**
     * @description: 配送業者名称数量获取
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/11
     */
    List<String> getDeliveryType();

    /**
     * @Param: mapping_value : 変換前値
     * @description: 获取 変換後管理ID
     * @return: com.lemonico.common.bean.Ms007_setting
     * @date: 2020/11/11
     */
    String getConverted_id(@Param("client_id") String client_id, @Param("kubun") Integer kubun,
        @Param("mapping_value") String mapping_value);

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
    Integer insertConvertedData(Ms007_setting ms007);

    /**
     * @Param:
     * @description: 获取所有的配送者信息
     * @return:
     * @date: 2020/11/16
     */
    List<Ms004_delivery> getDeliveryAll();

    /**
     * @Param:
     * @description: 获取所有的配送者时间带信息
     * @return:
     * @date: 2020/11/16
     */
    List<Ms006_delivery_time> getDeliveryTimeAll();

    /**
     * @Param:
     * @description: 获取的配送者时间带信息
     * @return:
     * @date: 2020/11/16
     */
    Ms006_delivery_time getDeliveryTimeById(@Param("delivery_time_id") Integer delivery_time_id);

    /**
     * @Param:
     * @description: 获取所有的支付方法信息
     * @return:
     * @date: 2021/01/26
     */
    List<Ms014_payment> getDeliveryPaymentAll(@Param("kubu") Integer kubu);

    /**
     * @Param:
     * @description: 根据支付方法id获取名称
     * @return:
     * @date: 2021/01/26
     */
    String getPayById(@Param("payment_id") Integer payment_id);

    /**
     * @Param:
     * @description: 支払方法を新期登録
     * @return:
     * @author: wqs
     * @date: 2021/06/26
     */
    Integer insertMs014Payment(Ms014_payment ms014);

    /**
     * @Param:
     * @description: 获取所有的支付方法信息
     * @return:
     * @author: wqs
     * @date: 2021/06/26
     */
    List<Ms014_payment> getDeliveryPaymentAllList();

    /**
     * @Param:
     * @description: 获取所有的配送者时间带信息
     * @return:
     * @author: wqs
     * @date: 2021/06/26
     */
    List<Ms006_delivery_time> getDeliveryTimeAllList();

    /**
     * @Param:
     * @description: 配送時間帯を新期登録
     * @return:
     * @author: wqs
     * @date: 2021/06/26
     */
    Integer insertMs006DeliveryTime(Ms006_delivery_time ms006);

    /**
     * @Param:
     * @description: 支払方法の名称からIDを取得
     * @return:
     * @date: 2021/01/26
     */
    List<Ms014_payment> getDeliveryPaymentName(@Param("payment_name") String payment_name);

    /**
     * @Param:
     * @description: 配送時間帯名称からIDを取得
     * @return:
     * @date: 2021/01/26
     */
    List<Ms006_delivery_time> getDeliveryTimeName(@Param("delivery_time_name") String delivery_time_name,
        @Param("delivery_nm") String delivery_nm);

    /**
     * @Description: //获取变换后的值
     *               @Date： 2021/7/7
     * @Param：
     * @return：
     */
    String getConvertedValue(@Param("client_id") String client_id, @Param("kubun") Integer kubun,
        @Param("mapping_value") String mapping_value);

    Ms004_delivery findDeliveryByName(@Param("delivery_nm") String deliveryNm,
        @Param("delivery_method_name") String deliveryMethodName);
}
