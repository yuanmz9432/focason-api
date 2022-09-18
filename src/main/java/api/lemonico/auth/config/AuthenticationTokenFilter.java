/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.config;



import api.lemonico.core.attribute.LcErrorCode;
import api.lemonico.core.attribute.LcErrorResource;
import api.lemonico.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

@Slf4j
public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter
{
    @Autowired
    private JWTGenerator jwtGenerator;

    @Autowired
    private UserService service;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        // リクエストヘッダーからトークンを取得するために、ServletResponseをHttpServletResponseに変更する。
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // TODO 下記URLであれば、ヘッダ取得処理を行わない。
        // リクエストヘッダーからアクセストークンを取得する。
        var requestTokenHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        String subject = null;
        if (Strings.isNotBlank(requestTokenHeader)) {
            Claims accessTokenClaims;
            try {
                accessTokenClaims = this.jwtGenerator.getClaims(requestTokenHeader);
            } catch (ExpiredJwtException e) {
                response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
                    LcErrorResource.builder()
                        .code(LcErrorCode.AUTH_TOKEN_EXPIRED.getValue())
                        .message(LcErrorCode.AUTH_TOKEN_EXPIRED.name())
                        .build()));
                return;
            } catch (SignatureException | MalformedJwtException e) {
                response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
                    LcErrorResource.builder()
                        .code(LcErrorCode.AUTH_TOKEN_INVALID.getValue())
                        .message(LcErrorCode.AUTH_TOKEN_INVALID.name())
                        .build()));
                return;
            }
            subject = (String) accessTokenClaims.get(Claims.SUBJECT);
        }

        if (Strings.isNotBlank(subject) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var loginUser = service.getLoginUserBySubject(subject);
            if (!Objects.isNull(loginUser)
                && (subject.equals(loginUser.getUsername()) || subject.equals(loginUser.getEmail()))) {
                var authentication =
                    new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                MDC.put("USERNAME", loginUser.getUsername());
            }
        }

        chain.doFilter(request, response);
    }


}
