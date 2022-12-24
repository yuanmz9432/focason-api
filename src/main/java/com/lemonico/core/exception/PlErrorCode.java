package com.lemonico.core.exception;



import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;

/**
 * 異常コード
 *
 * @since 1.0.0
 */
public enum PlErrorCode
{

    BAD_REQUEST("E400000"), VALIDATION_ERROR("E400001"), DATA_INTEGRITY_VIOLATION("E400002"), DUPLICATE_DATA(
        "E400003"), ILLEGAL_STATE("E400004"), UNAUTHORIZED("E401000"), USER_AUTHENTICATION_FAILURE(
            "E401001"), AUTH_TOKEN_INVALID("E401002"), AUTH_TOKEN_EXPIRED("E401003"), AUTH_TOKEN_REVOKED(
                "E401004"), FORBIDDEN("E403000"), MLS_ACCESS_DENIED("E403001"), RLS_ACCESS_DENIED(
                    "E403002"), ACCOUNT_DISABLED("E403211"), API_KEY_ACCESS_DENIED("E403300"), REQUEST_REJECTED(
                        "E400999"), NOT_FOUND("E404000"), RESOURCE_NOT_FOUND("E404001"), METHOD_NOT_ALLOWED(
                            "E405000"), CONFLICT("E409000"), MUTEX_LOCK_FAILED("E409001"), UNSUPPORTED_MEDIA_TYPE(
                                "E415000"), INTERNAL_SERVER_ERROR("E500000"), PROCESS_TIMEOUT("E500001");


    private final String value;

    PlErrorCode(final String value) {
        this.value = value;
    }

    @JsonCreator
    public static PlErrorCode of(String value) {
        return Arrays.stream(values()).filter((v) -> v.value.equals(value)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("LcErrorCode = '" + value + "' is not supported."));
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }
}
