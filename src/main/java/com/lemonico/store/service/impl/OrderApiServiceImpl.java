package com.lemonico.store.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ApiErrorDao;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.HttpUtils;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.XmlUtil;
import com.lemonico.core.utils.constants.BizLogiResEnum;
import com.lemonico.core.utils.constants.Constants;
import com.lemonico.ntm.bean.CheckAddressRequest;
import com.lemonico.ntm.bean.CheckAddressResponse;
import com.lemonico.store.dao.OrderApiDao;
import com.lemonico.store.dao.OrderDao;
import com.lemonico.store.dao.SponsorDao;
import com.lemonico.store.service.OrderApiService;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @className: ApiServiceImpl
 * @description: TODO 类描述
 * @date: 2020/9/9 12:36
 **/
@Service
public class OrderApiServiceImpl implements OrderApiService
{

    private final static Logger logger = LoggerFactory.getLogger(OrderApiServiceImpl.class);

    private final String tokenUrl = "https://auth.login.yahoo.co.jp/yconnect/v2/token";

    // アクセストークンに交換用URL
    private final String COLOR_ME_ACCESS_TOKEN_URL = "https://api.shop-pro.jp/oauth/token";

    // リダイレクトURL TODO 暫定対策：本番環境リリースする際に、URL変更する 恒久対策：application.ymlファイルにこのカラムを定義する
    // private final String REDIRECT_URL = "https://sunlogi.com/store/setting/api_auth";
    // private final String REDIRECT_URL = "http://localhost/store/setting/api_auth";
    // private final String REDIRECT_URL = "http://stg.sunlogi.com/store/setting/api_auth";

    // グラント種別
    private final String GRANT_TYPE = "authorization_code";

    @Resource
    private OrderApiDao orderApiDao;

    @Value("${baseUrl}")
    private String baseUrl;

    @Resource
    private OrderDao orderDao;

    @Resource
    private SponsorDao sponsorDao;

    @Resource
    private ApiErrorDao apiErrorDao;

    @Resource
    private ClientDao clientDao;

    @Value("${bizApiHost}")
    private String bizApiHost;

    @Value("${bizAuthCustomId}")
    private String bizAuthCustomId;

    @Value("${bizAuthCustomPwd}")
    private String bizAuthCustomPwd;

    /**
     * @description: 外部API連携(送り状連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @author: wang
     * @date: 2020/9/9
     */
    @Override
    public List<Tc203_order_client> getAllDataDelivery(String template) {
        // 外部API連携(送り状連携)
        return orderApiDao.getAllDataDelivery(template);
    }

    /**
     * @description: 外部API連携(受注連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @author: wang
     * @date: 2020/9/9
     */
    @Override
    public List<Tc203_order_client> getAllDataOrder(String template) {
        // 外部API連携(受注連携)
        return orderApiDao.getAllDataOrder(template);
    }

    /**
     * @description: 外部API連携(在庫連携)
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @author: wang
     * @date: 2020/9/9
     */
    @Override
    public List<Tc203_order_client> getAllDataStock(String template) {
        // 外部API連携(在庫連携)
        return orderApiDao.getAllDataStock(template);
    }

    /**
     * @description: 获取所有和shopify连携的信息
     * @return: java.util.List<com.lemonico.common.bean.Tc203_order_client>
     * @date: 2020/9/9
     */
    @Override
    public List<Tc203_order_client> getAllData(String template) {
        // 获取所有和shopify连携的信息
        return orderApiDao.getAllData(template);
    }

    /**
     * @Param: apiName
     * @param: client_id 店铺Id
     * @description: 根据id 和 client_id 查询以前是否存在
     * @return: com.lemonico.common.bean.Tc203_order_client
     * @date: 2020/9/9
     */
    @Override
    public Tc203_order_client getOrderClientInfoById(String apiName, String client_id, String template) {
        return orderApiDao.getOrderClientInfoById(apiName, client_id, null, template);
    }

    /**
     * @Param: jsonObject
     * @description: 新规店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    @Override
    public Integer insertOrderClient(JSONObject jsonObject) {
        return orderApiDao.insertOrderClient(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 修改店铺受注关系数据
     * @return: java.lang.Integer
     * @date: 2020/9/9
     */
    @Override
    public Integer updateOrderClient(JSONObject jsonObject) {
        return orderApiDao.updateOrderClient(jsonObject);
    }


