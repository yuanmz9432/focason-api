/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class PlForbiddenException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;

    static {
        ERROR_CODE = PlErrorCode.FORBIDDEN;
    }

    private static final String MESSAGE_TEMPLATE = "該当ユーザーが訪問禁止されている。";

    public PlForbiddenException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE);
    }

    public PlForbiddenException(Throwable cause, Object... args) {
        super(ERROR_CODE, cause, MESSAGE_TEMPLATE, args);
    }
}
