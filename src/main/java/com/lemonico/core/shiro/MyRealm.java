package com.lemonico.core.shiro;



import com.lemonico.common.bean.Ms200_customer;
import com.lemonico.common.service.LoginService;
import java.util.Collection;
import java.util.HashSet;
import javax.annotation.Resource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @className: MyRealm
 * @description: Custom realm
 * @date: 2020/05/08 9:20
 **/
@Component("myRealm")
public class MyRealm extends AuthorizingRealm
{

    private final static Logger logger = LoggerFactory.getLogger(MyRealm.class);

    @Resource
    private LoginService loginService;

    /**
     * @Param: token
     * @description: supportsを書き改める、UsernamePassword Tokenのみを処理する
     * @return: boolean
     * @date: 2020/05/19
     */
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof UsernamePasswordToken;
    }

    /**
     * @Param: principalCollection
     * @description: 判断権限
     * @return: org.apache.shiro.authz.AuthorizationInfo
     * @date: 2020/05/19
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Subject subject = SecurityUtils.getSubject();
        // check permission
        Collection<String> objects = new HashSet<>();
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.addStringPermissions(objects);
        return simpleAuthorizationInfo;
    }

    /**
     * @Param: token ： フィルターから転送したJwtToken
     * @description: Username verification
     * @return: org.apache.shiro.authc.AuthenticationInfo
     * @date: 2020/05/07
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken auth) throws AuthenticationException {
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) auth;
        String userName = usernamePasswordToken.getUsername();
        Ms200_customer userByName = loginService.getUserByName(userName, "1");
        if (userByName == null) {
            logger.error("登录Id：" + userName + "---->账户不存在");
            throw new AuthenticationException();
        }
        return new SimpleAuthenticationInfo(userName, userByName.getLogin_pw(),
            ByteSource.Util.bytes(userByName.getCredentialsSalt()),
            getName());
    }
}
