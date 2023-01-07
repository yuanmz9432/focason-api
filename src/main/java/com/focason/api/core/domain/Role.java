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
public enum Role
{
    /**
     * マネージャー
     */
    MANAGER(1),

    /**
     * 作業者
     */
    OPERATOR(2),

    /**
     * メンテナー
     */
    MAINTAINER(9);

    @Getter(onMethod = @__(@JsonValue))
    private final Integer value;

    public static Role of(Integer value) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equals(value))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("UserStatus = '" + value + "' is not supported."));
    }
}
