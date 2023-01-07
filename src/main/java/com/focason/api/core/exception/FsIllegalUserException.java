/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class FsIllegalUserException extends FsException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "The User('%s') was enable.";

    static {
        ERROR_CODE = FsErrorCode.ILLEGAL_STATE;
    }

    public FsIllegalUserException(String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, email);
    }
}
