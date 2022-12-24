package com.lemonico.core.utils.constants;

/**
 * @class BizLogiPredefinedEnum
 * @description
 * @date 2021/6/4
 **/
public enum BizLogiPredefinedEnum
{

    // 便種コード 航班类型代码
    BINSYUCODE_000("000", "陸便"), BINSYUCODE_030("030", "航空便"), BINSYUCODE_140("140", "クール冷蔵"), BINSYUCODE_141("141",
        "クール冷蔵(航空便)"), BINSYUCODE_150("150", "クール冷凍"), BINSYUCODE_151("151", "クール冷凍(航空便)"),

    // 代引支払方法区分 货到付款付款方式分类
    DAIBIKITYPE_NULL("", "なし"), DAIBIKITYPE_0("0", "なんでも決済"), DAIBIKITYPE_1("1", "現金"), DAIBIKITYPE_2("2",
        "クレジットカード・デビットカード"),

    // 重量1 重量2
    WEIGHT1_60("60", "2Kg(サイズ60)"), WEIGHT2_80("80", "5Kg(サイズ80)"), WEIGHT2_100("100", "10Kg(サイズ100)"), WEIGHT2_140("",
        "20Kg(サイズ140)"), WEIGHT2_160("", "30Kg(サイズ160)"),

    // 配達時間指定コード 交货时间规格代码
    SHITEITIMECODE_EMPTY("", "時間帯指定なし"), SHITEITIMECODE_NULL("null", "時間帯指定なし"), SHITEITIMECODE_00("00",
        "時間帯指定なし"), SHITEITIMECODE_01("01", "午前中"), SHITEITIMECODE_12("12", "12:00～14:00"), SHITEITIMECODE_14("14",
            "14:00～16:00"), SHITEITIMECODE_16("16", "16:00～18:00"), SHITEITIMECODE_18("18",
                "18:00～20:00"), SHITEITIMECODE_19("19", "19:00～21:00"), SHITEITIMECODE_04("04", "18:00～21:00"),

    // シール 印章
    CARESEAL1_011("011", "取扱注意"), CARESEAL2_013("013", "天地無用"), CARESEAL3_012("012", "貴重品"),

    // 配送会社コード 运输公司代码
    DELIVERYCODE_0001("0001", "佐川急便"), DELIVERYCODE_NULL("null", "佐川急便"), DELIVERYCODE_EMPTY("", "佐川急便"),

    // 出力レベル 输出级别
    OUTPUTLEVEL_000("000", "エラー＆ワーニング精査"), OUTPUTLEVEL_900("900", "エラー精査"),

    // 送り状コード 发票代码
    OKURICODE_A501("A501", "佐川急便A5サイズ圧着式送り状"), OKURICODE_A401("A401", "佐川急便A4納品書一体型送り状(2ピース)"), OKURICODE_L02("L02",
        "佐川急便圧着サーマル送り状"),

    // 荷物受渡書・出荷明細書出力タイプ 行李运送/发货单输出类型
    UKEWATASHIMEISAITYPE_000("000", "荷物受渡書"), UKEWATASHIMEISAITYPE_001("001",
        "荷物受渡書・出荷明細書"), UKEWATASHIMEISAITYPE_002("002", "出荷明細書"),

    // 元着コード 原始到达代码
    MOTOCHAKUCODE_0("0", "元払い"), MOTOCHAKUCODE_NULL("null", "元払い"), MOTOCHAKUCODE_EMPTY("", "元払い"), MOTOCHAKUCODE_1("1",
        "着払い"),

    // 集荷依頼時間指定コード 取件请求时间指定代码
    SHUKAIRAISHITEITIMECODE_00("00", "時間帯指定なし"), SHUKAIRAISHITEITIMECODE_09("09",
        "9:00～12:00"), SHUKAIRAISHITEITIMECODE_12("12", "12:00～15:00"), SHUKAIRAISHITEITIMECODE_15("15",
            "15:00～18:00"), SHUKAIRAISHITEITIMECODE_18("18", "18:00～21:00");



    private String code;
    private String msg;

    BizLogiPredefinedEnum(String code, String msg) {
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
