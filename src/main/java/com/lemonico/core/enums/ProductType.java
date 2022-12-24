package com.lemonico.core.enums;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.util.Arrays;

public enum ProductType
{
    // 0:普通商品
    NORMAL(0),
    // 1:同梱物
    BUNDLED(1),
    // 2:セット商品
    SET(2),
    // 9:仮登録
    ASSUMED(9);

    private final int value;

    ProductType(int value) {
        this.value = value;
    }

    public static ProductType get(int value) {
        return Arrays.stream(values()).filter(x -> x.value == value).findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INTERNAL_SERVER_ERROR));
    }

    public int getValue() {
        return value;
    }
}
