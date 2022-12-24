package com.lemonico.common.bean;



import com.opencsv.bean.CsvBindByName;
import lombok.Data;

/**
 * 楽天受注を管理するEntityクラス。
 */
@Data
public class Rk144_csv_order
{

    @CsvBindByName(column = "受注番号")
    private String item001;

    @CsvBindByName(column = "手数料")
    private String item002;

    @CsvBindByName(column = "割引金額")
    private String item003;

    @CsvBindByName(column = "注文日時")
    private String item005;

    @CsvBindByName(column = "注文日")
    private String item006;

    @CsvBindByName(column = "注文時間")
    private String item007;

    @CsvBindByName(column = "注文者ID(依頼主ID)")
    private String item008;

    @CsvBindByName(column = "注文確認日時")
    private String item009;

    @CsvBindByName(column = "注文確定日時")
    private String item010;

    @CsvBindByName(column = "発送指示日時")
    private String item011;

    @CsvBindByName(column = "発送完了報告日時")
    private String item012;

    @CsvBindByName(column = "支払方法")
    private String item013;

    @CsvBindByName(column = "クレジットカード支払い方法")
    private String item014;

    @CsvBindByName(column = "クレジットカード支払い回数")
    private String item015;

    @CsvBindByName(column = "注文種別")
    private String item018;

    @CsvBindByName(column = "複数送付先フラグ")
    private String item019;

    @CsvBindByName(column = "送付先一致フラグ")
    private String item020;

    @CsvBindByName(column = "離島フラグ")
    private String item021;

    @CsvBindByName(column = "楽天確認中フラグ")
    private String item022;

    @CsvBindByName(column = "警告表示タイプ")
    private String item023;

    @CsvBindByName(column = "楽天会員フラグ")
    private String item024;

    @CsvBindByName(column = "購入履歴修正有無フラグ")
    private String item025;

    @CsvBindByName(column = "商品金額(小計)")
    private String item026;

    @CsvBindByName(column = "消費税合計")
    private String item027;

    @CsvBindByName(column = "送料合計")
    private String item028;

    @CsvBindByName(column = "代金引換総計")
    private String item029;

    @CsvBindByName(column = "合計請求金額")
    private String item030;

    @CsvBindByName(column = "合計金額")
    private String item031;

    @CsvBindByName(column = "店舗発行クーポン利用額")
    private String item034;

    @CsvBindByName(column = "楽天発行クーポン利用額")
    private String item035;

    @CsvBindByName(column = "注文者郵便番号1")
    private String item036;

    @CsvBindByName(column = "注文者郵便番号2")
    private String item037;

    @CsvBindByName(column = "注文者都道府県")
    private String item038;

    @CsvBindByName(column = "注文者郡市区(固定値1:都道府県から分割)")
    private String item039;

    @CsvBindByName(column = "注文者詳細住所")
    private String item040;

    @CsvBindByName(column = "注文者姓")
    private String item041;

    @CsvBindByName(column = "注文者名")
    private String item042;

    @CsvBindByName(column = "注文者姓カナ")
    private String item043;

    @CsvBindByName(column = "注文者名カナ")
    private String item044;

    @CsvBindByName(column = "注文者電話番号1")
    private String item045;

    @CsvBindByName(column = "注文者電話番号2")
    private String item046;

    @CsvBindByName(column = "注文者電話番号3")
    private String item047;

    @CsvBindByName(column = "注文者メールアドレス")
    private String item048;

    @CsvBindByName(column = "注文者性別")
    private String item049;

    @CsvBindByName(column = "申込番号")
    private String item050;

    @CsvBindByName(column = "備考印字(1:有 0:無)")
    private String item051;

    @CsvBindByName(column = "明細書メッセージ")
    private String item052;

    @CsvBindByName(column = "明細書金額印字(1:有 0:無)")
    private String item053;

    @CsvBindByName(column = "明細同梱設定(1:有 0:無)")
    private String item054;

    @CsvBindByName(column = "出荷希望日")
    private String item055;

    @CsvBindByName(column = "配達担当者")
    private String item056;

    @CsvBindByName(column = "配送先性別")
    private String item057;

    @CsvBindByName(column = "配送先メールアドレス")
    private String item058;

    @CsvBindByName(column = "配送先郵便番号1")
    private String item059;

    @CsvBindByName(column = "配送先郵便番号2")
    private String item060;

    @CsvBindByName(column = "配送先都道府県")
    private String item061;

