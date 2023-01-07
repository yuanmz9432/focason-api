/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class FsValidationErrorException extends FsException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;

    static {
        ERROR_CODE = FsErrorCode.VALIDATION_ERROR;
    }

    public FsValidationErrorException(String messageTemplate, Object... args) {
        super(ERROR_CODE, messageTemplate, args);
    }

    public FsValidationErrorException(Throwable cause, String messageTemplate, Object... args) {
        super(ERROR_CODE, cause, messageTemplate, args);
    }
}
