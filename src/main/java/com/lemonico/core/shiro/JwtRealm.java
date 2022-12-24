package com.lemonico.core.shiro;



import com.lemonico.common.bean.Ms200_customer;
import com.lemonico.common.bean.Ms201_client;
import com.lemonico.common.service.ClientService;
import com.lemonico.common.service.LoginService;
import com.lemonico.core.exception.PlForbiddenException;
import com.lemonico.core.utils.StringTools;
import com.lemonico.wms.dao.WarehouseCustomerDao;
import javax.annotation.Resource;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @className: JwtRealm
 * @description: jwtRealm
 * @date: 2020/05/19 19:54
 **/
public class JwtRealm extends AuthorizingRealm
{

    private final static Logger logger = LoggerFactory.getLogger(JwtRealm.class);

    @Resource
    private LoginService loginService;

    @Resource
    private ClientService clientService;

    @Resource
    private WarehouseCustomerDao warehouseCustomerDao;

    // このレルムをJwtTokenのみを処理するように制限する
    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof JwtToken;
    }

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
        throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        // JwtToken から現在のユーザを取得する
        String client_id = null;
        if (!StringTools.isNullOrEmpty(jwtToken.getPrincipal())) {
            client_id = jwtToken.getClientId();
        }
        String user_id = JwtUtils.getClaimFiled((String) jwtToken.getCredentials(), "user_id");
        Ms200_customer userByUserId = loginService.getUserByUserId(user_id);
        String warehouse_cd = JwtUtils.getClaimFiled((String) jwtToken.getCredentials(), "warehouse_cd");
        String yoto = JwtUtils.getClaimFiled((String) jwtToken.getCredentials(), "yoto");
        if (!userByUserId.getYoto().equals(yoto)) {
            throw new PlForbiddenException();
        }
        if (warehouse_cd == null) {
            Ms201_client customerGroupInfo = clientService.getClientInfo(client_id);
            if (customerGroupInfo == null) {
                logger.error("登录Id：" + client_id + "---->店铺不存在");
                throw new AuthenticationException();
            }
        }
        if (warehouse_cd != null) {
            String warehouseId = warehouseCustomerDao.getWarehouseId(user_id, warehouse_cd);
            if (warehouseId == null) {
                logger.error("登录Id：" + client_id + "---->仓库不存在");
                throw new AuthenticationException();
            }
        }

        return new SimpleAuthenticationInfo(userByUserId.getLogin_id(), userByUserId.getLogin_pw(),
            ByteSource.Util.bytes(userByUserId.getCredentialsSalt()),
            getName());
    }
}
