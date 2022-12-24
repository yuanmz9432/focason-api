package com.lemonico.store.service;



import com.lemonico.common.bean.Tc200_order;
import com.lemonico.common.bean.Tc201_order_detail;

/**
 * @description 受注csv処理・Serviceインターフェース
 * 
 * @date 2020/06/23
 * @version 1.0
 **/
public interface OrderSqlService
{

    /**
     * @Description ヘッダーの内容が一致するか確認
     * @Param String[]
     * @return boolean
     */
    public boolean checkCsvHeader(String[] csvHeader);

    /**
     * @Description csvの内容を受注beanに注入
     * @Param Tc200_order,String[]
     * @return 注入後の受注bean
     */
    public Tc200_order OrderInjectionSql(Tc200_order order, String[] rowParams) throws Exception;

    /**
     * @Description csvの内容を受注明細beanに注入
     * @Param Tc201_order_detail,String[]
     * @return 注入後の受注明細bean
     */
    public Tc201_order_detail OrderDetailInjectionSql(Tc201_order_detail orderDetail, String[] rowParams)
        throws Exception;
}
