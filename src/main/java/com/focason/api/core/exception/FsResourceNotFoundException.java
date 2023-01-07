package com.focason.api.core.exception;



import com.focason.api.core.attribute.FsErrorCode;

public class FsResourceNotFoundException extends FsException
{
    private static final long serialVersionUID = 1L;
    private static final FsErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Resource '%s' specified id = '%s' does not exists.";

    static {
        ERROR_CODE = FsErrorCode.RESOURCE_NOT_FOUND;
    }

    public FsResourceNotFoundException(Class<?> resourceClass, Object object) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, resourceClass.getSimpleName(), object);
    }
}
