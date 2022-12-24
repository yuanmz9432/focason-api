/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class LcForbiddenException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;

    static {
        ERROR_CODE = LcErrorCode.FORBIDDEN;
    }

    private static final String MESSAGE_TEMPLATE = "該当ユーザーが訪問禁止されている。";

    public LcForbiddenException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE);
    }

    public LcForbiddenException(Throwable cause, Object... args) {
        super(ERROR_CODE, cause, MESSAGE_TEMPLATE, args);
    }
}
