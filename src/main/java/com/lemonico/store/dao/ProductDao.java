package com.lemonico.store.dao;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.ibatis.annotations.Param;

/**
 * @className: ProductDao
 * @description: Product Dao
 * @date: 2020/05/11
 **/
public interface ProductDao
{
    /**
     * @Description: 商品一覧
     * @Param: 顧客ID， 商品ID
     * @return: List
     * @Date: 2020/05/18
     */
    public List<Mc100_product> getProductList(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd, @Param("product_id") String[] product_id,
        @Param("search") String search, @Param("tags_id") String tags_id, @Param("bundled_flg") Integer bundled_flg,
        @Param("stock_flg") String stock_flg, @Param("set_flg") Integer set_flg,
        @Param("show_flg") Integer show_flg, @Param("count_flg") String count_flg,
        @Param("stockShow") String stockShow, @Param("kubun") int[] kubuns);

    /**
     * @Description: 顧客IDと製品IDに応じてtags_idの値を取得します
     * @Param: 顧客ID， 商品ID
     * @return: List
     * @Date: 2020/05/18
     */
    public List<Mc101_product_tag> getClientTags(@Param("client_id") String client_id,
        @Param("product_id") String[] product_id);

    /**
     * @Description: kubun
     * @Param: 顧客ID， 商品ID
     * @return: int
     * @Date: 2021/09/23
     */
    public Integer getKubun(@Param("client_id") String client_id,
        @Param("code") String code);

    /**
     * @Description: 商品登録(主表)
     * @Param: Mc100_product
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer insertProduct(Mc100_product product);

    /**
     * @Description: 商品登録(tag)
     * @Param: Mc101_product_tag
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer insertProductTag(Mc101_product_tag tags);

    /**
     * @Description: 商品登録関係テーブル
     * @Param: Pro_tag
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer insertProductTagRelationship(Mc104_tag pro_tag);

    /**
     * @Description: 商品登録画像
     * @Param: Pro_tag
     * @return: Integer
     * @Date: 2020/05/25
     */
    public Integer insertProductImg(Mc102_product_img product_img);

    /**
     * @Description: tagが存在するかどうかを判断する
     * @Param: タグ名
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer checkTagExist(String tag);

    /**
     * @Description: タグ名からtags_idを取得する
     * @Param: タグ名
     * @return: String
     * @Date: 2020/05/18
     */
    public String getTagIdByTagName(String tag);

    /**
     * @Description: 商品更新(主表)
     * @Param: Mc100_product
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer updateProduct(Mc100_product product);

    /**
     * @Description: 商品更新(tag)
     * @Param: Mc100_product
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer updateProductTag(Mc101_product_tag tags);

    /**
     * @Description: 商品IDから商品データを取得する
     * @Param: String
     * @return: Mc100_product
     * @Date: 2020/05/18
     */
    public Mc100_product getProductById(@Param("product_id") String product_id,
        @Param("client_id") String client_id);

    /**
     * @Description: 商品削除(del_flg)
     * @Param: String[]
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer deleteProduct(@Param("client_id") String client_id,
        @Param("product_id") String[] product_id,
        @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date);

    /**
     * @Description: 非表示商品設定(show_flg)
     * @Param: String[]
     * @return: Integer
     * @Author: zhangmj
     * @Date: 2020/11/11
     */
    public Integer showProduct(@Param("client_id") String client_id,
        @Param("product_id") String[] product_id,
        @Param("show_flg") Integer show_flg,
        @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date);

