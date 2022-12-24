package com.lemonico.store.service;



import com.alibaba.fastjson.JSONObject;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * @className: SettingService
 * @description: SettingService
 * @author: wang
 * @date: 2020/07/03
 **/
public interface SettingService
{
    /**
     * @Description: 店舗情報一覧の取得
     * @Param: user_id: 用户Id
     * @return: JSONObject
     * @Date: 2020/05/28
     */
    JSONObject getStoreList(String user_id);

    /**
     * @Description: 店舗情報一覧の確認
     * @Param: login_id: メールアドレス
     * @return: JSONObject
     * @Date: 2020/05/28
     */
    JSONObject checkStoreList(String login_id);

    /**
     * @Param: jsonObject ： client_id,mail,new_mail
     * @description: 店舗情報のメール更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/28
     */
    JSONObject updateMail(JSONObject jsonObject);

    /**
     * @Param: jsonObject ： client_id,pass,new_pass
     * @description: 店舗情報のPASS更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/05/28
     */
    JSONObject updatePass(JSONObject jsonObject);

    /**
     * 店舗情報のユーザ更新
     *
     * @param jsonObject client_idとそれに付随する各情報を含む
     * @return 結果JSON
     * @date 2020-06-12
     */
    JSONObject updateUser(JSONObject jsonObject);

    /**
     * @Param: jsonObject ： client_id
     * @description: 配送依頼先一覧
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/01
     */
    JSONObject getDeliveryList(String client_id, String sponsor_id);

    /**
     * @Param: jsonObject ： client_id
     * @description: 配送依頼先更新
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/02
     */
    JSONObject updateDeliveryList(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Param: client_id 店舗ID
     * @description: 配送依頼先の新規登録
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/02
     */
    JSONObject createDeliveryList(JSONObject jsonObject, HttpServletRequest request);

    /**
     * 品名設定
     *
     * @param client_id,description_setting
     * @return jsonObject
     * @date 2021-10-18
     */
    Integer descriptionSetting(String client_id, String description_setting);

    /**
     * あるアカウントに紐づいた顧客グループ情報の編集を行う
     *
     * @param jsonObject アカウント情報のJSON。
     * @return 結果の成否JSON
     * @date 2020-06-15
     */
    JSONObject updateClient(JSONObject jsonObject);

    /**
     * アカウント情報(顧客グループ情報)の取得を行う
     *
     * @param client_id 顧客ID
     * @return JSON
     * @date 2020-06-15
     */
    JSONObject getClient(String client_id);

    /**
     * @Param: jsonObject ： client_id
     * @description: 配送マスタの登録
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/03
     */
    JSONObject createCustomerDeliveryList(JSONObject jsonObject);

    /**
     * @Description: 配送先マスタ削除
     * @Param: mc200_customer_delivery(依頼先マスタ)
     * @return: json
     * @Date: 2020/8/22
     */
    Integer deleteCustomerDelivery(String[] delivery_id);

    /**
     * @Param: jsonObject ： client_id
     * @description: 配送マスタの更新
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/03
     */
    JSONObject updateCustomerDeliveryList(JSONObject jsonObject);

    /**
     * @Param: jsonObject ： client_id
     * @description: 配送マスタの取得
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/03
     */
    JSONObject getCustomerDeliveryList(String client_id, String delivery_id, String keyword);

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
    Integer updateMasterLogo(String client_id, String sponsor_id, String fileName);

    /**
     * @Param: client_id
     * @description: 获取店铺的所有员工
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    JSONObject getClientUserList(String client_id);

    /**
     * @Param: user_id
     * @description: 获取店铺员工信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    JSONObject getClientUserByUserId(String user_id);

    /**
     * @Param: jsonObject : client_id,login_id,login_pw,login_nm
     * @description: 新增属于该店铺的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    JSONObject insertClientUser(JSONObject jsonObject, HttpServletRequest request);

    /**
     * @Param: jsonObject userIdList
     * @description: 删除属于该店铺的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    JSONObject deleteClientUser(JSONObject jsonObject);

    /**
     * @Param: jsonObject
     * @description: 获取依頼主マスタ模板PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    JSONObject getMasterPdf(JSONObject jsonObject, Integer flag);

    /**
     * @throws Exception
     * @Description: CSV配送先マスタ登録をアップロードする
     * @Param:
     * @return: String
     * @Date: 2020/12/07
     */
    JSONObject recipientsCsvUpload(String client_id, HttpServletRequest req, MultipartFile file);

    /**
     * @Description: //TODO 添加客户自定义csv数据模板
     *               @Date： 2021/5/8
     * @Param：
     * @return：
     */
    JSONObject insertCsvCustom(JSONObject jsonObject, HttpServletRequest servletRequest);

    /**
     * @param jsonObject
     * @description: 生成纳品书-作业指示书
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/8 15:47
     */
    JSONObject getMasterDetailPdf(JSONObject jsonObject);

    /**
     * @Description: // 根据依赖主ID 删除依赖主master
     *               @Date： 2021/6/18
     * @Param：sponsor_id
     * @return：boolean
     */
    void deleteSponsor(String client_id, String sponsor_id);

    /**
     * @param client_id : 店铺Id
     * @description: 获取csv受注类型
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/24 10:39
     */
    JSONObject getCsvTmp();
}