    /**
     * @Param: client_id
     * @param: template
     * @Param: apiName
     * @description: 根据模板查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/11
     */
    @Override
    public JSONObject getOrderClientInfo(String client_id, String template, String apiName) {
        Tc203_order_client orderClientInfo = orderApiDao.getOrderClientInfo(client_id, template, apiName);
        if (orderClientInfo != null) {
            // 转换过期时间
            Date expire_date = orderClientInfo.getExpire_date();
            String newDate = "";
            try {
                newDate = CommonUtils.getNewDate(expire_date);
            } catch (Exception e) {
                newDate = "";
            }
            orderClientInfo.setStringExpireDate(newDate);
        }
        return CommonUtils.success(orderClientInfo);
    }

    /**
     * @Param: client_id
     * @description: 查询店铺受注关系表
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    @Override
    public JSONObject getOrderClientList(String client_id) {
        List<Tc203_order_client> orderClientList = orderApiDao.getOrderClientList(client_id);

        // 查询出依赖主的信息
        Map<String, List<Ms012_sponsor_master>> sponsorMap = new HashMap<>();
        if (orderClientList.size() != 0) {
            List<String> sponsorIds =
                orderClientList.stream().map(Tc203_order_client::getSponsor_id).distinct().collect(Collectors.toList());
            List<Ms012_sponsor_master> sponsorMasters = sponsorDao.getSponsorListById(sponsorIds);

            if (sponsorMasters != null && sponsorMasters.size() != 0) {
                sponsorMap = sponsorMasters.stream()
                    .collect(Collectors.groupingBy(Ms012_sponsor_master::getSponsor_id));
            }
        }

        // List<Ms018_api_error> clientApiError = apiErrorDao.getClientApiError(client_id, 5);
        // //查询api报错在五次以上的信息
        Map<Integer, List<Ms018_api_error>> apiErrorMap = new HashMap<>();
        // if (clientApiError != null && clientApiError.size() != 0) {
        // apiErrorMap = clientApiError.stream()
        // .collect(Collectors.groupingBy(Ms018_api_error::getOrder_id));
        // }

        // 获取当前时间
        LocalDate now = LocalDate.now();

        JSONArray jsonArray = new JSONArray();
        for (Tc203_order_client orderClient : orderClientList) {
            JSONObject json = new JSONObject();

            json.put("id", orderClient.getId());
            // API名称
            json.put("api_name", orderClient.getApi_name());
            // テンプレート
            json.put("template", orderClient.getTemplate());
            // 依頼主ID
            String sponsor_id = orderClient.getSponsor_id();

            // 获取依赖主名称
            String name = "";
            if (sponsorMap.containsKey(sponsor_id)) {
                List<Ms012_sponsor_master> sponsorMasters = sponsorMap.get(sponsor_id);
                name = sponsorMasters.get(0).getName();
            }
            // お名前
            json.put("name", name);
            // 出庫依頼連携
            json.put("shipment_status", orderClient.getShipment_status());
            // 受注連携設定
            json.put("order_status", orderClient.getOrder_status());
            // 送り状連携設定
            json.put("delivery_status", orderClient.getDelivery_status());
            json.put("stock_status", orderClient.getStock_status());
            int errFlg = 0;
            if (apiErrorMap.containsKey(orderClient.getId())) {
                errFlg = 1;
            }

            int expireFlg = 1;// 判断api设定是否过期
            // 判断api设定是否快过期
            int expireDateFlg = 0;
            if (!StringTools.isNullOrEmpty(orderClient.getExpire_date())) {
                Date expireDate = orderClient.getExpire_date();

                LocalDate future = expireDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                long between = ChronoUnit.DAYS.between(now, future);

                // 判断api设定的截止日期是否过期
                if (now.isBefore(future)) {
                    expireFlg = 1;
                } else {
                    expireFlg = 0;// 过期了
                }
                if (now.isBefore(future) && between <= 7.0) {
                    expireDateFlg = 1;
                }
            }
            json.put("expireFlg", expireFlg);
            json.put("expireDateFlg", expireDateFlg);
            // 报错状态
            json.put("errFlg", errFlg);
            jsonArray.add(json);

        }
        return CommonUtils.success(jsonArray);
    }

    /**
     * @description: 获取最大Id
     * @Param: getMaxId
     * @return: java.lang.Integer
     * @date: 2020/9/16
     */
    @Override
    public Integer getMaxId(String clientId) {
        return orderApiDao.getMaxId(clientId);
    }

