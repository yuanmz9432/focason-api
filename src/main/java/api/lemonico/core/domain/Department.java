/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.domain;



import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Arrays;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.seasar.doma.Domain;

@Domain(valueType = Integer.class, factoryMethod = "of")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Department
{
    /**
     * 倉庫
     */
    WAREHOUSE(1),

    /**
     * ストア
     */
    STORE(2);

    @Getter(onMethod = @__(@JsonValue))
    private final Integer value;

    public static Department of(Integer value) {
        return Arrays.stream(values())
            .filter(v -> v.getValue().equals(value))
            .findFirst()
            .orElseThrow(
                () -> new IllegalArgumentException("Department = '" + value + "' is not supported."));
    }
}
