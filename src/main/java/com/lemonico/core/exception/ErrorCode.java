package com.lemonico.core.exception;



import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Arrays;

/**
 * 異常コード
 *
 * @since 1.0.0
 */
public enum ErrorCode
{

    /**************************** 4xx ****************************/
    TOO_MANY_REQUESTS("E429000", "請求頻度は高いすぎて、後ほどアクセスしてください。"),

    /**************************** 500 ****************************/
    INTERNAL_SERVER_ERROR("E500000", "システムエラー"), DATABASE_ERROR("E512000", "データベースの操作が失敗した。"),


    /*********************** 共通 000 ~ 099 ***********************/
    CSV_FILE_INCORRECT("E200000", "CSVファイルが不正です"), WAREHOUSE_NOT_EXISTED("E200001",
        "該当店舗の対応倉庫は見つかりませんでした。管理者に確認してください。"), CSV_UPLOAD_FAILED("E200002",
            "CSVファイルのアップロードが失敗しました。"), EMAIL_INCORRECT("E200003", "メールアドレスが失効しました。"), DATA_TRANSFER_FAILED("E200004",
                "デートのパタンを転換することが失敗した。"), PDF_GENERATE_FAILED("E200007", "PDFファイルの生成が失敗した。"),


    /*********************** 商品 100 ~ 199 ***********************/
    SET_PRODUCT_UNDETECTABLE("E200100", "当商品は既にセット商品の子商品になって、消除できません。"),

    /*********************** 出荷 200 ~ 299 ***********************/
    INSUFFICIENT_STOCK("E200200", "出庫数は在庫数より多い"),

    E_40131("40131", ":合并出库依赖失败"), E_40132("40132", ":出庫依頼の復活が失敗しました"), E_40134("40134", ": セット商品を入庫依頼することはできません。"),

    E_50110("500110", "货架货物不足，将出库状态改为引当等待状态"), E_50111("500111", "ご指定のロケーションとロット番号の組み合わせは、すでに登録済みです。"),

    E_50113("500113", "領収書pdfを発行していない店舗。"), E_50114("500114", "一部登録成功しました。"), E_50115("500115",
        "同じ棚の優先順位は重複することができません"), E_50116("500116", "製品情報が空です。"), E_50118("500118",
            "商品移动后的货架出荷不可フラグ、賞味期限/在庫保管期限不同"), E_50120("500120", ": ご指定のCSVファイルは、規定の形式または項目と相違がありますので、再度ご確認ください。"),

    // 倉庫 外部連携
    E_60001("600001", "スマートCATに関する設定されたファイルディレクトリは存在しませんから、CSVファイルの生成が失敗しました。"), E_60002("600002",
        "スマートCATが設定しないから、CSVファイルの生成が失敗しました。"),

    // 店舗側設定
    E_A0001("A0001", "メールアドレスの更新が失敗しました。"), E_A0002("A0002", "パスワードの更新が失敗しました。"), E_A0003("A0003",
        "ユーザの更新が失敗しました。"), E_A0004("A0004",
            "アカウント情報の更新が失敗しました。"), E_A0005("A0005", "配送先マスタ情報の更新が失敗しました。"), E_A0006("A0006", "アカウント情報の取得が失敗しました。"),

    E_11005("11005", "[{}]　受注連携の商品登録失敗。"),

    // 出库货架分配错误信息
    E_12001("12001", "該当商品の入庫無し"), E_12002("12002", "該当商品のロケーション無し"), E_12003("12003", "商品の在庫数不足のため、出庫失敗"),
    // 出库防止多人操作
    E_13001("13001", "出庫依頼が既に削除されたため、出荷作業が開始できません。"), E_13002("13002", "同梱物のみを出庫することはできません"),

    // API連携異常情報
    /**
     * ColorMeの配送方法取得API実行失敗
     */
    E_CM001("CM001", "ColorMeの配送方法取得API実行失敗"),
    /**
     * ColorMeの支払方法取得API実行失敗
     */
    E_CM002("CM002", "ColorMeの支払方法取得API実行失敗"),
    /**
     * ColorMeの受注情報取得API実行失敗
     */
    E_CM003("CM003", "ColorMeの受注情報取得API実行失敗"),
    /**
     * ColorMeの受注情報の中に、配送情報が見つかりませんでした。
     */
    E_CM004("CM004", "ColorMeの受注情報の中に、配送情報が見つかりませんでした。"),
    /**
     * ColorMeの受注データが登録失敗
     */
    E_CM005("CM005", "ColorMeの受注データが登録失敗"),
    /**
     * ColorMeの受注データが登録失敗
     */
    E_CM006("CM006", "ColorMeの店舗設定取得API実行失敗"),
    /**
     * ColorMeの受注詳細データが登録失敗
     */
    E_CM007("CM007", "ColorMeの受注詳細データが登録失敗"),

    /**
     * Amazonの署名生成が失敗
     */
    E_AM001("AM001", "Amazonの署名生成が失敗"),

    /**
     * Next-Engine受注データ取得失敗
     */
    E_NE001("NE001", "Next-Engine受注データ取得失敗"),
    /**
     * Next-Engine受注詳細データ取得失敗
     */
    E_NE002("NE002", "Next-Engine受注詳細データ取得失敗"),
    /**
     * Next-Engine店舗データ取得失敗
     */
    E_NE003("NE003", "Next-Engine店舗データ取得失敗"),
    /**
     * Next-Engineの受注詳細データが登録失敗
     */
    E_NE004("NE004", "Next-Engineの受注詳細データが登録失敗"),
    /**
     * Next-Engineの配送先電話番号がフォーマット失敗
     */
    E_NE005("NE005", "Next-Engineの配送先電話番号がフォーマット失敗");

    private final String value;
    private final String detail;

    ErrorCode(final String value, final String detail) {
        this.value = value;
        this.detail = detail;
    }

    @JsonCreator
    public static ErrorCode of(String code) {
        return Arrays.stream(values()).filter((v) -> v.value.equals(code)).findFirst()
            .orElseThrow(() -> new IllegalArgumentException("PlErrorCode = '" + code + "' is not supported."));
    }

    public String getValue() {
        return value;
    }

    public String getDetail() {
        return detail;
    }
}