    /**
     * @Param: jsonObject
     * @description: 删除Api设定数据
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/16
     */
    @Override
    @Transactional
    public JSONObject deleteApiSet(JSONObject jsonObject) {
        try {
            orderApiDao.deleteApiSet(jsonObject);
        } catch (Exception e) {
            logger.error("删除Api设定数据失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        // 根据 api设定id 和 店铺id 获取api报错次数信息
        List<Ms018_api_error> apiErrorInfo =
            apiErrorDao.getApiErrorInfo(jsonObject.getString("id"), jsonObject.getString("client_id"));
        if (apiErrorInfo.size() != 0) {
            List<Integer> idList =
                apiErrorInfo.stream().map(Ms018_api_error::getId).distinct().collect(Collectors.toList());
            if (!StringTools.isNullOrEmpty(idList) && idList.size() != 0) {
                try {
                    apiErrorDao.deleteApiErrorInfo(idList);
                } catch (Exception e) {
                    logger.error("删除API报错信息失败 id={}", idList);
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
                }
            }
        }

        return CommonUtils.success();
    }

    @Override
    public Tc206_order_ftp getFtpClientInfoById(String ftp_host, String clientId, Integer get_send_flg) {
        return orderApiDao.getFtpClientInfoById(ftp_host, clientId, get_send_flg);
    }

    /**
     * @Param: jsonObject
     * @description: 新规店铺FTP情報
     * @return: java.lang.Integer
     * @author: HZM
     * @date: 2020/10/19
     */
    @Override
    public Integer insertFtpClient(JSONObject jsonObject) {
        return orderApiDao.insertFtpClient(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 修改店铺FTP情報
     * @return: java.lang.Integer
     * @author: HZM
     * @date: 2020/10/19
     */
    @Override
    public Integer updateFtpClient(JSONObject jsonObject) {
        return orderApiDao.updateFtpClient(jsonObject);
    }

    @Override
    public JSONObject getFtpClientInfo(String client_id, Integer get_send_flag) {
        Tc206_order_ftp ftpCientInfo = orderApiDao.getFtpClientInfo(client_id, get_send_flag);
        return CommonUtils.success(ftpCientInfo);
    }

    /**
     * @Param: jsonObject
     * @description: 获取base token
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/14
     */
    @Override
    public Boolean setBaseToken(JSONObject jsonObject) {
        String apiName = jsonObject.getString("state");
        String client_id = jsonObject.getString("client_id");
        Tc203_order_client orderClient = orderApiDao.getOrderClientInfoById(apiName, client_id, null, null);
        if (!StringTools.isNullOrEmpty(orderClient)) {
            HashMap<String, String> map = new HashMap<>();
            map.put("grant_type", "authorization_code");
            map.put("client_id", orderClient.getApi_key());
            map.put("client_secret", orderClient.getPassword());
            map.put("code", jsonObject.getString("code"));
            map.put("redirect_uri", baseUrl);
            String url = orderClient.getClient_url() + "/1/oauth/token";
            JSONObject jsonResult = HttpUtils.sendHttpsPost(url, map, null, null);
            String access_token = jsonResult.getString("token_type") + " " + jsonResult.getString("access_token");
            String refresh_token = jsonResult.getString("refresh_token");
            try {
                orderApiDao.updateToken(access_token, refresh_token, orderClient.getId(), client_id);
            } catch (Exception e) {
                logger.error("修改模板的token失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return true;
    }

    /**
     * @Param: jsonObject
     * @description: 获取base token
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/12/14
     */
    @Override
    public Boolean setNextEngineToken(JSONObject jsonObject) {
        String uid = jsonObject.getString("uid");
        String state = jsonObject.getString("state");
        String order_client_id = jsonObject.getString("order_client_id");
        String client_id = jsonObject.getString("client_id");
        String api_name = jsonObject.getString("api_name");

        // 检索API设定是否存在
        Tc203_order_client orderClient = orderApiDao.getOrderClientInfoById(api_name, client_id, order_client_id, null);
        if (StringTools.isNullOrEmpty(orderClient)) {
            return false;
        }

        HashMap<String, String> map = new HashMap<>();
        map.put("uid", uid);
        map.put("state", state);
        map.put("client_id", order_client_id);
        map.put("client_secret", orderClient.getPassword());
        String url = Constants.NextEngine_API + "/api_neauth";
        JSONObject jsonResult = HttpUtils.sendHttpsPost(url, map, null, null);
        String access_token = jsonResult.getString("access_token");
        String refresh_token = jsonResult.getString("refresh_token");
        try {
            orderApiDao.updateToken(access_token, refresh_token, orderClient.getId(), client_id);
        } catch (Exception e) {
            logger.error("修改模板的token失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return true;
    }

    /**
     * @Param: jsonObject
     * @description: 获取yahoo token
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/2/9
     */
    @Override
    @Transactional
    public Boolean setYahooToken(@RequestBody JSONObject jsonObject) {
        String apiName = jsonObject.getString("api_name");
        String client_id = jsonObject.getString("client_id");
        String redirect_uri = jsonObject.getString("redirect_uri");
        Tc203_order_client orderClient = orderApiDao.getOrderClientInfoById(apiName, client_id, null, null);
        if (!StringTools.isNullOrEmpty(orderClient)) {
            String serviceSecret = orderClient.getApi_key();
            String licenseKey = orderClient.getPassword();
            String authorization =
                "Basic " + Base64.getEncoder().encodeToString((serviceSecret + ":" + licenseKey).getBytes());
            HashMap<String, String> map = new HashMap<>();
            map.put("grant_type", "authorization_code");
            map.put("code", jsonObject.getString("code"));
            map.put("redirect_uri", redirect_uri);
            JSONObject jsonResult = HttpUtils.sendHttpsPost(tokenUrl, map, "Authorization", authorization);
            String access_token = jsonResult.getString("token_type") + " " + jsonResult.getString("access_token");
            String refresh_token = jsonResult.getString("refresh_token");
            try {
                orderApiDao.updateToken(access_token, refresh_token, orderClient.getId(), client_id);
            } catch (Exception e) {
                logger.error("修改模板的token失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }
        return true;
    }

    /**
     * @param jsonObject : BASE API模板的数据
     * @description: 新规或者编辑 BASE API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/6/9 15:44
     */
    @Override
    public JSONObject insertBASEOrder(JSONObject jsonObject) {
        String client_id = jsonObject.getString("client_id");
        HashMap<String, String> map = new HashMap<>();
        map.put("grant_type", "authorization_code");
        map.put("client_id", jsonObject.getString("api_key"));
        map.put("client_secret", jsonObject.getString("password"));
        map.put("code", jsonObject.getString("code"));
        map.put("redirect_uri", baseUrl);
        try {
            String url = jsonObject.getString("client_url") + "/1/oauth/token";
            JSONObject jsonResult = HttpUtils.sendHttpsPost(url, map, null, null);
            String access_token = jsonResult.getString("token_type") + " " + jsonResult.getString("access_token");
            String refresh_token = jsonResult.getString("refresh_token");
            jsonObject.put("access_token", access_token);
            jsonObject.put("refresh_token", refresh_token);
            String apiName = jsonObject.getString("api_name");
            Integer maxId = getMaxApiId(client_id);
            jsonObject.put("id", maxId);
            // APIの識別コードの修正 Modify by HZM 20201209
            DecimalFormat df = new DecimalFormat("000");
            String str2 = df.format(maxId);
            String template = jsonObject.getString("template");
            String identification = orderDao.getApiIdentification(template);
            identification = identification + str2;
            jsonObject.put("identification", identification);
            String old_api_name = jsonObject.getString("old_api_name");
            // 根据apiName 和 client_id 查询以前是否存在
            Tc203_order_client orderClient = getOrderClientInfoById(old_api_name, client_id, template);
            if (!StringTools.isNullOrEmpty(orderClient)) {
                // 编辑BASE模板的信息
                orderApiDao.updateOrderBase(jsonObject);
            } else {
                // 新规BASE模板的信息
                orderApiDao.insertOrderBase(jsonObject);
            }
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public JSONObject insertColorMeOrder(JSONObject jsonObject) {
        // jsonObjectから各項目値を取得
        String clientId = jsonObject.getString("client_id");
        String apiName = jsonObject.getString("api_name");
        String colorMeClientId = jsonObject.getString("colorMeKey");
        String colorMeClientSecret = jsonObject.getString("colorMePass");
        String code = jsonObject.getString("code");
        try {
            // access_token取得
            String accessToken = getColorMeAccessToken(colorMeClientId, colorMeClientSecret, code);
            jsonObject.put("access_token", accessToken);

            // id生成
            Integer maxId = getMaxApiId(clientId);
            jsonObject.put("id", maxId);

            // identification生成
            DecimalFormat df = new DecimalFormat("000");
            String str2 = df.format(maxId);
            String template = jsonObject.getString("template");
            String identification = orderDao.getApiIdentification(template);
            identification = identification + str2;
            jsonObject.put("identification", identification);
            String old_api_name = jsonObject.getString("old_api_name");

            // 既存クライアントが存在するかどうかによって、insertとupdateを行う
            Tc203_order_client orderClient = getOrderClientInfoById(old_api_name, clientId, null);
            if (!StringTools.isNullOrEmpty(orderClient)) {
                // Color MeのAPI情報更新
                orderApiDao.updateOrderColorMe(jsonObject);
            } else {
                // Color MeのAPI情報新規
                orderApiDao.insertOrderColorMe(jsonObject);
            }

            return CommonUtils.success();
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 認可コードをアクセストークンに交換
     *
     * @param clientId 店舗ID
     * @param clientSecret クライアントシークレット
     * @param code 認証コード
     * @return accessToken アクセストークン
     * @author YuanMingZe
     * @date 2021/06/24 12:26
     */
    private String getColorMeAccessToken(String clientId, String clientSecret, String code) {
        // マップ生成
        HashMap<String, String> map = new HashMap<>();
        map.put("client_id", clientId);
        map.put("client_secret", clientSecret);
        map.put("grant_type", GRANT_TYPE);
        map.put("code", code);
        map.put("redirect_uri", baseUrl);

        // リクエストを送信する
        JSONObject jsonResult = HttpUtils.sendHttpsPost(COLOR_ME_ACCESS_TOKEN_URL, map, null, null);
        // System.err.println(jsonResult.toString());

        String access_token = jsonResult.getString("token_type") + " " + jsonResult.getString("access_token");
        return access_token;
    }

    private Integer getMaxApiId(String clientId) {
        Integer id = getMaxId(clientId);
        int maxId = 1;
        if (!StringTools.isNullOrEmpty(id)) {
            maxId = id + 1;
        }
        return maxId;
    }

    @Override
    public JSONObject insertMakeShopAPIInfo(JSONObject jsonObject) {
        String clientId = jsonObject.getString("client_id");
        String apiName = jsonObject.getString("api_name");
        String oldApiName = jsonObject.getString("old_api_name");
        Integer maxId = getMaxApiId(clientId);
        jsonObject.put("id", maxId);
        DecimalFormat df = new DecimalFormat("000");
        String str2 = df.format(maxId);
        String template = jsonObject.getString("template");
        String identification = orderDao.getApiIdentification(template);
        identification = identification + str2;
        jsonObject.put("identification", identification);
        jsonObject.put("bikou1", jsonObject.getString("makeshop_product_token"));
        jsonObject.put("bikou2", jsonObject.getString("makeshop_id"));
        // apiNameとclientIdを検索キーとして、重複チェックを行う
        Tc203_order_client orderClient = getOrderClientInfoById(apiName, clientId, template);
        if (!StringTools.isNullOrEmpty(orderClient)) {
            // MakeShopのAPI情報を更新する
            orderApiDao.updateMakeShopAPIInfo(jsonObject);
        } else {
            // MakeShopのAPI情報を新規する
            orderApiDao.insertMakeShopAPIInfo(jsonObject);
        }
        return CommonUtils.success();
    }

    @Override
    @Transactional
    public JSONObject insertYahooAPIInfo(JSONObject jsonObject) {
        // jsonObjectから各項目値を取得
        String clientId = jsonObject.getString("client_id");
        String apiName = jsonObject.getString("api_name");
        String template = jsonObject.getString("template");

        // id生成
        Integer maxId = getMaxApiId(clientId);
        jsonObject.put("id", maxId);
        try {
            // identification生成
            DecimalFormat df = new DecimalFormat("000");
            String str2 = df.format(maxId);
            String identification = orderDao.getApiIdentification(template);
            identification = identification + str2;
            jsonObject.put("identification", identification);

            // 既存クライアントが存在するかどうかによって、insertとupdateを行う
            Tc203_order_client orderClient = getOrderClientInfoById(apiName, clientId, template);
            if (!StringTools.isNullOrEmpty(orderClient)) {
                // YahooのAPI情報更新
                orderApiDao.updateYahooApi(jsonObject);
            } else {
                // YahooのAPI情報新規
                orderApiDao.insertYahooApi(jsonObject);
            }

            setYahooToken(jsonObject);
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description 店铺仓库是否有ntm权限
     * @param warehouse_cd 倉庫CD
     * @param client_id 店舗ID
     * @param function_cd 功能CD
     * @return: boolean
     **/
    @Override
    public boolean hasNtmFunction(String warehouse_cd, String client_id, String function_cd) {
        List<String> functionCdList = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(function_cd);
        List<Ms001_function> ms001FunctionList = clientDao.function_info(client_id, functionCdList, warehouse_cd);
        return Optional.ofNullable(ms001FunctionList).map(
            functionList -> {
                return functionList.stream().filter(value -> value.getFunction_cd().equals("4"))
                    .collect(Collectors.toList()).size() > 0;
            }).orElse(false);
    }

    /**
     * 校验手机号
     */
    @Override
    public String checkPhoneNumber(Tc200_order tc200) {
        String phoneNumber = Optional.of(tc200.getReceiver_phone_number1()).orElse("") +
            Optional.of(tc200.getReceiver_phone_number2()).orElse("") +
            Optional.of(tc200.getReceiver_phone_number3());
        return Strings.isNullOrEmpty(phoneNumber) ? "[電話番号]" : "";
    }

    /**
     * 校验邮编番号合法性
     */
    @Override
    public String checkYubinLegal(Tc200_order tc200) {
        String url = bizApiHost + Constants.BIZ_CHECKADDRESS;
        CheckAddressRequest request = initCheckAddressRequest(tc200);
        XmlMapper builder = XmlUtil.getXmlMapperInstance();
        String errorMsg = "";

        String requestValue = "";
        try {
            requestValue = XmlUtil.bean2Xml(builder, request);
            logger.info("CheckAddress Request Param: " + requestValue);
            HashMap<String, String> params = Maps.newHashMap();
            params.put("value", requestValue);
            JSONObject responseBody = HttpUtils.sendPostRequest(url, params, null, "xml");
            String responseValue = responseBody.getString("value");
            logger.info("CheckAddress Response Body: " + responseValue);
            CheckAddressResponse responseBean =
                (CheckAddressResponse) XmlUtil.xml2Bean(builder, responseValue, CheckAddressResponse.class);

            if (!BizLogiResEnum.S0_0001.getCode().equals(responseBean.getResultCode())) {
                errorMsg = "[郵便番号]、[住所情報]";
            }

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return errorMsg;
    }

    /**
     * @description 构造邮编校验请求实体
     * @param tc200 受注信息
     * @return: CheckAddressRequest
     * @date 2022/1/10
     **/
    private CheckAddressRequest initCheckAddressRequest(Tc200_order tc200) {
        CheckAddressRequest request = new CheckAddressRequest();

        CheckAddressRequest.CustomerAuth customerAuth = request.new CustomerAuth();
        customerAuth.setCustomerId(bizAuthCustomId);
        customerAuth.setLoginPassword(bizAuthCustomPwd);
        request.setCustomerAuth(customerAuth);

        String address = tc200.getReceiver_todoufuken() + tc200.getReceiver_address1() + tc200.getReceiver_address2();
        String yubin = tc200.getReceiver_zip_code1() + tc200.getReceiver_zip_code2();
        request.setRequestYubin(yubin);
        request.setRequestAddress(address);

        return request;
    }

    /**
     * @param jsonObject : next-engine模板的数据
     * @description: 新规或者编辑 next-engine API模板
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2021/12/17 10:47
     */
    @Override
    public JSONObject insertNextEngineAPIInfo(JSONObject jsonObject) {
        // jsonObjectから各項目値を取得
        String clientId = jsonObject.getString("client_id");
        String apiName = jsonObject.getString("api_name");

        // id生成
        Integer maxId = getMaxApiId(clientId);
        jsonObject.put("id", maxId);
        try {
            // identification生成
            DecimalFormat df = new DecimalFormat("000");
            String str2 = df.format(maxId);
            String template = jsonObject.getString("template");
            String identification = orderDao.getApiIdentification(template);
            identification = identification + str2;
            jsonObject.put("identification", identification);

            // 既存クライアントが存在するかどうかによって、insertとupdateを行う
            Tc203_order_client orderClient = getOrderClientInfoById(apiName, clientId, template);
            if (!StringTools.isNullOrEmpty(orderClient)) {
                // NextEngineのAPI情報更新
                orderApiDao.updateNextEngineApi(jsonObject);
            } else {
                // NextEngineのAPI情報新規
                orderApiDao.insertNextEngineApi(jsonObject);
            }
            return CommonUtils.success();
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
