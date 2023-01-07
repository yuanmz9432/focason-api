/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class BaUnexpectedPhantomReadException extends BaException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Unexpected phantom read has occurred.";

    static {
        ERROR_CODE = FsErrorCode.INTERNAL_SERVER_ERROR;
    }

    public BaUnexpectedPhantomReadException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE, new Object());
    }
}
