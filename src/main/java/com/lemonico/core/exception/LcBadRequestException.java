/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class LcBadRequestException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;

    static {
        ERROR_CODE = LcErrorCode.BAD_REQUEST;
    }

    public LcBadRequestException(String messageTemplate, Object... args) {
        super(ERROR_CODE, messageTemplate, args);
    }

    public LcBadRequestException(Throwable cause, String messageTemplate, Object... args) {
        super(ERROR_CODE, cause, messageTemplate, args);
    }
}
