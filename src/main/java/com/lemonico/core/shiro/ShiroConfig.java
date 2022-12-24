package com.lemonico.core.shiro;



import com.lemonico.core.utils.PasswordHelper;
import java.util.*;
import javax.servlet.Filter;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.FirstSuccessfulStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.cache.MemoryConstrainedCacheManager;
import org.apache.shiro.cache.ehcache.EhCacheManager;
import org.apache.shiro.mgt.DefaultSessionStorageEvaluator;
import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.filter.authc.AnonymousFilter;
import org.apache.shiro.web.filter.authc.LogoutFilter;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

/**
 * Shiroコンフィグ
 *
 * @since 1.0.0
 */
@Configuration
public class ShiroConfig
{

    /**
     * Shiro lifecycle processors
     */
    @Bean
    public LifecycleBeanPostProcessor lifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * Open Shiro's annotations Configure the following two
     * beans(DefaultAdvisorAutoProxyCreator和AuthorizationAttributeSourceAdvisor)この機能を実現できる
     */
    @Bean
    @DependsOn({
        "lifecycleBeanPostProcessor"
    })
    public DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator advisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        advisorAutoProxyCreator.setProxyTargetClass(true);
        return advisorAutoProxyCreator;
    }

    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor() {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor =
            new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(defaultWebSecurityManager());
        return authorizationAttributeSourceAdvisor;
    }

    /**
     * SpringコンテナにJwtFilter
     * <p/>
     * Beanを登録しないでください。SpringがJwtFilterをグローバルフィルターとして登録しないようにする。
     *
     * @param filter {@link JwtFilter}
     * @return {@link FilterRegistrationBean}
     * @since 1.0.0
     */
    @Bean
    public FilterRegistrationBean<Filter> registration(JwtFilter filter) {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false);
        return registration;
    }

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter();
    }

    @Bean
    protected SessionStorageEvaluator sessionStorageEvaluator() {
        DefaultSessionStorageEvaluator sessionStorageEvaluator = new DefaultSessionStorageEvaluator();
        sessionStorageEvaluator.setSessionStorageEnabled(false);
        return sessionStorageEvaluator;
    }

    @Bean(name = "defaultWebSecurityManager")
    public DefaultWebSecurityManager defaultWebSecurityManager() {
        DefaultWebSecurityManager defaultWebSecurityManager = new DefaultWebSecurityManager();
        defaultWebSecurityManager.setAuthenticator(authenticator());
        List<Realm> realms = new ArrayList<>(16);
        realms.add(jwtRealm());
        realms.add(userRealm());
        defaultWebSecurityManager.setRealms(realms);
        // 关闭shiroDao 功能
        // shiroDao 機能をオフにする
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
        defaultWebSecurityManager.setSubjectDAO(subjectDAO);
        return defaultWebSecurityManager;
    }

    @Bean(name = "shiroFilter")
    public ShiroFilterFactoryBean shiroFilterFactoryBean(
        @Qualifier("defaultWebSecurityManager") DefaultWebSecurityManager defaultWebSecurityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(defaultWebSecurityManager);
        shiroFilterFactoryBean.setLoginUrl("/login");
        shiroFilterFactoryBean.setUnauthorizedUrl("/unauth");

        // 添加JWT过滤器
        // JWTフィルターを追加する
        HashMap<String, Filter> filterMap = new HashMap<>();
        filterMap.put("anon", new AnonymousFilter());
        filterMap.put("/api/auth/login", new AnonymousFilter());
        filterMap.put("jwt", new JwtFilter());
        filterMap.put("logout", new LogoutFilter());
        filterMap.put("/api/auth/register", new AnonymousFilter());
        filterMap.put("/api/v2/api-docs", new AnonymousFilter());
        filterMap.put("/api/configuration/**", new AnonymousFilter());
        shiroFilterFactoryBean.setFilters(filterMap);
        // 添加shiro过滤器
        Map<String, String> map = new LinkedHashMap<>();
        map.put("/api/druid/**", "anon");
        map.put("/api/actuator/**", "anon");
        map.put("/api/heartbeat", "anon");
        map.put("/api/sendEmail", "anon");
        map.put("/api/auth/login", "anon");
        map.put("/api/auth/logout", "anon");
        map.put("/api/auth/register", "anon");
        map.put("/api/auth/login/store", "anon");
        map.put("/api/auth/login/warehouse", "anon");
        map.put("/api/zip/info/token", "anon");
        map.put("/api/prefectures/token", "anon");
        map.put("/api/orders/**", "anon");
        map.put("/api/doc.html", "anon");
        map.put("/api/v2/**", "anon");
        map.put("/api/webjars/**", "anon");
        map.put("/api/configuration/**", "anon");
        map.put("/api/products", "anon");
        map.put("/api/warehouses", "anon");
        map.put("/api/warehouses/**", "anon");
        map.put("/api/login/check", "anon");
        map.put("/api/login/user/save", "anon");
        map.put("/api/login/client/insert", "anon");
        map.put("/api/login/changeUserStatus", "anon");
        map.put("/api/login/change/password", "anon");
        map.put("/api/login/warehouse/regist", "anon");
        map.put("/api/login/client/info", "anon");
        map.put("/api/login/email/address", "anon");
        map.put("/api/order/import/ftp/csv", "anon");
        map.put("/api/wms/setting/smart/windows/list", "anon");
        map.put("/api/wms/setting/smart/windows", "anon");
        map.put("/api/login/getNewsInfo", "anon");
        // 郵件接口測試
        // map.put("/api/client/mailTest", "anon");
        // PC端所需接口
        map.put("/api/pc/item/getImagePath", "anon");
        map.put("/api/wms/pc/shipments/incidents/**/**", "anon");
        map.put("/api/pc/delivery/**", "anon");
        map.put("/api/pc/delivery/delivery_time", "anon");
        map.put("/api/wms/pc/shipments/serial_no/**", "anon");
        map.put("/api/wms/pc/shipments/empty/serial_no/**", "anon");
        // ntm所需要的接口
        map.put("/api/stock/months", "anon");
        map.put("/api/stock/weeks", "anon");
        map.put("/api/DeliveryRelation/**", "anon");
        // 错误数据查询接口
        map.put("/api/error-data/excel", "anon");
        map.put("/api/**", "jwt");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

    @Bean
    public ModularRealmAuthenticator authenticator() {
        ModularRealmAuthenticator authenticator = new MultiRealmAuthenticator();
        AuthenticationStrategy strategy = new FirstSuccessfulStrategy();
        authenticator.setAuthenticationStrategy(strategy);
        return authenticator;
    }

    @Bean
    JwtRealm jwtRealm() {
        JwtRealm jwtRealm = new JwtRealm();
        CredentialsMatcher jwtCredentialsMatcher = new JwtCredentialsMatcher();
        jwtRealm.setCredentialsMatcher(jwtCredentialsMatcher);
        return jwtRealm;
    }

    @Bean
    public MyRealm userRealm() {
        MyRealm userRealm = new MyRealm();

        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher("SHA-1");
        credentialsMatcher.setHashIterations(16);
        userRealm.setCredentialsMatcher(hashedCredentialsMatcher());
        return userRealm;
    }

    @Bean
    public HashedCredentialsMatcher hashedCredentialsMatcher() {
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        // Hash algorithm
        hashedCredentialsMatcher.setHashAlgorithmName(PasswordHelper.ALGORITHM_NAME);
        // Hash number
        hashedCredentialsMatcher.setHashIterations(PasswordHelper.HASH_ITERATIONS);
        hashedCredentialsMatcher.setStoredCredentialsHexEncoded(true);
        return hashedCredentialsMatcher;
    }

    @Bean
    public PasswordHelper passwordHelper() {
        return new PasswordHelper();
    }

    /**
     * Configure the session cache manager
     */
    @Bean(name = "shiroCacheManager")
    public MemoryConstrainedCacheManager getMemoryConstrainedCacheManager() {
        return new MemoryConstrainedCacheManager();
    }

    @Bean(name = "cacheManager")
    public EhCacheManager cacheManager() {
        return new EhCacheManager();
    }

}
