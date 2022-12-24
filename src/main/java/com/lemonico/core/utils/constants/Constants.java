package com.lemonico.core.utils.constants;

/**
 * @description: Constant
 * @return:
 * @date: 2020/05/08
 */
public class Constants
{
    public static final String SUCCESS_CODE = "200";
    public static final String SUCCESS_MSG = "请求成功";
    // バーコード処理用の定数定義
    public static final int BARCODE_1D_WIDTH = 200;
    public static final int BARCODE_1D_HEIGHT = 50;
    public static final String BCD_PARENT_MID_PATH = "src/main/resources/static/barcode/";
    public static final String BCD_EXP_PATH = "exp/";
    public static final String BCD_OUT_PATH = "out/";
    public static final String TEST_BCD_IMG_EXP_SUFFIX = "_test_bcd.jpg";
    public static final String TEST_BCD_IMG_OUT_PREFIX = "OUT_";
    public static final String TXT_EXT = ".txt";
    public static final String JPG_EXT = ".jpg";
    public static final int START_ASC = 82;
    // 入库状态 ：入库待ち
    public static final int WAREHOUSE_STATUS_WAIT = 1;
    // 入库状态 ： 一时保存
    public static final int WAREHOUSE_STATUS_FINISHED = 3;
    // 入库状态 ： 已经一时保存过 现在选择一部登录
    public static final int WAREHOUSE_STATUS_ABNORMAL = 5;
    // 仓库默认货架
    public static final String DEFAULT_LOCATION_NAME = "NO-LOCATION";
    // barcode width
    public static final Double WIDTH_2 = 0.2;
    public static final Double WIDTH_3 = 0.3;
    public static final Double WIDTH_5 = 0.5;

    // shipment status
    public static final Integer CONFIRMATION_WAIT = 1; // 確認待ち
    public static final Integer ENTRANCE_WAITING = 2; // 入庫待ち(引当待ち)
    public static final Integer DELIVERY_WAIT = 3; // 出荷待ち
    public static final Integer DURINGWORK = 4; // 出荷作業中
    public static final Integer DERINGINSPECTION = 5; // 検品中
    public static final Integer INRESERVE = 6; // 出庫保留中
    public static final Integer INSPECTED = 7; // 検品済み
    public static final Integer SHIPPED = 8; // 出荷済み
    public static final Integer CASHPAYMENT = 9; // 入金待ち
    public static final Integer LOSSAPPROVAL = 11; // 出庫承認失败
    public static final Integer WAITFOREXITAPPROVAL = 41; // 出庫承認待ち
    public static final Integer COMPLETION = 42; // 出庫承認完了
    public static final Integer RETURNHANDLE = 90; // 返品処理

    /**
     * 汎用 (SL)
     */
    public static final String DEFAULT_ORDER_NO = "SL";
    /**
     * 楽天市場 (RK)
     */
    public static final String RAKUTE_ORDER_NO = "RK";
    /**
     * csv受注
     */
    public static final String CSV_ORDER_NO = "CSV";
    /**
     * Yahoo (YH)
     */
    public static final String YAHOO_ORDER_NO = "YH";
    /**
     * アマゾン (AM)
     */
    public static final String AMAZON_ORDER_NO = "AM";
    /**
     * S3
     */
    public static final String S3_ORDER_NO = "IAMS3";
    /**
     * NTM
     */
    public static final String NTM_ORDER_NO = "NT";
    /**
     * ECCUBE
     */
    public static final String ECCUBE = "eccube";
    /**
     * ECCUBE連携する
     */
    public static final String ECCUBE_NTME = "NTME";
    /**
     * ECCUBE連携しない
     */
    public static final String ECCUBE_NTMN = "NTMN";

    /**
     * アマゾン (AM)
     */
    public static final String NextEngine_ORDER_NO = "EN";
    public static final String NextEngine_URL = "https://base.next-engine.org";
    public static final String NextEngine_API = "https://api.next-engine.org";

    public static final String DON_T_WANT_TO_SHARE_THE_BOOK = "納品書同梱不要";

    public static final String NON_EXISTENT_GOODS = "出庫依頼商品の未登録(仮登録)が含まれるため、「確認待ち」になります。";

