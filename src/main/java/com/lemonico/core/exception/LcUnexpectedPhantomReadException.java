/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;



public class LcUnexpectedPhantomReadException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Unexpected phantom read has occurred.";

    static {
        ERROR_CODE = LcErrorCode.INTERNAL_SERVER_ERROR;
    }

    public LcUnexpectedPhantomReadException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE, new Object());
    }
}
