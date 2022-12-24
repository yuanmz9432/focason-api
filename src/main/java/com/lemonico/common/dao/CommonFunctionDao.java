package com.lemonico.common.dao;



import com.lemonico.common.bean.*;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface CommonFunctionDao
{
    /**
     * @Description: 倉庫と店舗に関する情報を取得する
     * @Param:
     * @return: Ms200_customer
     * @Date: 2020/06/17
     */
    public List<Ms200_customer> getClientInfo(@Param("login_nm") String login_nm, @Param("user_id") String user_id);

    /**
     * @Description: 倉庫と店舗に関する情報を取得する
     * @Param:
     * @return: Ms201_customer_group
     * @Date: 2020/06/17
     */
    public List<Ms201_client> getClientInfomation(String warehouse_cd);

    /**
     * @Description: 根据店铺ID获取仓库ID
     * @Param:
     * @return: String
     * @Date: 2020/07/21
     */
    public String getWarehouseIdByClientId(String client_id);

    /**
     * @Description: 获取商品的出库依赖数
     * @Param:
     * @return: String
     * @Date: 2020/08/07
     */
    public Integer getProductShipmentCount(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Description: 商品サイズ
     * @Param: なし
     * @return: List
     * @Date: 2020/9/14
     */
    public List<Ms010_product_size> getProductSize();

    /**
     * @Description: 获取用户功能权限
     * @Param:
     * @return:
     * @Date: 2021/01/18
     */
    public Ms204_customer_func getUserFunction(@Param("user_id") String user_id,
        @Param("function_cd") String function_cd);

    /**
     * @Description: 获取客户制定的csv出力模板数据
     * @return: Tc209_csv_template
     * @Date: 2021/03/19
     */
    public List<Tc209_setting_template> getClientCsvTemplate(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd,
        @Param("template_nm") String template_nm,
        @Param("yoto_id") String yoto_id);
}