    @CsvBindByName(column = "配送先郡市区(固定値1:都道府県から分割)")
    private String item062;

    @CsvBindByName(column = "配送先詳細住所")
    private String item063;

    @CsvBindByName(column = "配送先姓")
    private String item064;

    @CsvBindByName(column = "配送先名")
    private String item065;

    @CsvBindByName(column = "配送先姓カナ")
    private String item066;

    @CsvBindByName(column = "配送先名カナ")
    private String item067;

    @CsvBindByName(column = "配送先電話番号1")
    private String item068;

    @CsvBindByName(column = "配送先電話番号2")
    private String item069;

    @CsvBindByName(column = "配送先電話番号3")
    private String item070;

    @CsvBindByName(column = "商品名")
    private String item073;

    @CsvBindByName(column = "商品コード")
    private String item074;

    @CsvBindByName(column = "管理バーコード")
    private String item075;

    @CsvBindByName(column = "単価")
    private String item076;

    @CsvBindByName(column = "個数")
    private String item077;

    @CsvBindByName(column = "送料込別")
    private String item078;

    @CsvBindByName(column = "税込別")
    private String item079;

    @CsvBindByName(column = "代引手数料込別")
    private String item080;

    @CsvBindByName(column = "項目・選択肢")
    private String item081;

    @CsvBindByName(column = "商品オプション")
    private String item082;

    @CsvBindByName(column = "納期情報")
    private String item083;

    @CsvBindByName(column = "在庫タイプ")
    private String item084;

    @CsvBindByName(column = "ラッピングタイトル1")
    private String item085;

    @CsvBindByName(column = "ラッピング名1")
    private String item086;

    @CsvBindByName(column = "ラッピング料金1")
    private String item087;

    @CsvBindByName(column = "ラッピング税込別1")
    private String item088;

    @CsvBindByName(column = "ラッピング種類1")
    private String item089;

    @CsvBindByName(column = "ラッピングタイトル2")
    private String item090;

    @CsvBindByName(column = "ラッピング名2")
    private String item091;

    @CsvBindByName(column = "ラッピング料金2")
    private String item092;

    @CsvBindByName(column = "ラッピング税込別2")
    private String item093;

    @CsvBindByName(column = "ラッピング種類2")
    private String item094;

    @CsvBindByName(column = "配達希望時間帯")
    private String item095;

    @CsvBindByName(column = "配達希望日")
    private String item096;

    @CsvBindByName(column = "担当者")
    private String item097;

    @CsvBindByName(column = "ひとことメモ")
    private String item098;

    @CsvBindByName(column = "メール差込文(お客様へのメッセージ)")
    private String item099;

    @CsvBindByName(column = "ギフト配送希望(1:有 0:無)")
    private String item100;

    @CsvBindByName(column = "コメント")
    private String item101;

    @CsvBindByName(column = "利用端末")
    private String item102;

    @CsvBindByName(column = "メールキャリアコード")
    private String item103;

    @CsvBindByName(column = "あす楽希望フラグ")
    private String item104;

    @CsvBindByName(column = "医療品受注フラグ")
    private String item105;

    @CsvBindByName(column = "楽天スーパーDEAL商品受注フラグ")
    private String item106;

    @CsvBindByName(column = "メンバーシッププログラム受注タイプ")
    private String item107;

    @CsvBindByName(column = "決済手数料")
    private String item108;

    @CsvBindByName(column = "注文者負担金合計")
    private String item109;

    @CsvBindByName(column = "店舗負担金合計")
    private String item110;

    @CsvBindByName(column = "外税合計")
    private String item111;

    @CsvBindByName(column = "決済手数料税率")
    private String item112;

    @CsvBindByName(column = "ラッピング税率1")
    private String item113;

    @CsvBindByName(column = "ラッピング税額1")
    private String item114;

    @CsvBindByName(column = "ラッピング税率2")
    private String item115;

    @CsvBindByName(column = "ラッピング税額2")
    private String item116;

    @CsvBindByName(column = "送付先外税合計")
    private String item117;

    @CsvBindByName(column = "送付先送料税率")
    private String item118;

    @CsvBindByName(column = "送付先代引料税率")
    private String item119;

    @CsvBindByName(column = "商品税率")
    private String item120;

    @CsvBindByName(column = "商品毎税込価格")
    private String item121;

    @CsvBindByName(column = "10%税率")
    private String item122;

    @CsvBindByName(column = "10%請求金額")
    private String item123;

