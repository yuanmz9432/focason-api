package com.lemonico.core.enums;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.util.Arrays;

/**
 * @className: WarehousingEnum
 * @description: 入库状态
 * @date: 2021/11/1 17:29
 **/
public enum WarehousingEnum
{
    W_WAITING_FOR_WAREHOUSING(1, "入庫待ち"), W_UNDER_INSPECTION(2, "検品中"), W_FINISHED_INSPECTION(3, "検品完了"), W_FINISHED(4,
        "完了");

    private int status;
    private String msg;

    WarehousingEnum(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public static WarehousingEnum getStatus(int value) {
        return Arrays.stream(values()).filter(v -> v.getStatus() == value).findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.valueOf("WarehousingEnum不存在 status" + value)));
    }
}
