package api.lemonico.core.config;



import api.lemonico.common.JsonUtil;
import api.lemonico.core.exception.LcErrorCode;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint
{
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
        throws IOException, ServletException {
        JsonUtil.writeJson(response, LcErrorCode.AUTH_TOKEN_INVALID, null);
    }
}
