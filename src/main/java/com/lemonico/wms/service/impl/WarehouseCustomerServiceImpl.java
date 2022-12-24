package com.lemonico.wms.service.impl;



import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.common.service.CommonService;
import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.LcResourceAlreadyExistsException;
import com.lemonico.core.exception.LcUnauthorizedException;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.utils.*;
import com.lemonico.store.service.SettingService;
import com.lemonico.wms.dao.WarehouseCustomerDao;
import com.lemonico.wms.service.WarehouseCustomerService;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 倉庫設定管理サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class WarehouseCustomerServiceImpl implements WarehouseCustomerService
{

    private final static Logger logger = LoggerFactory.getLogger(WarehouseCustomerServiceImpl.class);

    private final WarehouseCustomerDao warehouseCustomerDao;
    private final LoginDao loginDao;
    private final PasswordHelper passwordHelper;
    private final ClientDao clientDao;
    private final CommonService commonService;
    private final MailTools mailTools;
    private final SettingService settingService;
    private final PathProps pathProps;
    @Value("${domain}")
    private String domain;

    /**
     * @Param: client_id : 店铺Id
     * @description: 获取该仓库下所有店铺的信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    @Override
    public JSONObject getClientInfoList(String warehouse_cd) {
        List<String> clientIdList = null;
        try {
            clientIdList = warehouseCustomerDao.getClientIdList(warehouse_cd);
        } catch (Exception e) {
            logger.error("根据仓库Id获取其包含的店铺失败，仓库Id为" + warehouse_cd);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        List<Ms201_client> clientInfoList = null;
        if (clientIdList.size() != 0) {
            try {
                clientInfoList = warehouseCustomerDao.getClientInfoListByClientId(clientIdList);
            } catch (Exception e) {
                logger.error("根据店铺Id获取店铺信息失败，店铺Id为" + clientIdList);
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
            }
        }
        return CommonUtils.success(clientInfoList);
    }

    /**
     * @Param: warehouse_id : 仓库id
     * @description: 获取到该仓库下说有店铺的权限
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/08
     */
    @Override
    public JSONObject getCustomerAuthority(String warehouse_id) {
        List<String> userIdList = null;
        try {
            userIdList = warehouseCustomerDao.getUserIdByWarehouseId(warehouse_id);
        } catch (Exception e) {
            logger.error("根据仓库id获取所有userId失败，仓库Id为" + warehouse_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        List<Ms200_customer> userInfoList = null;
        try {
            userInfoList = warehouseCustomerDao.getUserInfoByUserId(userIdList);
        } catch (Exception e) {
            logger.error("根据用户id获取用户信息失败,用户Id为" + userIdList);
        }
        return CommonUtils.success(userInfoList);
    }

    /**
     * @param: client_id : 店铺Id
     * @description: 根据店铺Id 查询店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/09
     */
    @Override
    public JSONObject getClientInfo(String client_id) {
        Ms201_client clientInfo;
        try {
            clientInfo = warehouseCustomerDao.getClientInfo(client_id);
        } catch (Exception e) {
            logger.error("根据店铺Id查询店铺信息失败，店铺Id为" + client_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success(clientInfo);
    }

    /**
     * @Param: jsonObject : 店铺的所有信息
     * @description: 根据店铺Id修改店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/10
     */
    @Override
    public JSONObject updateClientInfo(JSONObject jsonObject) {
        String date = jsonObject.getString("birthday");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Date birthday = null;
        try {
            if (!StringTools.isNullOrEmpty(date)) {
                birthday = format.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String num = jsonObject.getString("permonth");
        Integer permonth = 0;
        if (!StringTools.isNullOrEmpty(num)) {
            permonth = Integer.valueOf(num);
        }
        jsonObject.getString("permonth");
        try {
            String zip = jsonObject.getString("zip");
            if (!StringTools.isNullOrEmpty(zip)) {
                String formatZip = CommonUtils.formatZip(zip);
                jsonObject.put("zip", formatZip);
            }
            warehouseCustomerDao.updateClientInfo(jsonObject, birthday, permonth);
        } catch (Exception e) {
            logger.error("根据店铺Id修改店铺信息失败, 店铺Id为" + jsonObject.getString("client_id"));
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR);
        }
        return CommonUtils.success();
    }

    public String getMaxClientId() {
        // 获取本月的代号
        String accountId = CommonUtils.getLastClientIdStr();
        // 获取本月最大的店铺Id
        String lastClientId;
        try {
            lastClientId = loginDao.getLastClientId(accountId);
        } catch (Exception e) {
            logger.error("获取店铺最大Id失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        String clientId = CommonUtils.getMaxClientId(lastClientId, accountId);
        return clientId;
    }

    /**
     * @param: warehouseId : 仓库Id
     * @description: 根据仓库Id 获取仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @Override
    public JSONObject getWarehouseInfo(String warehouseId) {
        Mw400_warehouse warehouseInfo;
        try {
            warehouseInfo = warehouseCustomerDao.getWarehouseInfo(warehouseId);
        } catch (Exception e) {
            logger.error("根据仓库Id查询仓库信息失败，仓库Id为：" + warehouseId);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success(warehouseInfo);
    }

    /**
     * @Param: jsonObject : 仓库id
     * @description: 根据仓库Id更改仓库信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @Override
    public JSONObject updateWarehouseInfoByWarehouseId(JSONObject jsonObject) {
        try {
            String zip = jsonObject.getString("zip");
            if (!StringTools.isNullOrEmpty(zip)) {
                String formatZip = CommonUtils.formatZip(zip);
                jsonObject.put("zip", formatZip);
            }
            warehouseCustomerDao.updateWarehouseInfoByWarehouseId(jsonObject);
        } catch (Exception e) {
            logger.error("根据仓库Id更改仓库信息失败，仓库Id为：" + jsonObject.getString("warehouse_cd"));
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success();
    }

    /**
     * @Param: user_id : 用户Id
     * @description: 根据用户Id查询用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @Override
    public JSONObject getUserInfo(String user_id) {
        ArrayList<String> userIdList = new ArrayList<>();
        userIdList.add(user_id);
        List<Ms200_customer> userInfoByUserId;
        try {
            userInfoByUserId = warehouseCustomerDao.getUserInfoByUserId(userIdList);
        } catch (Exception e) {
            logger.error("根据用户Id查询用户信息失败，用户Id为:" + user_id);
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success(userInfoByUserId.get(0));
    }

    /**
     * @Param: jsonObject : 多个用户Id
     * @description: 根据用户Id删除用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @Override
    public JSONObject deleteUserInfo(JSONObject jsonObject) {
        JSONArray jsonArray = jsonObject.getJSONArray("userIdList");
        ArrayList<String> userIdList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            userIdList.add(String.valueOf(jsonArray.get(i)));
        }

        userIdList.stream().forEach(userId -> {
            List<Ms206_client_customer> clientListByUserId = clientDao.getClientListByUserId(userId, null);
            if (clientListByUserId.size() == 0) {
                try {
                    warehouseCustomerDao.deleteUserInfo(userId);
                } catch (Exception e) {
                    logger.error("根据用户Id删除用户失败, 用户Id为：" + userId);
                    logger.error(BaseException.print(e));
                    throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
                }
                warehouseCustomerDao.deleteCustomerAuthByUserId(userId);
            }
        });
        try {
            warehouseCustomerDao.deleteWharehouseCustomerByUserId(userIdList, jsonObject.getString("warehouseId"));
        } catch (Exception e) {
            logger.error(
                "根据用户和仓库的Id 删除用户和仓库的关系失败,用户Id为：" + userIdList + "---仓库Id为：" + jsonObject.getString("warehouseId"));
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        return CommonUtils.success();
    }

    /**
     * @param: login_id : 邮箱
     * @description: 根据邮箱查询用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @Override
    public JSONObject getUserInfoByLoginId(String login_id) {
        Ms200_customer customer = null;
        try {
            customer = loginDao.getUserByName(login_id, null);
        } catch (Exception e) {
            logger.error("根据邮箱查询用户信息失败,用户邮箱为：" + login_id);
        }
        return CommonUtils.success(customer);
    }

    /**
     * @param: jsonObject : login_id,login_nm
     * @description: 新增属于该仓库的用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/07/13
     */
    @Override
    @Transactional(rollbackFor = BaseException.class)
    public JSONObject insertUserInfo(JSONObject jsonObject, HttpServletRequest request) {
        // 修改这name
        String name = CommonUtils.getToken("login_nm", request);
        Date date = DateUtils.getDate();
        String userId = "";
        String loginNm = jsonObject.getString("login_id");
        Ms200_customer userByName;
        try {
            userByName = loginDao.getUserByNameAndYoto(loginNm, null, "2");
        } catch (Exception e) {
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }
        Integer flg = Integer.valueOf(jsonObject.getString("flg"));
        if (flg == 1) {
            // 验证旧密码是否输入正确
            UsernamePasswordToken token = new UsernamePasswordToken(loginNm, jsonObject.getString("oldLoginPw"));
            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token);
            } catch (Exception e) {
                throw new LcUnauthorizedException();
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
                throw new LcResourceAlreadyExistsException("ユーザー: " + jsonObject.getString("login_nm"));
            }
            Ms200_customer ms200Customer = JSONObject.toJavaObject(jsonObject, Ms200_customer.class);
            userId = getMaxUserId();
            ms200Customer.setUser_id(userId);
            ms200Customer.setUsekb("1");
            ms200Customer.setYoto("2");
            ms200Customer.setIns_usr(name);
            ms200Customer.setUpd_usr(name);
            ms200Customer.setUpd_date(date);
            ms200Customer.setIns_date(date);
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
            // 给用户添加user的权限
            jsonObject.put("user_id", userId);
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
        jsonObject.put("user_id", userId);
        // 将用户加入用户仓库关系表
        try {
            warehouseCustomerDao.insertWarehouseCustomer(jsonObject);
        } catch (Exception e) {
            logger.error("新增该仓库的员工失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR, e.getMessage());
        }

        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @description: 新增属于该仓库的店铺
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/6
     */
    @Override
    @Transactional(rollbackFor = BaseException.class)
    public JSONObject insertClientInfo(JSONObject jsonObject, HttpServletRequest request) {
        String clientId = getMaxClientId();
        jsonObject.put("client_id", clientId);
        String birthday = jsonObject.getString("birthday");
        Date date = DateUtils.stringToDate(birthday);

        Ms200_customer customer = loginDao.getUserByName(jsonObject.getString("email"), null);
        int pwdChangeflg = Integer.parseInt(jsonObject.getString("pwdChangeflg"));
        boolean newPassFlg = jsonObject.getBoolean("new_pass_flg");
        if (pwdChangeflg == 1) {
            // 验证其密码是否正确
            UsernamePasswordToken token = new UsernamePasswordToken(jsonObject.getString("email"),
                jsonObject.getString("oldLoginPwd"));
            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token);
            } catch (Exception e) {
                throw new LcUnauthorizedException();
            }

            customer.setLogin_nm(jsonObject.getString("login_nm"));

            // 修改密码
            customer.setLogin_pw(jsonObject.getString("new_pass"));
            PasswordHelper.encryptPassword(customer);
            loginDao.updateUserInfo(customer);
        } else if (newPassFlg) {
            // 新增属于该店铺的员工
            Ms200_customer ms200Customer = new Ms200_customer();
            ms200Customer.setUser_id(getMaxUserId());
            ms200Customer.setLogin_id(jsonObject.getString("email"));
            ms200Customer.setLogin_pw(jsonObject.getString("new_pass"));
            ms200Customer.setLogin_nm(jsonObject.getString("tnnm"));
            ms200Customer.setUsekb("1");
            ms200Customer.setYoto("1");
            ms200Customer.setDel_flg(0);
            PasswordHelper.encryptPassword(ms200Customer);
            customer = ms200Customer;
            try {
                loginDao.register(customer);
            } catch (Exception e) {
                logger.error("新增用户失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            // 给担当这用户增加admin权限
            String authority_cd = "1";
            try {
                loginDao.insertUserAuthority(authority_cd, customer.getUser_id(), "1");
            } catch (Exception e) {
                logger.error("增加用户权限失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }

        // 新增店铺
        try {
            String zip = jsonObject.getString("zip");
            if (!StringTools.isNullOrEmpty(zip)) {
                String formatZip = CommonUtils.formatZip(zip);
                jsonObject.put("zip", formatZip);
            }
            Integer permonth = null;
            if (jsonObject.getString("permonth") != "" && jsonObject.getString("permonth") != null) {
                permonth = Integer.valueOf(jsonObject.getString("permonth"));
            }
            clientDao.insertClientInfo(jsonObject, date, permonth);
        } catch (Exception e) {
            logger.error("新增店铺失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        // 将店铺和该仓库绑定
        // 设置店铺为使用中
        String kubun = "0";
        try {
            clientDao.insertWarehouseAndClient(jsonObject.getString("warehouse_cd"), clientId, kubun);
        } catch (Exception e) {
            logger.error("店铺和仓库绑定失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        // 将店铺和用户信息存到店舗別顧客マスタ
        try {
            clientDao.insertClientCustomer(clientId, customer.getUser_id());
        } catch (Exception e) {
            logger.error("将店铺和用户信息存到店舗別顧客マスタ表失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        // 将店铺信息 存到 商品設定用表
        try {
            clientDao.insertProductSetting(clientId);
        } catch (Exception e) {
            logger.error("将店铺信息 存到 商品設定用表失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        // 复制店铺信息到依赖主マスタ表
        JSONObject sponsorJson = new JSONObject();
        sponsorJson.put("detail_logo", null);
        sponsorJson.put("client_id", clientId);
        sponsorJson.put("sponsor_default", 1);
        sponsorJson.put("utilize", null);
        sponsorJson.put("name", jsonObject.getString("tnnm"));
        sponsorJson.put("name_kana", null);
        sponsorJson.put("company", jsonObject.getString("shop_nm"));
        sponsorJson.put("division", null);
        sponsorJson.put("postcode", jsonObject.getString("zip"));
        sponsorJson.put("prefecture", jsonObject.getString("tdfk"));
        sponsorJson.put("address1", jsonObject.getString("add1"));
        sponsorJson.put("address2", jsonObject.getString("add2"));

        sponsorJson.put("phone", jsonObject.getString("tel"));
        sponsorJson.put("email", jsonObject.getString("contactEmail"));
        sponsorJson.put("fax", jsonObject.getString("contactFax"));
        sponsorJson.put("contact", jsonObject.getString("contact"));
        sponsorJson.put("checkList", jsonObject.getJSONArray("checkList"));
        sponsorJson.put("contact_url", null);
        sponsorJson.put("detail_message", null);
        sponsorJson.put("send_message", null);
        // 依赖主マスタ新增
        if (jsonObject.getInteger("sponsor_flg") == 1) {
            try {
                settingService.createDeliveryList(sponsorJson, request);
            } catch (Exception e) {
                logger.error("依赖主マスタ新增失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
            }
        }

        // 给该用户发送邮件提醒 账号开通成功
        Integer status = Integer.valueOf(jsonObject.getString("status"));
        if (status == 1) {
            String pwd;
            Boolean oldpwdFlg = pwdChangeflg == 0 && newPassFlg == false;
            pwd = (pwdChangeflg == 1 || newPassFlg == true) ? jsonObject.getString("new_pass")
                : jsonObject.getString("oldLoginPwd");
            MailBean mailBean = mailTools.remindUserTemplate(jsonObject.getString("email"), pwd, domain, oldpwdFlg);
            commonService.sendMail(mailBean);
        }

        return CommonUtils.success();
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
     * @Description: スマートCatリスト
     * @Param: warehouse_id
     * @return: JSONObject
     * @Date: 2020/11/10
     */
    @Override
    public List<Mw406_wh_smartcat> getSmartCatList(String warehouse_cd, Integer id) {
        return warehouseCustomerDao.getSmartCatList(warehouse_cd, id);
    }

    /**
     * @Description: スマートCat更新
     * @Param: warehouse_id
     * @return: Integer
     * @Date: 2020/11/10
     */
    @Override
    public JSONObject updateSmartCatList(String warehouse_cd, Integer id, JSONObject jsonObject,
        HttpServletRequest request) {
        String upd_usr = CommonUtils.getToken("user_id", request);
        Date upd_date = DateUtils.getDate();

        // String filePath = jsonObject.getString("file_path");
        // File file = new File(filePath);
        // if(!file.exists()){
        // return CommonUtil.errorJson(ErrorEnum.E_10001);
        // }

        jsonObject.put("upd_usr", upd_usr);
        jsonObject.put("upd_date", upd_date);

        try {
            warehouseCustomerDao.updateSmartCatList(warehouse_cd, id, jsonObject);
        } catch (Exception e) {
            logger.debug("スマートCAT外部連携の設定が失敗しました");
        }
        return CommonUtils.success();
    }

    /**
     * @Description: CSV windows アプロード
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    @Override
    public JSONObject getSmartCatListWindow(String warehouse_cd) {
        List<Mw406_wh_smartcat> mw406_wh_smartcat = warehouseCustomerDao.getSmartCatList(warehouse_cd, 1);
        String smartPath = mw406_wh_smartcat.get(0).getFile_path();
        JSONObject json = new JSONObject();
        String tmpPath = smartPath.substring(smartPath.length() - 1);
        if (!tmpPath.equals("\\") && !tmpPath.equals("/")) {
            String[] tmpArr = smartPath.split("/");
            if (tmpArr.length > 1) {
                smartPath += "/";
            } else {
                smartPath += "\\";
            }
        }
        json.put("path", smartPath);
        List<Mw407_smart_file> smartCats = warehouseCustomerDao.getSmartCatListWindow(warehouse_cd);
        for (Mw407_smart_file mw407 : smartCats) {
            String path = domain + pathProps.getWms() + mw407.getFile_path() + mw407.getFile_name();
            mw407.setFile_path(path);
        }
        json.put("info", smartCats);

        return json;
    }

    /**
     * @Description: スマートCAT ファイル 挿入
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    @Override
    @Transactional
    public Integer inSmartCatListWindow(Mw407_smart_file mw407SmartFile) {
        return warehouseCustomerDao.inSmartCatListWindow(mw407SmartFile);
    }

    /**
     * @Description: スマートCAT ファイル 更新
     * @Param: warehouse_id
     * @return: Mw407_smart_file
     * @Date: 2020/11/13
     */
    @Override
    @Transactional
    public Integer upSmartCatListWindow(String warehouse_cd, HttpServletRequest httpServletRequest,
        String shipment_plan_id) {
        Date upd_date = DateUtils.getDate();
        Mw407_smart_file mw407_smart_file = new Mw407_smart_file();
        mw407_smart_file.setWarehouse_cd(warehouse_cd);
        mw407_smart_file.setUpd_date(upd_date);
        mw407_smart_file.setShipment_plan_id(shipment_plan_id);
        return warehouseCustomerDao.upSmartCatListWindow(mw407_smart_file);
    }

    /**
     * @Param: jsonObject : 店铺的所有信息
     * @description: 根据店铺Id修改店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/20
     */
    @Override
    public JSONObject updateDeliveryClientInfo(JSONObject jsonObject) {
        try {
            warehouseCustomerDao.updateDeliveryClientInfo(jsonObject);
        } catch (Exception e) {
            logger.error("根据店铺Id修改店铺配送信息失败, 店铺Id为" + jsonObject.getString("client_id"));
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.DATABASE_ERROR);
        }
        return CommonUtils.success();
    }

}
