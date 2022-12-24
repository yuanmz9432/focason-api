/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;



public class PlResourceAlreadyExistsException extends PlException
{
    private static final long serialVersionUID = 1L;
    private static final PlErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "『%s』は既に存在しました。";

    static {
        ERROR_CODE = PlErrorCode.DUPLICATE_DATA;
    }

    public PlResourceAlreadyExistsException(String value) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, value);
    }
}
