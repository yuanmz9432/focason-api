package api.lemonico.common;

import api.lemonico.core.exception.LcErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class JsonUtil {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void writeJson(HttpServletResponse response, LcErrorCode lcErrorCode, Object data) throws IOException {

        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.UNAUTHORIZED.value());

        Map<String, Object> params = new HashMap<>();
        params.put("code", lcErrorCode.getValue());
        params.put("message", lcErrorCode.name());
        params.put("data", data);

        response.getWriter().print(OBJECT_MAPPER.writeValueAsString(params));
    }
}
