/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.handler;



import com.focason.api.core.attribute.FsErrorCode;
import com.focason.api.core.attribute.FsErrorResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Component
public class FsDefaultAccessDeniedHandler implements AccessDeniedHandler
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
            FsErrorResource.builder()
                .code(FsErrorCode.FORBIDDEN.getValue())
                .message(FsErrorCode.FORBIDDEN.name())
                .build()));
    }
}
