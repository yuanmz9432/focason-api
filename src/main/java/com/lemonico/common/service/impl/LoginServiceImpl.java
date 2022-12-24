package com.lemonico.common.service.impl;



import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Splitter;
import com.lemonico.common.bean.*;
import com.lemonico.common.dao.ClientDao;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.common.service.LoginService;
import com.lemonico.core.exception.*;
import com.lemonico.core.props.PathProps;
import com.lemonico.core.shiro.JwtUtils;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.DateUtils;
import com.lemonico.core.utils.PasswordHelper;
import com.lemonico.core.utils.StringTools;
import com.lemonico.core.utils.apiLimit.GetIp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpHeaders;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @className: LoginServiceImpl
 * @description: ログインサービス 実現クラス
 * @date: 2020/05/12 9:27
 **/
@Service
public class LoginServiceImpl implements LoginService
{

    private final static Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Resource
    private LoginDao loginDao;

    @Resource
    private PasswordHelper passwordHelper;

    @Resource
    private ClientDao clientDao;

    @Resource
    private PathProps pathProps;

    /**
     * @Param loginNm ：ユーザ名
     * @description: ユーザーが存在するかどうかを調べる
     * @return: Ms200Customer
     * @date: 2020/05/12
     */
    @Override
    public Ms200_customer getUserByName(String loginNm, String usekb) {
        return loginDao.getUserByName(loginNm, usekb);
    }


    public String getMaxUserId() {
        return loginDao.getMaxUserId() == null ? "1" : String.valueOf(Integer.parseInt(loginDao.getMaxUserId()) + 1);
    }

    /**
     * @Param: client_id : 店铺ID
     * @description: 根据店铺ID获取店铺信息
     * @return: com.lemonico.common.bean.Ms200_customer
     * @date: 2020/05/12
     */
    @Override
    public Ms200_customer getUserByUserId(String client_id) {
        return loginDao.getUserByUserId(client_id);
    }