    public static final String THE_AMOUNT_OF_CASH_WITHDRAWAL_IS_NULL = "お支払い方法で代金引換をご指定ですが、代金引換の金額が0円です。再度ご確認ください。";

    /**
     * 管理员邮箱地址
     */
    public static final String ADMINISTRATOR_EMAIL_ADDRESS = "sunlogi@sunseer.co.jp";

    /**
     * api报错发信 title
     */
    public static final String API_ERROR_MESSAGE_TITLE = "【本番SunLOGI】API連携エラー発生";
    /**
     * api报错发信 headline
     */
    public static final String API_ERROR_MESSAGE_HEADLINE = "本番において、下記のAPI連携中にエラーが発生しました。";
    /**
     * Yahoo
     */
    public static final String YAHOO = "yahoo";
    /**
     * Rakuten
     */
    public static final String RAKUTEN = "Rakuten";
    /**
     * 入庫新規
     */
    public static final String NEW_WAREHOUSING = "入庫新規";
    /**
     * 出荷済み
     */
    public static final String SHIPMENT_FINISH = "出荷済み";

    /**
     * 送り状発行API (確認機能 | 発行依頼機能)
     */
    public static final String BIZ_SHIPPING = "shipping";

    /**
     * ファイル存在確認API
     */
    public static final String BIZ_CHECKFILE = "checkfile";

    /**
     * 利用実績API
     */
    public static final String BIZ_RIYOUJISSEKI = "riyoujisseki";

    /**
     * 送り状再発行依頼API
     */
    public static final String BIZ_RETRYPRINT = "retryprint";

    /**
     * 佐川急便マスタ参照API
     */
    public static final String BIZ_CHECKADDRESS = "checkaddress";

    /**
     * 荷物受渡書・出荷明細書発行AP
     */
    public static final String BIZ_UKEWATASHIMEISAI = "ukewatashimeisai";

    /**
     * データ返却API
     */
    public static final String BIZ_DATAOUTPUT = "dataoutput";

    // public static final String BIZ_API_HOST = "https://dummy.sgsystems.co.jp/rest/getxml/";
    public static final String BIZ_API_HOST = "http://beccl-st.biz-blue.net/api/";

    /**
     * 帳票の背景画像をPDFに表示させるかのフラグ 背景表示
     */
    public static final String BIZ_BACK_LAYER_FLG = "1";

    /**
     * ユーザー認証 カスタマーID
     */
    public static final String BIZ_AUTH_CUSTOM_ID = "11872182";

    /**
     * ユーザー認証 ログインパスワード
     */
    public static final String BIZ_AUTH_CUSTOM_PWD = "5Dipw9FKYVYAJfA6PIGzwA==";

    /**
     * 顧客コード(チェックデジット付)
     */
    public static final String BIZ_CUSTOM_CODE = "131357090981";

    /**
     * 下記の依頼主情報を参照
     */
    public static final String BIZ_SPONSOR_CUSTOM_ADDRESS_FLG = "1";

    /**
     * 顧客コードに紐づく出荷場情報を印字
     */
    public static final String BIZ_SPONSOR_NO_CUSTOM_ADDRESS_FLG = "0";

    /**
     * 通常出荷の送り状を発行
     */
    public static final String BIZ_NORMAL_INVOICE = "0";

    /**
     * 代金引換の送り状を発行
     */
    public static final String BIZ_CASH_INVOICE = "1";

    /**
     * 確認機能
     */
    public static final String BIZ_CONFIRM_FUNC_FLG = "0";

    /**
     * 発行依頼機能
     */
    public static final String BIZ_ISSUANCE_FLG = "1";

    /**
     * 設定なし
     */
    public static final String NO_SETTING = "設定なし";
    /**
     * admin
     */
    public static final String SUNLOGI = "sunlogi";

    /**
     * 普通商品
     */
    public static final int ORDINARY_PRODUCT = 0;
    /**
     * 同捆物
     */
    public static final int BUNDLED = 1;
    /**
     * set商品
     */
    public static final int SET_PRODUCT = 2;
    /**
     * 假登录
     */
    public static final int NOT_LOGGED_PRODUCT = 9;


