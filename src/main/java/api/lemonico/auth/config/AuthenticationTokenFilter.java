/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.config;



import api.lemonico.common.JsonUtil;
import api.lemonico.core.exception.LcErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.util.Objects;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter
{

    @Autowired
    private JWTProperties properties;

    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserDetailsService userDetailsService;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        // リクエストヘッダーからトークンを取得するために、ServletResponseをHttpServletResponseに変更する。
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;

        // TODO
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");

        // リクエストヘッダーからアクセストークンを取得する。
        var accessToken = request.getHeader(properties.getAccessTokenHeader());
        String email = null;
        if (Objects.nonNull(accessToken) && !"".equals(accessToken)) {
            // 解析access_token
            Claims accessTokenClaims;
            try {
                accessTokenClaims = this.jwtGenerator.getClaims(accessToken);
            } catch (ExpiredJwtException e) {
                JsonUtil.writeJson(response, LcErrorCode.AUTH_TOKEN_EXPIRED, null);
                return;
            } catch (SignatureException | MalformedJwtException e) {
                JsonUtil.writeJson(response, LcErrorCode.AUTH_TOKEN_INVALID, null);
                return;
            }
            email = (String) accessTokenClaims.get("sub");
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            LoginUser loginUser = (LoginUser) this.userDetailsService.loadUserByUsername(email);
            if (loginUser != null && !loginUser.isEnabled()) {
                JsonUtil.writeJson(response, LcErrorCode.FORBIDDEN, null);
                return;
            }
            if (email.equals(loginUser.getUsername())) {
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }


}
