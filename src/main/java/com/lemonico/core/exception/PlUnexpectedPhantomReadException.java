/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;



public class PlUnexpectedPhantomReadException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Unexpected phantom read has occurred.";

    static {
        ERROR_CODE = PlErrorCode.INTERNAL_SERVER_ERROR;
    }

    public PlUnexpectedPhantomReadException() {
        super(ERROR_CODE, MESSAGE_TEMPLATE, new Object());
    }
}