    /**
     * @Description: 商品がtagを更新する際には, 関係テーブルから関連tagを削除する
     * @Param: 顧客ID，商品ID
     * @return: Integer
     * @Date: 2020/05/18
     */
    public Integer deleteProductTag(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Description: 商品のNameとCodeを取得する
     * @Param: 顧客ID，商品ID
     * @return: Mc100_product
     * @Date: 2020/05/26
     */
    public Mc100_product getNameByProductId(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Description: 新規商品登録時にtags optionsを取得する
     * @Param: client_id
     * @return: List
     * @Date: 2020/06/1
     */
    public List<Mc101_product_tag> getTagsOptionsByClientId(@Param("client_id") String client_id);

    /**
     * @Description: 現在のユーザ商品IDが最大値を取得する(最大値を取得することにより 、 新規商品IDの値を決定する)
     * @Param:
     * @return: String
     * @Date: 2020/06/3
     */
    public String getMaxProductId(String client_id);

    /**
     * @Description: tagIDの最大値を取得する(最大値を取得することにより新規時のtagIDの値を決定する)
     * @Param:
     * @return: String
     * @Date: 2020/06/15
     */
    public String getMaxTagId();

    /**
     * @Description: 品名データを取得する
     * @Param:
     * @return: List
     * @Date: 2020/06/8
     */
    public List<Ms008_items> getItemsList(String category_cd);

    /**
     * @Description: 新规商品时商品名は繰り返し検証する
     * @Param:
     * @return: String
     * @Date: 2020/06/10
     */
    public String checkNameExist(@Param("client_id") String client_id,
        @Param("name") String name);

    /**
     * @Description: 新规商品时商品コードは繰り返し検証する
     * @Param:
     * @return: String
     * @Date: 2020/06/10
     */
    public String checkCodeExist(@Param("client_id") String client_id,
        @Param("code") String code);

    /**
     * @Description: 商品假登录查詢
     * @Param:
     * @return: String
     * @Date: 2021/11/9
     */
    public String checkFakeLogin(@Param("client_id") String client_id,
        @Param("code") String code);

    /**
     * @Description: 新規商品の場合, 商品バーコードは検証を繰り返す
     * @Param:
     * @return: String
     * @Date: 2020/06/10
     */
    public String checkBarcodeExist(@Param("client_id") String client_id,
        @Param("barcode") String barcode);

    /**
     * @Param: mc103_product_set
     * @description: 新规 セット商品明細マスタ
     * @return: java.lang.Integer
     * @date: 2020/06/12
     */
    Integer insertProductSet(Mc103_product_set mc103_product_set);

    /**
     * @Description:
     * @Param:
     * @return:
     * @Date: 2020/6/18
     */
    public List<Mc102_product_img> getProductImg(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @Description: 商品图片路径删除
     * @Param:
     * @return: Integer
     * @Date: 2020/06/22
     */
    public Integer productImgDelete(@Param("client_id") String client_id,
        @Param("product_id") String product_id);

    /**
     * @description: 最大setIdを取得する
     * @return: java.lang.Integer
     * @date: 2020/06/16
     */
    Integer getMaxSetSubId();

    /**
     * @Param: set_sub_id
     * @param: object : client_id
     * @description: 查询该商品所指定的セット商品
     * @return: java.util.List<com.lemonico.common.bean.Mc103_product_set>
     * @date: 2020/07/06
     */
    List<Mc103_product_set> getSetProductIdList(@Param("set_sub_id") Integer set_sub_id,
        @Param("object") JSONObject object);

    /**
     * @Description: セット商品查询
     * @Param: set_sub_id，client_id
     * @return: Mc100_product
     * @Date: 2020/10/29
     */
    List<Mc100_product> getProductSetList(@Param("set_sub_id") Integer set_sub_id,
        @Param("client_id") String client_id);

    /**
     * @Param: setId
     * @param: productId
     * @param: client_id
     * @description: 删除セット商品
     * @return: java.lang.Integer
     * @date: 2020/07/06
     */
    Integer deleteSetProduct(@Param("set_sub_id") Integer setId,
        @Param("product_id") String productId,
        @Param("client_id") String client_id);

    /**
     * @Param: setId
     * @param: productId
     * @param: client_id
     * @description: 恢复已经删除的set子商品
     * @return: java.lang.Integer
     * @date: 2021/12/23
     */
    Integer recoverSetProduct(@Param("set_sub_id") Integer setId, @Param("product_id") String productId,
        @Param("client_id") String client_id);

    /**
     * @Param: setId
     * @param: productId
     * @param: client_id
     * @param: product_cnt
     * @description: 修改セット商品个数
     * @return: java.lang.Integer
     * @date: 2020/07/06
     */
    Integer updateSetProductNumber(@Param("set_sub_id") Integer setId,
        @Param("product_id") String productId,
        @Param("client_id") String client_id,
        @Param("product_cnt") Integer product_cnt);

    /**
     * @Param: name : 商品名
     * @description: 根据商品名获取商品信息
     * @return: com.lemonico.common.bean.Mc100_product
     * @date: 2020/8/20
     */
    List<Mc100_product> getProductInfoByName(@Param("name") String name,
        @Param("client_id") String client_id);

    /**
     * @Param: barcode
     * @description: 根据code获取商品信息
     * @return: com.lemonico.common.bean.Mc100_product
     * @date: 2020/9/2
     */
    Mc100_product getProductInfoByCode(@Param("code") String code,
        @Param("client_id") String client_id);

    /**
     * @Param: barcode
     * @description: 根据code获取商品信息
     * @return: com.lemonico.common.bean.Mc100_product
     * @date: 2020/9/2
     */
    List<Mc100_product> getProductInfoListByCode(@Param("code") String code,
        @Param("client_id") String client_id);

    /**
     * @Param: barcode
     * @description: 根据code获取商品ID
     * @return: java.lang.String
     * @date: 2021/7/8
     */
    String getProductIdByCode(@Param("code") String code,
        @Param("client_id") String client_id);

    /**
     * @Param * @param: jsonObject
     * @description: 查询该商品是否为 セット商品
     * @return: java.util.List<com.lemonico.common.bean.Mc103_product_set>
     * @date: 2020/9/3
     */
    List<Mc103_product_set> getSetProductInfoList(@Param("jsonObject") JSONObject jsonObject);

    /**
     * @Param: product_id
     * @param: client_id
     * @param: img_sub_id
     * @description: 删除商品的指定图片
     * @return: java.lang.Integer
     * @date: 2020/9/23
     */
    Integer deleteProductImg(@Param("product_id") String product_id,
        @Param("client_id") String client_id,
        @Param("img_sub_id") String img_sub_id);

    /**
     * @Param: client_id
     * @param: product_id
     * @description: 查询该商品是否为セット商品 的子商品
     * @return: java.util.List<com.lemonico.common.bean.Mc103_product_set>
     * @date: 2020/10/27
     */
    List<Mc103_product_set> verificationSetProduct(@Param("client_id") String client_id,
        @Param("product_id") String product_id);


    /**
     * @Param: client_id
     * @Param: product_id
     * @Param: store_cnt
     * @Param: upd_usr
     * @Param: upd_date
     * @description: 店舗在庫数更新
     * @return: Integer
     * @author: wang
     * @date: 2021/2/3
     */
    public Integer updateStockStroeCnt(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("store_cnt") Integer store_cnt,
        @Param("upd_usr") String upd_usr,
        @Param("upd_date") Date upd_date);

    /**
     * @Param: client_id
     * @param: product_id
     * @description: 查询出库状态的セット商品
     * @return: java.util.Integer
     * @date: 2020/11/25
     */
    Integer selectShipmentSetProduct(@Param("client_id") String client_id, @Param("set_sub_id") String set_sub_id);

    public Integer getRenkeiProduct(Mc106_produce_renkei renkei_product);

    public void insertRenkeiProduct(Mc106_produce_renkei renkei_product);

    public List<Mc106_produce_renkei> selectRenkeiProduct();

    public Tc203_order_client getApiData(@Param("client_id") String client_id, @Param("api_id") Integer api_id);

    public Tc203_order_client getApiDataStock(@Param("client_id") String client_id, @Param("api_id") Integer api_id);

    public Tc203_order_client getApiDataDelivery(@Param("client_id") String client_id, @Param("api_id") Integer api_id);

    public List<Mc106_produce_renkei> selectRenkeiProduct(@Param("template") String template);

    public Tw300_stock getProductCnt(@Param("client_id") String client_id, @Param("product_id") String product_id);

    // 実在庫数ー依頼中数
    public Integer getProductAvaCnt(@Param("client_id") String client_id, @Param("product_id") String product_id);

    // 予備在庫数
    public Integer getProductYobiCnt(@Param("client_id") String client_id, @Param("product_id") String product_id);

    // 個人宅出荷予想数
    public Integer getProductPersonCnt(@Param("client_id") String client_id, @Param("product_id") String product_id);

    /**
     * @param: set_sub_id
     * @description: 根据set_sub_id查询set商品的子商品
     * @return: list
     * @date: 2020/12/30
     */
    List<Mc103_product_set> getSetProduct(@Param("set_sub_id") String set_sub_id, @Param("client_id") String client_id);

    public String getToken(@Param("client_id") String client_id, @Param("client_url") String client_url,
        @Param("api_key") String api_key, @Param("password") String passwd);

    public void setToken(@Param("client_id") String client_id, @Param("client_url") String client_url,
        @Param("api_key") String api_key, @Param("password") String passwd, @Param("token") String token);

    public void setLocationCode(@Param("client_id") String client_id, @Param("renkei_product_id") String variant_sku,
        @Param("variant_id") String stock_location_code);

    public List<Mc106_produce_renkei> getAllProductDataById(@Param("client_id") String client_id,
        @Param("api_id") Integer api_id);

    List<Mc100_product> getSetProductLists(@Param("set_sub_id") Integer set_sub_id,
        @Param("client_id") String client_id);

    /**
     * @param productIdList : 商品Id集合
     * @description: 根据商品Id获取商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/3/9 17:54
     */
    List<Mc100_product> getProductListById(@Param("productIdList") List<String> productIdList,
        @Param("warehouse_cd") String warehouseId, @Param("client_id") String clientId);

    /**
     * @param productCodeList : code集合
     * @param client_id : 店铺Id
     * @description: 根据多个商品code获取多个商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/3/31 12:49
     */
    List<Mc100_product> getProductInfoByCodeList(@Param("productCodeList") ArrayList<String> productCodeList,
        @Param("client_id") String client_id);

    /**
     * @param setSubIdList : set_sub_id 集合
     * @param clientId : 商品Id
     * @description: 根据多个set_sub_id获取多个set商品的集合
     * @return: java.util.List<com.lemonico.common.bean.Mc103_product_set>
     * @date: 2021/4/15 12:47
     */
    List<Mc103_product_set> getSetProductBySubIdList(@Param("setSubIdList") List<Integer> setSubIdList,
        @Param("client_id") String clientId);

    /**
     * @param client_id : 店铺ID
     * @description: 根据店铺ID获取商品集合
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/6/28 13:54
     */
    List<Mc100_product> getProductByClientId(@Param("client_id") String client_id, @Param("search") String searchName);

    /**
     * @Description: 保存商品对应数据
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    Integer setCorrespondingData(Mc110_product_options mc110);

    /**
     * @Description: 更新商品对应数据
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    Integer updateCorrespondingData(Mc110_product_options mc110);

    /**
     * @Description: 获取商品对应数据
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    List<Mc110_product_options> getCorrespondingList(@Param("client_id") String client_id,
        @Param("search") String search);


    /**
     * @Description: 查询商品是否在商品对应表中存在个数
     *               @Date： 2021/10/08
     * @Param：
     * @return：Integer
     */
    Integer getCorrespondence(@Param("client_id") String client_id, @Param("code") String code);

    /**
     * @Description: 商品master修改商品信息时，修改商品对应数据
     *               @Date： 2021/10/8
     *               @Param： code：修改后商品code，old_code：修改之前商品code
     * @return：Integer
     */
    Integer updateCorrespondence(@Param("client_id") String client_id,
        @Param("code") String code,
        @Param("old_code") String old_code,
        @Param("upd_usr") String login_nm,
        @Param("upd_date") Date date);

    /**
     * @Description: 删除商品对应数据
     *               @Date： 2021/7/8
     * @Param：
     * @return：boolean
     */
    Integer delCorresponding(@Param("client_id") String client_id, @Param("id") Integer id);

    Long getProductListCount(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd, @Param("product_id") String[] product_id,
        @Param("search") String search, @Param("tags_id") String tags_id, @Param("bundled_flg") Integer bundled_flg,
        @Param("stock_flg") String stock_flg, @Param("set_flg") Integer set_flg,
        @Param("show_flg") Integer show_flg, @Param("count_flg") String count_flg,
        @Param("stockShow") String stockShow);

    /**
     * Mc110商品コードを取得
     * TODO 暫定対策：MakeShopのMc110テーブルにオプション項目が登録されていないので、このメソッドで調べる
     *
     * @param code 商品コード
     * @param client_id 店舗ID
     * @return List<Mc110_product_options> Mc110商品リスト
     */
    List<Mc110_product_options> getMc110ProductByCode(@Param("code") String code,
        @Param("client_id") String client_id);

    /**
     * @Description: 获取所有商品列表
     *               @Date： 2021/7/14
     * @Param：
     * @return：Mc100_product
     */
    public List<Mc100_product> getProductRecordList(@Param("client_id") String client_id,
        @Param("warehouse_cd") String warehouse_cd, @Param("product_id") String[] product_id,
        @Param("search") String search, @Param("tags_id") String tags_id,
        @Param("typeList") List<Integer> typeList,
        @Param("stock_flg") String stock_flg, @Param("set_flg") Integer set_flg,
        @Param("show_flg") Integer show_flg, @Param("count_flg") String count_flg,
        @Param("stockShow") String stockShow);

    /**
     * @Description: 获取商品图片
     *               @Date： 2021/7/14
     * @Param：
     * @return：Mc100_product
     */
    public List<Mc102_product_img> getProductImgList(@Param("product_id_list") List<String> product_id_list,
        @Param("client_id") String client_id);

    /**
     * @Description: 获取set子商品
     *               @Date： 2021/7/14
     * @Param：
     * @return：Mc100_product
     */
    public List<Mc103_product_set> getSetProductList(@Param("set_sub_id") List<Integer> set_sub_id,
        @Param("client_id") String client_id);

    /**
     * @param client_id : 店铺Id
     * @param product_id : 商品Id
     * @param kubun : 商品区分
     * @param show_flg : 商品显示
     * @param loginNm : 操作者
     * @param date : 日期
     * @description: 修改之前系统不在的商品为普通商品
     * @return: java.lang.Integer
     * @date: 2021/8/9 17:58
     */
    Integer updateProductKubun(@Param("client_id") String client_id,
        @Param("product_id") String product_id,
        @Param("kubun") int kubun,
        @Param("show_flg") int show_flg,
        @Param("loginNm") String loginNm,
        @Param("date") Date date);


    /**
     * @param client_id : 店铺Id
     * @param search : 搜索条件
     * @param showFlg : 非商品显示与否 0：不显示 1：显示
     * @param stockFlg : 显示在库 0：0以下 1：在库数1以上 2：全部
     * @param column : 需要排序的字段
     * @param sortType : 排序的方式
     * @description: 查出满足条件的商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/8/18 17:44
     */
    List<Mc100_product> getOperatingList(@Param("client_id") String client_id,
        @Param("search") String search,
        @Param("show_flg") Integer showFlg,
        @Param("stock_flg") Integer stockFlg,
        @Param("column") String column,
        @Param("sortType") String sortType,
        @Param("tags_id") String tagsId,
        @Param("typeList") List<Integer> typeList,
        @Param("productIdList") List<String> productIdList,
        @Param("warehouse_cd") String warehouse_cd);

    /**
     * @description: 获取商品对应表最大ID
     * @return: java.lang.Integer
     * @date: 2021/9/9 15:52
     */
    Integer getCorrespondingMaxId();

    /**
     * @param client_id : 店铺Id
     * @param productIdList : 商品Id
     * @description: 根据店铺Id 商品Id 获取商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/10/4 14:33
     */
    List<Mc100_product> getProductInfoList(@Param("client_id") String client_id,
        @Param("productIdList") List<String> productIdList);

    /**
     * @param codeList : 商品code集合
     * @param client_id : 店铺Id
     * @description: 根据多个商品code获取商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/11/3 12:41
     */
    List<Mc100_product> getProductListByCodeList(@Param("codeList") List<String> codeList,
        @Param("client_id") String client_id);

    /**
     * @param client_id : 店铺Id
     * @param kubuns : 商品区分数组 0普通商品 1同捆物
     * @description: 获取入库csv模板数据
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2021/11/11 11:23
     */
    List<Mc100_product> getWarehouseCsvData(@Param("client_id") String client_id, @Param("kubuns") int[] kubuns);

    /**
     * 获取set商品的信息
     *
     * @param client_id
     * @param set_sub_id
     * @return
     */
    List<Mc100_product> getSetProductInfo(@Param("client_id") String client_id,
        @Param("set_sub_id") Integer set_sub_id);

    /**
     * 获取set子商品的详细信息
     *
     * @param client_id
     * @param set_sub_id
     * @return
     */
    List<Mc103_product_set> getSetProductDetail(@Param("client_id") String client_id,
        @Param("set_sub_id") Integer set_sub_id);

    /**
     * @param setSubIdList : set商品Id
     * @param client_id : 店铺Id
     * @description: 获取多个set商品信息
     * @return: java.util.List<com.lemonico.common.bean.Mc100_product>
     * @date: 2022/1/19 14:52
     */
    List<Mc100_product> getSetProducts(@Param("set_sub_id") List<Integer> setSubIdList,
        @Param("client_id") String client_id);
}