    @CsvBindByName(column = "10%請求額に対する税率")
    private String item124;

    @CsvBindByName(column = "10%合計金額")
    private String item125;

    @CsvBindByName(column = "10%決済手数料")
    private String item126;

    @CsvBindByName(column = "10%クーポン割引額")
    private String item127;

    @CsvBindByName(column = "10%利用ポイント数")
    private String item128;

    @CsvBindByName(column = "8%税率")
    private String item129;

    @CsvBindByName(column = "8%請求金額")
    private String item130;

    @CsvBindByName(column = "8%請求額に対する税率")
    private String item131;

    @CsvBindByName(column = "8%合計金額")
    private String item132;

    @CsvBindByName(column = "8%決済手数料")
    private String item133;

    @CsvBindByName(column = "8%クーポン割引額")
    private String item134;

    @CsvBindByName(column = "8%利用ポイント数")
    private String item135;

    @CsvBindByName(column = "0%税率")
    private String item136;

    @CsvBindByName(column = "0%請求金額")
    private String item137;

    @CsvBindByName(column = "0%請求額に対する税率")
    private String item138;

    @CsvBindByName(column = "0%合計金額")
    private String item139;

    @CsvBindByName(column = "0%決済手数料")
    private String item140;

    @CsvBindByName(column = "0%クーポン割引額")
    private String item141;

    @CsvBindByName(column = "0%利用ポイント数")
    private String item142;

    @CsvBindByName(column = "単品配送フラグ")
    private String item143;

    @CsvBindByName(column = "配送方法")
    private String item144;

    @CsvBindByName(column = "作業指示書備考1")
    private String item145;

    @CsvBindByName(column = "作業指示書備考2")
    private String item146;

    @CsvBindByName(column = "作業指示書備考3")
    private String item147;

    @CsvBindByName(column = "作業指示書備考4")
    private String item148;

    @CsvBindByName(column = "作業指示書備考5")
    private String item149;

    @CsvBindByName(column = "作業指示書備考6")
    private String item150;

    @CsvBindByName(column = "作業指示書備考7")
    private String item151;

    @CsvBindByName(column = "作業指示書備考8")
    private String item152;

    @CsvBindByName(column = "作業指示書備考9")
    private String item153;

    @CsvBindByName(column = "作業指示書備考10")
    private String item154;

    @CsvBindByName(column = "商品備考1")
    private String item155;

    @CsvBindByName(column = "商品備考2")
    private String item156;

    @CsvBindByName(column = "商品備考3")
    private String item157;

    @CsvBindByName(column = "商品備考4")
    private String item158;

    @CsvBindByName(column = "商品備考5")
    private String item159;

    @CsvBindByName(column = "商品備考6")
    private String item160;

    @CsvBindByName(column = "商品備考7")
    private String item161;

    @CsvBindByName(column = "商品備考8")
    private String item162;

    @CsvBindByName(column = "商品備考9")
    private String item163;

    @CsvBindByName(column = "商品備考10")
    private String item164;

    @CsvBindByName(column = "依頼主区分(1:注文者 M:依頼主ID)")
    private String item165;

    @CsvBindByName(column = "注文者会社名")
    private String item166;

    @CsvBindByName(column = "注文者部署")
    private String item167;

    @CsvBindByName(column = "配送先会社名")
    private String item168;

    @CsvBindByName(column = "配送先部署")
    private String item169;

    @CsvBindByName(column = "不在時宅配ボックス(1:有 0:無)")
    private String item170;

    @CsvBindByName(column = "割れ物注意(1:有 0:無)")
    private String item171;

    @CsvBindByName(column = "品名")
    private String item172;

    @CsvBindByName(column = "商品オプション1")
    private String item173;

    @CsvBindByName(column = "商品オプション値1")
    private String item174;

    @CsvBindByName(column = "商品オプション2")
    private String item175;

    @CsvBindByName(column = "商品オプション値2")
    private String item176;

    @CsvBindByName(column = "商品オプション3")
    private String item177;

    @CsvBindByName(column = "商品オプション値3")
    private String item178;

    @CsvBindByName(column = "商品オプション4")
    private String item179;

    @CsvBindByName(column = "商品オプション値4")
    private String item180;

    @CsvBindByName(column = "定期購入ID")
    private String item181;

    @CsvBindByName(column = "定期購入回数")
    private String item182;

    @CsvBindByName(column = "次回お届け予定日")
    private String item183;

    @CsvBindByName(column = "購入者備考欄")
    private String item184;