    /**
     * @Param: userId
     * @param: usekb
     * @description: 需要携带token的验证用户
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/22
     */
    @Override
    public JSONObject checkUserId(String loginId, String usekb, String yoto, String flg, HttpServletRequest request) {
        String client_id = CommonUtils.getToken("client_id", request);
        String warehouse_cd = CommonUtils.getToken("warehouse_cd", request);
        Ms200_customer userByName = loginDao.getUserByNameAndYoto(loginId, usekb, yoto);
        if (!StringTools.isNullOrEmpty(userByName)) {
            // flg 0 店铺侧验证
            if (flg.equals("0")) {
                List<Ms206_client_customer> clientListByUserId =
                    clientDao.getClientListByUserId(userByName.getUser_id(), client_id);
                if (clientListByUserId.size() != 0) {
                    throw new PlUnauthorizedException(PlErrorCode.USER_AUTHENTICATION_FAILURE,
                        "ログインが失効したが、ご再登録してください。");
                }
            }
            // flg 1 仓库侧验证
            if (flg.equals("1")) {
                Mw402_wh_client mw402_wh_client = loginDao.checkWarehouseUser(userByName.getUser_id(), warehouse_cd);
                if (!StringTools.isNullOrEmpty(mw402_wh_client)) {
                    throw new PlUnauthorizedException(PlErrorCode.AUTH_TOKEN_EXPIRED, "ログインが失効したが、ご再登録してください。");
                }
            }
        }
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject : loginId
     * @description: 保存用户信息，状态为申请中
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    @Override
    public JSONObject saveUser(JSONObject jsonObject, HttpServletResponse response) {
        Date date = DateUtils.getDate();

        Ms200_customer userByName = loginDao.getUserByName(jsonObject.getString("loginId"), null);
        Ms200_customer ms200_customer = new Ms200_customer();
        Integer status = 0;
        ms200_customer.setLogin_pw("123456");
        ms200_customer.setYoto("1");
        ms200_customer.setUsekb("2");
        ms200_customer.setIns_date(date);
        ms200_customer.setUpd_date(date);
        ms200_customer.setDel_flg(0);
        // 如果以前存在 则不进行注册
        if (StringTools.isNullOrEmpty(userByName)) {
            ms200_customer.setLogin_id(jsonObject.getString("loginId"));
            ms200_customer.setUser_id(getMaxUserId());
            passwordHelper.encryptPassword(ms200_customer);
            loginDao.register(ms200_customer);
        }
        String jwtToken = JwtUtils.sign(JwtUtils.SECRET, jsonObject.getString("loginId"), 1);
        ((HttpServletResponse) response).setHeader(JwtUtils.AUTH_HEADER, jwtToken);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @description: 新增店铺信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    @Override
    @Transactional(rollbackFor = BaseException.class)
    public JSONObject insertClient(JSONObject jsonObject) {
        String email = jsonObject.getString("email");
        // 获取本月的代号
        String accountId = CommonUtils.getLastClientIdStr();
        // 获取改代号的最大店铺Id
        String lastClientId = loginDao.getLastClientId(accountId);
        String clientId = CommonUtils.getMaxClientId(lastClientId, accountId);

        Ms200_customer ms200Customer;
        try {
            // 查询确认等待中的用户
            ms200Customer = loginDao.getUserByName(email, null);
        } catch (Exception e) {
            logger.error("查询确认等待中的用户失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        if ("1".equals(ms200Customer.getUsekb())) {
            // 如果useKb 为1 则证明该邮箱以前注册过，需要验证其密码是否正确
            String password = jsonObject.getString("password");
            UsernamePasswordToken token = new UsernamePasswordToken(email, password);

            Subject subject = SecurityUtils.getSubject();
            try {
                subject.login(token);
            } catch (Exception e) {
                // 密码验证失败
                throw new PlUnauthorizedException();
            }
        } else {
            // 新邮箱注册

            // 设置为使用区分为 使用中
            String usekb = "1";
            // 修改用户的所属店铺以及使用区分以及密码
            try {
                loginDao.updateUserClientByLoginId(email, usekb, ms200Customer.getLogin_pw(),
                    ms200Customer.getEncode_key(), jsonObject.getString("tnnm"));
            } catch (Exception e) {
                logger.error("修改用户的所属店铺以及使用区分以及密码失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            // 修改用户的权限
            // admin权限
            String authority_cd = "1";
            try {
                loginDao.insertUserAuthority(authority_cd, ms200Customer.getUser_id(), "1");
            } catch (Exception e) {
                logger.error("修改用户权限失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
        }
        // 新增店铺信息
        jsonObject.put("client_id", clientId);
        String birthday = jsonObject.getString("birthday");
        Date date = DateUtils.stringToDate(birthday);
        Integer permonth = Integer.valueOf(jsonObject.getString("permonth"));
        String logo = jsonObject.getString("logo");
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String nowTime = format.format(new Date());
        // 图片路径
        String uploadPath = pathProps.getLogo() + clientId + "/" + nowTime + "/" + logo;
        jsonObject.put("logo", uploadPath);
        // 格式化邮编番号
        String zip = jsonObject.getString("zip");
        if (!StringTools.isNullOrEmpty(zip)) {
            String newZip = CommonUtils.formatZip(zip);
            jsonObject.put("zip", newZip);
        }
        try {
            clientDao.insertClientInfo(jsonObject, date, permonth);
        } catch (Exception e) {
            logger.error("新增店铺失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        // 将店铺信息 存到 商品設定用表
        try {
            clientDao.insertProductSetting(clientId);
        } catch (Exception e) {
            logger.error("将店铺信息 存到 商品設定用表失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        // 将店铺和用户信息存到店舗別顧客マスタ
        try {
            clientDao.insertClientCustomer(clientId, ms200Customer.getUser_id());
        } catch (Exception e) {
            logger.error("将店铺和用户信息存到店舗別顧客マスタ表失败");
            logger.error(BaseException.print(e));
            throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
        return CommonUtils.success(clientId);
    }

    /**
     * @Param: email
     * @description: 更改用户状态
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/5
     */
    @Override
    public JSONObject changeUserStatus(String email) {
        Ms200_customer userByName = loginDao.getUserByName(email, "2");
        Ms200_customer user = loginDao.getUserByName(email, "1");
        if (StringTools.isNullOrEmpty(userByName) && StringTools.isNullOrEmpty(user)) {
            throw new PlResourceNotFoundException("ユーザー： " + email);
        } else {
            String usekb = "3";
            try {
                loginDao.updateUserStatusById(email, usekb);
            } catch (Exception e) {
                logger.error("修改用户状态失败");
                logger.error(BaseException.print(e));
                throw new BaseException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
            }
            return CommonUtils.success();
        }
    }

    /**
     * @Param: jsonObject
     * @description: 更改用户密码
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/8/6
     */
    @Override
    public JSONObject changeUserPwd(JSONObject jsonObject) {

        String email = jsonObject.getString("email");
        Ms200_customer ms200Customer = loginDao.getUserByName(email, "1");
        if (StringTools.isNullOrEmpty(ms200Customer)) {
            throw new PlResourceNotFoundException("ユーザー： " + email);
        } else {
            ms200Customer.setEncode_key("");
            ms200Customer.setLogin_pw(jsonObject.getString("password"));
            passwordHelper.encryptPassword(ms200Customer);
            loginDao.updateUserClientByLoginId(jsonObject.getString("email"),
                ms200Customer.getUsekb(), ms200Customer.getLogin_pw(), ms200Customer.getEncode_key(), null);
            return CommonUtils.success();
        }
    }

    /**
     * 郵便番号による住所情報を取得する
     *
     * @param zip 郵便番号
     * @return 住所情報
     * @since 1.0.0
     */
    @Override
    public JSONObject getZipInfo(String zip) {
        return CommonUtils.success(loginDao.getZipInfo(zip));
    }

    /**
     * 都道府県リストを取得する
     *
     * @return 都道府県リスト
     * @since 1.0.0
     */
    @Override
    public JSONObject getPrefectureList() {
        return CommonUtils.success(loginDao.getTodoufukenList());
    }

    /**
     * @Param: userId
     * @description: 根据登录Id获取店铺信息
     * @return: java.lang.String
     * @date: 2020/9/21
     */
    @Override
    public JSONObject getClientInfoByLoginId(String userId, ServletResponse response, HttpServletRequest request) {

        JSONObject jsonObject = new JSONObject();
        // 根据userId 查询店铺的信息
        List<Ms201_client> clients = loginDao.getClientInfoByUserId(userId);
        // logger.info("userid"+userId+"店舗"+clients.toString());
        // 根据user_id 查询仓库的信息
        List<Mw400_warehouse> warehouses = loginDao.getWarehouseInfoByUserId(userId);
        // logger.info("userid"+userId+"倉庫"+clients.toString());
        // 登录者没有仓库和店铺
        if (clients.size() == 0 && warehouses.size() == 0) {
            jsonObject.put("client", clients);
            jsonObject.put("warehouse", warehouses);
            jsonObject.put("status", "101");
            return CommonUtils.success(jsonObject);
        }
        // 获取当前登录者ip
        String ip = GetIp.getIpAddress(request);
        logger.info("UserID:" + userId + " now_IPAddress:" + ip);
        // ip符合的店铺
        ArrayList<Ms201_client> accordClients = new ArrayList<>();
        // ip符合的仓库
        ArrayList<Mw400_warehouse> accordWarehouses = new ArrayList<>();
        clients.forEach(ms201 -> {
            if (!StringTools.isNullOrEmpty(ms201.getIp_address())) {
                List<String> storeIps =
                    Splitter.on(",").omitEmptyStrings().trimResults().splitToList(ms201.getIp_address());
                logger.info("店舗ID:" + ms201.getClient_id() + " AllowIP：" + ms201.getIp_address());
                if (storeIps.contains(ip)) {
                    accordClients.add(ms201);
                }
            } else {
                accordClients.add(ms201);
            }
        });

        warehouses.forEach(mw400 -> {
            if (!StringTools.isNullOrEmpty(mw400.getIp_address())) {
                List<String> warehouseIps =
                    Splitter.on(",").omitEmptyStrings().trimResults().splitToList(mw400.getIp_address());
                logger.info("倉庫CD:" + mw400.getWarehouse_cd() + " AllowIP:" + mw400.getIp_address());
                if (warehouseIps.contains(ip)) {
                    accordWarehouses.add(mw400);
                }
            } else {
                accordWarehouses.add(mw400);
            }
        });
        // for (int i = 0; i < warehouses.size(); i++) {
        // Mw400_warehouse mw400_warehouse = warehouses.get(i);
        // if(!StringTools.isNullOrEmpty(mw400_warehouse.getIp_address())){
        // List<String> storeIps =
        // Splitter.on(",").omitEmptyStrings().trimResults().splitToList(mw400_warehouse.getIp_address());
        // if(storeIps.contains(ip)){
        // accordWarehouses.add(mw400_warehouse);
        // }
        // }else {
        // accordWarehouses.add(mw400_warehouse);
        // }
        // }
        jsonObject.put("client", accordClients);
        jsonObject.put("warehouse", accordWarehouses);
        // 登录者本身没有店铺仓库 和 没有允许ip 的区分
        jsonObject.put("status", "102");
        String jwtToken = JwtUtils.sign(JwtUtils.SECRET, userId);
        ((HttpServletResponse) response).setHeader(JwtUtils.AUTH_HEADER, jwtToken);
        return CommonUtils.success(jsonObject);
    }

    /**
     * @Param: jsonObject
     * @description: 店铺侧登陆
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/21
     */
    @Override
    public JSONObject getClientToken(JSONObject jsonObject, HttpServletResponse response) {

        String userId = jsonObject.getString("user_id");
        Ms200_customer customer = loginDao.getUserByUserId(userId);

        String jwtToken = JwtUtils.sign(jsonObject.getString("client_id"), customer.getLogin_nm(), JwtUtils.SECRET,
            customer.getYoto(), customer.getUser_id(), customer.getLogin_id());
        response.addHeader(HttpHeaders.AUTHORIZATION, jwtToken);
        return CommonUtils.success();
    }

    /**
     * @Param: jsonObject
     * @param: response
     * @description: 仓库侧登陆
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/9/21
     */
    @Override
    public JSONObject getWarehouseToken(JSONObject jsonObject, HttpServletResponse response) {
        String userId = jsonObject.getString("user_id");
        Ms200_customer customer = loginDao.getUserByUserId(userId);
        String jwtToken = JwtUtils.sign(jsonObject.getString("warehouse_id"), customer.getLogin_nm(), JwtUtils.SECRET,
            1, customer.getYoto(),
            customer.getUser_id(), customer.getUser_id());
        ((HttpServletResponse) response).setHeader(JwtUtils.AUTH_HEADER, jwtToken);
        return CommonUtils.success();
    }

    /**
     * @description: 解析邮箱修改密码链接中的邮箱地址
     * @return: java.lang.String
     * @date: 2020/09/28
     */
    @Override
    public String checkEmailAddress(String login_id) {
        String loginId = loginDao.checkEmailAddress(login_id);
        return loginId;
    }

    /**
     * @Param: login_id : 登录Id
     * @description: 获取用户信息
     * @return: com.alibaba.fastjson.JSONObject
     * @date: 2020/11/4
     */
    @Override
    public JSONObject storeUserInfo(String login_id) {
        Ms200_customer customer = loginDao.getUserByNameAndYoto(login_id, null, null);
        return CommonUtils.success(customer);
    }
}
