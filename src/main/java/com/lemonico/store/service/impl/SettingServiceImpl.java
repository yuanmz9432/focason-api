package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.google.common.base.Strings;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.common.dao.ProductSettingDao;
import com.lemonico.common.service.CommonService;
import com.lemonico.core.exception.*;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.store.dao.SettingDao;
import com.lemonico.store.service.SettingService;
import com.lemonico.wms.dao.WarehouseCustomerDao;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 店鋪設定管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SettingServiceImpl implements SettingService
{
    private final static Logger logger = LoggerFactory.getLogger(SettingServiceImpl.class);
    private final CommonService commonService;
    private final SettingDao settingDao;
    private final LoginDao loginDao;
    private final ClientDao clientDao;
    private final WarehouseCustomerDao warehouseCustomerDao;
    private final ProductSettingDao productSettingDao;
    private final MailTools mailTools;
    private final PasswordHelper passwordHelper;
    private final PathProps pathProps;

    /**
     * @description: 店舗情報一覧取得
     * @param: 用户Id
     * @return: List
     * @author: wang
     * @date: 2020/6/30
     */
    @Override
    public JSONObject getStoreList(String user_id) {
        // 初期化
        JSONObject jsonObject = new JSONObject();
        Ms200_customer ms200cus = new Ms200_customer();
        // 初期化
        List<Ms200_customer> list = new ArrayList<Ms200_customer>();

        // 店舗IDがない場合、処理なし
        if (!Strings.isNullOrEmpty(user_id)) {
            try {
                logger.info("DBから店舗情報を検索");
                // アカウント情報取得
                list = settingDao.getStoreList(user_id);
                // 取得した件数0場合、処理なし
                if (list.size() > 0) {
                    // アカウント情報
                    ms200cus = list.get(0);
                    jsonObject.put("email", ms200cus.getLogin_id());
                    jsonObject.put("pass", ms200cus.getLogin_pw());
                    jsonObject.put("tnnm", ms200cus.getLogin_nm());
                    // jsonObject.put("customer_cd", ms200cus.getCustomer_cd());
                    jsonObject.put("yoto", ms200cus.getYoto());
                    jsonObject.put("biko1", ms200cus.getBiko1());

                    // 開始
                    logger.info("アカウント情報の取得成功");
                    return CommonUtils.success(jsonObject);
                } else {
                    // 更新結果が0場合、更新失敗として返す
                    logger.error("アカウント情報の取得失敗");
                    return CommonUtils.failure(ErrorCode.E_A0006);
                }

            } catch (Exception e) {
                // handle exceptions occured in mybatis
                logger.error("DBからSQL検索エラー", e);
                // 何か処理なしの場合、エラーとして返す
                return CommonUtils.failure(ErrorCode.E_A0006);
            }

        } else {
            throw new PlValidationErrorException("必須パラメーターが存在するので、ご確認お願いします。");
        }
    }

    /**
     * @description: 店舗情報一覧取得
     * @param: 顧客CD， 出庫依頼ID
     * @return: List
     * @author: wang
     * @date: 2020/6/30
     */
    @Override
    public JSONObject checkStoreList(String email) {
        // 初期化
        JSONObject jsonObject = new JSONObject();
        List<Ms200_customer> list = new ArrayList<Ms200_customer>();

        // 店舗IDがない場合、処理なし
        if (!Strings.isNullOrEmpty(email)) {
            try {
                logger.info("DBから店舗情報を検索");
                // アカウント情報取得
                list = settingDao.checkStoreList(email);
                // 取得した件数0場合、処理なし
                if (list.size() > 0) {
                    // アカウント情報
                    logger.info("既存メールアドレスの確認:有");
                    jsonObject.put("confirm", "1");
                } else {
                    // 更新結果が0場合、更新失敗として返す
                    logger.error("既存メールアドレスの確認：無");
                    jsonObject.put("confirm", "0");
                }
                return CommonUtils.success(jsonObject);

            } catch (Exception e) {
                // handle exceptions occured in mybatis
                logger.error("DBからSQL検索エラー", e);
                // 何か処理なしの場合、エラーとして返す
                return CommonUtils.failure(ErrorCode.E_A0006);
            }

        } else {
            throw new PlValidationErrorException("必須パラメーターが存在するので、ご確認お願いします。");
        }
    }

    /**
     * @param: jsonObject : client_id,mail,new_mail
     * @description: 店舗情報のメール更新
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/01
     */
    @Override
    public JSONObject updateMail(JSONObject jsonObject) {

        if (!jsonObject.isEmpty()) {

            UsernamePasswordToken token = new UsernamePasswordToken(jsonObject.getString("email"),
                jsonObject.getString("pass"));
            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token);
            } catch (Exception e) {
                throw new PlUnauthorizedException();
            }

            ArrayList<String> userIdList = new ArrayList<>();
            userIdList.add(jsonObject.getString("user_id"));

            List<Ms200_customer> ms200Customers = warehouseCustomerDao.getUserInfoByUserId(userIdList);
            if (ms200Customers.size() != 0) {
                Ms200_customer ms200Customer = ms200Customers.get(0);
                if ("admin".equals(ms200Customer.getAuthority_nm())) {
                    clientDao.updateMailById(jsonObject.getString("new_email"), jsonObject.getString("client_id"));
                }
            }

            settingDao.updateMail(jsonObject.getString("new_email"), jsonObject.getString("email"),
                jsonObject.getString("user_id"));
            return CommonUtils.success();

        }
        // 何か処理なしの場合、エラーとして返す
        return CommonUtils.failure(ErrorCode.E_A0001);
    }

    /**
     * @param: jsonObject : client_id,pass,new_pass
     * @description: 店舗情報のPASS更新
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/01
     */
    @Override
    public JSONObject updatePass(JSONObject jsonObject) {
        UsernamePasswordToken token = new UsernamePasswordToken(jsonObject.getString("email"),
            jsonObject.getString("pass"));
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (Exception e) {
            throw new PlUnauthorizedException();
        }
        // 初期化
        Ms200_customer ms200cus = new Ms200_customer();
        if (!jsonObject.isEmpty()) {

            ms200cus = settingDao.getStoreClientId(jsonObject.getString("userId"));

            if (!StringTools.isNullOrEmpty(ms200cus)) {
                ms200cus.setLogin_pw(jsonObject.getString("new_pass"));
                PasswordHelper.encryptPassword(ms200cus);
                try {
                    settingDao.updatePass(ms200cus.getLogin_pw(), ms200cus.getEncode_key(),
                        jsonObject.getString("userId"));
                } catch (Exception e) {
                    logger.error("パスワードの更新失敗");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.E_A0002);
                }
                return CommonUtils.success();
            }
        }
        return CommonUtils.failure(ErrorCode.E_A0002);
    }

    /**
     * ユーザー情報の更新
     *
     * @param jsonObject client_idとそれに付随する各情報を含む
     * @return 結果JSON
     * @date 2020-06-12
     */
    @Override
    public JSONObject updateUser(JSONObject jsonObject) {
        Ms200_customer ms200cus = new Ms200_customer();
        if (!jsonObject.isEmpty()) {
            try {
                // 更新情報をセット
                ms200cus.setBiko1(jsonObject.getString("biko1"));
                ms200cus.setLogin_nm(jsonObject.getString("login_nm"));
                ms200cus.setUser_id(jsonObject.getString("user_id"));
                // ユーザ更新
                int lines = settingDao.updateUser(ms200cus);

                // 更新結果が0場合、更新失敗として返す
                if (lines > 0) {
                    logger.info("ユーザの更新成功");
                    return CommonUtils.success("ユーザの更新が成功しました。");
                } else {
                    logger.error("ユーザの更新失敗");
                    return CommonUtils.failure(ErrorCode.E_A0003);
                }

            } catch (DataAccessException e) {
                logger.error("ユーザの更新中にエラー", e);
                return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 何か処理なしの場合、エラーとして返す
        return CommonUtils.failure(ErrorCode.E_A0003);

    }

    /**
     * @param: client_id 店舗ID sponsor_id 依頼先ID
     * @description: 配送依頼先一覧の取得
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/01
     */
    @Override
    public JSONObject getDeliveryList(String client_id, String sponsor_id) {
        // 初期化
        JSONObject senders = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<Ms012_sponsor_master> list = new ArrayList<Ms012_sponsor_master>();

        try {

            if (!"#".equals(sponsor_id)) {
                // 配送情報取得(リスト)
                list = settingDao.getDeliveryListOne(client_id, sponsor_id);
            } else {
                // 配送情報取得(リスト)
                list = settingDao.getDeliveryListAll(client_id);
            }
            // 返却値0場合、処理せず
            if (list.size() > 0) {
                logger.info("取得件数：" + list.size());
                for (Ms012_sponsor_master ms012sm : list) {
                    // JSON生成
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("client_id", ms012sm.getClient_id());
                    jsonObject.put("sponsor_id", ms012sm.getSponsor_id());
                    jsonObject.put("utilize", ms012sm.getUtilize());
                    jsonObject.put("name", ms012sm.getName());
                    jsonObject.put("name_kana", ms012sm.getName_kana());
                    jsonObject.put("company", ms012sm.getCompany());
                    jsonObject.put("division", ms012sm.getDivision());
                    jsonObject.put("postcode", ms012sm.getPostcode());
                    jsonObject.put("prefecture", ms012sm.getPrefecture());
                    jsonObject.put("address1", ms012sm.getAddress1());
                    jsonObject.put("address2", ms012sm.getAddress2());
                    jsonObject.put("phone", ms012sm.getPhone());
                    jsonObject.put("fax", ms012sm.getFax());
                    jsonObject.put("email", ms012sm.getEmail());
                    jsonObject.put("contact", ms012sm.getContact());
                    jsonObject.put("contact_url", ms012sm.getContact_url());
                    jsonObject.put("detail_logo", ms012sm.getDetail_logo());
                    jsonObject.put("detail_message", ms012sm.getDetail_message());
                    jsonObject.put("send_message", ms012sm.getSend_message());
                    jsonObject.put("sponsor_default", ms012sm.getSponsor_default());
                    jsonObject.put("label_note", ms012sm.getLabel_note());
                    jsonObject.put("delivery_note_type", ms012sm.getDelivery_note_type());
                    jsonObject.put("price_on_delivery_note", ms012sm.getPrice_on_delivery_note());

                    // JSONを配列にセット
                    jsonArray.add(jsonObject);
                }
            } else {
                logger.info("取得件数：0");
            }
        } catch (Exception e) {
            // handle exceptions occured in mybatis
            logger.error("DBからSQL検索エラー", e);
        }

        // 結果を返す
        senders.put("senders", jsonArray);
        return senders;
    }

    /**
     * @param: JSONObject(依頼先マスタ)
     * @description: 配送依頼先更新
     * @return: com.alibaba.fastjson.JSONObject
     * @author: wang
     * @date: 2020/07/02
     */
    @Override
    public JSONObject updateDeliveryList(JSONObject jsonObject, HttpServletRequest request) {
        Date date = DateUtils.getDate();
        String loginNm = CommonUtils.getToken("login_nm", request);
        Ms012_sponsor_master ms012sm = new Ms012_sponsor_master();

        if (!jsonObject.isEmpty()) {
            try {
                // 配送デフォルト設定(1：デフォルト 0：無)
                int sponsorDef = stringToInt(jsonObject.getString("sponsor_default"));
                // 更新情報をセット
                ms012sm.setClient_id(jsonObject.getString("client_id"));
                ms012sm.setSponsor_id(jsonObject.getString("sponsor_id"));
                ms012sm.setUtilize(jsonObject.getString("utilize"));
                ms012sm.setName(CommonUtils.trimSpace(jsonObject.getString("name")));
                ms012sm.setName_kana(CommonUtils.trimSpace(jsonObject.getString("name_kana")));
                ms012sm.setCompany(CommonUtils.trimSpace(jsonObject.getString("company")));
                ms012sm.setDivision(CommonUtils.trimSpace(jsonObject.getString("division")));
                String postcode = jsonObject.getString("postcode");
                if (!StringTools.isNullOrEmpty(postcode)) {
                    String zip = CommonUtils.formatZip(postcode);
                    ms012sm.setPostcode(zip.trim());
                }
                ms012sm.setPrefecture(jsonObject.getString("prefecture"));
                ms012sm.setAddress1(CommonUtils.trimSpace(jsonObject.getString("address1")));
                ms012sm.setAddress2(CommonUtils.trimSpace(jsonObject.getString("address2")));
                ms012sm.setPhone(CommonUtils.trimSpace(jsonObject.getString("phone")));
                ms012sm.setFax(CommonUtils.trimSpace(jsonObject.getString("fax")));
                ms012sm.setEmail(CommonUtils.trimSpace(jsonObject.getString("email")));
                int contact = Integer.parseInt(jsonObject.getString("contact"));
                StringBuilder builder = new StringBuilder("0000");
                if (contact != 0) {
                    JSONArray checkList = jsonObject.getJSONArray("checkList");
                    StringBuilder name = new StringBuilder();
                    for (int i = 0; i < checkList.size(); i++) {
                        name.append(checkList.getString(i));
                    }
                    if (name.toString().contains("check1")) {
                        builder.replace(1, 2, "1");
                    }
                    if (name.toString().contains("check2")) {
                        builder.replace(2, 3, "1");
                    }
                    if (name.toString().contains("check3")) {
                        builder.replace(3, 4, "1");
                    }
                }
                ms012sm.setContact(stringToInt(builder.toString()));
                ms012sm.setContact_url(CommonUtils.trimSpace(jsonObject.getString("contact_url")));
                String detail_logo = jsonObject.getString("detail_logo");
                // 图片路径
                String uploadPath = pathProps.getImage();
                // 如果没有上传logo则赋予默认logo
                if (detail_logo == null || detail_logo == "" || detail_logo.equals("")) {
                    uploadPath += "white.jpg";
                } else if (jsonObject.getBoolean("change_flg")) {
                    SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
                    String nowTime = format.format(new Date());
                    uploadPath += jsonObject.getString("sponsor_id") + "/" + jsonObject.getString("client_id") + "/"
                        + nowTime + "/" + detail_logo;
                } else {
                    uploadPath = detail_logo;
                }
                ms012sm.setDetail_logo(uploadPath);
                ms012sm.setLabel_note(jsonObject.getString("label_note"));
                Integer deliveryNoteType = jsonObject.getInteger("delivery_note_type");
                Integer price_on_delivery_note = jsonObject.getInteger("price_on_delivery_note");
                int priceOnDeliveryNote =
                    StringTools.isNullOrEmpty(price_on_delivery_note) ? 0 : price_on_delivery_note;
                if (!StringTools.isNullOrEmpty(deliveryNoteType) && deliveryNoteType == 0) {
                    priceOnDeliveryNote = 0;
                }
                ms012sm.setDelivery_note_type(StringTools.isNullOrEmpty(deliveryNoteType) ? 0 : deliveryNoteType);
                ms012sm.setPrice_on_delivery_note(priceOnDeliveryNote);
                ms012sm.setDetail_message(jsonObject.getString("detail_message"));
                ms012sm.setSend_message(jsonObject.getString("send_message"));
                ms012sm.setSponsor_default(sponsorDef);
                ms012sm.setUpd_usr(loginNm);
                ms012sm.setUpd_date(date);

                // 配送依頼先更新
                int lines = settingDao.updateDeliveryList(ms012sm);

                // 更新結果が0場合、更新失敗として返す
                if (lines > 0) {
                    logger.info("配送依頼先の更新成功");

                    // デフォルト依頼先を1にする場合、その他デフォルト設定を0にする
                    if (!(sponsorDef == 0)) {
                        settingDao.updateDeliveryDefault(jsonObject.getString("client_id"),
                            jsonObject.getString("sponsor_id"));
                    }
                    if (sponsorDef == 0) {
                        // 查询有没有sponsor_default为1 的数据
                        Integer sponsorDefault = 1;
                        Ms012_sponsor_master sponsorDefaultInfo = settingDao
                            .getSponsorDefaultInfo(jsonObject.getString("client_id"), sponsorDefault);
                        if (StringTools.isNullOrEmpty(sponsorDefaultInfo)) {
                            settingDao.updateSponsorMasterSponsorDefault(jsonObject.getString("client_id"),
                                jsonObject.getString("sponsor_id"));
                        }
                    }
                    return CommonUtils.success("配送依頼先の更新が成功しました。");
                } else {
                    logger.error("配送依頼先の更新失敗");
                    return CommonUtils.failure(ErrorCode.E_A0003);
                }

            } catch (Exception e) {
                logger.error("配送依頼先の更新中にエラー", e);
                return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 何か処理なしの場合、エラーとして返す
        return CommonUtils.failure(ErrorCode.E_A0003);

    }

    /**
     * @param: jsonObject ： client_id, name,
     * @description: 配送依頼先の新規登録
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/06/01
     */
    @Override
    public JSONObject createDeliveryList(JSONObject jsonObject, HttpServletRequest request) {
        Date date = DateUtils.getDate();
        String loginNm = CommonUtils.getToken("login_nm", request);
        Ms012_sponsor_master ms012sm = new Ms012_sponsor_master();
        String detail_logo = jsonObject.getString("detail_logo");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String nowTime = format.format(new Date());
        String sponsorId = getMaxSponsorId();

        if (!jsonObject.isEmpty()) {
            try {
                // 配送デフォルト設定(1：デフォルト 0：無)
                int sponsorDf = stringToInt(jsonObject.getString("sponsor_default"));
                // 查询有没有sponsor_default为1 的数据
                Integer sponsorDefault = 1;
                Ms012_sponsor_master sponsorDefaultInfo = settingDao
                    .getSponsorDefaultInfo(jsonObject.getString("client_id"), sponsorDefault);
                if (sponsorDf == 1) {
                    if (!StringTools.isNullOrEmpty(sponsorDefaultInfo)) {
                        settingDao.updateDeliveryDefault(jsonObject.getString("client_id"), sponsorId);
                    }
                } else {
                    if (StringTools.isNullOrEmpty(sponsorDefaultInfo)) {
                        sponsorDf = 1;
                    }
                }

                // 更新情報をセット
                ms012sm.setClient_id(jsonObject.getString("client_id"));
                ms012sm.setSponsor_id(sponsorId);
                ms012sm.setUtilize(jsonObject.getString("utilize"));
                ms012sm.setName(CommonUtils.trimSpace(jsonObject.getString("name")));
                ms012sm.setName_kana(CommonUtils.trimSpace(jsonObject.getString("name_kana")));
                ms012sm.setCompany(CommonUtils.trimSpace(jsonObject.getString("company")));
                ms012sm.setDivision(CommonUtils.trimSpace(jsonObject.getString("division")));
                // 邮编番号格式化
                String postcode = jsonObject.getString("postcode");
                if (!StringTools.isNullOrEmpty(postcode)) {
                    String zip = CommonUtils.formatZip(postcode);
                    ms012sm.setPostcode(zip.trim());
                }
                ms012sm.setPrefecture(jsonObject.getString("prefecture").trim());
                ms012sm.setAddress1(CommonUtils.trimSpace(jsonObject.getString("address1")));
                ms012sm.setAddress2(CommonUtils.trimSpace(jsonObject.getString("address2")));
                ms012sm.setPhone(CommonUtils.trimSpace(jsonObject.getString("phone")));
                ms012sm.setFax(CommonUtils.trimSpace(jsonObject.getString("fax")));
                ms012sm.setEmail(CommonUtils.trimSpace(jsonObject.getString("email")));
                int contact = Integer.parseInt(jsonObject.getString("contact"));
                StringBuilder builder = new StringBuilder("0000");
                if (contact != 0) {
                    JSONArray checkList = jsonObject.getJSONArray("checkList");
                    StringBuilder name = new StringBuilder();
                    for (int i = 0; i < checkList.size(); i++) {
                        name.append(checkList.getString(i));
                    }
                    if (name.toString().contains("check1")) {
                        builder.replace(1, 2, "1");
                    }
                    if (name.toString().contains("check2")) {
                        builder.replace(2, 3, "1");
                    }
                    if (name.toString().contains("check3")) {
                        builder.replace(3, 4, "1");
                    }
                }
                ms012sm.setContact(stringToInt(builder.toString()));
                ms012sm.setContact_url(jsonObject.getString("contact_url"));

                // 图片路径
                String uploadPath = pathProps.getImage();
                // 如果没有上传logo则赋予默认logo
                if (detail_logo == null || detail_logo.equals("")) {
                    uploadPath += "white.jpg";
                } else {
                    uploadPath += sponsorId + "/" + jsonObject.getString("client_id") + "/" + nowTime + "/"
                        + detail_logo;
                }
                ms012sm.setDetail_logo(uploadPath);

                ms012sm.setDetail_message(jsonObject.getString("detail_message"));
                ms012sm.setSend_message(jsonObject.getString("send_message"));
                ms012sm.setSponsor_default(sponsorDf);
                ms012sm.setIns_usr(loginNm);
                ms012sm.setIns_date(date);
                ms012sm.setUpd_usr(loginNm);
                ms012sm.setUpd_date(date);
                ms012sm.setLabel_note(jsonObject.getString("label_note"));

                Integer deliveryNoteType = jsonObject.getInteger("delivery_note_type");
                Integer price_on_delivery_note = jsonObject.getInteger("price_on_delivery_note");
                int priceOnDeliveryNote =
                    StringTools.isNullOrEmpty(price_on_delivery_note) ? 0 : price_on_delivery_note;
                if (!StringTools.isNullOrEmpty(deliveryNoteType) && deliveryNoteType == 0) {
                    priceOnDeliveryNote = 0;
                }
                ms012sm.setDelivery_note_type(StringTools.isNullOrEmpty(deliveryNoteType) ? 0 : deliveryNoteType);
                ms012sm.setPrice_on_delivery_note(priceOnDeliveryNote);

                // 配送依頼先更新
                int lines = settingDao.createDeliveryList(ms012sm);
                // 更新結果が0場合、更新失敗として返す
                if (lines > 0) {
                    logger.info("配送依頼先の登録成功:" + sponsorId);
                    // デフォルト依頼先を1にする場合、その他デフォルト設定を0にする
                    return CommonUtils.success("配送依頼先の登録が成功しました。");
                } else {
                    logger.error("配送依頼先の登録失敗");
                    return CommonUtils.failure(ErrorCode.E_A0003);
                }

            } catch (Exception e) {
                logger.error("配送依頼先の新規登録中にエラー", e);
                return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        // 何か処理なしの場合、エラーとして返す
        return CommonUtils.failure(ErrorCode.E_A0003);
    }

    /**
     * @Description: 配送先マスタ削除
     * @Param: mc200_customer_delivery(依頼先マスタ)
     * @return: json
     * @Date: 2020/8/22
     */
    @Override
    public Integer deleteCustomerDelivery(String[] delivery_id) {
        settingDao.deleteCustomerDelivery(delivery_id);
        return 0;
    }

    /**
     * ユーザーのパスワードのハッシュ値を生成する。
     *
     * @param user パスワードを所持するユーザーを表す。"pass"と"client_id"のフィールドが必要。
     * @return エンコード(ハッシュ化)パスワードを含んだJSONObject。
     * @date 2020-06-08
     */
    public Ms200_customer getHashPassword(JSONObject user, boolean bool) {

        Ms200_customer ms200cus = new Ms200_customer();
        // 既存情報
        ms200cus = settingDao.getStoreClientId(user.getString("userId"));
        if (bool) {
            // 新規ハッシュ生成
            ms200cus.setLogin_pw(user.getString("new_pass"));
            PasswordHelper.encryptPassword(ms200cus);
        } else {
            // 既存ハッシュ取得
            ms200cus.setLogin_pw(user.getString("pass"));
            String pass = passwordHelper.toHashPassword(ms200cus);
            ms200cus.setLogin_pw(pass);
        }
        return ms200cus;
    }

    /**
     * あるアカウントに紐づいた顧客グループ情報の編集を行う
     *
     * @param jsonObject アカウント情報のJSON。
     * @return 結果の成否JSON
     * @date 2020-06-15
     */
    @Override
    public JSONObject updateClient(JSONObject jsonObject) {
        // 初期化
        Ms201_client ms200cg = new Ms201_client();

        try {
            // アカウント情報取得
            logger.info(jsonObject.getString("client_id"));
            ms200cg.setClient_id(jsonObject.getString("client_id"));// 店舗ID
            ms200cg.setCorporation_flg(jsonObject.getString("corporation_flg")); // 事業形態
            ms200cg.setCorporation_number(jsonObject.getString("corporation_number")); // 法人番号
            ms200cg.setShop_nm(jsonObject.getString("shop_nm"));// 屋号・ショップ名
            ms200cg.setTnnm(jsonObject.getString("tnnm")); // 担当者名
            ms200cg.setCountry_region(jsonObject.getString("country_region")); // 国・地域
            String zip = jsonObject.getString("zip");
            if (!StringTools.isNullOrEmpty(zip)) {
                String formatZip = CommonUtils.formatZip(zip);
                ms200cg.setZip(formatZip); // 郵便番号
            }
            ms200cg.setTdfk(jsonObject.getString("tdfk")); // 都道府県
            ms200cg.setAdd1(jsonObject.getString("add1")); // 住所１
            ms200cg.setAdd2(jsonObject.getString("add2"));// 住所2
            ms200cg.setTel(jsonObject.getString("tel")); // 電話番号
            try {
                ms200cg.setBirthday(stringToDate(jsonObject.getString("birthday"))); // 生年月日
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            ms200cg.setUrl(jsonObject.getString("url")); // ショップ等url
            ms200cg.setPermonth(jsonObject.getInteger("permonth")); // 月の取扱量
            ms200cg.setContact_time(jsonObject.getString("contact_time")); // 連絡可能時間帯
            ms200cg.setColor(jsonObject.getString("color")); // 顔色
            ms200cg.setMail(jsonObject.getString("mail")); // メールアドレス
            ms200cg.setDescription_setting(jsonObject.getString("description_setting")); // 品名設定方法
            // データ更新
            int lines = settingDao.updateClient(ms200cg);

            // 更新結果が0場合、更新失敗として返す
            if (lines > 0) {
                logger.info("アカウント情報の更新成功");
                return CommonUtils.success("アカウント情報の更新が成功しました。");
            } else {
                logger.error("アカウント情報の更新失敗");
                return CommonUtils.failure(ErrorCode.E_A0004);
            }
        } catch (Throwable e) {
            // TODO 自動生成された catch ブロック
            logger.error("アカウント情報更新中にエラー発生", e);
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 品名設定
     *
     * @param client_id,description_setting
     * @return Integer
     * @date 2021-10-18
     */
    @Override
    public Integer descriptionSetting(String client_id, String description_setting) {
        return settingDao.descriptionSetting(client_id, description_setting);
    }

    /**
     * アカウント情報(顧客グループ情報)の取得を行う
     *
     * @param client_id 顧客ID
     * @return JSON
     * @date 2020-06-15
     */
    @Override
    public JSONObject getClient(String client_id) {
        // 初期化
        Ms201_client ms200cg;
        JSONObject resultJson = new JSONObject();

        // 値なし場合、処理せず
        if (!Strings.isNullOrEmpty(client_id)) {
            try {
                // 情報取得
                ms200cg = settingDao.getClient(client_id);
                // JSON生成

                // resultJson.put("customer_cd", ms200cg.getCustomer_cd());
                resultJson.put("corporation_flg", ms200cg.getCorporation_flg()); // 事業形態
                resultJson.put("corporation_number", ms200cg.getCorporation_number()); // 法人番号
                resultJson.put("shop_nm", ms200cg.getShop_nm());// 会社名
                resultJson.put("client_nm", ms200cg.getClient_nm());// 店舗名
                resultJson.put("country_region", ms200cg.getCountry_region()); // 国・地域
                resultJson.put("zip", ms200cg.getZip()); // 郵便番号
                resultJson.put("tdfk", ms200cg.getTdfk()); // 都道府県
                resultJson.put("add1", ms200cg.getAdd1()); // 住所１
                resultJson.put("add2", ms200cg.getAdd2()); // 住所２
                resultJson.put("tel", ms200cg.getTel()); // 電話番号
                resultJson.put("birthday", dateToString(ms200cg.getBirthday())); // 生年月日
                resultJson.put("tnnm", ms200cg.getTnnm()); // 担当者名
                resultJson.put("url", ms200cg.getUrl()); // ショップURL
                resultJson.put("permonth", ms200cg.getPermonth()); // 月の取扱量
                resultJson.put("contact_time", ms200cg.getContact_time()); // 連絡可能時間帯
                resultJson.put("color", ms200cg.getColor()); // 顔色
                resultJson.put("mail", ms200cg.getMail()); // 担当メール
                resultJson.put("description_setting", ms200cg.getDescription_setting()); // 品名設定

            } catch (Exception e) {
                logger.error("ユーザの更新中にエラー", e);
                return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        // 結果が0場合、取得失敗として返す
        if (!resultJson.isEmpty()) {
            logger.info("データの取得成功");
            return CommonUtils.success(resultJson);
        } else {
            logger.error("データの取得失敗");
            return CommonUtils.failure(ErrorCode.E_A0003);
        }
    }

    /**
     * データ型を文字列に変換。
     *
     * @param date データ。
     * @return 文字列。
     * @author wang
     * @date 2020-07-01
     */
    private String dateToString(Date date) {
        if (Objects.isNull(date)) {
            date = new Date();
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    /**
     * 文字列をデータ型に変換。
     *
     * @param str 文字列
     * @return date
     * @throws Throwable
     * @author wang
     * @date 2020-07-01
     */
    private Date stringToDate(String str) throws Throwable {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        if (Strings.isNullOrEmpty(str)) {
            Date date = new Date();
            str = date.toString();
        }
        return dateFormat.parse(str);
    }

    /**
     * 文字列をデータ型に変換。
     *
     * @param str 文字列
     * @return date
     * @throws Throwable
     * @author wang
     * @date 2020-07-01
     */
    private Integer stringToInt(String str) {
        int num = 0;
        if (!Strings.isNullOrEmpty(str)) {
            num = Integer.parseInt(str);
        }
        return num;
    }

    /**
     * 依頼先IDの最大値を取得。
     *
     * @return 文字列
     * @throws Throwable
     * @author wang
     * @date 2020-07-01
     */
    private String getMaxSponsorId() {
        Integer id = settingDao.getMaxSponsorId();
        if (id != null && id > 0) {
            id = id + 1;
        } else {
            id = 1;
        }
        // TODO 自動生成されたメソッド・スタブ
        return id.toString();
    }

    /**
     * @Description: 配送先マスタ登録
     * @Param: client id 店舗ID
     * @return: json(mc200_customer_delivery)
     * @Author: wang
     * @Date: 2020/06/01
     */
    @Override
    public JSONObject createCustomerDeliveryList(JSONObject jsonObject) {
        // a初期化
        Mc200_customer_delivery mc200cd = new Mc200_customer_delivery();

        try {
            // a情報取得
            mc200cd.setDelivery_id(stringToInt(jsonObject.getString("delivery_id")));
            mc200cd.setClient_id(jsonObject.getString("client_id"));
            mc200cd.setName(CommonUtils.trimSpace(jsonObject.getString("name")));
            String postcode = jsonObject.getString("postcode");
            if (!StringTools.isNullOrEmpty(postcode)) {
                String zip = CommonUtils.formatZip(postcode);
                mc200cd.setPostcode(zip.trim());
            }
            mc200cd.setPrefecture(CommonUtils.trimSpace(jsonObject.getString("prefecture")));
            mc200cd.setAddress1(CommonUtils.trimSpace(jsonObject.getString("address1")));
            mc200cd.setAddress2(CommonUtils.trimSpace(jsonObject.getString("address2")));
            mc200cd.setCompany(CommonUtils.trimSpace(jsonObject.getString("company")));
            mc200cd.setDivision(CommonUtils.trimSpace(jsonObject.getString("division")));
            mc200cd.setPhone(CommonUtils.trimSpace(jsonObject.getString("phone")));
            mc200cd.setEmail(CommonUtils.trimSpace(jsonObject.getString("email")));
            mc200cd.setForm(jsonObject.getInteger("form"));
            mc200cd.setDel_flg(stringToInt(jsonObject.getString("del_flg")));

            if (!StringTools.isNullOrEmpty(jsonObject.getString("delivery_method"))) {
                mc200cd.setDelivery_method(jsonObject.getString("delivery_method"));
            } else {
                mc200cd.setDelivery_method(null);
            }
            mc200cd.setDelivery_carrier(jsonObject.getString("delivery_carrier"));
            mc200cd.setDelivery_date(jsonObject.getInteger("delivery_date"));
            mc200cd.setDelivery_time_slot(jsonObject.getString("delivery_time_slot"));
            mc200cd.setBox_delivery(jsonObject.getInteger("box_delivery"));
            mc200cd.setFragile_item(jsonObject.getInteger("fragile_item"));
            mc200cd.setCushioning_unit(jsonObject.getString("cushioning_unit"));
            mc200cd.setCushioning_type(jsonObject.getString("cushioning_type"));
            mc200cd.setGift_wrapping_unit(jsonObject.getString("gift_wrapping_unit"));
            mc200cd.setGift_wrapping_type(jsonObject.getString("gift_wrapping_type"));
            mc200cd.setDelivery_note_type(jsonObject.getString("delivery_note_type"));
            mc200cd.setPrice_on_delivery_note(jsonObject.getInteger("price_on_delivery_note"));
            JSONArray jsonArray = jsonObject.getJSONArray("delivery_instructions_list");
            for (int i = 0; i < jsonArray.size(); i++) {
                switch (i) {
                    case 0:
                        mc200cd.setInvoice_special_notes(jsonArray.getString(0));
                        break;
                    case 1:
                        mc200cd.setBikou1(jsonArray.getString(1));
                        break;
                    case 2:
                        mc200cd.setBikou2(jsonArray.getString(2));
                        break;
                    case 3:
                        mc200cd.setBikou3(jsonArray.getString(3));
                        break;
                    case 4:
                        mc200cd.setBikou4(jsonArray.getString(4));
                        break;
                    case 5:
                        mc200cd.setBikou5(jsonArray.getString(5));
                        break;
                    case 6:
                        mc200cd.setBikou6(jsonArray.getString(6));
                        break;
                    case 7:
                        mc200cd.setBikou7(jsonArray.getString(7));
                        break;
                    default:
                        break;
                }
            }

            // データ更新
            int lines = settingDao.createCustomerDeliveryList(mc200cd);

            // 更新結果が0場合、更新失敗として返す
            if (lines > 0) {
                logger.info("配送先マスタの登録成功");
                return CommonUtils.success("アカウント情報の更新が成功しました。");
            } else {
                logger.error("配送先マスタの登録失敗");
                return CommonUtils.failure(ErrorCode.E_A0005);
            }
        } catch (Throwable e) {
            // TODO 自動生成された catch ブロック
            logger.error("配送先マスタ更新中にエラー発生", e);
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 配送先マスタ更新
     * @Param:client id 店舗ID
     * @return: json(mc200_customer_delivery)
     * @Author: wang
     * @Date: 2020/06/01
     */
    @Override
    public JSONObject updateCustomerDeliveryList(JSONObject jsonObject) {
        // a初期化
        Mc200_customer_delivery mc200cd = new Mc200_customer_delivery();

        try {
            int delivery_id = stringToInt(jsonObject.getString("delivery_id"));

            // a情報取得
            mc200cd.setDelivery_id(delivery_id);
            mc200cd.setClient_id(jsonObject.getString("client_id"));
            mc200cd.setName(CommonUtils.trimSpace(jsonObject.getString("name")));
            String postcode = jsonObject.getString("postcode");
            if (!StringTools.isNullOrEmpty(postcode)) {
                String zip = CommonUtils.formatZip(postcode);
                mc200cd.setPostcode(zip.trim());
            }
            mc200cd.setPrefecture(CommonUtils.trimSpace(jsonObject.getString("prefecture")));
            mc200cd.setAddress1(CommonUtils.trimSpace(jsonObject.getString("address1")));
            mc200cd.setAddress2(CommonUtils.trimSpace(jsonObject.getString("address2")));
            mc200cd.setCompany(CommonUtils.trimSpace(jsonObject.getString("company")));
            mc200cd.setDivision(CommonUtils.trimSpace(jsonObject.getString("division")));
            mc200cd.setPhone(CommonUtils.trimSpace(jsonObject.getString("phone")));
            mc200cd.setEmail(CommonUtils.trimSpace(jsonObject.getString("email")));
            mc200cd.setForm(jsonObject.getInteger("form"));
            mc200cd.setDel_flg(stringToInt(jsonObject.getString("del_flg")));
            if (!StringTools.isNullOrEmpty(jsonObject.getString("delivery_method"))) {
                mc200cd.setDelivery_method(jsonObject.getString("delivery_method"));
            } else {
                mc200cd.setDelivery_method(null);
            }
            mc200cd.setDelivery_carrier(jsonObject.getString("delivery_carrier"));
            mc200cd.setDelivery_date(jsonObject.getInteger("delivery_date"));
            mc200cd.setDelivery_time_slot(jsonObject.getString("delivery_time_slot"));
            mc200cd.setBox_delivery(jsonObject.getInteger("box_delivery"));
            mc200cd.setFragile_item(jsonObject.getInteger("fragile_item"));
            mc200cd.setCushioning_unit(jsonObject.getString("cushioning_unit"));
            mc200cd.setCushioning_type(jsonObject.getString("cushioning_type"));
            mc200cd.setGift_wrapping_unit(jsonObject.getString("gift_wrapping_unit"));
            mc200cd.setGift_wrapping_type(jsonObject.getString("gift_wrapping_type"));
            mc200cd.setDelivery_note_type(jsonObject.getString("delivery_note_type"));
            mc200cd.setPrice_on_delivery_note(jsonObject.getInteger("price_on_delivery_note"));
            JSONArray jsonArray = jsonObject.getJSONArray("delivery_instructions_list");
            for (int i = 0; i < jsonArray.size(); i++) {
                switch (i) {
                    case 0:
                        mc200cd.setInvoice_special_notes(jsonArray.getString(0));
                        break;
                    case 1:
                        mc200cd.setBikou1(jsonArray.getString(1));
                        break;
                    case 2:
                        mc200cd.setBikou2(jsonArray.getString(2));
                        break;
                    case 3:
                        mc200cd.setBikou3(jsonArray.getString(3));
                        break;
                    case 4:
                        mc200cd.setBikou4(jsonArray.getString(4));
                        break;
                    case 5:
                        mc200cd.setBikou5(jsonArray.getString(5));
                        break;
                    case 6:
                        mc200cd.setBikou6(jsonArray.getString(6));
                        break;
                    case 7:
                        mc200cd.setBikou7(jsonArray.getString(7));
                        break;
                    default:
                        break;
                }
            }
            // aデータ更新
            int lines = settingDao.updateCustomerDeliveryList(mc200cd);

            // a更新結果が0場合、更新失敗として返す
            if (lines > 0) {
                logger.info("配送先マスタの更新成功");
                return CommonUtils.success("アカウント情報の更新が成功しました。");
            } else {
                logger.error("配送先マスタの更新失敗");
                return CommonUtils.failure(ErrorCode.E_A0005);
            }
        } catch (Throwable e) {
            // TODO 自動生成された catch ブロック
            logger.error("アカウント情報更新中にエラー発生", e);
            return CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @Description: 配送先マスタ取得
     * @Param: client id 店舗ID delivery_id 配送先ID
     * @return: json(mc200_customer_delivery)
     * @Author: wang
     * @Date: 2020/06/01
     */
    @Override
    public JSONObject getCustomerDeliveryList(String client_id, String delivery_id, String keyword) {
        // a初期化
        JSONObject deliverys = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        List<Mc200_customer_delivery> list = new ArrayList<Mc200_customer_delivery>();

        try {
            // a配送情報取得
            list = settingDao.getCustomerDelivery(client_id, delivery_id, keyword);

            // a返却値0場合、処理せず
            if (list.size() > 0) {
                logger.info("取得件数：" + list.size());
                for (Mc200_customer_delivery mc200cd : list) {
                    JSONObject jsonObject = new JSONObject();
                    logger.info(mc200cd.getDelivery_id().toString());
                    // JSON生成
                    jsonObject.put("delivery_id", mc200cd.getDelivery_id());
                    jsonObject.put("client_id", mc200cd.getClient_id());
                    jsonObject.put("name", mc200cd.getName());
                    jsonObject.put("postcode", mc200cd.getPostcode());
                    jsonObject.put("prefecture", mc200cd.getPrefecture());
                    jsonObject.put("address1", mc200cd.getAddress1());
                    jsonObject.put("address2", mc200cd.getAddress2());
                    jsonObject.put("company", mc200cd.getCompany());
                    jsonObject.put("division", mc200cd.getDivision());
                    jsonObject.put("phone", mc200cd.getPhone());
                    jsonObject.put("email", mc200cd.getEmail());
                    jsonObject.put("del_flg", mc200cd.getDel_flg());
                    jsonObject.put("form", mc200cd.getForm());
                    jsonObject.put("delivery_method", mc200cd.getDelivery_method());
                    jsonObject.put("delivery_carrier", mc200cd.getDelivery_carrier());
                    jsonObject.put("delivery_date", mc200cd.getDelivery_date());
                    jsonObject.put("delivery_time_slot", mc200cd.getDelivery_time_slot());
                    jsonObject.put("box_delivery", mc200cd.getBox_delivery());
                    jsonObject.put("fragile_item", mc200cd.getFragile_item());
                    jsonObject.put("cushioning_unit", mc200cd.getCushioning_unit());
                    jsonObject.put("cushioning_type", mc200cd.getCushioning_type());
                    jsonObject.put("gift_wrapping_unit", mc200cd.getGift_wrapping_unit());
                    jsonObject.put("gift_wrapping_type", mc200cd.getGift_wrapping_type());
                    jsonObject.put("delivery_note_type", mc200cd.getDelivery_note_type());
                    jsonObject.put("price_on_delivery_note", mc200cd.getPrice_on_delivery_note());
                    JSONArray jArray = new JSONArray();
                    if (!StringTools.isNullOrEmpty(mc200cd.getInvoice_special_notes())) {
                        jArray.add(mc200cd.getInvoice_special_notes());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou1())) {
                        jArray.add(mc200cd.getBikou1());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou2())) {
                        jArray.add(mc200cd.getBikou2());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou3())) {
                        jArray.add(mc200cd.getBikou3());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou4())) {
                        jArray.add(mc200cd.getBikou4());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou5())) {
                        jArray.add(mc200cd.getBikou5());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou6())) {
                        jArray.add(mc200cd.getBikou6());
                    }
                    if (!StringTools.isNullOrEmpty(mc200cd.getBikou7())) {
                        jArray.add(mc200cd.getBikou7());
                    }
                    // jObject.put("invoice_special_notes", mc200cd.getInvoice_special_notes());
                    // jObject.put("bikou1", mc200cd.getBikou1());
                    // jObject.put("bikou2", mc200cd.getBikou2());
                    // jObject.put("bikou3", mc200cd.getBikou3());
                    // jObject.put("bikou4", mc200cd.getBikou4());
                    // jObject.put("bikou5", mc200cd.getBikou5());
                    // jObject.put("bikou6", mc200cd.getBikou6());
                    // jObject.put("bikou7", mc200cd.getBikou7());
                    jsonObject.put("delivery_instructions_list", jArray);

                    // JSONを配列にセット
                    jsonArray.add(jsonObject);
                }
            } else {
                logger.info("取得件数：0");
            }
        } catch (Exception e) {
            // handle exceptions occured in mybatis
            logger.error("DBからSQL検索エラー", e);
        }

        // a結果を返す
        deliverys.put("deliverys", jsonArray);
        return deliverys;
    }

    /**
     * @Param: client_id
     * @description: 获取最新的依赖主Id
     * @return: java.lang.String
     * @date: 2020/8/27
     */
    @Override
    public String getLastSponsorIdByClientId(String client_id) {
        return settingDao.getLastSponsorIdByClientId(client_id);
    }

    /**
     * @Param: client_id
     * @param: sponsor_id
     * @description: 更改master logo
     * @return: java.lang.Integer
     * @date: 2020/8/27
     */
    @Override
    public Integer updateMasterLogo(String client_id, String sponsor_id, String fileName) {

        SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
        String nowTime = format.format(new Date());
        // 图片路径
        String uploadPath = pathProps.getImage() + sponsor_id + "/" + client_id + "/" + nowTime + "/" + fileName;

        return settingDao.updateMasterLogo(client_id, sponsor_id, uploadPath);
    }

    /**
     * @Param: client_id
     * @description: 获取店铺的所有员工
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @Override
    public JSONObject getClientUserList(String client_id) {
        List<String> userIdList = settingDao.getUserIdListByClientId(client_id);
        List<Ms200_customer> userData = getUserData(userIdList);
        return CommonUtils.success(userData);
    }

    /**
     * @Param: user_id
     * @description: 获取店铺员工信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @Override
    public JSONObject getClientUserByUserId(String user_id) {
        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(user_id);
        List<Ms200_customer> userData = getUserData(userIdList);
        return CommonUtils.success(userData.get(0));
    }

    /**
     * @Param: jsonObject : client_id,login_id,login_pw,login_nm
     * @description: 新增属于该店铺的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */
    @Override
    public JSONObject insertClientUser(JSONObject jsonObject, HttpServletRequest request) {
        Integer flg = Integer.valueOf(jsonObject.getString("flg"));
        String loginNm = jsonObject.getString("login_id");
        String name = CommonUtils.getToken("login_nm", request);
        Date date = DateUtils.getDate();
        String userId = "";
        Ms200_customer userByName;
        try {
            userByName = loginDao.getUserByName(loginNm, null);
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        if (flg == 1) {
            // 验证旧密码是否输入正确
            UsernamePasswordToken token = new UsernamePasswordToken(loginNm, jsonObject.getString("oldLoginPw"));
            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token);
            } catch (Exception e) {
                throw new PlUnauthorizedException();
            }
            String checkedFlg = jsonObject.getString("checkedFlg");
            userByName.setLogin_nm(jsonObject.getString("login_nm"));
            if (checkedFlg.equals("1")) {
                userByName.setLogin_pw(jsonObject.getString("login_pw"));
                PasswordHelper.encryptPassword(userByName);
                String emailFlg = jsonObject.getString("emailFlg");
                if (emailFlg.equals("1")) {
                    MailBean mailBean = mailTools.sendUserEmail(loginNm, jsonObject.getString("login_pw"));
                    commonService.sendMail(mailBean);
                }
            }
            loginDao.updateUserInfo(userByName);
            userId = userByName.getUser_id();
        } else {
            if (userByName != null) {
                throw new PlResourceAlreadyExistsException("ユーザー: " + jsonObject.getString("login_nm"));
            }
            Ms200_customer ms200Customer = JSONObject.toJavaObject(jsonObject, Ms200_customer.class);
            userId = getMaxUserId();
            ms200Customer.setUser_id(userId);
            ms200Customer.setUsekb("1");
            ms200Customer.setYoto("1");
            ms200Customer.setUpd_date(date);
            ms200Customer.setIns_date(date);
            ms200Customer.setUpd_usr(name);
            ms200Customer.setIns_usr(name);
            ms200Customer.setDel_flg(0);
            PasswordHelper.encryptPassword(ms200Customer);
            // 将用户加入用户表
            try {
                loginDao.register(ms200Customer);
            } catch (Exception e) {
                logger.error("用户表插入失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
            }
            jsonObject.put("user_id", userId);
            // 给用户添加user的权限
            jsonObject.put("authority_cd", 2);
            jsonObject.put("authority_kb", 0);
            try {
                warehouseCustomerDao.insertCustomerAuth(jsonObject);
            } catch (Exception e) {
                logger.error("给用户添加权限失败, 用户Id为：" + jsonObject.getString("user_id"));
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
            }
        }
        // 将用户加入到 店舗別顧客マスタ
        clientDao.insertClientCustomer(jsonObject.getString("client_id"), userId);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject userIdList
     * @description: 删除属于该店铺的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/27
     */

    @Override
    public JSONObject deleteClientUser(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("userIdList");
        ArrayList<String> userIdList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            userIdList.add(String.valueOf(jsonArray.get(i)));
        }

        userIdList.stream().forEach(userId -> {
            List<Mw400_warehouse> warehouseInfoByUserId = loginDao.getWarehouseInfoByUserId(userId);
            if (warehouseInfoByUserId.size() == 0) {
                try {
                    warehouseCustomerDao.deleteUserInfo(userId);
                } catch (Exception e) {
                    logger.error("根据用户Id删除用户失败, 用户Id为：" + userId);
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
                }
                try {
                    warehouseCustomerDao.deleteCustomerAuthByUserId(userId);
                } catch (Exception e) {
                    logger.error("删除用户权限失败");
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
                }
            }
        });
        // 删除店铺和用户的关联
        clientDao.deleteClientCustomer(jsonObject.getString("client_id"), userIdList);

        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @description: 获取依頼主マスタ模板PDF
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/29
     */
    @Override
    public JSONObject getMasterPdf(JSONObject jsonObject, Integer flag) {
        String sponsorId = jsonObject.getString("sponsor_id");
        String clientId = jsonObject.getString("client_id");
        Mc105_product_setting productSetting = productSettingDao.getProductSetting(clientId, null);
        Integer tax = productSetting.getTax();
        String product_tax = (tax == 1) ? "税抜" : "税込";
        jsonObject.put("product_tax", product_tax);
        // 総計通常税率金额
        jsonObject.put("totalWithNormalTaxPrice", 10);
        // 総計軽減税率金额
        jsonObject.put("totalWithReducedTaxPrice", 0);
        jsonObject.put("total_with_normal_tax", 110);
        jsonObject.put("total_with_reduced_tax", 0);
        Integer version = productSetting.getVersion();
        if (tax == 0) {
            jsonObject.put("tax", 0);
            jsonObject.put("total_amount", "100");
        } else {
            jsonObject.put("tax", 10);
            jsonObject.put("total_amount", "110");
        }

        Integer deliveryNoteType = jsonObject.getInteger("delivery_note_type");
        if (!StringTools.isNullOrEmpty(deliveryNoteType) && deliveryNoteType == 0) {
            jsonObject.put("delivery_note_type", Constants.DON_T_WANT_TO_SHARE_THE_BOOK);
        } else {
            jsonObject.put("delivery_note_type", "同梱する");
        }

        Integer price_on_delivery_note = 0;
        if (flag == 1) {
            List<Ms012_sponsor_master> deliveryListOne = settingDao.getDeliveryListOne(clientId, sponsorId);
            Ms012_sponsor_master ms012_sponsor_master = deliveryListOne.get(0);
            price_on_delivery_note = ms012_sponsor_master.getPrice_on_delivery_note();
            if (ms012_sponsor_master.getDelivery_note_type() == 0) {
                jsonObject.put("delivery_note_type", Constants.DON_T_WANT_TO_SHARE_THE_BOOK);
            }
            jsonObject.put("company", "");
            jsonObject.put("order_no", "S000000001");
            jsonObject.put("handling_charge", 0);
            String detail_logo = ms012_sponsor_master.getDetail_logo();
            String logoPath = pathProps.getRoot() + detail_logo;
            jsonObject.put("detail_logo", logoPath);
            jsonObject.put("form", 2);
            jsonObject.put("postcode", ms012_sponsor_master.getPostcode());
            jsonObject.put("subtotal_amount", 100);
            jsonObject.put("product_kind_plan_cnt", 1);
            jsonObject.put("delivery_charge", 0);
            jsonObject.put("cash_on_delivery", 1);
            Integer contact = ms012_sponsor_master.getContact();
            String value = CommonUtils.getContact(contact);
            jsonObject.put("contact", value);
            jsonObject.put("sponsorEmail", ms012_sponsor_master.getEmail());
            jsonObject.put("sponsorFax", ms012_sponsor_master.getFax());
            jsonObject.put("sponsorPhone", ms012_sponsor_master.getPhone());
            if (contact == 1) {
                jsonObject.put("contact_info", ms012_sponsor_master.getEmail());
            } else {
                jsonObject.put("contact_info", ms012_sponsor_master.getPhone());
            }
            jsonObject.put("phone", ms012_sponsor_master.getPhone());
            jsonObject.put("prefecture", ms012_sponsor_master.getPrefecture());
            jsonObject.put("surname", "サンロジ太郎");
            jsonObject.put("name", ms012_sponsor_master.getName());
            jsonObject.put("sponsor_company", ms012_sponsor_master.getCompany());
            jsonObject.put("address1", ms012_sponsor_master.getAddress1());
            jsonObject.put("address2", ms012_sponsor_master.getAddress2());
            jsonObject.put("detail_message", ms012_sponsor_master.getDetail_message());

            JSONArray jsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("product_plan_cnt", 1);
            json.put("is_reduced_tax", 0);
            json.put("unit_price", 100);
            json.put("name", "商品名");
            json.put("code", "ST-001");
            if (tax == 0) {
                json.put("price", 100);
            } else {
                json.put("price", 110);
            }
            json.put("kubun", 0);
            jsonArray.add(json);
            jsonObject.put("items", jsonArray);
        } else {
            price_on_delivery_note = jsonObject.getInteger("price_on_delivery_note");
            if (!StringTools.isNullOrEmpty(deliveryNoteType) && deliveryNoteType == 0) {
                price_on_delivery_note = 0;
            }
            jsonObject.put("company", "");
            jsonObject.put("order_no", "S000000001");
            JSONArray checkList = jsonObject.getJSONArray("checkList");
            jsonObject.put("handling_charge", 0);
            // a图片路径
            String uploadPath = pathProps.getRoot() + pathProps.getImage() + "white.jpg";
            jsonObject.put("detail_logo", uploadPath);
            jsonObject.put("form", 2);
            jsonObject.put("postcode", jsonObject.getString("postcode"));
            jsonObject.put("subtotal_amount", 100);
            jsonObject.put("product_kind_plan_cnt", 1);
            jsonObject.put("delivery_charge", 0);
            jsonObject.put("cash_on_delivery", 1);
            Integer contact = jsonObject.getInteger("contact");
            StringBuilder builder = new StringBuilder("0000");
            if (contact != 0) {
                StringBuilder name = new StringBuilder();
                for (int i = 0; i < checkList.size(); i++) {
                    name.append(checkList.getString(i));
                }
                if (name.toString().contains("check1")) {
                    builder.replace(1, 2, "1");
                }
                if (name.toString().contains("check2")) {
                    builder.replace(2, 3, "1");
                }
                if (name.toString().contains("check3")) {
                    builder.replace(3, 4, "1");
                }
            }
            String value = builder.substring(1, 4);
            if (value.equals("000")) {
                value = "0";
            }
            jsonObject.put("contact", value);
            jsonObject.put("sponsorEmail", jsonObject.getString("email"));
            jsonObject.put("sponsorFax", jsonObject.getString("fax"));
            jsonObject.put("sponsorPhone", jsonObject.getString("phone"));
            if (contact == 1) {
                jsonObject.put("contact_info", jsonObject.getString("email"));
            } else {
                jsonObject.put("contact_info", jsonObject.getString("phone"));
            }
            jsonObject.put("phone", jsonObject.getString("phone"));
            jsonObject.put("prefecture", jsonObject.getString("prefecture"));
            jsonObject.put("surname", "サンロジ太郎");
            jsonObject.put("name", jsonObject.getString("name"));
            jsonObject.put("sponsor_company", jsonObject.getString("company"));
            jsonObject.put("address1", jsonObject.getString("address1"));
            jsonObject.put("address2", jsonObject.getString("address2"));
            jsonObject.put("detail_message", jsonObject.getString("detail_message"));

            JSONArray jsonArray = new JSONArray();
            JSONObject json = new JSONObject();
            json.put("product_plan_cnt", 1);
            json.put("is_reduced_tax", 0);
            json.put("unit_price", 100);
            json.put("name", "商品名");
            json.put("code", "ST-001");
            if (tax == 0) {
                json.put("price", 100);
            } else {
                json.put("price", 110);
            }
            json.put("kubun", 0);


            jsonArray.add(json);
            jsonObject.put("items", jsonArray);
        }
        String codeName = "S000000001";
        String codePath = pathProps.getRoot() + pathProps.getStore() + DateUtils.getDateMonth() + "/code/" + codeName;
        String pdfName = clientId + "-" + "S000000001-" + System.currentTimeMillis() + ".pdf";
        String relativePath = pathProps.getStore() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
        try {
            if (StringTools.isNullOrEmpty(price_on_delivery_note)) {
                price_on_delivery_note = 0;
            }
            // 新版
            // 金额印字设定
            if (price_on_delivery_note == 1) {
                PdfTools.createNewShipmentPricePdf(jsonObject, codePath, pdfPath);
            } else {
                PdfTools.createNewShipmentsPDF(jsonObject, codePath, pdfPath);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @description: 获取最大用户Id
     * @return: java.lang.String
     * @date: 2020/07/13
     */
    public String getMaxUserId() {
        return loginDao.getMaxUserId() == null ? "1" : String.valueOf(Integer.valueOf(loginDao.getMaxUserId()) + 1);
    }

    /**
     * @Param: userIdList
     * @description: 获取用户信息
     * @return: java.util.List<com.lemonico.common.bean.Ms200_customer>
     * @date: 2020/8/27
     */
    public List<Ms200_customer> getUserData(List<String> userIdList) {
        List<Ms200_customer> userInfoByUserId;
        try {
            userInfoByUserId = warehouseCustomerDao.getUserInfoByUserId(userIdList);
        } catch (Exception e) {
            logger.error("根据用户Id查询用户信息失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return userInfoByUserId;
    }

    /**
     * @throws Exception
     * @Description: CSV配送先マスタ登録をアップロードする
     * @Param:
     * @return: String
     * @Date: 2020/12/07
     */
    @Override
    public JSONObject recipientsCsvUpload(String client_id, HttpServletRequest req, MultipartFile file) {
        CsvReader csvReader1 = null;
        CsvReader csvReader2 = null;
        try {
            String fileName = System.currentTimeMillis() + file.getOriginalFilename();
            // 获取当前项目的真实路径
            String path = req.getServletContext().getRealPath("");
            String realPath = (String) path.subSequence(0, path.length() - 7);
            // a获取当前的年份+月份
            Calendar date = Calendar.getInstance();
            int year = Integer.valueOf(date.get(Calendar.YEAR));
            int month = date.get(Calendar.MONTH) + 1;
            String datePath = year + "" + month;
            // a拼接图片保存路径
            String destFileName = realPath + "resources" + File.separator + "static" + File.separator + "csv"
                + File.separator + datePath + File.separator + fileName;
            // a第一次运行的时候创建文件夹
            File destFile = new File(destFileName);
            destFile.getParentFile().mkdirs();
            // a把浏览器上传的文件复制到目标路径
            file.transferTo(destFile);
            // 验证编码格式
            if (!CommonUtils.determineEncoding(destFile.toURI().toURL(), new String[] {
                "SHIFT_JIS"
            })) {
                throw new PlBadRequestException("ご指定のCSVファイルが、取り扱いできる形式（SHIFT-JIS）ではありません。");
            }
            // a错误信息list
            List<String> list = new ArrayList<String>();
            // a读取上传的CSV文件
            InputStreamReader isr = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            CsvReader csvReader = new CsvReader(isr);
            int num = 0;
            int count = 0;
            boolean flag = true;
            while (csvReader.readRecord()) {
                if (StringTools.isNullOrEmpty(csvReader.getRawRecord())) {
                    continue;
                }
                num++;
                if (num == 1) {
                    String header = csvReader.getRawRecord().replaceAll("\"", "");
                    if (!header
                        .equals("事業形態(1:法人 2:個人),会社名(法人場合必須),部署,お名前(個人場合必須),郵便番号,都道府県,住所,マンション・ビル名,電話番号,メールアドレス")) {
                        throw new PlBadRequestException("項目名称に不備があります。");
                    }
                }
                if (num > 1000) {
                    throw new PlBadRequestException("一度に登録できるデータは最大1000件です。");
                }
            }
            if (num <= 1) {
                throw new PlBadRequestException("CSVファイルにデータは空にしてはいけません。");
            }
            csvReader.close();
            num = 0;
            String mail_regex =
                "^([a-zA-Z0-9])+([a-zA-Z0-9\\._-])*@([a-zA-Z0-9])+([a-zA-Z0-9\\._-]*)\\.([a-zA-Z0-9])+([a-zA-Z0-9\\._-]?)+([a-zA-Z0-9]+)+$";
            String phone_regex = "^0([0-9]{1,3})-([0-9]{1,4})-([0-9]{1,4})";
            String postcode_regex = "^([0-9]{3})-([0-9]{4})";
            // a验证入库信息
            InputStreamReader isr1 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            csvReader1 = new CsvReader(isr1);
            csvReader1.readHeaders();
            while (csvReader1.readRecord()) {
                String tmp = csvReader1.getRawRecord();
                if (StringTools.isNullOrEmpty(tmp)) {
                    continue;
                }
                // count计算创建的数组长度
                count++;
                String[] params = tmp.split(",", -1);
                num++;
                int k = 0;
                String form = "";
                // 事業形態(1:法人 2:個人)
                form = params[k].replaceAll("\"", "");
                if (StringTools.isNullOrEmpty(form)) {
                    form = "2";
                    params[k] = "2";
                }
                if (CommonUtils.toInteger(form) < 1 && CommonUtils.toInteger(form) > 2) {
                    list.add("[" + (num + 1) + "行目] : 事業形態(1:法人 2:個人)の設定値は半角数字(0、1）のみし入力できません。");
                    flag = false;
                }
                k++;
                // 会社名(法人場合必須)
                if (StringTools.isNullOrEmpty(params[k].replaceAll("\"", "")) && (form == "1" || form.equals("1"))) {
                    list.add("[" + (num + 1) + "行目] : 会社名(法人場合必須)は空であってはいけません。");
                    flag = false;
                }
                k++;
                // 部署
                if (StringTools.isNullOrEmpty(params[k].replaceAll("\"", "")) && params[k].length() > 30) {
                    list.add("[" + (num + 1) + "行目] : 部署は、100文字以内でご入力ください。");
                    flag = false;
                }
                k++;
                // お名前(個人場合必須)
                if (StringTools.isNullOrEmpty(params[k].replaceAll("\"", "")) && (form == "2" || form.equals("2"))) {
                    list.add("[" + (num + 1) + "行目] : お名前(個人場合必須)は空であってはいけません。");
                    flag = false;
                }
                k++;
                // 郵便番号
                if (StringTools.isNullOrEmpty(params[k].replaceAll("\"", ""))) {
                    list.add("[" + (num + 1) + "行目] : 郵便番号は空であってはいけません。");
                    flag = false;
                } else {
                    if (params[k].replaceAll("\"", "").matches(postcode_regex) == false) {
                        list.add("[" + (num + 1) + "行目] : 郵便番号はの形式が正しくありません。例:000-0000。");
                        flag = false;
                    }
                }
                k++;
                // 都道府県
                if (StringTools.isNullOrEmpty(params[k].replaceAll("\"", ""))) {
                    list.add("[" + (num + 1) + "行目] : 都道府県は空であってはいけません。");
                    flag = false;
                }
                k++;
                // 住所
                if (StringTools.isNullOrEmpty(params[k].replaceAll("\"", ""))) {
                    list.add("[" + (num + 1) + "行目] : 住所は空であってはいけません。");
                    flag = false;
                }
                k++;
                // マンション・ビル名
                k++;
                // 電話番号
                if (!StringTools.isNullOrEmpty(params[k].replaceAll("\"", ""))) {
                    if (params[k].replaceAll("\"", "").matches(phone_regex) == false) {
                        list.add("[" + (num + 1) + "行目] : 電話番号はの形式が正しくありません。例:000-0000-0000。");
                        flag = false;
                    }
                }
                k++;
                // メールアドレス
                if (!StringTools.isNullOrEmpty(params[k].replaceAll("\"", ""))) {
                    if (params[k].replaceAll("\"", "").matches(mail_regex) == false) {
                        list.add("[" + (num + 1) + "行目] : メールアドレスはの形式が正しくありません。");
                        flag = false;
                    }
                }
            }
            // a如验证不通过则抛出异常
            if (!flag) {
                String json = JSON.toJSONString(list);
                throw new PlBadRequestException(json);
            }
            num = 0;
            // 获取用户名
            String loginNm = CommonUtils.getToken("login_nm", req);
            InputStreamReader isr2 = new InputStreamReader(new FileInputStream(destFile), "SJIS");
            csvReader2 = new CsvReader(isr2);
            csvReader2.readHeaders();
            while (csvReader2.readRecord()) {
                String tmp = csvReader2.getRawRecord();
                if (StringTools.isNullOrEmpty(tmp)) {
                    continue;
                }
                Date updateDate = DateUtils.getDate();
                Mc200_customer_delivery customer_delivery = new Mc200_customer_delivery();
                customer_delivery.setClient_id(client_id);
                customer_delivery.setUpd_date(updateDate);
                customer_delivery.setUpd_usr(loginNm);
                customer_delivery.setIns_date(updateDate);
                customer_delivery.setIns_usr(loginNm);
                customer_delivery.setDel_flg(0);
                // count计算创建的数组长度
                String[] params = tmp.split(",", -1);
                num++;
                int k = 0;
                String form = "";
                // 事業形態(1:法人 2:個人)
                form = params[k].replaceAll("\"", "");
                if (StringTools.isNullOrEmpty(form)) {
                    form = "2";
                }
                customer_delivery.setForm(CommonUtils.toInteger(form));
                k++;
                // 会社名(法人場合必須)
                customer_delivery.setCompany(params[k].replaceAll("\"", ""));
                k++;
                // 部署
                customer_delivery.setDivision(params[k].replaceAll("\"", ""));
                k++;
                // お名前(個人場合必須)
                customer_delivery.setName(params[k].replaceAll("\"", ""));
                k++;
                // 郵便番号
                customer_delivery.setPostcode(params[k].replaceAll("\"", ""));
                k++;
                // 都道府県
                customer_delivery.setPrefecture(params[k].replaceAll("\"", ""));
                k++;
                // 住所
                customer_delivery.setAddress1(params[k].replaceAll("\"", ""));
                k++;
                // マンション・ビル名
                customer_delivery.setAddress2(params[k].replaceAll("\"", ""));
                k++;
                // 電話番号
                customer_delivery.setPhone(params[k].replaceAll("\"", ""));
                k++;
                // メールアドレス
                customer_delivery.setEmail(params[k].replaceAll("\"", ""));
                customer_delivery.setDelivery_method("1");
                customer_delivery.setDelivery_note_type("0");
                customer_delivery.setPrice_on_delivery_note(0);
                customer_delivery.setBox_delivery(0);
                customer_delivery.setFragile_item(0);
                customer_delivery.setCushioning_unit("0");
                customer_delivery.setGift_wrapping_unit("0");
                settingDao.createCustomerDeliveryList(customer_delivery);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.CSV_UPLOAD_FAILED);
        } catch (IOException e) {
            e.printStackTrace();
            return CommonUtils.failure(ErrorCode.CSV_UPLOAD_FAILED);
        } finally {
            if (csvReader1 != null) {
                csvReader1.close();
            }
            if (csvReader2 != null) {
                csvReader2.close();
            }
        }
        return CommonUtils.success();
    }

    /**
     * @param jsonObject
     * @Description: 添加客户自定义csv数据模板
     *               @Date： 2021/5/8
     * @Param：
     * @return：
     */
    @Override
    public JSONObject insertCsvCustom(JSONObject jsonObject, HttpServletRequest servletRequest) {

        Date nowTime = DateUtils.getDate();
        String login_nm = CommonUtils.getToken("login_nm", servletRequest);

        Tc209_setting_template tc209 = new Tc209_setting_template();
        tc209.setWarehouse_cd(jsonObject.getString("warehouse_cd"));
        tc209.setClient_id(jsonObject.getString("client_id"));
        tc209.setTemplate_nm(jsonObject.getString("template_nm"));
        tc209.setYoto_id(jsonObject.getString("yoto_id"));
        tc209.setEncoding(jsonObject.getString("encoding"));
        tc209.setConstant(jsonObject.getString("constant"));
        tc209.setData(jsonObject.getString("data"));
        tc209.setIns_usr(login_nm);
        tc209.setIns_date(nowTime);
        tc209.setUpd_usr(login_nm);
        tc209.setUpd_date(nowTime);

        Integer template_cd = jsonObject.getInteger("template_cd");
        if (!StringTools.isNullOrEmpty(template_cd)) {
            tc209.setTemplate_cd(template_cd);
            settingDao.updateCsvCustom(tc209);
        } else {
            // 新规
            settingDao.insertCsvCustom(tc209);
        }
        return CommonUtils.success();
    }

    /**
     * @param jsonObject
     * @description: 生成纳品书-作业指示书
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/8 15:47
     */
    @Override
    public JSONObject getMasterDetailPdf(JSONObject jsonObject) {
        String sponsorId = jsonObject.getString("sponsor_id");
        String clientId = jsonObject.getString("client_id");
        Mc105_product_setting productSetting = productSettingDao.getProductSetting(clientId, null);
        Integer tax = productSetting.getTax();
        String product_tax = (tax == 1) ? "税抜" : "税込";
        jsonObject.put("product_tax", product_tax);
        // 総計通常税率金额
        jsonObject.put("totalWithNormalTaxPrice", 10);
        // 総計軽減税率金额
        jsonObject.put("totalWithReducedTaxPrice", 0);
        jsonObject.put("total_with_normal_tax", 110);
        jsonObject.put("total_with_reduced_tax", 0);
        if (tax == 0) {
            jsonObject.put("tax", 0);
            jsonObject.put("total_amount", "100");
        } else {
            jsonObject.put("tax", 10);
            jsonObject.put("total_amount", "110");
        }
        List<Ms012_sponsor_master> deliveryListOne = settingDao.getDeliveryListOne(clientId, sponsorId);
        Ms012_sponsor_master ms012_sponsor_master = deliveryListOne.get(0);
        jsonObject.put("company", "");
        jsonObject.put("order_no", "S000000001");
        jsonObject.put("handling_charge", 0);
        String detail_logo = ms012_sponsor_master.getDetail_logo();
        String logoPath = pathProps.getRoot() + detail_logo;
        jsonObject.put("detail_logo", logoPath);
        jsonObject.put("form", 2);
        jsonObject.put("masterPostcode", ms012_sponsor_master.getPostcode());
        jsonObject.put("subtotal_amount", 100);
        jsonObject.put("product_kind_plan_cnt", 1);
        jsonObject.put("delivery_charge", 0);
        jsonObject.put("cash_on_delivery", 1);
        Integer contact = ms012_sponsor_master.getContact();
        String value = CommonUtils.getContact(contact);
        jsonObject.put("contact", value);
        jsonObject.put("sponsorEmail", ms012_sponsor_master.getEmail());
        jsonObject.put("sponsorFax", ms012_sponsor_master.getFax());
        jsonObject.put("sponsorPhone", ms012_sponsor_master.getPhone());
        if (contact == 1) {
            jsonObject.put("contact_info", ms012_sponsor_master.getEmail());
        } else {
            jsonObject.put("contact_info", ms012_sponsor_master.getPhone());
        }
        jsonObject.put("phone", ms012_sponsor_master.getPhone());
        jsonObject.put("prefecture", ms012_sponsor_master.getPrefecture());
        jsonObject.put("surname", "サンロジ太郎");
        jsonObject.put("postcode", "0000000");
        jsonObject.put("address1", "XXXXXXX");
        jsonObject.put("address2", "XXXXXXX");


        jsonObject.put("name", ms012_sponsor_master.getName());
        jsonObject.put("sponsor_company", ms012_sponsor_master.getCompany());
        jsonObject.put("masterAddress1", ms012_sponsor_master.getAddress1());
        jsonObject.put("masterAddress2", ms012_sponsor_master.getAddress2());
        jsonObject.put("detail_message", ms012_sponsor_master.getDetail_message());
        jsonObject.put("masterPrefecture", ms012_sponsor_master.getPrefecture());

        jsonObject.put("order_name", "-");
        jsonObject.put("order_todoufuken", "");
        jsonObject.put("order_address1", "");
        jsonObject.put("order_address2", "");
        jsonObject.put("order_zip_code1", "");
        jsonObject.put("order_zip_code2", "");

        // 是否显示金额
        jsonObject.put("price_on_delivery_note", ms012_sponsor_master.getPrice_on_delivery_note());

        JSONArray jsonArray = new JSONArray();
        JSONObject json = new JSONObject();
        json.put("product_plan_cnt", 1);
        json.put("is_reduced_tax", 0);
        json.put("unit_price", 100);
        json.put("client_id", "RS001");
        json.put("product_id", "P000001");
        json.put("name", "商品名");
        json.put("locationName", "NO-LOCATION");
        json.put("code", "ST-001");
        if (tax == 0) {
            json.put("price", 100);
        } else {
            json.put("price", 110);
        }
        json.put("bundled_flg", 0);
        jsonArray.add(json);
        jsonObject.put("items", jsonArray);
        Ms201_client clientInfo = clientDao.getClientInfo(clientId);
        jsonObject.put("client_nm", clientInfo.getClient_nm());
        jsonObject.put("instructions_special_notes", "出荷指示特記事項1");
        jsonObject.put("bikou1", "");
        jsonObject.put("payment_method_name", "銀行振込");
        jsonObject.put("bikou2", "");
        jsonObject.put("bikou3", "");
        jsonObject.put("shipping_date", "2021/01/01");
        jsonObject.put("delivery_date", "2021/01/01");
        jsonObject.put("bikou4", "");
        jsonObject.put("method", "");
        jsonObject.put("bikou5", "");
        jsonObject.put("bikou6", "");
        jsonObject.put("delivery_time_slot", "12時～14時");
        jsonObject.put("bikou7", "");
        jsonObject.put("cushioning_unit", "");
        jsonObject.put("gift_wrapping_unit", "");
        jsonObject.put("setItems", jsonArray);
        JSONArray bundled = new JSONArray();
        jsonObject.put("bundled", bundled);

        Integer deliveryNoteType = ms012_sponsor_master.getDelivery_note_type();
        if (!StringTools.isNullOrEmpty(deliveryNoteType) && deliveryNoteType == 0) {
            jsonObject.put("delivery_note_type", Constants.DON_T_WANT_TO_SHARE_THE_BOOK);
        } else {
            jsonObject.put("delivery_note_type", "納品書同梱");
        }

        jsonObject.put("order_name", "-");
        jsonObject.put("order_todoufuken", "");
        jsonObject.put("order_address1", "");
        jsonObject.put("order_address2", "");
        jsonObject.put("order_zip_code1", "");
        jsonObject.put("order_zip_code2", "");

        String codeName = "S000000001";
        String codePath = pathProps.getRoot() + pathProps.getStore() + DateUtils.getDateMonth() + "/code/" + codeName;
        BarcodeUtils.generateCode128Barcode(codeName, codePath, Constants.WIDTH_3);
        jsonObject.put("codePath", codePath);

        String pdfName = CommonUtils.getPdfName(jsonObject.getString("client_id"), "shipment", "detail", "S000000001");
        String relativePath = pathProps.getStore() + DateUtils.getDateMonth() + "/" + pdfName;
        String pdfPath = pathProps.getRoot() + relativePath;
        try {
            PdfTools.createStoreShipmentsDetailPDF(jsonObject, pdfPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CommonUtils.success(relativePath);
    }

    /**
     * @param sponsor_id
     * @Description: // 根据依赖主ID 删除依赖主master
     *               @Date： 2021/6/18
     * @Param：sponsor_id
     * @return：boolean
     */
    @Override
    public void deleteSponsor(String client_id, String sponsor_id) {
        settingDao.deleteSponsor(client_id, sponsor_id);
    }

    /**
     * @description: 获取csv受注类型
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/24 10:39
     */
    @Override
    public JSONObject getCsvTmp() {
        List<Ms017_csv_template> csvTmp = settingDao.getCsvTmp();
        JSONArray jsonArray = new JSONArray();
        for (Ms017_csv_template csvTemplate : csvTmp) {
            JSONObject json = new JSONObject();
            json.put("template", csvTemplate.getTemplate());
            json.put("identification", csvTemplate.getIdentification());
            jsonArray.add(json);
        }
        return CommonUtils.success(jsonArray);
    }
}
