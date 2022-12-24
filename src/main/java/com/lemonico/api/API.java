package com.lemonico.api;



import com.lemonico.core.exception.BaseException;
import com.lemonico.core.exception.ErrorCode;
import java.util.Arrays;

/**
 * API名称のEnum定義1
 */
public enum API
{

    SHOPIFY("SP", "shopify"), BASE("BS", "BASE"), ECFORCE("EC", "ecforce"), ECCUBE("EB", "eccube"), YAHOO("YH",
        "yahoo"), COLORME("CM", "colorme"), QOOTEN("QT",
            "Qooten"), MAKESHOP("MK", "MakeShop"), RAKUTEN("RT", "Rakuten"), NEXTENGINE("NE", "next-engine");

    private final String identification;
    private final String name;

    API(String identification, String name) {
        this.identification = identification;
        this.name = name;
    }

    public String getIdentification() {
        return identification;
    }

    public String getName() {
        return name;
    }

    public static API getIdentification(String name) {
        return Arrays.stream(values()).filter(v -> v.name.equals(name)).findFirst()
            .orElseThrow(() -> new BaseException(ErrorCode.valueOf("該当API【" + name + "】が存在しません。")));
    }
}
