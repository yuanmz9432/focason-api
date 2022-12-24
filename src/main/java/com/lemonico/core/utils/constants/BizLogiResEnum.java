package com.lemonico.core.utils.constants;

/**
 * @class BizLogiEnum
 * @description
 * @date 2021/6/4
 **/
public enum BizLogiResEnum
{

    S0_0001("S0-0001", "正常終了"),

    E8_0001("E8-0001", "エラー有り"), E8_0002("E8-0002", "問合番号が取得できません"), E8_0003("E8-0003", "問合番号が取得できません");

    private String code;
    private String msg;

    BizLogiResEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
