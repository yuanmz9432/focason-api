package api.lemonico.core.auth;

import api.lemonico.common.JsonUtil;
import api.lemonico.core.exception.LcErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

public class AuthenticationTokenFilter extends UsernamePasswordAuthenticationFilter {

    @Autowired
    private JWTProperties properties;

    @Autowired
	private JWTGenerator jwtGenerator;

    @Autowired
	private UserDetailsService userDetailsService;
	
	@Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        // 将 ServletRequest 转换为 HttpServletRequest 才能拿到请求头中的 token
        HttpServletResponse response = (HttpServletResponse)servletResponse;
        HttpServletRequest request = (HttpServletRequest)servletRequest;

        // 解决跨域问题 TODO
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Allow-Headers", "*");

        // 从请求头获取access_token
        String accessToken = request.getHeader(properties.getAccessTokenHeader());
        String email = null;
        if (Objects.nonNull(accessToken) && !"".equals(accessToken)) {
            // 解析access_token
            Claims accessTokenClaims;
            try{
                accessTokenClaims = this.jwtGenerator.getClaims(accessToken);
            } catch (ExpiredJwtException e) {
                JsonUtil.writeJson(response, LcErrorCode.AUTH_TOKEN_EXPIRED, null);
                return;
            } catch (SignatureException | MalformedJwtException e) {
                JsonUtil.writeJson(response, LcErrorCode.AUTH_TOKEN_INVALID, null);
                return;
            }

            // 解析
            // 尝试拿 token 中的 username
            // 若是没有 token 或者拿 username 时出现异常，那么 username 为 null
            email = (String) accessTokenClaims.get("sub");
        }

        // 如果上面解析 token 成功并且拿到了 username 并且本次会话的权限还未被写入
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 用 UserDetailsService 从数据库中拿到用户的 UserDetails 类
            // UserDetails 类是 Spring Security 用于保存用户权限的实体类
            LoginUser loginUser = (LoginUser) this.userDetailsService.loadUserByUsername(email);
            // 检查用户带来的 token 是否有效
            // 包括 token 和 userDetails 中用户名是否一样， token 是否过期， token 生成时间是否在最后一次密码修改时间之前
            // 若是检查通过
            if (loginUser != null && !loginUser.isEnabled()){
                JsonUtil.writeJson(response, LcErrorCode.FORBIDDEN, null);
                return;
            }
            if (email.equals(loginUser.getUsername())) {
                // 生成通过认证
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // 将权限写入本次会话
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }


}
