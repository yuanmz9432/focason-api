package com.lemonico.common.service;



import com.lemonico.common.bean.Mc105_product_setting;
import org.apache.ibatis.annotations.Param;

/**
 * @program: sunlogic
 * @description: 顧客別機能マスタ
 * @create: 2020-06-03 16:48
 **/
public interface ProductSettingService
{

    /**
     * @Description: 現在ログインしている顧客のプロダクト設定情報を取得する
     * @Param: 顧客ID,機能コード
     * @return: Mc105_product_setting
     * @Date: 2020/05/27
     */
    public Mc105_product_setting getProductSetting(@Param("client_id") String client_id,
        @Param("set_cd") Integer set_cd);

    /**
     * @Description: 商品消費税設定
     * @Param: Mc105_product_setting
     * @return: Integer
     * @Date: 2020/05/27
     */
    public Integer updateProductTax(@Param("tax") int tax, @Param("accordion") int accordion,
        @Param("client_id") String client_id);

    /**
     * @Description: 金額印字設定
     * @Param: Mc105_product_setting
     * @return: Integer
     * @Date: 2020/05/27
     */
    public Integer updateProductNote(@Param("price_on_delivery_note") int price_on_delivery_note,
        @Param("delivery_note_type") int delivery_note_type,
        @Param("client_id") String client_id);

}
