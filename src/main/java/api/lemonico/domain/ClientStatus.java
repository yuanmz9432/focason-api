/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.domain;



import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.seasar.doma.Domain;

@Domain(valueType = Integer.class, factoryMethod = "of")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum ClientStatus
{
    /**
     * 通常
     */
    NORMAL(1),

    /**
     * ブラックユーザー
     */
    BLOCKED(2),

    /**
     * 退会
     */
    LOGOUT(3);

    @Getter(onMethod = @__(@JsonValue))
    private final Integer value;

    public static ClientStatus of(Integer value) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equals(value))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("UserStatus = '" + value + "' is not supported."));
    }
}
