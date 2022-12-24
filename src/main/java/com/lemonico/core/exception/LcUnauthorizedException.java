/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class LcUnauthorizedException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;

    static {
        ERROR_CODE = LcErrorCode.UNAUTHORIZED;
    }

    private static final String MESSAGE_TEMPLATE = "パスワードエラーで、もう一回ご入力ください。";

    public LcUnauthorizedException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE);
    }

    public LcUnauthorizedException(LcErrorCode lcErrorCode, String message) {
        super(lcErrorCode, message);
    }

    public LcUnauthorizedException(Throwable cause, Object... args) {
        super(ERROR_CODE, cause, MESSAGE_TEMPLATE, args);
    }
}
