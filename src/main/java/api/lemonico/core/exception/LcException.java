/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.exception;



import api.lemonico.core.attribute.LcErrorCode;

public class LcException extends RuntimeException
{
    private final LcErrorCode code;

    public LcException(LcErrorCode code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
    }

    public LcException(LcErrorCode code, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.code = code;
    }

    public LcErrorCode getCode() {
        return this.code;
    }
}
