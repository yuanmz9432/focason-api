/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.BaErrorCode;

public class BaIllegalUserException extends BaException
{
    private static final long serialVersionUID = 1L;
    private static final BaErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "The User('%s') was enable.";

    static {
        ERROR_CODE = BaErrorCode.ILLEGAL_STATE;
    }

    public BaIllegalUserException(String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, email);
    }
}
