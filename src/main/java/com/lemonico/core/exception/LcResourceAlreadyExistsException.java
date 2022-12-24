/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.core.exception;



public class LcResourceAlreadyExistsException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "『%s』は既に存在しました。";

    static {
        ERROR_CODE = LcErrorCode.DUPLICATE_DATA;
    }

    public LcResourceAlreadyExistsException(String value) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, value);
    }
}
