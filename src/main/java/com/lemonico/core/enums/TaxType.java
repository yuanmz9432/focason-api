package com.lemonico.core.enums;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.util.Arrays;

public enum TaxType
{
    // 1:内税（税込み）
    EXCLUDED("excluded", 1),
    // 2:外税（税抜き）
    INCLUDED("included", 0);

    private final String label;

    private final int value;

    TaxType(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public static TaxType get(int value) {
        return Arrays.stream(values()).filter(x -> x.getValue() == value).findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
}
