/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;


public class PlValidationErrorException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;

    static {
        ERROR_CODE = PlErrorCode.VALIDATION_ERROR;
    }

    public PlValidationErrorException(String messageTemplate, Object... args) {
        super(ERROR_CODE, messageTemplate, args);
    }

    public PlValidationErrorException(Throwable cause, String messageTemplate, Object... args) {
        super(ERROR_CODE, cause, messageTemplate, args);
    }
}
