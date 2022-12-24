package com.lemonico.core.enums;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.util.Arrays;


/**
 * スイッチ定義
 */
public enum SwitchEnum
{
    // オン
    ON(1),
    // オフ
    OFF(0);

    private final int code;

    public int getCode() {
        return code;
    }

    SwitchEnum(int code) {
        this.code = code;
    }

    public static SwitchEnum get(int code) {
        return Arrays.stream(values()).filter(x -> x.code == code).findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
