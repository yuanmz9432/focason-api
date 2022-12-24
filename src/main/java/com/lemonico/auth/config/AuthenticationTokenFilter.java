/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.auth.config;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonico.core.attribute.LcErrorCode;
import com.lemonico.core.attribute.LcErrorResource;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter
{
    @Autowired
    private JWTGenerator jwtGenerator;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 認証必要ないURI
     */
    private static final List<String> UN_AUTHORITY_PATHS =
        List.of("/api/heartbeat", "/api/auth/login", "/api/auth/register");

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        // リクエストヘッダーからトークンを取得するために、ServletResponseをHttpServletResponseに変更する。
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MDC.put("USERNAME", "ADMIN");
        // 下記URL以外であれば、ヘッダ取得処理を行わない。
        if (!UN_AUTHORITY_PATHS.contains(request.getRequestURI())) {
            // リクエストヘッダーからアクセストークンを取得する。
            var requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String subject = null;
            if (Strings.isNotBlank(requestTokenHeader)) {
                Claims accessTokenClaims;
                try {
                    // リクエストヘッダから、アクセストークン情報を洗い出す。
                    accessTokenClaims = this.jwtGenerator.getClaims(requestTokenHeader);
                } catch (ExpiredJwtException e) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
                        LcErrorResource.builder()
                            .code(LcErrorCode.AUTH_TOKEN_EXPIRED.getValue())
                            .message(LcErrorCode.AUTH_TOKEN_EXPIRED.name())
                            .build()));
                    return;
                } catch (SignatureException | MalformedJwtException e) {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
                        LcErrorResource.builder()
                            .code(LcErrorCode.AUTH_TOKEN_INVALID.getValue())
                            .message(LcErrorCode.AUTH_TOKEN_INVALID.name())
                            .build()));
                    return;
                }
                // アクセストークン情報から、SUB情報を取得
                subject = (String) accessTokenClaims.get(Claims.SUBJECT);
            }

            // SUB情報が存在し、かつ認証が取れない場合、下記処理を行う
            if (Strings.isNotBlank(subject) && SecurityContextHolder.getContext().getAuthentication() == null) {
                // TODO fix user check logic
                // var loginUser = service.getLoginUserBySubject(subject);
                // if (!Objects.isNull(loginUser)
                // && (subject.equals(loginUser.getUuid()))) {
                // var authentication =
                // new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                // authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // SecurityContextHolder.getContext().setAuthentication(authentication);
                // // MDCにUUIDとユーザ名を設定する
                // MDC.put("UUID", loginUser.getUuid());
                // MDC.put("USERNAME", loginUser.getUsername());
                // }
            }
        }

        chain.doFilter(request, response);
    }


}