    @CsvBindByName(column = "税区分(0:税込 1:税抜 2:非課税)")
    private String item185;

    @CsvBindByName(column = "軽減税率(0:10% 1:8%)")
    private String item186;

    @CsvBindByName(column = "商品小計")
    private String item187;

    @CsvBindByName(column = "商品手数料")
    private String item188;

    @CsvBindByName(column = "商品割引金額")
    private String item189;

    @CsvBindByName(column = "商品送料")
    private String item190;

    @CsvBindByName(column = "送り状特記事項")
    private String item191;

    private Integer row;

    public String getItem001() {
        return item001;
    }

    public void setItem001(String item001) {
        this.item001 = item001;
    }

    public String getItem002() {
        return item002;
    }

    public void setItem002(String item002) {
        this.item002 = item002;
    }

    public String getItem003() {
        return item003;
    }

    public void setItem003(String item003) {
        this.item003 = item003;
    }

    public String getItem005() {
        return item005;
    }

    public void setItem005(String item005) {
        this.item005 = item005;
    }

    public String getItem006() {
        return item006;
    }

    public void setItem006(String item006) {
        this.item006 = item006;
    }

    public String getItem007() {
        return item007;
    }

    public void setItem007(String item007) {
        this.item007 = item007;
    }

    public String getItem008() {
        return item008;
    }

    public void setItem008(String item008) {
        this.item008 = item008;
    }

    public String getItem009() {
        return item009;
    }

    public void setItem009(String item009) {
        this.item009 = item009;
    }

    public String getItem010() {
        return item010;
    }

    public void setItem010(String item010) {
        this.item010 = item010;
    }

    public String getItem011() {
        return item011;
    }

    public void setItem011(String item011) {
        this.item011 = item011;
    }

    public String getItem012() {
        return item012;
    }

    public void setItem012(String item012) {
        this.item012 = item012;
    }

    public String getItem013() {
        return item013;
    }

    public void setItem013(String item013) {
        this.item013 = item013;
    }

    public String getItem014() {
        return item014;
    }

    public void setItem014(String item014) {
        this.item014 = item014;
    }

    public String getItem015() {
        return item015;
    }

    public void setItem015(String item015) {
        this.item015 = item015;
    }

    public String getItem018() {
        return item018;
    }

    public void setItem018(String item018) {
        this.item018 = item018;
    }

    public String getItem019() {
        return item019;
    }

    public void setItem019(String item019) {
        this.item019 = item019;
    }

    public String getItem020() {
        return item020;
    }

    public void setItem020(String item020) {
        this.item020 = item020;
    }

    public String getItem021() {
        return item021;
    }

    public void setItem021(String item021) {
        this.item021 = item021;
    }

    public String getItem022() {
        return item022;
    }

    public void setItem022(String item022) {
        this.item022 = item022;
    }

    public String getItem023() {
        return item023;
    }

    public void setItem023(String item023) {
        this.item023 = item023;
    }

    public String getItem024() {
        return item024;
    }

    public void setItem024(String item024) {
        this.item024 = item024;
    }

    public String getItem025() {
        return item025;
    }

    public void setItem025(String item025) {
        this.item025 = item025;
    }

    public String getItem026() {
        return item026;
    }

    public void setItem026(String item026) {
        this.item026 = item026;
    }

    public String getItem027() {
        return item027;
    }

    public void setItem027(String item027) {
        this.item027 = item027;
    }

    public String getItem028() {
        return item028;
    }

    public void setItem028(String item028) {
        this.item028 = item028;
    }

    public String getItem029() {
        return item029;
    }

    public void setItem029(String item029) {
        this.item029 = item029;
    }

    public String getItem030() {
        return item030;
    }

    public void setItem030(String item030) {
        this.item030 = item030;
    }

    public String getItem031() {
        return item031;
    }

    public void setItem031(String item031) {
        this.item031 = item031;
    }

    public String getItem034() {
        return item034;
    }

    public void setItem034(String item034) {
        this.item034 = item034;
    }

    public String getItem035() {
        return item035;
    }

    public void setItem035(String item035) {
        this.item035 = item035;
    }

    public String getItem036() {
        return item036;
    }

    public void setItem036(String item036) {
        this.item036 = item036;
    }

    public String getItem037() {
        return item037;
    }

    public void setItem037(String item037) {
        this.item037 = item037;
    }

    public String getItem038() {
        return item038;
    }

    public void setItem038(String item038) {
        this.item038 = item038;
    }

