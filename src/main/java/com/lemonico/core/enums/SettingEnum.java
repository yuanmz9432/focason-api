package com.lemonico.core.enums;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.util.Arrays;

/**
 * @className: SettingEnum
 * @description: 店铺设定类型
 * @date: 2021/11/12 17:01
 **/
public enum SettingEnum
{
    // 配送方法
    DELIVERY_METHOD(1),
    // 配送时间带
    DELIVERY_TIME_ZONE(2),
    // 支付方法
    PAYMENT_METHOD(3);

    private final int code;

    public int getCode() {
        return code;
    }

    SettingEnum(int code) {
        this.code = code;
    }

    public static SettingEnum get(int code) {
        return Arrays.stream(values()).filter(x -> x.code == code).findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
