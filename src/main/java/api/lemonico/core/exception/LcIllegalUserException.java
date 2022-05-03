/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.exception;



import api.lemonico.core.attribute.LcErrorCode;

public class LcIllegalUserException extends LcException
{
    private static final long serialVersionUID = 1L;
    private static final LcErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "The User('%s') was enable.";

    static {
        ERROR_CODE = LcErrorCode.ILLEGAL_STATE;
    }

    public LcIllegalUserException(String email) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, email);
    }
}
