/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.configurer;



import api.lemonico.auth.config.AuthenticationTokenFilter;
import api.lemonico.core.handler.DefaultAccessDeniedHandler;
import api.lemonico.core.handler.EntryPointUnauthorizedHandler;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class WebSecurityConfigurer
{
    private EntryPointUnauthorizedHandler unauthorizedHandler;
    private DefaultAccessDeniedHandler accessDeniedHandler;
    private AuthenticationConfiguration authenticationConfiguration;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public AuthenticationTokenFilter authenticationTokenFilterBean() throws Exception {
        AuthenticationTokenFilter authenticationTokenFilter = new AuthenticationTokenFilter();
        authenticationTokenFilter.setAuthenticationManager(authenticationManager());
        return authenticationTokenFilter;
    }

    private static final String[] AUTH_WHITELIST = {
        "/swagger-resources/**",
        "/swagger-ui.html",
        "/swagger-ui/**",
        "/v2/api-docs",
        "/v3/api-docs",
        "/webjars/**",
        "/heartbeat",
        "/auth/**",
        "/actuator/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeRequests()
            // OPTIONS请求全部放行
            .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // 登录接口放行
            .antMatchers(AUTH_WHITELIST).permitAll()
            // 権限設定
            .antMatchers("/users/**").hasAnyAuthority("AUTH_USER")
            // 其他接口全部接受验证
            .anyRequest().authenticated()
            .and()
            // 配置被拦截时的处理
            .exceptionHandling()
            .authenticationEntryPoint(this.unauthorizedHandler) // 添加 token 无效或者没有携带 token 时的处理
            .accessDeniedHandler(this.accessDeniedHandler) // 添加无权限时的处理
            .and()
            .csrf().disable() // 禁用 Spring Security 自带的跨域处理
            .sessionManagement() // 定制我们自己的 session 策略
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)// 调整为让 Spring Security 不创建和使用 session
            .and().addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }
}