    // macro 比对字段
    /**
     * お届け先郵便番号
     */
    public static final int ITEM_1 = 1;
    /**
     * お届け先住所
     */
    public static final int ITEM_2 = 2;
    /**
     * お届け先名前
     */
    public static final int ITEM_3 = 3;
    /**
     * 商品名
     */
    public static final int ITEM_4 = 4;
    /**
     * 商品コード
     */
    public static final int ITEM_5 = 5;
    /**
     * 配送方法
     */
    public static final int ITEM_6 = 6;
    /**
     * 支払方法
     */
    public static final int ITEM_7 = 7;
    /**
     * 購入者備考欄
     */
    public static final int ITEM_8 = 8;
    /**
     * 合計金額
     */
    public static final int ITEM_9 = 9;
    /**
     * 合計購入数
     */
    public static final int ITEM_10 = 10;
    /**
     * 定期購入回数
     */
    public static final int ITEM_11 = 11;
    /**
     * 商品金額（合計）
     */
    public static final int ITEM_12 = 12;
    /**
     * 購入者と配送先の名前と住所が異なる場合
     */
    public static final int ITEM_13 = 13;
    /**
     * 依頼主
     */
    public static final int ITEM_14 = 14;
    /**
     * 手数料
     */
    public static final int ITEM_15 = 15;
    /**
     * 送料
     */
    public static final int ITEM_16 = 16;
    /**
     * 割引金額
     */
    public static final int ITEM_17 = 17;
    /**
     * 商品オプション
     */
    public static final int ITEM_18 = 18;

    // 比较方法
    /**
     * 含む
     */
    public static final int METHOD_1 = 1;
    /**
     * 含まない
     */
    public static final int METHOD_2 = 2;
    /**
     * 同じ
     */
    public static final int METHOD_3 = 3;
    /**
     * と同じではない
     */
    public static final int METHOD_4 = 4;
    /**
     * から始まる
     */
    public static final int METHOD_5 = 5;
    /**
     * で終わる
     */
    public static final int METHOD_6 = 6;
    /**
     * より小さい
     */
    public static final int METHOD_11 = 11;
    /**
     * より大きい
     */
    public static final int METHOD_12 = 12;
    /**
     * 等しい
     */
    public static final int METHOD_13 = 13;
    /**
     * 以上
     */
    public static final int METHOD_14 = 14;
    /**
     * 以下
     */
    public static final int METHOD_15 = 15;
    /**
     * 等しくない
     */
    public static final int METHOD_16 = 16;
    /**
     * 同じ
     */
    public static final int METHOD_17 = 17;
    /**
     * と同じではない
     */
    public static final int METHOD_18 = 18;
    /**
     * 空欄
     */
    public static final int METHOD_19 = 19;
    /**
     * 空欄ではない
     */
    public static final int METHOD_20 = 20;

    // macro 匹配执行方法
    /**
     * 配送方法を変更する
     */
    public static final int ACTION_1 = 1;
    /**
     * 支払方法を変更する
     */
    public static final int ACTION_2 = 2;
    /**
     * 明細書を追加する
     */
    public static final int ACTION_3 = 3;
    /**
     * 同梱物を指定する
     */
    public static final int ACTION_4 = 4;
    /**
     * 作業指示書 特記事項に追記する
     */
    public static final int ACTION_5 = 5;
    /**
     * 納品書 明細メッセージに追記する
     */
    public static final int ACTION_6 = 6;
    /**
     * 配送希望：割れ物注意に追加する
     */
    public static final int ACTION_7 = 7;
    /**
     * 配送希望：不在時宅配ボックスに追加する
     */
    public static final int ACTION_8 = 8;
    /**
     * 緩衝材を指定する
     */
    public static final int ACTION_9 = 9;
    /**
     * ギフトラッピングを指定する
     */
    public static final int ACTION_10 = 10;
    /**
     * 確認待ちに変更する
     */
    public static final int ACTION_11 = 11;
    /**
     * 送り状特記事項にする
     */
    public static final int ACTION_12 = 12;
    /**
     * 本番环境
     */
    public static final String PRO_ENVIRONMENT = "pro";
    /**
     * 验证环境
     */
    public static final String STG_ENVIRONMENT = "stg";
    /**
     * 开发环境
     */
    public static final String DEV_ENVIRONMENT = "dev";

    /**
     * 住所不包含门牌号
     */
    public static final String DOES_NOT_INCLUDE_HOUSE_NUMBER = "住所（番地）の入力が正しくありません、入力された内容をお確かめください。";
}
