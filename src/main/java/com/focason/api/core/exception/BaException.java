/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class BaException extends RuntimeException
{
    private final FsErrorCode code;

    public BaException(FsErrorCode code, String format, Object... args) {
        super(String.format(format, args));
        this.code = code;
    }

    public BaException(FsErrorCode code, Throwable cause, String format, Object... args) {
        super(String.format(format, args), cause);
        this.code = code;
    }

    public FsErrorCode getCode() {
        return this.code;
    }
}
