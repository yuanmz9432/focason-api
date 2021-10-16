package api.lemonico.core.handler;



import api.lemonico.common.JsonUtil;
import api.lemonico.core.exception.LcErrorCode;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAccessDeniedHandler implements AccessDeniedHandler
{
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        JsonUtil.writeJson(response, LcErrorCode.FORBIDDEN, null);
    }
}
