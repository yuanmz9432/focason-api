package com.lemonico.core.shiro;



import java.util.Collection;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.realm.Realm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @className: MultiRealmAuthenticator
 * @description: ベリファイアをカスタマイズする、Shiroが異常に戻れない問題を解決する
 * @date: 2020/05/19 19:59
 **/
public class MultiRealmAuthenticator extends ModularRealmAuthenticator
{

    private final static Logger logger = LoggerFactory.getLogger(ModularRealmAuthenticator.class);

    @Override
    protected AuthenticationInfo doMultiRealmAuthentication(Collection<Realm> realms, AuthenticationToken token)
        throws AuthenticationException {
        AuthenticationStrategy strategy = getAuthenticationStrategy();
        AuthenticationInfo aggregate = strategy.beforeAllAttempts(realms, token);
        AuthenticationException authenticationException = null;
        for (Realm realm : realms) {
            aggregate = strategy.beforeAttempt(realm, token, aggregate);
            if (realm.supports(token)) {
                AuthenticationInfo info = null;
                try {
                    info = realm.getAuthenticationInfo(token);
                } catch (AuthenticationException e) {
                    authenticationException = e;
                }
                aggregate = strategy.afterAttempt(realm, token, info, aggregate, authenticationException);
            }
        }
        if (authenticationException != null) {
            throw authenticationException;
        }
        aggregate = strategy.afterAllAttempts(token, aggregate);
        return aggregate;
    }

}