    public String getItem039() {
        return item039;
    }

    public void setItem039(String item039) {
        this.item039 = item039;
    }

    public String getItem040() {
        return item040;
    }

    public void setItem040(String item040) {
        this.item040 = item040;
    }

    public String getItem041() {
        return item041;
    }

    public void setItem041(String item041) {
        this.item041 = item041;
    }

    public String getItem042() {
        return item042;
    }

    public void setItem042(String item042) {
        this.item042 = item042;
    }

    public String getItem043() {
        return item043;
    }

    public void setItem043(String item043) {
        this.item043 = item043;
    }

    public String getItem044() {
        return item044;
    }

    public void setItem044(String item044) {
        this.item044 = item044;
    }

    public String getItem045() {
        return item045;
    }

    public void setItem045(String item045) {
        this.item045 = item045;
    }

    public String getItem046() {
        return item046;
    }

    public void setItem046(String item046) {
        this.item046 = item046;
    }

    public String getItem047() {
        return item047;
    }

    public void setItem047(String item047) {
        this.item047 = item047;
    }

    public String getItem048() {
        return item048;
    }

    public void setItem048(String item048) {
        this.item048 = item048;
    }

    public String getItem049() {
        return item049;
    }

    public void setItem049(String item049) {
        this.item049 = item049;
    }

    public String getItem050() {
        return item050;
    }

    public void setItem050(String item050) {
        this.item050 = item050;
    }

    public String getItem051() {
        return item051;
    }

    public void setItem051(String item051) {
        this.item051 = item051;
    }

    public String getItem052() {
        return item052;
    }

    public void setItem052(String item052) {
        this.item052 = item052;
    }

    public String getItem053() {
        return item053;
    }

    public void setItem053(String item053) {
        this.item053 = item053;
    }

    public String getItem054() {
        return item054;
    }

    public void setItem054(String item054) {
        this.item054 = item054;
    }

    public String getItem055() {
        return item055;
    }

    public void setItem055(String item055) {
        this.item055 = item055;
    }

    public String getItem056() {
        return item056;
    }

    public void setItem056(String item056) {
        this.item056 = item056;
    }

    public String getItem057() {
        return item057;
    }

    public void setItem057(String item057) {
        this.item057 = item057;
    }

    public String getItem058() {
        return item058;
    }

    public void setItem058(String item058) {
        this.item058 = item058;
    }

    public String getItem059() {
        return item059;
    }

    public void setItem059(String item059) {
        this.item059 = item059;
    }

    public String getItem060() {
        return item060;
    }

    public void setItem060(String item060) {
        this.item060 = item060;
    }

    public String getItem061() {
        return item061;
    }

    public void setItem061(String item061) {
        this.item061 = item061;
    }

    public String getItem062() {
        return item062;
    }

    public void setItem062(String item062) {
        this.item062 = item062;
    }

    public String getItem063() {
        return item063;
    }

    public void setItem063(String item063) {
        this.item063 = item063;
    }

    public String getItem064() {
        return item064;
    }

    public void setItem064(String item064) {
        this.item064 = item064;
    }

    public String getItem065() {
        return item065;
    }

    public void setItem065(String item065) {
        this.item065 = item065;
    }

    public String getItem066() {
        return item066;
    }

    public void setItem066(String item066) {
        this.item066 = item066;
    }

    public String getItem067() {
        return item067;
    }

    public void setItem067(String item067) {
        this.item067 = item067;
    }

    public String getItem068() {
        return item068;
    }

    public void setItem068(String item068) {
        this.item068 = item068;
    }

    public String getItem069() {
        return item069;
    }

    public void setItem069(String item069) {
        this.item069 = item069;
    }

    public String getItem070() {
        return item070;
    }

    public void setItem070(String item070) {
        this.item070 = item070;
    }

    public String getItem073() {
        return item073;
    }

    public void setItem073(String item073) {
        this.item073 = item073;
    }

    public String getItem074() {
        return item074;
    }

    public void setItem074(String item074) {
        this.item074 = item074;
    }

    public String getItem075() {
        return item075;
    }

    public void setItem075(String item075) {
        this.item075 = item075;
    }

    public String getItem076() {
        return item076;
    }

    public void setItem076(String item076) {
        this.item076 = item076;
    }

    public String getItem077() {
        return item077;
    }

    public void setItem077(String item077) {
        this.item077 = item077;
    }

    public String getItem078() {
        return item078;
    }

    public void setItem078(String item078) {
        this.item078 = item078;
    }

