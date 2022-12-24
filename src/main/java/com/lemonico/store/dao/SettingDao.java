package com.lemonico.store.dao;



import com.lemonico.common.bean.*;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SettingDao
{

    /**
     * @Param: user_id 用户ID
     * @description: 店舗情報一覧の取得
     * @return: List<Ms200_customer>
     * @author: wang
     * @date: 2020/07/1
     */
    List<Ms200_customer> getStoreList(@Param("user_id") String user_id);

    /**
     * @Param: client_id 店舗ID
     * @description: 店舗情報一覧の取得
     * @return: List<Ms200_customer>
     * @author: wang
     * @date: 2020/07/1
     */
    List<Ms200_customer> checkStoreList(@Param("login_id") String login_id);

    /**
     * @Param: client_id 店舗ID
     * @description: 店舗情報一覧の取得
     * @return: Ms200_customer
     * @author: wang
     * @date: 2020/07/1
     */
    Ms200_customer getStoreClientId(@Param("user_id") String userId);

    /**
     * @description: 店舗情報のメール更新
     * @Param: login_id 新しいメールアドレス<br>
     * @Param: login_pw
     * @Param: old_login_id mail メールアドレス<br>
     *         pass パスワード client_id 店舗ID<br>
     * @return: java.lang.Integer
     * @date: 2020/05/28
     */
    Integer updateMail(@Param("login_id") String loginId,
        @Param("old_login_id") String old_login_id,
        @Param("user_id") String user_id);

    /**
     * @description: 店舗情報のPASS更新
     * @Param: new_mail 新しいパスワード<br>
     *         pass パスワード encode_key ハッシュ値<br>
     *         client_id 店舗ID<br>
     * @return: java.lang.Integer
     * @date: 2020/05/28
     */
    Integer updatePass(@Param("login_pw") String new_pass,
        @Param("encode_key") String encode_key,
        @Param("user_id") String user_id);

    /**
     * @description: 配送依頼先一覧
     * @Param: client_id 店舗ID
     * @return: Stringで結果返す
     * @date: 2020/06/01
     */
    List<Ms012_sponsor_master> getDeliveryListAll(@Param("client_id") String client_id);

    /**
     * @description: 配送依頼先一覧
     * @Param: client_id 店舗ID
     * @param: sponsor_id 依頼先ID
     * @return: Stringで結果返す
     * @date: 2020/06/01
     */
    List<Ms012_sponsor_master> getDeliveryListOne(@Param("client_id") String client_id,
        @Param("sponsor_id") String sponsor_id);

    /**
     * 配送依頼先新規追加。項目の情報を用いて新規追加を行う。
     *
     * @param ms012_sponsor_master
     * @return Integer
     * @date 2020/06/10
     */
    Integer updateDeliveryList(Ms012_sponsor_master ms012_sponsor_master);

    /**
     * 依頼先IDを取得
     *
     * @return 結果の数(1でないとおかしい)
     * @author wang
     * @date 2020-07-3
     */
    Integer getMaxSponsorId();

    /**
     * 配送依頼先新規追加。必須項目の情報を用いて新規追加を行う。
     *
     * @param ms012_sponsor_master
     * @return Integer
     * @date 2020-06-09
     */
    Integer createDeliveryList(Ms012_sponsor_master ms012_sponsor_master);

    /**
     * 配送依頼先新規追加。必須項目の情報を用いて新規追加を行う。
     *
     * @return Integer
     * @author wang
     * @date 2020-07-01
     */
    Integer updateDeliveryDefault(@Param("client_id") String client_id,
        @Param("sponsor_id") String sponsor_id);

    /**
     * @Description: 配送先マスタ削除
     * @Param: mc200_customer_delivery(依頼先マスタ)
     * @return: json
     * @Date: 2020/8/22
     */
    Integer deleteCustomerDelivery(@Param("delivery_id") String[] delivery_id);

    /**
     * ユーザー情報設定の更新
     *
     * @param user client_idを使用
     * @return 結果の数(1でないとおかしい)
     * @date 2020-06-12
     */
    Integer updateUser(Ms200_customer user);

    /**
     * 品名設定
     *
     * @param client_id,description_setting
     * @return Integer
     * @date 2021-10-18
     */
    Integer descriptionSetting(@Param("client_id") String client_id,
        @Param("description_setting") String description_setting);

    /**
     * 顧客グループ情報を取得
     *
     * @param client_id 顧客グループID
     * @return 顧客グループ
     * @author wang
     * @date 2020-06-12
     */
    Ms201_client getClient(@Param("client_id") String client_id);

    /**
     * 顧客グループ情報を編集する。
     *
     * @param ms201_client 更新後の顧客グループ情報。顧客グループIDで照合。
     * @return Integer。成功件数なので1でないと困る
     * @date 2020-06-15
     */
    Integer updateClient(Ms201_client ms201_client);

    /**
     * @description: 配送依頼先一覧
     * @Param: client_id 店舗ID
     * @param: delivery_id 配送先ID
     * @return: Stringで結果返す
     * @author: wang
     * @date: 2020/07/03
     */
    List<Mc200_customer_delivery> getCustomerDelivery(@Param("client_id") String client_id,
        @Param("delivery_id") String delivery_id,
        @Param("keyword") String keyword);

    /**
     * 配送依頼先新規追加。項目の情報を用いて新規追加を行う。
     *
     * @param mc200_customer_delivery
     * @return Integer
     * @author wang
     * @date 2020/07/03
     */
    Integer updateCustomerDeliveryList(Mc200_customer_delivery mc200_customer_delivery);

    /**
     * 配送依頼先新規追加。必須項目の情報を用いて新規追加を行う。
     *
     * @param mc200_customer_delivery
     * @return Integer
     * @author wang
     * @date 2020/07/03
     */
    Integer createCustomerDeliveryList(Mc200_customer_delivery mc200_customer_delivery);

    /**
     * @Param: client_id
     * @param: sponsorDefault
     * @description: 查询有没有sponsor_default为1 的数据
     * @return: com.lemonico.common.bean.Ms012_sponsor_master
     * @date: 2020/8/24
     */
    Ms012_sponsor_master getSponsorDefaultInfo(@Param("client_id") String client_id,
        @Param("sponsor_default") Integer sponsorDefault);

    /**
     * @param client_id 店铺id
     * @param name 依赖主名称
     * @description 根据依赖主名获取依赖主信息
     * @return: Ms012_sponsor_master
     * @date 2022/1/10
     **/
    Ms012_sponsor_master getSponsorByName(@Param("client_id") String client_id,
        @Param("name") String name);

    /**
     * @param client_id 店铺id
     * @param form 个人/法人状态
     * @description 根据个人/法人类型获取依赖主信息
     * @return: Ms012_sponsor_master
     * @date 2022/1/10
     **/
    Ms012_sponsor_master getSponsorByForm(@Param("client_id") String client_id,
        @Param("form") Integer form);

    /**
     * @Param: client_id
     * @param: sponsor_id
     * @description: 修改依頼主マスタ デフォルト
     * @return: java.lang.Integer
     * @date: 2020/8/24
     */
    Integer updateSponsorMasterSponsorDefault(@Param("client_id") String client_id,
        @Param("sponsor_id") String sponsor_id);

    /**
     * @Param: client_id
     * @description: 获取最新的依赖主Id
     * @return: java.lang.String
     * @date: 2020/8/27
     */
    String getLastSponsorIdByClientId(String client_id);

    /**
     * @Param: client_id
     * @param: sponsor_id
     * @description: 更改master logo
     * @return: java.lang.Integer
     * @date: 2020/8/27
     */
    Integer updateMasterLogo(@Param("client_id") String client_id,
        @Param("sponsor_id") String sponsor_id,
        @Param("detail_logo") String uploadPath);

    /**
     * @Param: client_id
     * @description: 根据店铺Id 获取所有员工ID
     * @return: java.util.List<java.lang.String>
     * @date: 2020/8/27
     */
    List<String> getUserIdListByClientId(@Param("client_id") String client_id);

    /**
     * @Param: Ms012_sponsor_master
     * @description:验证csv读取依赖主信息是否存在
     * @return:
     * @date: 2020/9/30
     */
    Ms012_sponsor_master checkSponsorExist(Ms012_sponsor_master ms012);

    /**
     * @Param:
     * @description: csv导出模板信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/05/06
     */
    List<Tc209_setting_template> getSetTemplate(@Param("warehouse_cd") String warehouse_cd,
        @Param("client_id") String client_id,
        @Param("template_cd") Integer template_cd,
        @Param("yoto_id") String yoto_id);

    /**
     * @Param:
     * @description: 获取不同csv模板title
     * @return:
     * @date: 2021/05/06
     */
    List<Tc210_setting_yoto> getTemplateTitle(@Param("yoto_id") String yoto_id);

    /**
     * @Description: //TODO 添加客户自定义csv数据模板
     *               @Date： 2021/5/8
     * @Param：
     * @return：
     */
    Integer insertCsvCustom(Tc209_setting_template tc209CsvTemplate);

    /**
     * @Description: //TODO 更新店铺自定义csv模板信息
     *               @Date： 2021/5/8
     * @Param：
     * @return：
     */
    Integer updateCsvCustom(Tc209_setting_template tc209CsvTemplate);

    /**
     * @Param:
     * @description: 删除店铺自定义csv模板信息
     * @return:
     * @date: 2021/5/8
     */
    Integer deleteCustomTemplate(Integer template_cd);

    /**
     * @Description: // 根据依赖主ID 删除依赖主master
     *               @Date： 2021/6/18
     * @Param：sponsor_id
     * @return：Integer
     */
    Integer deleteSponsor(@Param("client_id") String client_id,
        @Param("sponsor_id") String sponsor_id);

    /**
     * @description: 获取csv受注类型
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/24 10:39
     */
    List<Ms017_csv_template> getCsvTmp();

    /**
     * @param client_id : 店铺Id
     * @param sponsorIds : 依赖主Id集合
     * @description: 获取多个依赖主信息
     * @return: java.util.List<com.lemonico.common.bean.Ms012_sponsor_master>
     * @date: 2022/3/1 17:44
     */
    List<Ms012_sponsor_master> getDeliveryListMore(@Param("client_id") String client_id,
        @Param("sponsorIds") List<String> sponsorIds);
}
