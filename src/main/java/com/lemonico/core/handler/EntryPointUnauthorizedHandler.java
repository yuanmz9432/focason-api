/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.handler;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemonico.core.attribute.PlErrorCode;
import com.lemonico.core.attribute.PlErrorResource;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class EntryPointUnauthorizedHandler
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public void commence(HttpServletRequest request, HttpServletResponse response)
        throws IOException {
        response.getWriter().print(OBJECT_MAPPER.writeValueAsString(
            PlErrorResource.builder()
                .code(PlErrorCode.AUTH_TOKEN_INVALID.getValue())
                .message(PlErrorCode.AUTH_TOKEN_INVALID.name())
                .build()));

    }
}