    public String getItem079() {
        return item079;
    }

    public void setItem079(String item079) {
        this.item079 = item079;
    }

    public String getItem080() {
        return item080;
    }

    public void setItem080(String item080) {
        this.item080 = item080;
    }

    public String getItem081() {
        return item081;
    }

    public void setItem081(String item081) {
        this.item081 = item081;
    }

    public String getItem082() {
        return item082;
    }

    public void setItem082(String item082) {
        this.item082 = item082;
    }

    public String getItem083() {
        return item083;
    }

    public void setItem083(String item083) {
        this.item083 = item083;
    }

    public String getItem084() {
        return item084;
    }

    public void setItem084(String item084) {
        this.item084 = item084;
    }

    public String getItem085() {
        return item085;
    }

    public void setItem085(String item085) {
        this.item085 = item085;
    }

    public String getItem086() {
        return item086;
    }

    public void setItem086(String item086) {
        this.item086 = item086;
    }

    public String getItem087() {
        return item087;
    }

    public void setItem087(String item087) {
        this.item087 = item087;
    }

    public String getItem088() {
        return item088;
    }

    public void setItem088(String item088) {
        this.item088 = item088;
    }

    public String getItem089() {
        return item089;
    }

    public void setItem089(String item089) {
        this.item089 = item089;
    }

    public String getItem090() {
        return item090;
    }

    public void setItem090(String item090) {
        this.item090 = item090;
    }

    public String getItem091() {
        return item091;
    }

    public void setItem091(String item091) {
        this.item091 = item091;
    }

    public String getItem092() {
        return item092;
    }

    public void setItem092(String item092) {
        this.item092 = item092;
    }

    public String getItem093() {
        return item093;
    }

    public void setItem093(String item093) {
        this.item093 = item093;
    }

    public String getItem094() {
        return item094;
    }

    public void setItem094(String item094) {
        this.item094 = item094;
    }

    public String getItem095() {
        return item095;
    }

    public void setItem095(String item095) {
        this.item095 = item095;
    }

    public String getItem096() {
        return item096;
    }

    public void setItem096(String item096) {
        this.item096 = item096;
    }

    public String getItem097() {
        return item097;
    }

    public void setItem097(String item097) {
        this.item097 = item097;
    }

    public String getItem098() {
        return item098;
    }

    public void setItem098(String item098) {
        this.item098 = item098;
    }

    public String getItem099() {
        return item099;
    }

    public void setItem099(String item099) {
        this.item099 = item099;
    }

    public String getItem100() {
        return item100;
    }

    public void setItem100(String item100) {
        this.item100 = item100;
    }

    public String getItem101() {
        return item101;
    }

    public void setItem101(String item101) {
        this.item101 = item101;
    }

    public String getItem102() {
        return item102;
    }

    public void setItem102(String item102) {
        this.item102 = item102;
    }

    public String getItem103() {
        return item103;
    }

    public void setItem103(String item103) {
        this.item103 = item103;
    }

    public String getItem104() {
        return item104;
    }

    public void setItem104(String item104) {
        this.item104 = item104;
    }

    public String getItem105() {
        return item105;
    }

    public void setItem105(String item105) {
        this.item105 = item105;
    }

    public String getItem106() {
        return item106;
    }

    public void setItem106(String item106) {
        this.item106 = item106;
    }

    public String getItem107() {
        return item107;
    }

    public void setItem107(String item107) {
        this.item107 = item107;
    }

    public String getItem108() {
        return item108;
    }

    public void setItem108(String item108) {
        this.item108 = item108;
    }

    public String getItem109() {
        return item109;
    }

    public void setItem109(String item109) {
        this.item109 = item109;
    }

    public String getItem110() {
        return item110;
    }

    public void setItem110(String item110) {
        this.item110 = item110;
    }

    public String getItem111() {
        return item111;
    }

    public void setItem111(String item111) {
        this.item111 = item111;
    }

    public String getItem112() {
        return item112;
    }

    public void setItem112(String item112) {
        this.item112 = item112;
    }

    public String getItem113() {
        return item113;
    }

    public void setItem113(String item113) {
        this.item113 = item113;
    }

    public String getItem114() {
        return item114;
    }

    public void setItem114(String item114) {
        this.item114 = item114;
    }

    public String getItem115() {
        return item115;
    }

    public void setItem115(String item115) {
        this.item115 = item115;
    }

    public String getItem116() {
        return item116;
    }

