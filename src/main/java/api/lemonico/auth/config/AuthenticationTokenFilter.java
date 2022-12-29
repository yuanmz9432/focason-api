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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.server.resource.BearerTokenError;
import org.springframework.security.oauth2.server.resource.BearerTokenErrors;
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

    private static final Pattern AUTHORIZATION_PATTERN =
        Pattern.compile("^Bearer (?<token>[a-zA-Z0-9-:._~+/]+=*)$", Pattern.CASE_INSENSITIVE);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
        throws IOException, ServletException {
        // リクエストヘッダーからトークンを取得するために、ServletResponseをHttpServletResponseに変更する。
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        MDC.put("USERNAME", "ADMIN");
        var authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        // 下記URL以外であれば、ヘッダ取得処理を行わない。
        if (Strings.isNotBlank(authorizationHeader)) {
            // リクエストヘッダーからアクセストークンを取得する。
            Matcher matcher = AUTHORIZATION_PATTERN.matcher(authorizationHeader);
            if (!matcher.matches()) {
                BearerTokenError error = BearerTokenErrors.invalidToken("Bearer token is malformed");
                throw new OAuth2AuthenticationException(error);
            }
            var jwtToken = matcher.group("token");
            String subject = null;
            if (Strings.isNotBlank(jwtToken)) {
                Claims accessTokenClaims;
                try {
                    // リクエストヘッダから、アクセストークン情報を洗い出す。
                    accessTokenClaims = this.jwtGenerator.getClaims(jwtToken);
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
                var loginUser = service.getLoginUserBySubject(subject);
                if (!Objects.isNull(loginUser)
                    && (subject.equals(loginUser.getUuid()))) {
                    var authentication =
                        new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    // MDCにUUIDとユーザ名を設定する
                    MDC.put("UUID", loginUser.getUuid());
                    MDC.put("USERNAME", loginUser.getUsername());
                }
            }
        }

        chain.doFilter(request, response);
    }


}
