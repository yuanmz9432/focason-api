/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.config;



import api.lemonico.core.attribute.LcErrorCode;
import api.lemonico.core.attribute.LcErrorResource;
import api.lemonico.domain.UserType;
import api.lemonico.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import java.io.IOException;
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
    private JwtProps jwtProps;

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

        // リクエストヘッダーからアクセストークンを取得する。
        var accessToken = request.getHeader(HttpHeaders.AUTHORIZATION);
        String email = null;
        if (Strings.isNotBlank(accessToken)) {
            Claims accessTokenClaims;
            try {
                accessTokenClaims = this.jwtGenerator.getClaims(accessToken);
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
            email = (String) accessTokenClaims.get(Claims.SUBJECT);
        }

        if (Strings.isNotBlank(email) && SecurityContextHolder.getContext().getAuthentication() == null) {
            var userResource = service.getResourceByEmail(email);
            if (userResource.isPresent()) {
                if (UserType.LOGOUT.equals(UserType.of(userResource.get().getType()))) {
                    response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
                        LcErrorResource.builder()
                            .code(LcErrorCode.FORBIDDEN.getValue())
                            .message(LcErrorCode.FORBIDDEN.name())
                            .build()));
                    return;
                }
                if (email.equals(userResource.get().getEmail())) {
                    var authentication = new UsernamePasswordAuthenticationToken(userResource, null, null);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    MDC.put("CLIENT_CODE", userResource.get().getUuid());
                }
            }
        }

        chain.doFilter(request, response);
    }


}