    public void setItem116(String item116) {
        this.item116 = item116;
    }

    public String getItem117() {
        return item117;
    }

    public void setItem117(String item117) {
        this.item117 = item117;
    }

    public String getItem118() {
        return item118;
    }

    public void setItem118(String item118) {
        this.item118 = item118;
    }

    public String getItem119() {
        return item119;
    }

    public void setItem119(String item119) {
        this.item119 = item119;
    }

    public String getItem120() {
        return item120;
    }

    public void setItem120(String item120) {
        this.item120 = item120;
    }

    public String getItem121() {
        return item121;
    }

    public void setItem121(String item121) {
        this.item121 = item121;
    }

    public String getItem122() {
        return item122;
    }

    public void setItem122(String item122) {
        this.item122 = item122;
    }

    public String getItem123() {
        return item123;
    }

    public void setItem123(String item123) {
        this.item123 = item123;
    }

    public String getItem124() {
        return item124;
    }

    public void setItem124(String item124) {
        this.item124 = item124;
    }

    public String getItem125() {
        return item125;
    }

    public void setItem125(String item125) {
        this.item125 = item125;
    }

    public String getItem126() {
        return item126;
    }

    public void setItem126(String item126) {
        this.item126 = item126;
    }

    public String getItem127() {
        return item127;
    }

    public void setItem127(String item127) {
        this.item127 = item127;
    }

    public String getItem128() {
        return item128;
    }

    public void setItem128(String item128) {
        this.item128 = item128;
    }

    public String getItem129() {
        return item129;
    }

    public void setItem129(String item129) {
        this.item129 = item129;
    }

    public String getItem130() {
        return item130;
    }

    public void setItem130(String item130) {
        this.item130 = item130;
    }

    public String getItem131() {
        return item131;
    }

    public void setItem131(String item131) {
        this.item131 = item131;
    }

    public String getItem132() {
        return item132;
    }

    public void setItem132(String item132) {
        this.item132 = item132;
    }

    public String getItem133() {
        return item133;
    }

    public void setItem133(String item133) {
        this.item133 = item133;
    }

    public String getItem134() {
        return item134;
    }

    public void setItem134(String item134) {
        this.item134 = item134;
    }

    public String getItem135() {
        return item135;
    }

    public void setItem135(String item135) {
        this.item135 = item135;
    }

    public String getItem136() {
        return item136;
    }

    public void setItem136(String item136) {
        this.item136 = item136;
    }

    public String getItem137() {
        return item137;
    }

    public void setItem137(String item137) {
        this.item137 = item137;
    }

    public String getItem138() {
        return item138;
    }

    public void setItem138(String item138) {
        this.item138 = item138;
    }

    public String getItem139() {
        return item139;
    }

    public void setItem139(String item139) {
        this.item139 = item139;
    }

    public String getItem140() {
        return item140;
    }

    public void setItem140(String item140) {
        this.item140 = item140;
    }

    public String getItem141() {
        return item141;
    }

    public void setItem141(String item141) {
        this.item141 = item141;
    }

    public String getItem142() {
        return item142;
    }

    public void setItem142(String item142) {
        this.item142 = item142;
    }

    public String getItem143() {
        return item143;
    }

    public void setItem143(String item143) {
        this.item143 = item143;
    }

    public String getItem144() {
        return item144;
    }

    public void setItem144(String item144) {
        this.item144 = item144;
    }

    public String getItem145() {
        return item145;
    }

    public void setItem145(String item145) {
        this.item145 = item145;
    }

    public String getItem146() {
        return item146;
    }

    public void setItem146(String item146) {
        this.item146 = item146;
    }

    public String getItem147() {
        return item147;
    }

    public void setItem147(String item147) {
        this.item147 = item147;
    }

    public String getItem148() {
        return item148;
    }

    public void setItem148(String item148) {
        this.item148 = item148;
    }

    public String getItem149() {
        return item149;
    }

    public void setItem149(String item149) {
        this.item149 = item149;
    }

    public String getItem150() {
        return item150;
    }

    public void setItem150(String item150) {
        this.item150 = item150;
    }

    public String getItem151() {
        return item151;
    }

    public void setItem151(String item151) {
        this.item151 = item151;
    }

    public String getItem152() {
        return item152;
    }

    public void setItem152(String item152) {
        this.item152 = item152;
    }

    public String getItem153() {
        return item153;
    }

    public void setItem153(String item153) {
        this.item153 = item153;
    }

    public String getItem154() {
        return item154;
    }

