/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.core.domain;



import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.seasar.doma.Domain;

@Domain(valueType = Integer.class, factoryMethod = "of")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserStatus
{
    /**
     * 無効
     */
    UNAVAILABLE(0),

    /**
     * 有効
     */
    AVAILABLE(1);

    @Getter(onMethod = @__(@JsonValue))
    private final Integer value;

    public static UserStatus of(Integer value) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equals(value))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("UserStatus = '" + value + "' is not supported."));
    }
}
