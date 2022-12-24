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
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
            LcErrorResource.builder()
                .code(LcErrorCode.MISSING_AUTH_TOKEN.getValue())
                .message(LcErrorCode.MISSING_AUTH_TOKEN.name())
                .build()));

    }
}
