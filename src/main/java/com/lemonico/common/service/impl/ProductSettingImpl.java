package com.lemonico.common.service.impl;



import com.lemonico.common.bean.Mc105_product_setting;
import com.lemonico.common.dao.ProductSettingDao;
import com.lemonico.common.service.ProductSettingService;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @program: sunlogic
 * @description: 顧客別機能マスタ
 * @create: 2020-06-03 16:57
 **/
@Service
public class ProductSettingImpl implements ProductSettingService
{

    @Autowired
    private ProductSettingDao productSettingDao;

    /**
     * @Description: 現在ログインしている顧客のプロダクト設定情報を取得する
     * @Param: 顧客ID,機能コード
     * @return: Mc105_product_setting
     * @Date: 2020/05/27
     */
    @Override
    public Mc105_product_setting getProductSetting(@Param("client_id") String client_id,
        @Param("set_cd") Integer set_cd) {
        return productSettingDao.getProductSetting(client_id, set_cd);
    }

    /**
     * @Description: 商品消費税設定
     * @Param: Integer
     * @return: Mc105_product_setting
     * @Date: 2020/05/27
     */
    @Override
    public Integer updateProductTax(int tax, int accordion, String client_id) {
        return productSettingDao.updateProductTax(tax, accordion, client_id);
    }

    /**
     * @Description: 金額印字設定
     * @Param: Mc105_product_setting
     * @return: Integer
     * @Date: 2020/05/27
     */
    @Override
    public Integer updateProductNote(int price_on_delivery_note, int delivery_note_type, String client_id) {
        return productSettingDao.updateProductNote(price_on_delivery_note, delivery_note_type, client_id);
    }
}
