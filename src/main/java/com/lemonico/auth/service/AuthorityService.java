package com.lemonico.auth.service;



import com.alibaba.fastjson.JSONObject;
import com.lemonico.auth.resource.LoginUserResource;
import com.lemonico.common.bean.Ms200_customer;
import com.lemonico.common.dao.LoginDao;
import com.lemonico.core.exception.ErrorCode;
import com.lemonico.core.exception.PlResourceAlreadyExistsException;
import com.lemonico.core.exception.PlResourceNotFoundException;
import com.lemonico.core.exception.PlUnauthorizedException;
import com.lemonico.core.shiro.JwtUtils;
import com.lemonico.core.utils.CommonUtils;
import com.lemonico.core.utils.PasswordHelper;
import javax.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

/**
 * 認証認可サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({
    @Autowired, @Lazy
}))
public class AuthorityService
{

    private final static Logger logger = LoggerFactory.getLogger(AuthorityService.class);

    @Resource
    private LoginDao loginDao;

    /**
     * ログイン処理
     *
     * @param loginUserResource ログインユーザー情報
     * @return JWTトークン
     * @since 1.0.0
     */
    public String login(LoginUserResource loginUserResource) {
        final String username = loginUserResource.getUsername();
        final String password = loginUserResource.getPassword();

        UsernamePasswordToken usernamePasswordToken = new UsernamePasswordToken(username, password);
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return null;
        }
        try {
            subject.login(usernamePasswordToken);
        } catch (IncorrectCredentialsException e) {
            throw new PlUnauthorizedException();
        } catch (AuthenticationException e) {
            throw new PlResourceNotFoundException(String.format("ユーザー: %s", username));
        }

        Ms200_customer ms200Customer = loginDao.getUserByName(username, "1");
        return JwtUtils.sign(JwtUtils.SECRET, ms200Customer.getUser_id());
    }

    /**
     * 新規登録
     *
     * @param jsonObject 新規登録情報
     * @return 処理結果情報
     * @since 1.0.0
     */
    public JSONObject register(JSONObject jsonObject) {
        final String username = jsonObject.getString("login_id");
        Ms200_customer existedCustomer = loginDao.getUserByName(username, null);
        if (existedCustomer != null) {
            throw new PlResourceAlreadyExistsException("ユーザー: " + username);
        }
        Ms200_customer ms200Customer = JSONObject.toJavaObject(jsonObject, Ms200_customer.class);
        ms200Customer.setUser_id(getMaxUserId());
        ms200Customer.setYoto("1");
        ms200Customer.setDel_flg(1);
        PasswordHelper.encryptPassword(ms200Customer);
        return loginDao.register(ms200Customer) > 0 ? CommonUtils.success()
            : CommonUtils.failure(ErrorCode.INTERNAL_SERVER_ERROR);
    }

    /**
     * ログアウト
     *
     * @since 1.0.0
     */
    public JSONObject logout() {
        Subject subject = SecurityUtils.getSubject();
        Session session = subject.getSession();
        session.removeAttribute("yoto");
        subject.logout();
        return CommonUtils.success();
    }

    public String getMaxUserId() {
        return loginDao.getMaxUserId() == null ? "1" : String.valueOf(Integer.parseInt(loginDao.getMaxUserId()) + 1);
    }
}
