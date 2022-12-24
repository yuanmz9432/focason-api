/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class PlUnauthorizedException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;

    static {
        ERROR_CODE = PlErrorCode.UNAUTHORIZED;
    }

    private static final String MESSAGE_TEMPLATE = "パスワードエラーで、もう一回ご入力ください。";

    public PlUnauthorizedException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE);
    }

    public PlUnauthorizedException(PlErrorCode plErrorCode, String message) {
        super(plErrorCode, message);
    }

    public PlUnauthorizedException(Throwable cause, Object... args) {
        super(ERROR_CODE, cause, MESSAGE_TEMPLATE, args);
    }
}
