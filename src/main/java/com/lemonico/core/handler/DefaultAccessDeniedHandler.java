/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.handler;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonico.core.attribute.LcErrorCode;
import com.lemonico.core.attribute.LcErrorResource;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class DefaultAccessDeniedHandler implements AccessDeniedHandler
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
            LcErrorResource.builder()
                .code(LcErrorCode.FORBIDDEN.getValue())
                .message(LcErrorCode.FORBIDDEN.name())
                .build()));
    }
}
