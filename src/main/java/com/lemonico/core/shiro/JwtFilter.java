package com.lemonico.core.shiro;



import java.io.PrintWriter;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * リクエストチェック処理
 *
 * @since 1.0.0
 */
public class JwtFilter extends BasicHttpAuthenticationFilter
{

    private final static Logger logger = LoggerFactory.getLogger(BasicHttpAuthenticationFilter.class);

    // 前置处理
    @Override
    protected boolean preHandle(ServletRequest request, ServletResponse response) throws Exception {
        HttpServletRequest httpServletRequest = WebUtils.toHttp(request);
        HttpServletResponse httpServletResponse = WebUtils.toHttp(response);
        // クロスドメインをするとき、最初にオプション リクエストを送信する。ここでは、オプション リクエストの通常の状態に直接戻す。
        if (httpServletRequest.getMethod().equals(RequestMethod.OPTIONS.name())) {
            httpServletResponse.setStatus(HttpStatus.OK.value());
            return false;
        }
        return super.preHandle(request, response);
    }

    // 后置处理
    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        // クロスドメインサポートを追加する
        this.fillCorsHeader(WebUtils.toHttp(request), WebUtils.toHttp(response));
    }

    /**
     * フィルターブロックの解除方法
     * trueを返す場合は、アクセスを許可する
     * falseを返す場合は、アクセスを禁止する。onAccessDenied（）に入る
     */
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) {
        // もとはログイン要求するかどうかを判断しする。本例では登録要求をブロックしない。ヘッダーにJWT トークンフィールドが含まれているかどうかを検出するために使用される
        if (this.isLoginRequest(request, response)) {
            return false;
        }
        boolean allowed = false;
        try {
            // ヘッダーのJWT トークンの内容が正しいかどうかを確認し、トークンを使用してログインしてみる
            allowed = executeLogin(request, response);
        } catch (IllegalStateException e) { // not found any token
            logger.error("Not found any token");
        } catch (Exception e) {
            logger.error("Error occurs when login", e);
        }
        return allowed || super.isPermissive(mappedValue);
    }

    // ヘッダーにJWT トークン フィールドが含まれているかどうかを確認する
    @Override
    protected boolean isLoginAttempt(ServletRequest request, ServletResponse response) {
        return ((HttpServletRequest) request).getHeader(JwtUtils.AUTH_HEADER) == null;
    }


    // 身分検証は、JWTトークンが正当かどうかを検証する
    @Override
    protected boolean executeLogin(ServletRequest request, ServletResponse response) {
        AuthenticationToken token = createToken(request, response);
        if (token == null) {
            String msg = "createToken method implementation returned null. A valid non-null AuthenticationToken "
                + "must be created in order to execute a login attempt.";
            throw new IllegalStateException(msg);
        }

        try {
            Subject subject = getSubject(request, response);
            // Shiroにログイン検証を渡す
            subject.login(token);
            return onLoginSuccess(token, subject, request, response);
        } catch (AuthenticationException e) {
            return onLoginFailure(token, e, request, response);
        }
    }

    // 从 Header 里提取 JWT token
    // ヘッダーからJWT トークンを抽出する
    @Override
    protected AuthenticationToken createToken(ServletRequest servletRequest, ServletResponse servletResponse) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String authorization = httpServletRequest.getHeader(JwtUtils.AUTH_HEADER);
        return new JwtToken(authorization);
    }

    // isAccess Allowed（）メソッドはfalseに戻って、このメソッドに入れる。アクセスが拒否されるという意味である
    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        HttpServletResponse httpResponse = WebUtils.toHttp(servletResponse);
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.setContentType("application/json;charset=UTF-8");
        httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
        PrintWriter writer = httpResponse.getWriter();
        writer.write("{\"errCode\": 401, \"msg\": \"UNAUTHORIZED\"}");
        fillCorsHeader(WebUtils.toHttp(servletRequest), httpResponse);
        return false;
    }

    // ShiroはJWT トークンを利用して、ログインに成功すると、このメソッドに入れる
    @Override
    protected boolean onLoginSuccess(AuthenticationToken token, Subject subject, ServletRequest request,
        ServletResponse response) {
        HttpServletResponse httpResponse = WebUtils.toHttp(response);
        String newToken = null;
        if (token instanceof JwtToken) {
            newToken = JwtUtils.refreshTokenExpired(token.getCredentials().toString(), JwtUtils.SECRET);
        }
        if (newToken != null) {
            httpResponse.setHeader(JwtUtils.AUTH_HEADER, newToken);
        }
        return true;
    }

    // ShiroはJWT トークンを利用して、登録に失敗すると、このメソッドに入れる
    @Override
    protected boolean onLoginFailure(AuthenticationToken token, AuthenticationException e, ServletRequest request,
        ServletResponse response) {
        // ここではそのままfalseを戻って、後のonAccessDenied()メソッドに処理を渡す
        return false;
    }

    // クロスドメインサポート
    protected void fillCorsHeader(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        httpServletResponse.setHeader("Access-control-Allow-Origin", httpServletRequest.getHeader("Origin"));
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "PUT,DELETE,GET,POST,OPTIONS,HEAD");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
            httpServletRequest.getHeader("Access-Control-Request-Headers"));
    }

}