    public void setItem154(String item154) {
        this.item154 = item154;
    }

    public String getItem155() {
        return item155;
    }

    public void setItem155(String item155) {
        this.item155 = item155;
    }

    public String getItem156() {
        return item156;
    }

    public void setItem156(String item156) {
        this.item156 = item156;
    }

    public String getItem157() {
        return item157;
    }

    public void setItem157(String item157) {
        this.item157 = item157;
    }

    public String getItem158() {
        return item158;
    }

    public void setItem158(String item158) {
        this.item158 = item158;
    }

    public String getItem159() {
        return item159;
    }

    public void setItem159(String item159) {
        this.item159 = item159;
    }

    public String getItem160() {
        return item160;
    }

    public void setItem160(String item160) {
        this.item160 = item160;
    }

    public String getItem161() {
        return item161;
    }

    public void setItem161(String item161) {
        this.item161 = item161;
    }

    public String getItem162() {
        return item162;
    }

    public void setItem162(String item162) {
        this.item162 = item162;
    }

    public String getItem163() {
        return item163;
    }

    public void setItem163(String item163) {
        this.item163 = item163;
    }

    public String getItem164() {
        return item164;
    }

    public void setItem164(String item164) {
        this.item164 = item164;
    }

    public String getItem165() {
        return item165;
    }

    public void setItem165(String item165) {
        this.item165 = item165;
    }

    public String getItem166() {
        return item166;
    }

    public void setItem166(String item166) {
        this.item166 = item166;
    }

    public String getItem167() {
        return item167;
    }

    public void setItem167(String item167) {
        this.item167 = item167;
    }

    public String getItem168() {
        return item168;
    }

    public void setItem168(String item168) {
        this.item168 = item168;
    }

    public String getItem169() {
        return item169;
    }

    public void setItem169(String item169) {
        this.item169 = item169;
    }

    public String getItem170() {
        return item170;
    }

    public void setItem170(String item170) {
        this.item170 = item170;
    }

    public String getItem171() {
        return item171;
    }

    public void setItem171(String item171) {
        this.item171 = item171;
    }

    public String getItem172() {
        return item172;
    }

    public void setItem172(String item172) {
        this.item172 = item172;
    }

    public String getItem173() {
        return item173;
    }

    public void setItem173(String item173) {
        this.item173 = item173;
    }

    public String getItem174() {
        return item174;
    }

    public void setItem174(String item174) {
        this.item174 = item174;
    }

    public String getItem175() {
        return item175;
    }

    public void setItem175(String item175) {
        this.item175 = item175;
    }

    public String getItem176() {
        return item176;
    }

    public void setItem176(String item176) {
        this.item176 = item176;
    }

    public String getItem177() {
        return item177;
    }

    public void setItem177(String item177) {
        this.item177 = item177;
    }

    public String getItem178() {
        return item178;
    }

    public void setItem178(String item178) {
        this.item178 = item178;
    }

    public String getItem179() {
        return item179;
    }

    public void setItem179(String item179) {
        this.item179 = item179;
    }

    public String getItem180() {
        return item180;
    }

    public void setItem180(String item180) {
        this.item180 = item180;
    }

    public String getItem181() {
        return item181;
    }

    public void setItem181(String item181) {
        this.item181 = item181;
    }

    public String getItem182() {
        return item182;
    }

    public void setItem182(String item182) {
        this.item182 = item182;
    }

    public String getItem183() {
        return item183;
    }

    public void setItem183(String item183) {
        this.item183 = item183;
    }

    public String getItem184() {
        return item184;
    }

    public void setItem184(String item184) {
        this.item184 = item184;
    }

    public String getItem185() {
        return item185;
    }

    public void setItem185(String item185) {
        this.item185 = item185;
    }

    public String getItem186() {
        return item186;
    }

    public void setItem186(String item186) {
        this.item186 = item186;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public String getItem187() {
        return item187;
    }

    public void setItem187(String item187) {
        this.item187 = item187;
    }

    public String getItem188() {
        return item188;
    }

    public void setItem188(String item188) {
        this.item188 = item188;
    }

    public String getItem189() {
        return item189;
    }

    public void setItem189(String item189) {
        this.item189 = item189;
    }

    public String getItem190() {
        return item190;
    }

    public void setItem190(String item190) {
        this.item190 = item190;
    }

    public String getItem191() {
        return item191;
    }

    public void setItem191(String item191) {
        this.item191 = item191;
    }
}
