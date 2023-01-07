package com.focason.api.core.exception;



import com.focason.api.core.attribute.BaErrorCode;

public class BaResourceNotFoundException extends BaException
{
    private static final long serialVersionUID = 1L;
    private static final BaErrorCode ERROR_CODE;
    private static final String MESSAGE_TEMPLATE = "Resource '%s' specified id = '%s' does not exists.";

    static {
        ERROR_CODE = BaErrorCode.RESOURCE_NOT_FOUND;
    }

    public BaResourceNotFoundException(Class<?> resourceClass, Object object) {
        super(ERROR_CODE, MESSAGE_TEMPLATE, resourceClass.getSimpleName(), object);
    }
}
