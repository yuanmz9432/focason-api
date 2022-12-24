CREATE TABLE "public"."mc100_product"
(
    "product_id"      varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "client_id"       varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "warehouse_cd"    varchar(20) COLLATE "pg_catalog"."default"  NOT NULL,
    "name"            varchar(300) COLLATE "pg_catalog"."default" NOT NULL,
    "code"            varchar(100) COLLATE "pg_catalog"."default",
    "set_flg"         int4                                        DEFAULT 0,
    "set_sub_id"      int4,
    "barcode"         varchar(20) COLLATE "pg_catalog"."default",
    "bundled_flg"     int4                                        DEFAULT 0,
    "is_reduced_tax"  int4                                        DEFAULT 0,
    "price"           int4,
    "identifier"      varchar(10) COLLATE "pg_catalog"."default",
    "description_cd"  varchar(50) COLLATE "pg_catalog"."default",
    "origin"          varchar(100) COLLATE "pg_catalog"."default",
    "size_cd"         varchar(10) COLLATE "pg_catalog"."default",
    "tags_id"         varchar(300) COLLATE "pg_catalog"."default",
    "show_flg"        int4                                        DEFAULT 0,
    "weight"          float8,
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int4                                        DEFAULT 0,
    "tax_flag"        int4,
    "url"             text COLLATE "pg_catalog"."default",
    "bikou"           varchar(200) COLLATE "pg_catalog"."default",
    "kubun"           int4                                        DEFAULT 0,
    "english_name"    varchar(300) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "cost_price"      int4                                        DEFAULT 0,
    "product_type"    varchar COLLATE "pg_catalog"."default",
    "product_cd"      int4,
    "sort_no"         int4,
    "ntm_price"       int4,
    "ntm_memo"        varchar(255) COLLATE "pg_catalog"."default",
    "eccube_show_flg" int4,
    "serial_flg"      int4,
    CONSTRAINT "mc100_product_PKC" PRIMARY KEY ("product_id", "client_id")
);
ALTER TABLE "public"."mc100_product" OWNER TO "prologi";
CREATE INDEX "mc100_product_PKI" ON "public"."mc100_product" USING btree (
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "code" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "kubun" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc100_product"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."mc100_product"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc100_product"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."mc100_product"."name" IS '商品名';
COMMENT
ON COLUMN "public"."mc100_product"."code" IS '商品コード';
COMMENT
ON COLUMN "public"."mc100_product"."set_flg" IS 'セット商品フラグ:0：無　1:有';
COMMENT
ON COLUMN "public"."mc100_product"."set_sub_id" IS 'セット商品ID';
COMMENT
ON COLUMN "public"."mc100_product"."barcode" IS '管理バーコード';
COMMENT
ON COLUMN "public"."mc100_product"."bundled_flg" IS '同梱物フラグ:0：無　1:有';
COMMENT
ON COLUMN "public"."mc100_product"."is_reduced_tax" IS '軽減税率適用商品:0：無　1:有';
COMMENT
ON COLUMN "public"."mc100_product"."price" IS '商品価格税込';
COMMENT
ON COLUMN "public"."mc100_product"."identifier" IS '商品識別番号';
COMMENT
ON COLUMN "public"."mc100_product"."description_cd" IS '品名コード';
COMMENT
ON COLUMN "public"."mc100_product"."origin" IS '原産国';
COMMENT
ON COLUMN "public"."mc100_product"."size_cd" IS '商品サイズコード';
COMMENT
ON COLUMN "public"."mc100_product"."tags_id" IS 'タグID';
COMMENT
ON COLUMN "public"."mc100_product"."show_flg" IS '表示商品フラグ:0:表示　1:非表示';
COMMENT
ON COLUMN "public"."mc100_product"."weight" IS '重量';
COMMENT
ON COLUMN "public"."mc100_product"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc100_product"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc100_product"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc100_product"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc100_product"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON COLUMN "public"."mc100_product"."tax_flag" IS '課税:0:税込 1:税抜 2:非課税 ﾇﾐ､・ﾏ､ｲ:11 ﾋﾄ槻ﾎ衒・12)';
COMMENT
ON COLUMN "public"."mc100_product"."url" IS 'QRコード';
COMMENT
ON COLUMN "public"."mc100_product"."bikou" IS '備考';
COMMENT
ON COLUMN "public"."mc100_product"."kubun" IS '商品区分(0: 通常商品 1: 同捆商品 2: set商品 9:假登录)';
COMMENT
ON COLUMN "public"."mc100_product"."english_name" IS '商品英語名';
COMMENT
ON COLUMN "public"."mc100_product"."cost_price" IS '商品原価';
COMMENT
ON COLUMN "public"."mc100_product"."product_type" IS '商品カテゴリ SP/SHA/SP1.....';
COMMENT
ON COLUMN "public"."mc100_product"."product_cd" IS '商品级别优先度';
COMMENT
ON COLUMN "public"."mc100_product"."sort_no" IS 'ソート 排序';
COMMENT
ON COLUMN "public"."mc100_product"."ntm_price" IS 'NTM-価格';
COMMENT
ON COLUMN "public"."mc100_product"."ntm_memo" IS 'ピッキングメモ';
COMMENT
ON COLUMN "public"."mc100_product"."eccube_show_flg" IS 'eccube的数据是否显示，1:公開，2：非公開';
COMMENT
ON COLUMN "public"."mc100_product"."serial_flg" IS 'シリアルフラグ';
COMMENT
ON TABLE "public"."mc100_product" IS '商品マスタ:商品管理(顧客CD)';

CREATE TABLE "public"."mc101_product_tag"
(
    "tags_id"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "tags"     varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
    "ins_usr"  varchar(100) COLLATE "pg_catalog"."default",
    "ins_date" timestamp(6),
    "upd_usr"  varchar(100) COLLATE "pg_catalog"."default",
    "upd_date" timestamp(6),
    "del_flg"  int4 DEFAULT 0,
    CONSTRAINT "mc101_product_tag_PKC" PRIMARY KEY ("tags_id")
);
ALTER TABLE "public"."mc101_product_tag" OWNER TO "prologi";
CREATE UNIQUE INDEX "mc101_product_tag_PKI" ON "public"."mc101_product_tag" USING btree (
    "tags_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc101_product_tag"."tags_id" IS 'タグID';
COMMENT
ON COLUMN "public"."mc101_product_tag"."tags" IS 'タグ:商品検索用';
COMMENT
ON COLUMN "public"."mc101_product_tag"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc101_product_tag"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc101_product_tag"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc101_product_tag"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc101_product_tag"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mc101_product_tag" IS '商品タグマスタ:商品ID、顧客CD、商品タグ';

CREATE TABLE "public"."mc102_product_img"
(
    "product_id"  varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "client_id"   varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "img_sub_id"  int4                                        NOT NULL,
    "product_img" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "ins_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"    timestamp(6),
    "upd_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"    timestamp(6),
    "del_flg"     int4,
    CONSTRAINT "mc102_product_img_PKC" PRIMARY KEY ("product_id", "client_id", "img_sub_id")
);
ALTER TABLE "public"."mc102_product_img" OWNER TO "prologi";
CREATE UNIQUE INDEX "mc102_product_img_PKI" ON "public"."mc102_product_img" USING btree (
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "img_sub_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc102_product_img"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."mc102_product_img"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc102_product_img"."img_sub_id" IS '商品画像枝番';
COMMENT
ON COLUMN "public"."mc102_product_img"."product_img" IS '商品画像パス';
COMMENT
ON COLUMN "public"."mc102_product_img"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc102_product_img"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc102_product_img"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc102_product_img"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc102_product_img"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mc102_product_img" IS '商品画像:商品ID（SKU単位）、顧客CD、商品画像枝番';

CREATE TABLE "public"."mc103_product_set"
(
    "client_id"   varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "set_sub_id"  int4                                       NOT NULL,
    "product_id"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_cnt" int4                                       NOT NULL,
    "ins_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"    timestamp(6),
    "upd_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"    timestamp(6),
    "del_flg"     int4 DEFAULT 0,
    CONSTRAINT "mc103_product_set_PKC" PRIMARY KEY ("client_id", "set_sub_id", "product_id")
);
ALTER TABLE "public"."mc103_product_set" OWNER TO "prologi";
CREATE INDEX "mc103_product_set_PKI" ON "public"."mc103_product_set" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "set_sub_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc103_product_set"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc103_product_set"."set_sub_id" IS 'セット商品ID';
COMMENT
ON COLUMN "public"."mc103_product_set"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."mc103_product_set"."product_cnt" IS '商品個数';
COMMENT
ON COLUMN "public"."mc103_product_set"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc103_product_set"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc103_product_set"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc103_product_set"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc103_product_set"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mc103_product_set" IS 'セット商品明細マスタ:セット商品ID、明細商品ID、顧客CD';

CREATE TABLE "public"."mc104_tag"
(
    "tags_id"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "upd_usr"    varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"   timestamp(6),
    "del_flg"    int4 DEFAULT 0,
    CONSTRAINT "mc104_tag_PKC" PRIMARY KEY ("tags_id", "product_id", "client_id")
);
ALTER TABLE "public"."mc104_tag" OWNER TO "prologi";
CREATE UNIQUE INDEX "mc104_tag_PKI" ON "public"."mc104_tag" USING btree (
    "tags_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc104_tag"."tags_id" IS 'タグID';
COMMENT
ON COLUMN "public"."mc104_tag"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."mc104_tag"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc104_tag"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc104_tag"."upd_date" IS '更新日付';
COMMENT
ON COLUMN "public"."mc104_tag"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mc104_tag" IS '商品タグ';

CREATE TABLE "public"."mc105_product_setting"
(
    "set_cd"                 int4                                       NOT NULL,
    "client_id"              varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "tax"                    int4                                       NOT NULL DEFAULT 0,
    "accordion"              int4                                       NOT NULL,
    "price_on_delivery_note" int4                                       NOT NULL,
    "info"                   varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"               timestamp(6),
    "upd_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"               timestamp(6),
    "del_flg"                int4                                                DEFAULT 0,
    "delivery_note_type"     int4                                                DEFAULT 0,
    "version"                int4                                                DEFAULT 1,
    CONSTRAINT "mc105_product_setting_PKC" PRIMARY KEY ("set_cd", "client_id")
);
ALTER TABLE "public"."mc105_product_setting" OWNER TO "prologi";
CREATE UNIQUE INDEX "mc105_product_setting_PKI" ON "public"."mc105_product_setting" USING btree (
    "set_cd" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc105_product_setting"."set_cd" IS '設定CD';
COMMENT
ON COLUMN "public"."mc105_product_setting"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc105_product_setting"."tax" IS '税込・税抜:0: 税込 1:税抜';
COMMENT
ON COLUMN "public"."mc105_product_setting"."accordion" IS '端数処理:0:切り捨て 1:切り上げ 2: 四捨五入';
COMMENT
ON COLUMN "public"."mc105_product_setting"."price_on_delivery_note" IS '同梱明細書の金額印字:0:印字する 1: 印字しない';
COMMENT
ON COLUMN "public"."mc105_product_setting"."info" IS '備考';
COMMENT
ON COLUMN "public"."mc105_product_setting"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc105_product_setting"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc105_product_setting"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc105_product_setting"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc105_product_setting"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON COLUMN "public"."mc105_product_setting"."delivery_note_type" IS '明細書の同梱設定';
COMMENT
ON COLUMN "public"."mc105_product_setting"."version" IS '明細書のバージョン';
COMMENT
ON TABLE "public"."mc105_product_setting" IS '商品設定用:設定項目CD';

CREATE TABLE "public"."mc106_produce_renkei"
(
    "product_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"         varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "api_id"            int4                                       NOT NULL,
    "renkei_product_id" varchar(100) COLLATE "pg_catalog"."default",
    "variant_id"        varchar(100) COLLATE "pg_catalog"."default",
    "inventory_type"    varchar(10) COLLATE "pg_catalog"."default",
    "ins_date"          timestamp(6),
    "ins_usr"           varchar(100) COLLATE "pg_catalog"."default",
    "biko"              text COLLATE "pg_catalog"."default",
    "del_flg"           int4
);
ALTER TABLE "public"."mc106_produce_renkei" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."product_id" IS 'サンロジ商品ID';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."api_id" IS 'アプリID';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."renkei_product_id" IS 'EC店舗商品ID';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."variant_id" IS '外部連携ID(ロケコード)';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."inventory_type" IS '商品類別(楽天用)';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."biko" IS '備考';
COMMENT
ON COLUMN "public"."mc106_produce_renkei"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."mc106_produce_renkei" IS '外部商品紐付マスタ';

CREATE TABLE "public"."mc107_ntm_product_master"
(
    "no"         int4 NOT NULL,
    "name"       varchar(100) COLLATE "pg_catalog"."default",
    "product1"   varchar(10) COLLATE "pg_catalog"."default",
    "product2"   varchar(10) COLLATE "pg_catalog"."default",
    "product3"   varchar(10) COLLATE "pg_catalog"."default",
    "product4"   varchar(10) COLLATE "pg_catalog"."default",
    "product5"   varchar(10) COLLATE "pg_catalog"."default",
    "product6"   varchar(10) COLLATE "pg_catalog"."default",
    "product7"   varchar(10) COLLATE "pg_catalog"."default",
    "product8"   varchar(10) COLLATE "pg_catalog"."default",
    "product9"   varchar(10) COLLATE "pg_catalog"."default",
    "product10"  varchar(10) COLLATE "pg_catalog"."default",
    "start_date" timestamp(6),
    "end_date"   timestamp(6),
    "dm_flag"    int4,
    CONSTRAINT "mc107_ntm_product_master_pkey" PRIMARY KEY ("no")
);
ALTER TABLE "public"."mc107_ntm_product_master" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."no" IS '通しNo';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."name" IS '翻訳名称';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product1" IS '商品1';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product2" IS '商品2';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product3" IS '商品3';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product4" IS '商品4';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product5" IS '商品5';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product6" IS '商品6';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product7" IS '商品7';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product8" IS '商品8';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product9" IS '商品9';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."product10" IS '商品10';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."start_date" IS '開始日';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."end_date" IS '終了日';
COMMENT
ON COLUMN "public"."mc107_ntm_product_master"."dm_flag" IS 'DM便不可フラグ';
COMMENT
ON TABLE "public"."mc107_ntm_product_master" IS 'NTM商品マスタ';

CREATE TABLE "public"."mc108_ntm_area_master"
(
    "area_code" varchar(20) COLLATE "pg_catalog"."default",
    "area_name" varchar(50) COLLATE "pg_catalog"."default",
    "ins_date"  timestamp(6),
    "ins_usr"   varchar(10) COLLATE "pg_catalog"."default",
    "upd_date"  timestamp(6),
    "upd_usr"   varchar(10) COLLATE "pg_catalog"."default",
    "del_flg"   int4
);
ALTER TABLE "public"."mc108_ntm_area_master" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."area_code" IS '支店コード';
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."area_name" IS '支店名前';
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc108_ntm_area_master"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mc108_ntm_area_master" IS '支店マスタ';

CREATE TABLE "public"."mc109_ntm_yubin_change_history"
(
    "before"      varchar(20) COLLATE "pg_catalog"."default",
    "after"       varchar(20) COLLATE "pg_catalog"."default",
    "change_date" timestamp(6),
    "ins_usr"     varchar(10) COLLATE "pg_catalog"."default",
    "ins_date"    timestamp(6),
    "upd_usr"     varchar(10) COLLATE "pg_catalog"."default",
    "upd_date"    timestamp(6),
    "del_flg"     int4
);
ALTER TABLE "public"."mc109_ntm_yubin_change_history" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."before" IS '変更前';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."after" IS '変更後';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."change_date" IS '変更日';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc109_ntm_yubin_change_history"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."mc109_ntm_yubin_change_history" IS '郵便番号変更履歴';

CREATE TABLE "public"."mc110_product_options"
(
    "id"            int4                                       NOT NULL,
    "client_id"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shop_code"     varchar(100) COLLATE "pg_catalog"."default",
    "product_code"  varchar(70) COLLATE "pg_catalog"."default",
    "sub_code"      varchar(30) COLLATE "pg_catalog"."default",
    "code"          varchar(100) COLLATE "pg_catalog"."default",
    "options"       text COLLATE "pg_catalog"."default",
    "option_name1"  varchar(100) COLLATE "pg_catalog"."default",
    "option_value1" varchar(100) COLLATE "pg_catalog"."default",
    "option_name2"  varchar(100) COLLATE "pg_catalog"."default",
    "option_value2" varchar(100) COLLATE "pg_catalog"."default",
    "option_name3"  varchar(100) COLLATE "pg_catalog"."default",
    "option_value3" varchar(100) COLLATE "pg_catalog"."default",
    "option_name4"  varchar(100) COLLATE "pg_catalog"."default",
    "option_value4" varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"      timestamp(6),
    "ins_usr"       varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"      timestamp(6),
    "upd_usr"       varchar(100) COLLATE "pg_catalog"."default",
    "del_flg"       int4 DEFAULT 0,
    CONSTRAINT "mc110_product_PKC" PRIMARY KEY ("id")
);
ALTER TABLE "public"."mc110_product_options" OWNER TO "prologi";
CREATE INDEX "mc110_product_ix1" ON "public"."mc110_product_options" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "mc110_product_pki" ON "public"."mc110_product_options" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc110_product_options"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."mc110_product_options"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc110_product_options"."shop_code" IS '商品管理番号(商品URL)';
COMMENT
ON COLUMN "public"."mc110_product_options"."product_code" IS '商品コード:指定ない 商品管理番号と同じ値';
COMMENT
ON COLUMN "public"."mc110_product_options"."sub_code" IS '対応コード';
COMMENT
ON COLUMN "public"."mc110_product_options"."code" IS 'サンロジ商品コード';
COMMENT
ON COLUMN "public"."mc110_product_options"."options" IS '全てオプション文字(検索用)';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_name1" IS 'オプション名1';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_value1" IS 'オプション値1';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_name2" IS 'オプション名2';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_value2" IS 'オプション値2';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_name3" IS 'オプション名3';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_value3" IS 'オプション値3';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_name4" IS 'オプション名4';
COMMENT
ON COLUMN "public"."mc110_product_options"."option_value4" IS 'オプション値4';
COMMENT
ON COLUMN "public"."mc110_product_options"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc110_product_options"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc110_product_options"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc110_product_options"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc110_product_options"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mc110_product_options" IS '商品マスタ:商品対応表';

CREATE TABLE "public"."mc200_customer_delivery"
(
    "delivery_id"            int4                                        NOT NULL,
    "client_id"              varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "name"                   varchar(100) COLLATE "pg_catalog"."default",
    "postcode"               varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "prefecture"             varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "address1"               varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "address2"               varchar(100) COLLATE "pg_catalog"."default",
    "company"                varchar(50) COLLATE "pg_catalog"."default",
    "phone"                  varchar(20) COLLATE "pg_catalog"."default",
    "email"                  varchar(50) COLLATE "pg_catalog"."default",
    "ins_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"               timestamp(6),
    "upd_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"               timestamp(6),
    "del_flg"                int4                                       DEFAULT 0,
    "form"                   int4,
    "delivery_method"        varchar(30) COLLATE "pg_catalog"."default",
    "delivery_carrier"       varchar(30) COLLATE "pg_catalog"."default",
    "delivery_date"          int4,
    "delivery_time_slot"     varchar(20) COLLATE "pg_catalog"."default",
    "box_delivery"           int4                                       DEFAULT 0,
    "fragile_item"           int4                                       DEFAULT 0,
    "cushioning_unit"        varchar(1) COLLATE "pg_catalog"."default"  DEFAULT 0,
    "gift_wrapping_unit"     varchar(1) COLLATE "pg_catalog"."default"  DEFAULT 0,
    "cushioning_type"        varchar(50) COLLATE "pg_catalog"."default",
    "gift_wrapping_type"     varchar(200) COLLATE "pg_catalog"."default",
    "delivery_note_type"     varchar(10) COLLATE "pg_catalog"."default",
    "price_on_delivery_note" int4,
    "invoice_special_notes"  varchar(255) COLLATE "pg_catalog"."default",
    "bikou1"                 varchar(255) COLLATE "pg_catalog"."default",
    "bikou2"                 varchar(255) COLLATE "pg_catalog"."default",
    "bikou3"                 varchar(255) COLLATE "pg_catalog"."default",
    "bikou4"                 varchar(255) COLLATE "pg_catalog"."default",
    "bikou5"                 varchar(255) COLLATE "pg_catalog"."default",
    "bikou6"                 varchar(255) COLLATE "pg_catalog"."default",
    "bikou7"                 varchar(255) COLLATE "pg_catalog"."default",
    "division"               varchar(30) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    CONSTRAINT "mc200_customer_delivery_PKC" PRIMARY KEY ("delivery_id")
);
ALTER TABLE "public"."mc200_customer_delivery" OWNER TO "prologi";
CREATE UNIQUE INDEX "mc200_customer_delivery_PKI" ON "public"."mc200_customer_delivery" USING btree (
    "delivery_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."delivery_id" IS '管理ID';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."name" IS '顧客名';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."postcode" IS '郵便番号';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."address1" IS '住所１';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."address2" IS '住所２';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."company" IS '会社名';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."phone" IS '電話番号';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."email" IS 'メールアドレス';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."form" IS '事業形態';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."delivery_method" IS '配送便指定';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."delivery_carrier" IS '配送会社';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."delivery_date" IS 'お届け希望日';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."delivery_time_slot" IS '配達希望時間帯';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."box_delivery" IS '不在時宅配ボックス';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."fragile_item" IS '割れ物注意';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."cushioning_unit" IS '緩衝材単位:0: なし 1:注文単位';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."gift_wrapping_unit" IS 'ギフトラッピング単位:0: なし 1:注文単位';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."cushioning_type" IS '緩衝材種別';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."gift_wrapping_type" IS 'ギフトラッピングタイプ';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."delivery_note_type" IS '明細書の同梱設定';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."price_on_delivery_note" IS '明細書への金額印字指定';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."invoice_special_notes" IS '出荷指示特記事項';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou4" IS '備考4';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou5" IS '備考5';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou6" IS '備考6';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."bikou7" IS '備考7';
COMMENT
ON COLUMN "public"."mc200_customer_delivery"."division" IS '部署';
COMMENT
ON TABLE "public"."mc200_customer_delivery" IS '顧客配送先マスタ:顧客CD、枝番';

CREATE TABLE "public"."ms001_function"
(
    "function_cd" varchar(2) COLLATE "pg_catalog"."default"  NOT NULL,
    "function_nm" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "biko"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"    timestamp(6),
    "upd_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"    timestamp(6),
    "del_flg"     int2,
    CONSTRAINT "ms001_function_PKC" PRIMARY KEY ("function_cd")
);
ALTER TABLE "public"."ms001_function" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms001_function_PKI" ON "public"."ms001_function" USING btree (
    "function_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms001_function"."function_cd" IS '機能コード';
COMMENT
ON COLUMN "public"."ms001_function"."function_nm" IS '機能名称';
COMMENT
ON COLUMN "public"."ms001_function"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms001_function"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms001_function"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms001_function"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms001_function"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms001_function"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms001_function" IS '機能マスタ:機能（管理者、入庫、出庫）';

CREATE TABLE "public"."ms002_authority"
(
    "authority_cd" varchar(2) COLLATE "pg_catalog"."default"  NOT NULL,
    "authority_nm" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
    "biko"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms002_authority_PKC" PRIMARY KEY ("authority_cd")
);
ALTER TABLE "public"."ms002_authority" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms002_authority_PKI" ON "public"."ms002_authority" USING btree (
    "authority_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms002_authority"."authority_cd" IS '権限コード';
COMMENT
ON COLUMN "public"."ms002_authority"."authority_nm" IS '権限名';
COMMENT
ON COLUMN "public"."ms002_authority"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms002_authority"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms002_authority"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms002_authority"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms002_authority"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms002_authority"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms002_authority" IS '権限マスタ:権限';

CREATE TABLE "public"."ms003_work"
(
    "operation_cd" varchar(2) COLLATE "pg_catalog"."default"  NOT NULL,
    "operation_nm" varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
    "biko"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms003_work_PKC" PRIMARY KEY ("operation_cd")
);
ALTER TABLE "public"."ms003_work" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms003_work_PKI" ON "public"."ms003_work" USING btree (
    "operation_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms003_work"."operation_cd" IS '作業コード';
COMMENT
ON COLUMN "public"."ms003_work"."operation_nm" IS '作業名';
COMMENT
ON COLUMN "public"."ms003_work"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms003_work"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms003_work"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms003_work"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms003_work"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms003_work"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms003_work" IS '作業マスタ:作業内容（管理者、入庫、出庫）';

CREATE TABLE "public"."ms004_delivery"
(
    "delivery_cd"          varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "delivery_method"      varchar(10) COLLATE "pg_catalog"."default",
    "delivery_nm"          varchar(50) COLLATE "pg_catalog"."default",
    "delivery_method_name" varchar(50) COLLATE "pg_catalog"."default",
    "delivery_method_csv"  varchar(10) COLLATE "pg_catalog"."default",
    "sort_no"              int4,
    "delivery_code"        varchar(50) COLLATE "pg_catalog"."default",
    "info"                 varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"              varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"             timestamp(6),
    "upd_usr"              varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"             timestamp(6),
    "del_flg"              int4,
    "default_size_cd"      varchar(10) COLLATE "pg_catalog"."default",
    CONSTRAINT "ms004_delivery_PKC" PRIMARY KEY ("delivery_cd")
);
ALTER TABLE "public"."ms004_delivery" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms004_delivery_PKI" ON "public"."ms004_delivery" USING btree (
    "delivery_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms004_delivery"."default_size_cd" IS 'デフォルトサイズCD';
COMMENT
ON TABLE "public"."ms004_delivery" IS '配送方法マスタ';

CREATE TABLE "public"."ms005_address"
(
    "address_cd"        int4                                      NOT NULL,
    "todoufuken_cd"     varchar(2) COLLATE "pg_catalog"."default" NOT NULL,
    "zip"               varchar(8) COLLATE "pg_catalog"."default" NOT NULL,
    "todoufuken"        varchar(10) COLLATE "pg_catalog"."default",
    "todoufuken_kana"   varchar(20) COLLATE "pg_catalog"."default",
    "shikuchouson"      varchar(100) COLLATE "pg_catalog"."default",
    "shikuchouson_kana" varchar(100) COLLATE "pg_catalog"."default",
    "city_area"         varchar(100) COLLATE "pg_catalog"."default",
    "city_area_kn"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"           varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"          timestamp(6),
    "upd_usr"           varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"          timestamp(6),
    "del_flg"           int2,
    CONSTRAINT "ms005_address_PKC" PRIMARY KEY ("address_cd")
);
ALTER TABLE "public"."ms005_address" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms005_address_PKI" ON "public"."ms005_address" USING btree (
    "address_cd" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON TABLE "public"."ms005_address" IS '住所マスタ';

CREATE TABLE "public"."ms006_delivery_time"
(
    "delivery_time_id"   int4 NOT NULL,
    "kubu"               int4                                       DEFAULT 1,
    "delivery_nm"        varchar(50) COLLATE "pg_catalog"."default",
    "delivery_time_name" varchar(100) COLLATE "pg_catalog"."default",
    "delivery_time_csv"  varchar(50) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "sort_no"            int4,
    "info"               varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"            varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"           timestamp(6),
    "upd_usr"            varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"           timestamp(6),
    "del_flg"            int2,
    CONSTRAINT "ms006_delivery_time_PKC" PRIMARY KEY ("delivery_time_id")
);
ALTER TABLE "public"."ms006_delivery_time" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms006_delivery_time_pki" ON "public"."ms006_delivery_time" USING btree (
    "delivery_time_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms006_delivery_time"."delivery_time_id" IS '配送時間管理ID';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."kubu" IS '利用区分';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."delivery_nm" IS '配送会社';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."delivery_time_name" IS '配送時間';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."delivery_time_csv" IS 'csv出力値';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."sort_no" IS '優先順位';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."info" IS '説明';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."ms006_delivery_time"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms006_delivery_time" IS '配送時間マスタ';

CREATE TABLE "public"."ms007_setting"
(
    "id"              int4                                       NOT NULL,
    "client_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "kubun"           int4                                       NOT NULL,
    "mapping_value"   varchar(100) COLLATE "pg_catalog"."default",
    "converted_value" varchar(100) COLLATE "pg_catalog"."default",
    "converted_id"    varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int4,
    CONSTRAINT "ms007_setting_PKC" PRIMARY KEY ("id", "client_id", "kubun")
);
ALTER TABLE "public"."ms007_setting" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms007_setting_PKI" ON "public"."ms007_setting" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "kubun" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms007_setting"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."ms007_setting"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms007_setting"."kubun" IS '区分';
COMMENT
ON COLUMN "public"."ms007_setting"."mapping_value" IS '設定値';
COMMENT
ON COLUMN "public"."ms007_setting"."converted_value" IS '変換値';
COMMENT
ON COLUMN "public"."ms007_setting"."converted_id" IS '変換ID';
COMMENT
ON COLUMN "public"."ms007_setting"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms007_setting"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."ms007_setting"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms007_setting"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."ms007_setting"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms007_setting" IS '配送連携設定マスタ';

CREATE TABLE "public"."ms008_items"
(
    "category_cd"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "item_id"      int4                                       NOT NULL,
    "item_nm"      varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "item_nm_kana" varchar(100) COLLATE "pg_catalog"."default",
    "item_nm_en"   varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms008_items_PKC" PRIMARY KEY ("category_cd", "item_id")
);
ALTER TABLE "public"."ms008_items" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms008_items_PKI" ON "public"."ms008_items" USING btree (
    "category_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "item_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms008_items"."category_cd" IS 'カテゴリーコード';
COMMENT
ON COLUMN "public"."ms008_items"."item_id" IS '品名CD';
COMMENT
ON COLUMN "public"."ms008_items"."item_nm" IS '品名';
COMMENT
ON COLUMN "public"."ms008_items"."item_nm_kana" IS '品名カナ';
COMMENT
ON COLUMN "public"."ms008_items"."item_nm_en" IS '品名英語表記';
COMMENT
ON COLUMN "public"."ms008_items"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms008_items"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms008_items"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms008_items"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms008_items"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms008_items" IS '品名マスタ:品名';

CREATE TABLE "public"."ms009_definition"
(
    "sys_kind"     int4                                       NOT NULL,
    "sys_cd"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "sys_name"     varchar(100) COLLATE "pg_catalog"."default",
    "info"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms009_definition_PKC" PRIMARY KEY ("sys_kind", "sys_cd", "warehouse_cd")
);
ALTER TABLE "public"."ms009_definition" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms009_definition_PKI" ON "public"."ms009_definition" USING btree (
    "sys_kind" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "sys_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms009_definition"."sys_kind" IS '名称区分:1:緩衝材 2: ギフトラッピング';
COMMENT
ON COLUMN "public"."ms009_definition"."sys_cd" IS '名称コード';
COMMENT
ON COLUMN "public"."ms009_definition"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."ms009_definition"."sys_name" IS '名称';
COMMENT
ON COLUMN "public"."ms009_definition"."info" IS '備考';
COMMENT
ON COLUMN "public"."ms009_definition"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms009_definition"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms009_definition"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms009_definition"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms009_definition"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms009_definition" IS '名称区分マスタ:名称区分、名称CD';

CREATE TABLE "public"."ms010_product_size"
(
    "size_cd"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "name"     varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "type"     varchar(10) COLLATE "pg_catalog"."default",
    "height"   int4,
    "length"   int4,
    "weight"   float8,
    "biko"     varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"  varchar(100) COLLATE "pg_catalog"."default",
    "ins_date" timestamp(6),
    "upd_usr"  varchar(100) COLLATE "pg_catalog"."default",
    "upd_date" timestamp(6),
    "del_flg"  int2,
    CONSTRAINT "ms010_product_size_PKC" PRIMARY KEY ("size_cd")
);
ALTER TABLE "public"."ms010_product_size" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms010_product_size_PKI" ON "public"."ms010_product_size" USING btree (
    "size_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms010_product_size"."size_cd" IS 'サイズCD';
COMMENT
ON COLUMN "public"."ms010_product_size"."name" IS '名称';
COMMENT
ON COLUMN "public"."ms010_product_size"."type" IS '規格';
COMMENT
ON COLUMN "public"."ms010_product_size"."height" IS '高さ';
COMMENT
ON COLUMN "public"."ms010_product_size"."length" IS '長さ';
COMMENT
ON COLUMN "public"."ms010_product_size"."weight" IS '重量';
COMMENT
ON COLUMN "public"."ms010_product_size"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms010_product_size"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms010_product_size"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms010_product_size"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms010_product_size"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms010_product_size"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms010_product_size" IS '商品サイズマスタ:商品サイズ管理';

CREATE TABLE "public"."ms011_config"
(
    "set_cd"    int2                                        NOT NULL,
    "client_id" varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "set_key"   varchar(20) COLLATE "pg_catalog"."default"  NOT NULL,
    "set_value" varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "info"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"   varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"  timestamp(6),
    "upd_usr"   varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"  timestamp(6),
    "del_flg"   int2 DEFAULT 0,
    CONSTRAINT "ms011_config_PKC" PRIMARY KEY ("set_cd")
);
ALTER TABLE "public"."ms011_config" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms011_config_pki" ON "public"."ms011_config" USING btree (
    "set_cd" "pg_catalog"."int2_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms011_config"."set_cd" IS '管理ID';
COMMENT
ON COLUMN "public"."ms011_config"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms011_config"."set_key" IS '項目コード（英数字）';
COMMENT
ON COLUMN "public"."ms011_config"."set_value" IS '項目値（楽天項目選択肢）';
COMMENT
ON COLUMN "public"."ms011_config"."info" IS 'コメント';
COMMENT
ON COLUMN "public"."ms011_config"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms011_config"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."ms011_config"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms011_config"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."ms011_config"."del_flg" IS '削除フラグ';

CREATE TABLE "public"."ms012_sponsor_master"
(
    "sponsor_id"             varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "client_id"              varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "utilize"                varchar(20) COLLATE "pg_catalog"."default",
    "name"                   varchar(40) COLLATE "pg_catalog"."default"  NOT NULL,
    "name_kana"              varchar(30) COLLATE "pg_catalog"."default",
    "company"                varchar(50) COLLATE "pg_catalog"."default",
    "division"               varchar(50) COLLATE "pg_catalog"."default",
    "postcode"               varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "prefecture"             varchar(50) COLLATE "pg_catalog"."default",
    "address1"               varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "address2"               varchar(100) COLLATE "pg_catalog"."default",
    "phone"                  varchar(20) COLLATE "pg_catalog"."default",
    "email"                  varchar(100) COLLATE "pg_catalog"."default",
    "contact"                int4                                        NOT NULL,
    "contact_url"            varchar(100) COLLATE "pg_catalog"."default",
    "detail_logo"            varchar(100) COLLATE "pg_catalog"."default",
    "detail_message"         text COLLATE "pg_catalog"."default",
    "send_message"           text COLLATE "pg_catalog"."default",
    "sponsor_default"        int4,
    "ins_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"               timestamp(6),
    "upd_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"               timestamp(6),
    "del_flg"                int4 DEFAULT 0,
    "fax"                    varchar(50) COLLATE "pg_catalog"."default",
    "label_note"             varchar(50) COLLATE "pg_catalog"."default",
    "delivery_note_type"     int4 DEFAULT 1,
    "price_on_delivery_note" int4 DEFAULT 1,
    "form"                   int4,
    CONSTRAINT "ms012_sponsor_master_PKC" PRIMARY KEY ("sponsor_id", "client_id")
);
ALTER TABLE "public"."ms012_sponsor_master" OWNER TO "prologi";
CREATE INDEX "ms012_sponsor_master_IX1" ON "public"."ms012_sponsor_master" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "ms012_sponsor_master_PKI" ON "public"."ms012_sponsor_master" USING btree (
    "sponsor_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."sponsor_id" IS '管理ID';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."utilize" IS '利用区分 :99999:CSVから注文者';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."name" IS 'お名前';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."name_kana" IS 'お名前（フリガナ）';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."company" IS '会社名';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."division" IS '部署';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."postcode" IS '郵便番号';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."address1" IS '住所';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."address2" IS 'マンション?ビル名';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."phone" IS '電話番号';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."email" IS 'メールアドレス';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."contact" IS 'お問い合わせ先設定:0: 電話番号　1: メールアドレス　2: お問い合わせフォーム';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."contact_url" IS 'お問い合わせフォームURL';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."detail_logo" IS '明細書ロゴ';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."detail_message" IS '明細書メッセージ';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."send_message" IS '発送通知メッセージ';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."sponsor_default" IS 'デフォルト:1: デフォルト';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."fax" IS 'FAX';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."label_note" IS '品名';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."delivery_note_type" IS '納品書の同梱設定';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."price_on_delivery_note" IS '納品書の金額印字';
COMMENT
ON COLUMN "public"."ms012_sponsor_master"."form" IS '1法人 2個人';
COMMENT
ON TABLE "public"."ms012_sponsor_master" IS '依頼主マスタ';

CREATE TABLE "public"."ms013_api_template"
(
    "id"             int4 NOT NULL,
    "template"       varchar(128) COLLATE "pg_catalog"."default",
    "identification" varchar(5) COLLATE "pg_catalog"."default",
    "ins_date"       timestamp(6),
    "ins_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "biko"           varchar(300) COLLATE "pg_catalog"."default",
    "del_flg"        int4,
    CONSTRAINT "ms013_api_template_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."ms013_api_template" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."ms013_api_template"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."ms013_api_template"."template" IS 'ECモール';
COMMENT
ON COLUMN "public"."ms013_api_template"."identification" IS '識別番号(内部用)';
COMMENT
ON COLUMN "public"."ms013_api_template"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."ms013_api_template"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms013_api_template"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms013_api_template"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms013_api_template" IS 'API連携管理マスタ';

CREATE TABLE "public"."ms014_payment"
(
    "payment_id"   int4 NOT NULL,
    "kubu"         int4 DEFAULT 1,
    "payment_name" varchar(100) COLLATE "pg_catalog"."default",
    "payment_csv"  varchar(100) COLLATE "pg_catalog"."default",
    "sort_no"      int4,
    "info"         varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms014_payment_id_PKC" PRIMARY KEY ("payment_id")
);
ALTER TABLE "public"."ms014_payment" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."ms014_payment"."payment_id" IS '決済管理ID';
COMMENT
ON COLUMN "public"."ms014_payment"."kubu" IS '利用区分';
COMMENT
ON COLUMN "public"."ms014_payment"."payment_name" IS '決済方法';
COMMENT
ON COLUMN "public"."ms014_payment"."payment_csv" IS 'csv出力値';
COMMENT
ON COLUMN "public"."ms014_payment"."sort_no" IS '優先順位';
COMMENT
ON COLUMN "public"."ms014_payment"."info" IS '説明';
COMMENT
ON COLUMN "public"."ms014_payment"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms014_payment"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."ms014_payment"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms014_payment"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."ms014_payment"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms014_payment" IS '決済方法マスタ';

CREATE TABLE "public"."ms015_news"
(
    "id"            int4 NOT NULL,
    "date"          date,
    "context"       varchar(300) COLLATE "pg_catalog"."default",
    "html_filename" int4,
    "ins_usr"       varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"      date,
    "del_flg"       int2,
    CONSTRAINT "ms015_news_id_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."ms015_news" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."ms015_news"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."ms015_news"."date" IS '日付';
COMMENT
ON COLUMN "public"."ms015_news"."context" IS '知らせ内容';
COMMENT
ON COLUMN "public"."ms015_news"."html_filename" IS 'HTMLファイル名';
COMMENT
ON COLUMN "public"."ms015_news"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms015_news"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."ms015_news"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms015_news" IS 'お知らせ管理';

CREATE TABLE "public"."ms016_macro"
(
    "id"                int4                                       NOT NULL,
    "client_id"         varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "macro_name"        varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "event_name"        varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "conditions_kube"   int4 DEFAULT 1,
    "conditions_item01" int4,
    "conditions01"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg01"  int4,
    "conditions_item02" int4,
    "conditions02"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg02"  int4,
    "conditions_item03" int4,
    "conditions03"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg03"  int4,
    "conditions_item04" int4,
    "conditions04"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg04"  int4,
    "conditions_item05" int4,
    "conditions05"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg05"  int4,
    "conditions_item06" int4,
    "conditions06"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg06"  int4,
    "conditions_item07" int4,
    "conditions07"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg07"  int4,
    "conditions_item08" int4,
    "conditions08"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg08"  int4,
    "conditions_item09" int4,
    "conditions09"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg09"  int4,
    "conditions_item10" int4,
    "conditions10"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg10"  int4,
    "conditions11"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_item11" int4,
    "conditions_flg11"  int4,
    "conditions12"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_item12" int4,
    "conditions_flg12"  int4,
    "priority"          int4,
    "action_code"       int4,
    "action_content"    varchar(500) COLLATE "pg_catalog"."default",
    "macro_status"      int4 DEFAULT 0,
    "ins_usr"           varchar(30) COLLATE "pg_catalog"."default",
    "ins_date"          timestamp(6),
    "upd_usr"           varchar(30) COLLATE "pg_catalog"."default",
    "upd_date"          timestamp(6),
    "del_flg"           int4 DEFAULT 0,
    "conditions_item13" int4,
    "conditions13"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg13"  int4,
    "conditions_item14" int4,
    "conditions14"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg14"  int4,
    "conditions_item15" int4,
    "conditions15"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg15"  int4,
    "conditions_item16" int4,
    "conditions16"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg16"  int4,
    "conditions_item17" int4,
    "conditions17"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg17"  int4,
    "conditions_item18" int4,
    "conditions18"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg18"  int4,
    "conditions_item19" int4,
    "conditions19"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg19"  int4,
    "conditions_item20" int4,
    "conditions20"      varchar(500) COLLATE "pg_catalog"."default",
    "conditions_flg20"  int4,
    "start_time"        timestamp(6),
    "end_time"          timestamp(6)
);
ALTER TABLE "public"."ms016_macro" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."ms016_macro"."id" IS 'ID';
COMMENT
ON COLUMN "public"."ms016_macro"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms016_macro"."macro_name" IS 'マクロ名';
COMMENT
ON COLUMN "public"."ms016_macro"."event_name" IS 'イベント名';
COMMENT
ON TABLE "public"."ms016_macro" IS 'マクロ';

CREATE TABLE "public"."ms017_csv_template"
(
    "id"             int4 NOT NULL,
    "template"       varchar(128) COLLATE "pg_catalog"."default",
    "identification" varchar(10) COLLATE "pg_catalog"."default",
    "ins_date"       timestamp(6),
    "ins_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "biko"           varchar(300) COLLATE "pg_catalog"."default",
    "del_flg"        int4 DEFAULT 0,
    CONSTRAINT "ms017_csv_template_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."ms017_csv_template" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."ms017_csv_template"."id" IS 'id';
COMMENT
ON COLUMN "public"."ms017_csv_template"."template" IS '受注方式';
COMMENT
ON COLUMN "public"."ms017_csv_template"."identification" IS '識別子';
COMMENT
ON COLUMN "public"."ms017_csv_template"."ins_date" IS '作成者';
COMMENT
ON COLUMN "public"."ms017_csv_template"."ins_usr" IS '作成日時';
COMMENT
ON COLUMN "public"."ms017_csv_template"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms017_csv_template"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms017_csv_template" IS '店舗CSV種類';

CREATE TABLE "public"."ms018_api_error"
(
    "id"          int4 NOT NULL,
    "order_id"    int4,
    "client_id"   varchar(10) COLLATE "pg_catalog"."default",
    "template"    varchar(100) COLLATE "pg_catalog"."default",
    "error_count" int4,
    "ins_usr"     varchar(10) COLLATE "pg_catalog"."default",
    "ins_date"    timestamp(6),
    "upd_usr"     varchar(10) COLLATE "pg_catalog"."default",
    "upd_date"    timestamp(6),
    "del_flg"     int2,
    CONSTRAINT "ms018_api_error_PKC" PRIMARY KEY ("id")
);
ALTER TABLE "public"."ms018_api_error" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms018_api_error_PKI" ON "public"."ms018_api_error" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms018_api_error"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."ms018_api_error"."order_id" IS 'api連携ID';
COMMENT
ON COLUMN "public"."ms018_api_error"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms018_api_error"."template" IS 'フォーマット';
COMMENT
ON COLUMN "public"."ms018_api_error"."error_count" IS 'エラー回数';
COMMENT
ON COLUMN "public"."ms018_api_error"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms018_api_error"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms018_api_error"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms018_api_error"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms018_api_error"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms018_api_error" IS 'api連携時のエラー記録';

CREATE TABLE "public"."ms200_customer"
(
    "user_id"      varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "login_id"     varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "login_pw"     varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"    varchar(10) COLLATE "pg_catalog"."default",
    "login_nm"     varchar(100) COLLATE "pg_catalog"."default",
    "usekb"        varchar(1) COLLATE "pg_catalog"."default"   NOT NULL DEFAULT 0,
    "yoto"         varchar(1) COLLATE "pg_catalog"."default"   NOT NULL,
    "notice"       varchar(1) COLLATE "pg_catalog"."default",
    "encode_key"   varchar(100) COLLATE "pg_catalog"."default",
    "old_login_pw" varchar(100) COLLATE "pg_catalog"."default",
    "old_login_id" varchar(100) COLLATE "pg_catalog"."default",
    "biko1"        varchar(100) COLLATE "pg_catalog"."default",
    "biko2"        varchar(100) COLLATE "pg_catalog"."default",
    "biko3"        varchar(100) COLLATE "pg_catalog"."default",
    "biko4"        varchar(100) COLLATE "pg_catalog"."default",
    "biko5"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "lst_date"     timestamp(6),
    "del_flg"      int2                                        NOT NULL DEFAULT 0,
    CONSTRAINT "ms200_customer_PKC" PRIMARY KEY ("user_id")
);
ALTER TABLE "public"."ms200_customer" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms200_customer_PKI" ON "public"."ms200_customer" USING btree (
    "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms200_customer"."user_id" IS '顧客CD:アカウントID';
COMMENT
ON COLUMN "public"."ms200_customer"."login_id" IS 'ログインID';
COMMENT
ON COLUMN "public"."ms200_customer"."login_pw" IS 'パスワード';
COMMENT
ON COLUMN "public"."ms200_customer"."client_id" IS '所属店舗ID';
COMMENT
ON COLUMN "public"."ms200_customer"."login_nm" IS 'ログイン名';
COMMENT
ON COLUMN "public"."ms200_customer"."usekb" IS '使用区分:0：未使用 1：使用中 2：申請中 3：確定待';
COMMENT
ON COLUMN "public"."ms200_customer"."yoto" IS '用途:1:店舗側 2:倉庫側 3:管理側 9:全て';
COMMENT
ON COLUMN "public"."ms200_customer"."notice" IS '通知';
COMMENT
ON COLUMN "public"."ms200_customer"."encode_key" IS '盐值';
COMMENT
ON COLUMN "public"."ms200_customer"."old_login_pw" IS '旧パスワード';
COMMENT
ON COLUMN "public"."ms200_customer"."old_login_id" IS '旧ログインID';
COMMENT
ON COLUMN "public"."ms200_customer"."biko1" IS '備考1';
COMMENT
ON COLUMN "public"."ms200_customer"."biko2" IS '備考2';
COMMENT
ON COLUMN "public"."ms200_customer"."biko3" IS '備考3';
COMMENT
ON COLUMN "public"."ms200_customer"."biko4" IS '備考4';
COMMENT
ON COLUMN "public"."ms200_customer"."biko5" IS '備考5';
COMMENT
ON COLUMN "public"."ms200_customer"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms200_customer"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms200_customer"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms200_customer"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms200_customer"."lst_date" IS '最終ログイン';
COMMENT
ON COLUMN "public"."ms200_customer"."del_flg" IS '削除フラグ:0:利用中 1:削除済';
COMMENT
ON TABLE "public"."ms200_customer" IS '顧客マスタ:店舗管理(ログイン情報)';

CREATE TABLE "public"."ms201_client"
(
    "client_id"            varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_nm"            varchar(50) COLLATE "pg_catalog"."default",
    "shop_nm"              varchar(100) COLLATE "pg_catalog"."default",
    "logo"                 varchar(100) COLLATE "pg_catalog"."default",
    "validity_str_date"    date,
    "validity_end_date"    date,
    "corporation_flg"      varchar(1) COLLATE "pg_catalog"."default",
    "corporation_number"   varchar(20) COLLATE "pg_catalog"."default",
    "department"           varchar(100) COLLATE "pg_catalog"."default",
    "main_office_flg"      int2,
    "birthday"             date,
    "country_region"       varchar(100) COLLATE "pg_catalog"."default",
    "tel"                  varchar(20) COLLATE "pg_catalog"."default",
    "fax"                  varchar(20) COLLATE "pg_catalog"."default",
    "zip"                  varchar(10) COLLATE "pg_catalog"."default",
    "tdfk"                 varchar(10) COLLATE "pg_catalog"."default",
    "add1"                 varchar(100) COLLATE "pg_catalog"."default",
    "add2"                 varchar(100) COLLATE "pg_catalog"."default",
    "tnnm"                 varchar(30) COLLATE "pg_catalog"."default",
    "tnbs"                 varchar(30) COLLATE "pg_catalog"."default",
    "mail"                 varchar(100) COLLATE "pg_catalog"."default",
    "contact_time"         varchar(10) COLLATE "pg_catalog"."default",
    "url"                  varchar(300) COLLATE "pg_catalog"."default",
    "permonth"             int4,
    "delivery_method"      varchar(50) COLLATE "pg_catalog"."default",
    "color"                varchar(50) COLLATE "pg_catalog"."default",
    "biko"                 varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"              varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"             timestamp(6),
    "upd_usr"              varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"             timestamp(6),
    "label_note"           varchar(50) COLLATE "pg_catalog"."default",
    "bill_customer_cd"     varchar(20) COLLATE "pg_catalog"."default",
    "fare_manage_cd"       varchar(10) COLLATE "pg_catalog"."default",
    "customer_code"        varchar(20) COLLATE "pg_catalog"."default",
    "delivery_code"        varchar(50) COLLATE "pg_catalog"."default",
    "yamato_manage_code"   varchar(20) COLLATE "pg_catalog"."default",
    "sagawa_nisugata_code" varchar(20) COLLATE "pg_catalog"."default",
    "ip_address"           varchar(100) COLLATE "pg_catalog"."default",
    "seino_delivery_code"  varchar(20) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "description_setting"  varchar(1) COLLATE "pg_catalog"."default"  DEFAULT '0':: character varying,
    CONSTRAINT "ms201_client_PKC" PRIMARY KEY ("client_id")
);
ALTER TABLE "public"."ms201_client" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms201_client_PKI" ON "public"."ms201_client" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms201_client"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms201_client"."client_nm" IS '店舗名';
COMMENT
ON COLUMN "public"."ms201_client"."shop_nm" IS '店舗表示名';
COMMENT
ON COLUMN "public"."ms201_client"."logo" IS '店舗ログ';
COMMENT
ON COLUMN "public"."ms201_client"."validity_str_date" IS '有効開始日付';
COMMENT
ON COLUMN "public"."ms201_client"."validity_end_date" IS '有効終了日付';
COMMENT
ON COLUMN "public"."ms201_client"."corporation_flg" IS '事業形態:1：法人 2：個人';
COMMENT
ON COLUMN "public"."ms201_client"."corporation_number" IS '法人番号';
COMMENT
ON COLUMN "public"."ms201_client"."department" IS '部署';
COMMENT
ON COLUMN "public"."ms201_client"."main_office_flg" IS '本社所在地フラグ';
COMMENT
ON COLUMN "public"."ms201_client"."birthday" IS '生年月日';
COMMENT
ON COLUMN "public"."ms201_client"."country_region" IS '国・地域';
COMMENT
ON COLUMN "public"."ms201_client"."tel" IS '電話番号';
COMMENT
ON COLUMN "public"."ms201_client"."fax" IS 'FAX';
COMMENT
ON COLUMN "public"."ms201_client"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."ms201_client"."tdfk" IS '都道府県';
COMMENT
ON COLUMN "public"."ms201_client"."add1" IS '住所１';
COMMENT
ON COLUMN "public"."ms201_client"."add2" IS '住所２';
COMMENT
ON COLUMN "public"."ms201_client"."tnnm" IS '担当者名';
COMMENT
ON COLUMN "public"."ms201_client"."tnbs" IS '担当部署';
COMMENT
ON COLUMN "public"."ms201_client"."mail" IS '担当メール';
COMMENT
ON COLUMN "public"."ms201_client"."contact_time" IS '連絡可能時間帯';
COMMENT
ON COLUMN "public"."ms201_client"."url" IS '店舗URL';
COMMENT
ON COLUMN "public"."ms201_client"."permonth" IS '出荷件数';
COMMENT
ON COLUMN "public"."ms201_client"."delivery_method" IS '配送方法';
COMMENT
ON COLUMN "public"."ms201_client"."color" IS '顔色';
COMMENT
ON COLUMN "public"."ms201_client"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms201_client"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms201_client"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms201_client"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms201_client"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms201_client"."label_note" IS '品名';
COMMENT
ON COLUMN "public"."ms201_client"."bill_customer_cd" IS '請求顧客コード';
COMMENT
ON COLUMN "public"."ms201_client"."fare_manage_cd" IS '運賃管理番号';
COMMENT
ON COLUMN "public"."ms201_client"."customer_code" IS 'お客様コード';
COMMENT
ON COLUMN "public"."ms201_client"."delivery_code" IS '荷送人コード';
COMMENT
ON COLUMN "public"."ms201_client"."yamato_manage_code" IS '荷姿コード';
COMMENT
ON COLUMN "public"."ms201_client"."sagawa_nisugata_code" IS 'ご請求先分類コード';
COMMENT
ON COLUMN "public"."ms201_client"."ip_address" IS 'ログイン許可IP';
COMMENT
ON COLUMN "public"."ms201_client"."seino_delivery_code" IS '西濃運輸荷送人コード';
COMMENT
ON COLUMN "public"."ms201_client"."description_setting" IS '品名設定';
COMMENT
ON TABLE "public"."ms201_client" IS '店舗マスタ:店舗情報管理テーブル';

CREATE TABLE "public"."ms202_customer_wh"
(
    "client_id"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "kubun"        varchar(1) COLLATE "pg_catalog"."default" DEFAULT 0,
    "yobi"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms202_customer_wh_PKC" PRIMARY KEY ("client_id", "warehouse_cd")
);
ALTER TABLE "public"."ms202_customer_wh" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms202_customer_wh_PKI" ON "public"."ms202_customer_wh" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms202_customer_wh"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."kubun" IS '使用区分:0:利用中　1:未利用';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."yobi" IS '備考';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms202_customer_wh"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms202_customer_wh" IS '店舗別倉庫マスタ:店舗別倉庫管理(店舗側)';

CREATE TABLE "public"."ms203_customer_auth"
(
    "user_id"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "authority_cd" varchar(2) COLLATE "pg_catalog"."default"  NOT NULL,
    "authority_kb" varchar(1) COLLATE "pg_catalog"."default"  NOT NULL,
    "biko"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "ms203_customer_auth_PKC" PRIMARY KEY ("user_id", "authority_cd")
);
ALTER TABLE "public"."ms203_customer_auth" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms203_customer_auth_PKI" ON "public"."ms203_customer_auth" USING btree (
    "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "authority_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms203_customer_auth"."user_id" IS '顧客CD';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."authority_cd" IS '権限コード';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."authority_kb" IS '使用区分:0：参照 1：全処理';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms203_customer_auth"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms203_customer_auth" IS '顧客別権限マスタ:権限（管理者、入庫、出庫)';

CREATE TABLE "public"."ms204_customer_func"
(
    "user_id"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "function_cd" varchar(2) COLLATE "pg_catalog"."default"  NOT NULL,
    "function_kb" varchar(1) COLLATE "pg_catalog"."default"  NOT NULL DEFAULT 1,
    "biko"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"    timestamp(6),
    "upd_usr"     varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"    timestamp(6),
    "del_flg"     int2                                                DEFAULT 0,
    CONSTRAINT "ms204_customer_func_PKC" PRIMARY KEY ("user_id", "function_cd")
);
ALTER TABLE "public"."ms204_customer_func" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms204_customer_func_PKI" ON "public"."ms204_customer_func" USING btree (
    "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "function_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms204_customer_func"."user_id" IS '顧客CD';
COMMENT
ON COLUMN "public"."ms204_customer_func"."function_cd" IS '機能コード';
COMMENT
ON COLUMN "public"."ms204_customer_func"."function_kb" IS '使用区分:1:可　2:不可';
COMMENT
ON COLUMN "public"."ms204_customer_func"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms204_customer_func"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms204_customer_func"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms204_customer_func"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms204_customer_func"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms204_customer_func"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."ms204_customer_func" IS '顧客別機能マスタ:機能（管理者、入庫、出庫）';

CREATE TABLE "public"."ms205_customer_history"
(
    "operation_id"   int4                                       NOT NULL,
    "user_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "plan_id"        varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "operation_date" timestamp(6)                               NOT NULL,
    "operation_cd"   varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "api_url"        varchar(300) COLLATE "pg_catalog"."default",
    "biko"           varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"       timestamp(6),
    "upd_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"       timestamp(6),
    "del_flg"        int2,
    CONSTRAINT "ms205_customer_history_PKC" PRIMARY KEY ("operation_id")
);
ALTER TABLE "public"."ms205_customer_history" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms205_customer_history_PKI" ON "public"."ms205_customer_history" USING btree (
    "operation_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms205_customer_history"."operation_id" IS '作業管理ID';
COMMENT
ON COLUMN "public"."ms205_customer_history"."user_id" IS '作業者(顧客CD)';
COMMENT
ON COLUMN "public"."ms205_customer_history"."plan_id" IS '依頼ID';
COMMENT
ON COLUMN "public"."ms205_customer_history"."operation_date" IS '作業時間';
COMMENT
ON COLUMN "public"."ms205_customer_history"."operation_cd" IS '作業コード';
COMMENT
ON COLUMN "public"."ms205_customer_history"."api_url" IS 'API URL';
COMMENT
ON COLUMN "public"."ms205_customer_history"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms205_customer_history"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms205_customer_history"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms205_customer_history"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms205_customer_history"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms205_customer_history"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms205_customer_history" IS '顧客別作業履歴:作業履歴記録（管理者、入庫、出庫）';

CREATE TABLE "public"."ms206_client_customer"
(
    "client_id" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "user_id"   varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "yobi"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"   varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"  timestamp(6),
    "upd_usr"   varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"  timestamp(6),
    "del_flg"   int2,
    CONSTRAINT "ms206_client_customer_PKC" PRIMARY KEY ("client_id", "user_id")
);
ALTER TABLE "public"."ms206_client_customer" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms206_client_customer_PKI" ON "public"."ms206_client_customer" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms206_client_customer"."client_id" IS '店舗コード';
COMMENT
ON COLUMN "public"."ms206_client_customer"."user_id" IS '顧客CD';
COMMENT
ON COLUMN "public"."ms206_client_customer"."yobi" IS '備考';
COMMENT
ON COLUMN "public"."ms206_client_customer"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms206_client_customer"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms206_client_customer"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms206_client_customer"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms206_client_customer"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."ms206_client_customer" IS '店舗別顧客マスタ:店舗別顧客管理(店舗側)';

CREATE TABLE "public"."ms207_all_func"
(
    "id"           int4                                      NOT NULL,
    "function_cd"  varchar(2) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default",
    "client_id"    varchar(10) COLLATE "pg_catalog"."default",
    "function_kb"  varchar(1) COLLATE "pg_catalog"."default" NOT NULL DEFAULT 0,
    "biko"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2                                               DEFAULT 0,
    CONSTRAINT "ms207_all_func_PKC" PRIMARY KEY ("id", "function_cd")
);
ALTER TABLE "public"."ms207_all_func" OWNER TO "prologi";
CREATE UNIQUE INDEX "ms207_all_func_PKI" ON "public"."ms207_all_func" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "function_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."ms207_all_func"."id" IS 'ID';
COMMENT
ON COLUMN "public"."ms207_all_func"."function_cd" IS '機能コード';
COMMENT
ON COLUMN "public"."ms207_all_func"."warehouse_cd" IS '倉庫CD';
COMMENT
ON COLUMN "public"."ms207_all_func"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."ms207_all_func"."function_kb" IS '使用区分:0:可　1:不可';
COMMENT
ON COLUMN "public"."ms207_all_func"."biko" IS '備考';
COMMENT
ON COLUMN "public"."ms207_all_func"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."ms207_all_func"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."ms207_all_func"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."ms207_all_func"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."ms207_all_func"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."ms207_all_func" IS '目的别機能マスタ:店舗、倉庫';

CREATE TABLE "public"."mw400_warehouse"
(
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_nm" varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "alias"        varchar(100) COLLATE "pg_catalog"."default",
    "tel"          varchar(20) COLLATE "pg_catalog"."default",
    "fax"          varchar(20) COLLATE "pg_catalog"."default",
    "zip"          varchar(10) COLLATE "pg_catalog"."default",
    "todoufuken"   varchar(10) COLLATE "pg_catalog"."default",
    "address1"     varchar(100) COLLATE "pg_catalog"."default",
    "address2"     varchar(100) COLLATE "pg_catalog"."default",
    "responsible"  varchar(20) COLLATE "pg_catalog"."default",
    "mail"         varchar(50) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    "ip_address"   varchar(100) COLLATE "pg_catalog"."default",
    CONSTRAINT "mw400_warehouse_PKC" PRIMARY KEY ("warehouse_cd")
);
ALTER TABLE "public"."mw400_warehouse" OWNER TO "prologi";
CREATE UNIQUE INDEX "mw400_warehouse_PKI" ON "public"."mw400_warehouse" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw400_warehouse"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."mw400_warehouse"."warehouse_nm" IS '倉庫名称';
COMMENT
ON COLUMN "public"."mw400_warehouse"."alias" IS '備考';
COMMENT
ON COLUMN "public"."mw400_warehouse"."tel" IS '電話番号';
COMMENT
ON COLUMN "public"."mw400_warehouse"."fax" IS 'FAX';
COMMENT
ON COLUMN "public"."mw400_warehouse"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."mw400_warehouse"."todoufuken" IS '都道府県';
COMMENT
ON COLUMN "public"."mw400_warehouse"."address1" IS '住所１';
COMMENT
ON COLUMN "public"."mw400_warehouse"."address2" IS '住所２';
COMMENT
ON COLUMN "public"."mw400_warehouse"."responsible" IS '担当者名';
COMMENT
ON COLUMN "public"."mw400_warehouse"."mail" IS 'メール';
COMMENT
ON COLUMN "public"."mw400_warehouse"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw400_warehouse"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw400_warehouse"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mw400_warehouse"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw400_warehouse"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."mw400_warehouse"."ip_address" IS 'ログイン許可IP';
COMMENT
ON TABLE "public"."mw400_warehouse" IS '倉庫マスタ:倉庫情報';

CREATE TABLE "public"."mw401_wh_delivery"
(
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "delivery_cd"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "delivery_kb"  varchar(10) COLLATE "pg_catalog"."default",
    "info"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "mw401_wh_delivery_PKC" PRIMARY KEY ("warehouse_cd", "delivery_cd")
);
ALTER TABLE "public"."mw401_wh_delivery" OWNER TO "prologi";
CREATE UNIQUE INDEX "mw401_wh_delivery_PKI" ON "public"."mw401_wh_delivery" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "delivery_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."delivery_cd" IS '配送業者CD';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."delivery_kb" IS '使用区分:1:利用中　2:利用不可';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."info" IS '備考';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw401_wh_delivery"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."mw401_wh_delivery" IS '倉庫別運送業者マスタ:倉庫運送業者';

CREATE TABLE "public"."mw402_wh_client"
(
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "user_id"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "yobi"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2,
    CONSTRAINT "mw402_wh_client_PKC" PRIMARY KEY ("warehouse_cd", "user_id")
);
ALTER TABLE "public"."mw402_wh_client" OWNER TO "prologi";
CREATE UNIQUE INDEX "mw402_wh_client_PKI" ON "public"."mw402_wh_client" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "user_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw402_wh_client"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."mw402_wh_client"."user_id" IS '顧客CD';
COMMENT
ON COLUMN "public"."mw402_wh_client"."yobi" IS '備考';
COMMENT
ON COLUMN "public"."mw402_wh_client"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw402_wh_client"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw402_wh_client"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mw402_wh_client"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw402_wh_client"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."mw402_wh_client" IS '倉庫別顧客マスタ:倉庫別顧客管理(倉庫側)';

CREATE TABLE "public"."mw404_location"
(
    "location_id"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "wh_location_cd"  varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
    "wh_location_nm"  varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
    "info"            varchar(100) COLLATE "pg_catalog"."default",
    "priority"        int4,
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int2,
    "bestbefore_date" date,
    "status"          int4                                       DEFAULT 0,
    "lot_no"          varchar(30) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    CONSTRAINT "mw404_location_PKC" PRIMARY KEY ("location_id", "warehouse_cd")
);
ALTER TABLE "public"."mw404_location" OWNER TO "prologi";
CREATE UNIQUE INDEX "mw404_location_IX1" ON "public"."mw404_location" USING btree (
    "location_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "mw404_location_PKI" ON "public"."mw404_location" USING btree (
    "location_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw404_location"."location_id" IS 'ロケーションID';
COMMENT
ON COLUMN "public"."mw404_location"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."mw404_location"."wh_location_cd" IS '倉庫ロケーション';
COMMENT
ON COLUMN "public"."mw404_location"."wh_location_nm" IS 'ロケーション名称';
COMMENT
ON COLUMN "public"."mw404_location"."info" IS '備考';
COMMENT
ON COLUMN "public"."mw404_location"."priority" IS '優先順位';
COMMENT
ON COLUMN "public"."mw404_location"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw404_location"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw404_location"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mw404_location"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw404_location"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."mw404_location"."bestbefore_date" IS '賞味期限/在庫保管期限';
COMMENT
ON COLUMN "public"."mw404_location"."status" IS '出荷不可フラグ (0: 可   1：不可)';
COMMENT
ON COLUMN "public"."mw404_location"."lot_no" IS 'ロット番号';
COMMENT
ON TABLE "public"."mw404_location" IS '倉庫ロケマスタ:倉庫ロケーション管理';

CREATE TABLE "public"."mw405_product_location"
(
    "location_id"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "stock_cnt"       int4 DEFAULT 0,
    "reserve_cnt"     int4 DEFAULT 0,
    "lot_no"          varchar(30) COLLATE "pg_catalog"."default",
    "bestbefore_date" date,
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int2,
    "not_delivery"    int4 DEFAULT 0,
    "requesting_cnt"  int4 DEFAULT 0,
    "status"          int4 DEFAULT 0,
    CONSTRAINT "mw405_product_location_PKC" PRIMARY KEY ("location_id", "client_id", "product_id")
);
ALTER TABLE "public"."mw405_product_location" OWNER TO "prologi";
CREATE INDEX "mw405_product_location_PKI" ON "public"."mw405_product_location" USING btree (
    "location_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int2_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw405_product_location"."location_id" IS 'ロケーションID';
COMMENT
ON COLUMN "public"."mw405_product_location"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."mw405_product_location"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."mw405_product_location"."stock_cnt" IS '在庫数';
COMMENT
ON COLUMN "public"."mw405_product_location"."reserve_cnt" IS '引当数';
COMMENT
ON COLUMN "public"."mw405_product_location"."lot_no" IS 'ロット番号';
COMMENT
ON COLUMN "public"."mw405_product_location"."bestbefore_date" IS '賞味期限/在庫保管期限';
COMMENT
ON COLUMN "public"."mw405_product_location"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw405_product_location"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw405_product_location"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mw405_product_location"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw405_product_location"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."mw405_product_location"."not_delivery" IS '不可配送数';
COMMENT
ON COLUMN "public"."mw405_product_location"."requesting_cnt" IS '在庫依頼中数量';
COMMENT
ON COLUMN "public"."mw405_product_location"."status" IS '出荷フラグ';
COMMENT
ON TABLE "public"."mw405_product_location" IS '商品ロケーション管理';

CREATE TABLE "public"."mw406_wh_smartcat"
(
    "id"           int4                                        NOT NULL,
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "cooperation"  varchar(30) COLLATE "pg_catalog"."default"  NOT NULL,
    "file_path"    varchar(300) COLLATE "pg_catalog"."default" NOT NULL,
    "file_start"   varchar(20) COLLATE "pg_catalog"."default",
    "file_end"     varchar(20) COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"     timestamp(6),
    "upd_usr"      varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"     timestamp(6),
    "del_flg"      int2 DEFAULT 0,
    CONSTRAINT "mw406_wh_smartcat_PKC" PRIMARY KEY ("id", "warehouse_cd")
);
ALTER TABLE "public"."mw406_wh_smartcat" OWNER TO "prologi";
CREATE UNIQUE INDEX "mw406_wh_smartcat_PKI" ON "public"."mw406_wh_smartcat" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."id" IS 'ID';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."warehouse_cd" IS '倉庫ID';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."cooperation" IS '連携名';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."file_path" IS '保存先（PC環境）';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."file_start" IS 'ファイル接頭語';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."file_end" IS 'ファイル接尾語';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw406_wh_smartcat"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."mw406_wh_smartcat" IS '倉庫別スマートCatマスタ';

CREATE TABLE "public"."mw407_smart_file"
(
    "id"               int4                                       NOT NULL,
    "warehouse_cd"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "file_path"        varchar(200) COLLATE "pg_catalog"."default",
    "file_name"        varchar(80) COLLATE "pg_catalog"."default",
    "flag"             int4 DEFAULT 0,
    "ip"               varchar(20) COLLATE "pg_catalog"."default",
    "upd_date"         timestamp(6),
    "upd_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"         timestamp(6),
    "ins_usr"          varchar(100) COLLATE "pg_catalog"."default",
    CONSTRAINT "mw407_smart_file_PKC" PRIMARY KEY ("id", "warehouse_cd", "shipment_plan_id")
);
ALTER TABLE "public"."mw407_smart_file" OWNER TO "prologi";
CREATE UNIQUE INDEX "mw407_smart_file_PKI" ON "public"."mw407_smart_file" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."mw407_smart_file"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."mw407_smart_file"."warehouse_cd" IS '倉庫CD';
COMMENT
ON COLUMN "public"."mw407_smart_file"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."mw407_smart_file"."file_path" IS 'パス';
COMMENT
ON COLUMN "public"."mw407_smart_file"."file_name" IS 'ファイル名';
COMMENT
ON COLUMN "public"."mw407_smart_file"."flag" IS '出力フラグ:0:出力ない、1:出力済み';
COMMENT
ON COLUMN "public"."mw407_smart_file"."ip" IS 'CSV出力IP';
COMMENT
ON COLUMN "public"."mw407_smart_file"."upd_date" IS '作成日時';
COMMENT
ON COLUMN "public"."mw407_smart_file"."upd_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw407_smart_file"."ins_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw407_smart_file"."ins_usr" IS '更新者';
COMMENT
ON TABLE "public"."mw407_smart_file" IS 'スマートCATファイル';

CREATE TABLE "public"."mw410_delivery_takes_days"
(
    "sender"     varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "receiver"   varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "sagawa_day" int4                                        NOT NULL,
    "ins_usr"    varchar(10) COLLATE "pg_catalog"."default",
    "upd_date"   timestamp(6),
    "del_flg"    int4 DEFAULT 0
);
ALTER TABLE "public"."mw410_delivery_takes_days" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."mw410_delivery_takes_days"."sender" IS '送信者都道府県';
COMMENT
ON COLUMN "public"."mw410_delivery_takes_days"."receiver" IS 'レシーバー都道府県';
COMMENT
ON COLUMN "public"."mw410_delivery_takes_days"."sagawa_day" IS '届け佐川急便日';
COMMENT
ON COLUMN "public"."mw410_delivery_takes_days"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."mw410_delivery_takes_days"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."mw410_delivery_takes_days"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON TABLE "public"."mw410_delivery_takes_days" IS '遠方配送天数';

CREATE TABLE "public"."tc100_warehousing_plan"
(
    "client_id"             varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd"          varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "id"                    varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "request_date"          timestamp(6),
    "arrival_date"          date,
    "status"                int4,
    "inspection_type"       varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "identifier"            varchar(30) COLLATE "pg_catalog"."default",
    "product_kind_plan_cnt" int4,
    "quantity"              int4,
    "shipment_return"       int4 DEFAULT 0,
    "tracking_codes_1"      varchar(30) COLLATE "pg_catalog"."default",
    "tracking_codes_2"      varchar(30) COLLATE "pg_catalog"."default",
    "tracking_codes_3"      varchar(30) COLLATE "pg_catalog"."default",
    "tracking_codes_4"      varchar(30) COLLATE "pg_catalog"."default",
    "tracking_codes_5"      varchar(30) COLLATE "pg_catalog"."default",
    "ins_usr"               varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"              timestamp(6),
    "upd_usr"               varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"              timestamp(6),
    "del_flg"               int4 DEFAULT 0,
    "warehousing_date"      timestamp(6),
    CONSTRAINT "tc100_warehousing_plan_PKC" PRIMARY KEY ("client_id", "warehouse_cd", "id")
);
ALTER TABLE "public"."tc100_warehousing_plan" OWNER TO "prologi";
CREATE UNIQUE INDEX "tc100_warehousing_plan_PKI" ON "public"."tc100_warehousing_plan" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."request_date" IS '入庫依頼日';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."arrival_date" IS '入庫予定日';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."status" IS '入庫ステータス:1:入庫待ち 2:検品中 3:検品完了 4:完了';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."inspection_type" IS '検品タイプ';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."identifier" IS '入庫依頼識別子';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."product_kind_plan_cnt" IS '入庫依頼商品種類数';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."quantity" IS '入庫依頼商品合記';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."shipment_return" IS '返品フラグ:0:返品なし 1:返品';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."tracking_codes_1" IS 'お問合せ伝票番号1';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."tracking_codes_2" IS 'お問合せ伝票番号2';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."tracking_codes_3" IS 'お問合せ伝票番号3';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."tracking_codes_4" IS 'お問合せ伝票番号4';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."tracking_codes_5" IS 'お問合せ伝票番号5';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON COLUMN "public"."tc100_warehousing_plan"."warehousing_date" IS '検品処理日';
COMMENT
ON TABLE "public"."tc100_warehousing_plan" IS '入庫依頼管理TBL:入庫依頼情報';

CREATE TABLE "public"."tc101_warehousing_plan_detail"
(
    "client_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "id"              varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "quantity"        int4,
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int4                                       DEFAULT 0,
    "status"          int4                                       DEFAULT 0,
    "lot_no"          varchar(30) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "bestbefore_date" date,
    "location_status" int4                                       DEFAULT 0,
    "shipping_flag"   int4                                       DEFAULT 0,
    CONSTRAINT "tc101_warehousing_plan_detail_PKC" PRIMARY KEY ("client_id", "warehouse_cd", "id", "product_id")
);
ALTER TABLE "public"."tc101_warehousing_plan_detail" OWNER TO "prologi";
CREATE INDEX "tc101_warehousing_plan_detail_IX1" ON "public"."tc101_warehousing_plan_detail" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "tc101_warehousing_plan_detail_PKI" ON "public"."tc101_warehousing_plan_detail" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."quantity" IS '入庫依頼数';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."status" IS '入庫ステータス(0:未入庫  1:入庫完了)';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."lot_no" IS 'ロット番号';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."bestbefore_date" IS '賞味期限/在庫保管期限';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."location_status" IS '0:出荷可 1:出荷不可';
COMMENT
ON COLUMN "public"."tc101_warehousing_plan_detail"."shipping_flag" IS '出荷フラグ';
COMMENT
ON TABLE "public"."tc101_warehousing_plan_detail" IS '入庫依頼明細:入庫予定SKU別';

CREATE TABLE "public"."tc200_order"
(
    "purchase_order_no"           varchar(30) COLLATE "pg_catalog"."default"  NOT NULL,
    "warehouse_cd"                varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "client_id"                   varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "outer_order_no"              varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "history_id"                  varchar(25) COLLATE "pg_catalog"."default",
    "outer_order_status"          int4,
    "order_datetime"              timestamp(6),
    "payment_method"              varchar(50) COLLATE "pg_catalog"."default",
    "order_type"                  int4,
    "member_info"                 varchar(50) COLLATE "pg_catalog"."default",
    "product_price_excluding_tax" int4                                        DEFAULT 0,
    "tax_total"                   int4                                        DEFAULT 0,
    "cash_on_delivery_fee"        int4                                        DEFAULT 0,
    "other_fee"                   int4                                        DEFAULT 0,
    "delivery_total"              int4                                        DEFAULT 0,
    "billing_total"               int4                                        NOT NULL,
    "order_zip_code1"             varchar(10) COLLATE "pg_catalog"."default",
    "order_zip_code2"             varchar(10) COLLATE "pg_catalog"."default",
    "order_todoufuken"            varchar(10) COLLATE "pg_catalog"."default",
    "order_address1"              varchar(100) COLLATE "pg_catalog"."default",
    "order_address2"              varchar(100) COLLATE "pg_catalog"."default",
    "order_family_name"           varchar(50) COLLATE "pg_catalog"."default",
    "order_first_name"            varchar(50) COLLATE "pg_catalog"."default",
    "order_family_kana"           varchar(50) COLLATE "pg_catalog"."default",
    "order_first_kana"            varchar(50) COLLATE "pg_catalog"."default",
    "order_phone_number1"         varchar(20) COLLATE "pg_catalog"."default",
    "order_phone_number2"         varchar(20) COLLATE "pg_catalog"."default",
    "order_phone_number3"         varchar(20) COLLATE "pg_catalog"."default",
    "order_mail"                  varchar(100) COLLATE "pg_catalog"."default",
    "order_gender"                int2,
    "receiver_zip_code1"          varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "receiver_zip_code2"          varchar(10) COLLATE "pg_catalog"."default",
    "receiver_todoufuken"         varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "receiver_address1"           varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "receiver_address2"           varchar(100) COLLATE "pg_catalog"."default",
    "receiver_family_name"        varchar(50) COLLATE "pg_catalog"."default"  NOT NULL,
    "receiver_first_name"         varchar(50) COLLATE "pg_catalog"."default",
    "receiver_order_family_kana"  varchar(50) COLLATE "pg_catalog"."default",
    "receiver_order_first_kana"   varchar(50) COLLATE "pg_catalog"."default",
    "receiver_phone_number1"      varchar(20) COLLATE "pg_catalog"."default",
    "receiver_phone_number2"      varchar(20) COLLATE "pg_catalog"."default",
    "receiver_phone_number3"      varchar(20) COLLATE "pg_catalog"."default",
    "receiver_mail"               varchar(100) COLLATE "pg_catalog"."default",
    "receiver_gender"             int2,
    "deliveryman"                 varchar(50) COLLATE "pg_catalog"."default",
    "delivery_time_slot"          varchar(10) COLLATE "pg_catalog"."default",
    "delivery_date"               date,
    "shipment_plan_id"            varchar(20) COLLATE "pg_catalog"."default",
    "import_datetime"             timestamp(6)                                NOT NULL,
    "shipping_request_datetime"   timestamp(6),
    "shipment_wish_date"          date,
    "shipment_plan_date"          date,
    "delivery_type"               varchar(10) COLLATE "pg_catalog"."default",
    "delivery_method"             varchar(50) COLLATE "pg_catalog"."default",
    "delivery_company"            varchar(50) COLLATE "pg_catalog"."default",
    "receiver_wish_method"        varchar(50) COLLATE "pg_catalog"."default",
    "gift_wish"                   varchar(50) COLLATE "pg_catalog"."default",
    "detail_bundled"              varchar(10) COLLATE "pg_catalog"."default"  DEFAULT '同梱する':: character varying,
    "detail_price_print"          varchar(1) COLLATE "pg_catalog"."default"   DEFAULT 1,
    "detail_message"              text COLLATE "pg_catalog"."default",
    "bikou1"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou2"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou4"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou5"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou6"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou7"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou8"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou9"                      varchar(100) COLLATE "pg_catalog"."default",
    "bikou10"                     varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"                     varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"                    timestamp(6),
    "upd_usr"                     varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"                    timestamp(6),
    "del_flg"                     int4                                        DEFAULT 0,
    "handling_charge"             int4,
    "sponsor_id"                  varchar(10) COLLATE "pg_catalog"."default",
    "bikou_flg"                   int4,
    "order_flag"                  int4,
    "order_company"               varchar(50) COLLATE "pg_catalog"."default",
    "order_division"              varchar(30) COLLATE "pg_catalog"."default",
    "receiver_company"            varchar(50) COLLATE "pg_catalog"."default",
    "receiver_division"           varchar(30) COLLATE "pg_catalog"."default",
    "label_note"                  varchar(50) COLLATE "pg_catalog"."default",
    "box_delivery"                int4,
    "fragile_item"                int4,
    "delivery_id"                 varchar(10) COLLATE "pg_catalog"."default"  DEFAULT NULL:: character varying,
    "bill_barcode"                varchar(50) COLLATE "pg_catalog"."default",
    "total_with_normal_tax"       int4,
    "total_with_reduced_tax"      int4,
    "payment_id"                  varchar(50) COLLATE "pg_catalog"."default",
    "buy_id"                      varchar(30) COLLATE "pg_catalog"."default",
    "buy_cnt"                     int4,
    "next_delivery_date"          date,
    "memo"                        text COLLATE "pg_catalog"."default",
    "form"                        int4                                        DEFAULT 2,
    "invoice_special_notes"       varchar(150) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "related_order_no"            text COLLATE "pg_catalog"."default",
    CONSTRAINT "tc200_order_PKC" PRIMARY KEY ("purchase_order_no")
);
ALTER TABLE "public"."tc200_order" OWNER TO "prologi";
CREATE INDEX "tc200_order_PKI" ON "public"."tc200_order" USING btree (
    "purchase_order_no" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "outer_order_no" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "outer_order_status" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc200_order"."purchase_order_no" IS '受注番号';
COMMENT
ON COLUMN "public"."tc200_order"."warehouse_cd" IS '倉庫管理番号';
COMMENT
ON COLUMN "public"."tc200_order"."client_id" IS '顧客管理番号';
COMMENT
ON COLUMN "public"."tc200_order"."outer_order_no" IS '外部受注番号';
COMMENT
ON COLUMN "public"."tc200_order"."history_id" IS '受注取込履歴ID';
COMMENT
ON COLUMN "public"."tc200_order"."outer_order_status" IS '外部注文ステータス';
COMMENT
ON COLUMN "public"."tc200_order"."order_datetime" IS '注文日時';
COMMENT
ON COLUMN "public"."tc200_order"."payment_method" IS '支払方法';
COMMENT
ON COLUMN "public"."tc200_order"."order_type" IS '注文種別';
COMMENT
ON COLUMN "public"."tc200_order"."member_info" IS '会員情報';
COMMENT
ON COLUMN "public"."tc200_order"."product_price_excluding_tax" IS '商品税抜金額';
COMMENT
ON COLUMN "public"."tc200_order"."tax_total" IS '消費税合計';
COMMENT
ON COLUMN "public"."tc200_order"."cash_on_delivery_fee" IS '代引料';
COMMENT
ON COLUMN "public"."tc200_order"."other_fee" IS 'その他金額';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_total" IS '送料合計';
COMMENT
ON COLUMN "public"."tc200_order"."billing_total" IS '合計請求金額';
COMMENT
ON COLUMN "public"."tc200_order"."order_zip_code1" IS '注文者郵便番号1';
COMMENT
ON COLUMN "public"."tc200_order"."order_zip_code2" IS '注文者郵便番号2';
COMMENT
ON COLUMN "public"."tc200_order"."order_todoufuken" IS '注文者住所都道府県';
COMMENT
ON COLUMN "public"."tc200_order"."order_address1" IS '注文者住所郡市区';
COMMENT
ON COLUMN "public"."tc200_order"."order_address2" IS '注文者詳細住所';
COMMENT
ON COLUMN "public"."tc200_order"."order_family_name" IS '注文者姓';
COMMENT
ON COLUMN "public"."tc200_order"."order_first_name" IS '注文者名';
COMMENT
ON COLUMN "public"."tc200_order"."order_family_kana" IS '注文者姓カナ';
COMMENT
ON COLUMN "public"."tc200_order"."order_first_kana" IS '注文者名カナ';
COMMENT
ON COLUMN "public"."tc200_order"."order_phone_number1" IS '注文者電話番号1';
COMMENT
ON COLUMN "public"."tc200_order"."order_phone_number2" IS '注文者電話番号2';
COMMENT
ON COLUMN "public"."tc200_order"."order_phone_number3" IS '注文者電話番号3';
COMMENT
ON COLUMN "public"."tc200_order"."order_mail" IS '注文者メールアドレス';
COMMENT
ON COLUMN "public"."tc200_order"."order_gender" IS '注文者性別';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_zip_code1" IS '配送先郵便番号1';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_zip_code2" IS '配送先郵便番号2';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_todoufuken" IS '配送先住所都道府県';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_address1" IS '配送先住所郡市区';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_address2" IS '配送先詳細住所';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_family_name" IS '配送先姓';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_first_name" IS '配送先名';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_order_family_kana" IS '配送先姓カナ';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_order_first_kana" IS '配送先名カナ';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_phone_number1" IS '配送先電話番号1';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_phone_number2" IS '配送先電話番号2';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_phone_number3" IS '配送先電話番号3';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_mail" IS '配送先メールアドレス';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_gender" IS '配送先性別';
COMMENT
ON COLUMN "public"."tc200_order"."deliveryman" IS '配達担当者';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_time_slot" IS '配達希望時間帯';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_date" IS '配達希望日';
COMMENT
ON COLUMN "public"."tc200_order"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tc200_order"."import_datetime" IS '取込日時';
COMMENT
ON COLUMN "public"."tc200_order"."shipping_request_datetime" IS '出庫依頼日時';
COMMENT
ON COLUMN "public"."tc200_order"."shipment_wish_date" IS '出荷希望日';
COMMENT
ON COLUMN "public"."tc200_order"."shipment_plan_date" IS '出庫予定日';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_type" IS '配送便指定';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_method" IS '配送方法';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_company" IS '配送会社指定';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_wish_method" IS '受取方法希望';
COMMENT
ON COLUMN "public"."tc200_order"."gift_wish" IS 'ギフト配送希望';
COMMENT
ON COLUMN "public"."tc200_order"."detail_bundled" IS '明細同梱設定';
COMMENT
ON COLUMN "public"."tc200_order"."detail_price_print" IS '明細書金額印字';
COMMENT
ON COLUMN "public"."tc200_order"."detail_message" IS '明細書メッセージ';
COMMENT
ON COLUMN "public"."tc200_order"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."tc200_order"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."tc200_order"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."tc200_order"."bikou4" IS '備考4';
COMMENT
ON COLUMN "public"."tc200_order"."bikou5" IS '備考5';
COMMENT
ON COLUMN "public"."tc200_order"."bikou6" IS '備考6';
COMMENT
ON COLUMN "public"."tc200_order"."bikou7" IS '備考7';
COMMENT
ON COLUMN "public"."tc200_order"."bikou8" IS '備考8';
COMMENT
ON COLUMN "public"."tc200_order"."bikou9" IS '備考9';
COMMENT
ON COLUMN "public"."tc200_order"."bikou10" IS '備考10';
COMMENT
ON COLUMN "public"."tc200_order"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc200_order"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc200_order"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc200_order"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc200_order"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tc200_order"."handling_charge" IS '手数料';
COMMENT
ON COLUMN "public"."tc200_order"."sponsor_id" IS '注文者ID(依頼主ID)';
COMMENT
ON COLUMN "public"."tc200_order"."bikou_flg" IS '備考内容显示flg';
COMMENT
ON COLUMN "public"."tc200_order"."order_flag" IS '注文者依頼フラグ';
COMMENT
ON COLUMN "public"."tc200_order"."order_company" IS '注文者会社名';
COMMENT
ON COLUMN "public"."tc200_order"."order_division" IS '注文者部署';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_company" IS '配送先会社名';
COMMENT
ON COLUMN "public"."tc200_order"."receiver_division" IS '配送先部署';
COMMENT
ON COLUMN "public"."tc200_order"."delivery_id" IS '配送業者ID(外部連携)';
COMMENT
ON COLUMN "public"."tc200_order"."bill_barcode" IS '請求コード';
COMMENT
ON COLUMN "public"."tc200_order"."total_with_normal_tax" IS '消費税合計10%';
COMMENT
ON COLUMN "public"."tc200_order"."total_with_reduced_tax" IS '消費税合計8%';
COMMENT
ON COLUMN "public"."tc200_order"."payment_id" IS '取引ID(GMO)';
COMMENT
ON COLUMN "public"."tc200_order"."buy_id" IS '定期購入ID';
COMMENT
ON COLUMN "public"."tc200_order"."buy_cnt" IS '定期購入回数';
COMMENT
ON COLUMN "public"."tc200_order"."next_delivery_date" IS '次回お届け予定日';
COMMENT
ON COLUMN "public"."tc200_order"."memo" IS '購入者備考欄';
COMMENT
ON COLUMN "public"."tc200_order"."form" IS '1: 法人 2: 个人';
COMMENT
ON COLUMN "public"."tc200_order"."invoice_special_notes" IS '送り状特記事項';
COMMENT
ON COLUMN "public"."tc200_order"."related_order_no" IS 'Qoo10関連注文番号';
COMMENT
ON TABLE "public"."tc200_order" IS '受注依頼管理テーブル';

CREATE TABLE "public"."tc201_order_detail"
(
    "order_detail_no"     varchar(35) COLLATE "pg_catalog"."default" NOT NULL,
    "purchase_order_no"   varchar(30) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"          varchar(10) COLLATE "pg_catalog"."default",
    "product_code"        varchar(100) COLLATE "pg_catalog"."default",
    "product_name"        varchar(300) COLLATE "pg_catalog"."default",
    "unit_price"          int4,
    "number"              int4,
    "product_total_price" int4,
    "product_option"      text COLLATE "pg_catalog"."default",
    "wrapping_title1"     varchar(50) COLLATE "pg_catalog"."default",
    "wrapping_name1"      varchar(50) COLLATE "pg_catalog"."default",
    "wrapping_price1"     int4,
    "wrapping_tax1"       int4,
    "wrapping_type1"      varchar(50) COLLATE "pg_catalog"."default",
    "wrapping_title2"     varchar(50) COLLATE "pg_catalog"."default",
    "wrapping_type2"      varchar(50) COLLATE "pg_catalog"."default",
    "wrapping_name2"      varchar(50) COLLATE "pg_catalog"."default",
    "wrapping_price2"     int4,
    "wrapping_tax2"       int4,
    "bikou1"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou2"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou4"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou5"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou6"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou7"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou8"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou9"              varchar(100) COLLATE "pg_catalog"."default",
    "bikou10"             varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"            timestamp(6),
    "upd_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"            timestamp(6),
    "del_flg"             int4 DEFAULT 0,
    "is_reduced_tax"      int4,
    "set_sub_id"          int4,
    "bundled_flg"         int4 DEFAULT 0,
    "tax_flag"            int4,
    "product_barcode"     varchar(20) COLLATE "pg_catalog"."default",
    "product_kubun"       int4 DEFAULT 0,
    "option_price"        int4,
    CONSTRAINT "tc201_order_detail_PKC" PRIMARY KEY ("order_detail_no")
);
ALTER TABLE "public"."tc201_order_detail" OWNER TO "prologi";
CREATE INDEX "tc201_order_detail_PKI" ON "public"."tc201_order_detail" USING btree (
    "order_detail_no" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "purchase_order_no" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc201_order_detail"."order_detail_no" IS '受注明細番号:商品明細、配送会社に使われる情報などe.g.''SL-OD-DT-202006121344-999''';
COMMENT
ON COLUMN "public"."tc201_order_detail"."purchase_order_no" IS '受注番号';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_id" IS '商品ID:商品マスタ.商品IDここYes(FK)にしない理由として、取り込んだ商品はマスタにまた登録していない状況を備えて、画面側から赤字提示 再度相談した結果、やはり、また登録されてない商品にて、空いている可能性があるため、一旦NotNull設定しない';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_code" IS '商品コード:商品マスタ.商品コード上記と同じ、マスタに確認して登録してから更新可能';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_name" IS '商品名:商品マスタ.商品名';
COMMENT
ON COLUMN "public"."tc201_order_detail"."unit_price" IS '単価';
COMMENT
ON COLUMN "public"."tc201_order_detail"."number" IS '個数';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_total_price" IS '商品計';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_option" IS '商品オプション';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_title1" IS 'ラッピングタイトル1';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_name1" IS 'ラッピング名1';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_price1" IS 'ラッピング料金1';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_tax1" IS 'ラッピング税込別1';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_type1" IS 'ラッピング種類1';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_title2" IS 'ラッピングタイトル2';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_type2" IS 'ラッピング種類2:一旦、楽天のサンプルデータを参照し、ラッピングデータを2セットまで設定したが、他の足りない情報を保存するため、備考のコラムを追加したことでカバーする';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_name2" IS 'ラッピング名2';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_price2" IS 'ラッピング料金2';
COMMENT
ON COLUMN "public"."tc201_order_detail"."wrapping_tax2" IS 'ラッピング税込別2';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou4" IS '備考4';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou5" IS '備考5';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou6" IS '備考6';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou7" IS '備考7';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou8" IS '備考8';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou9" IS '備考9';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bikou10" IS '備考10';
COMMENT
ON COLUMN "public"."tc201_order_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc201_order_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc201_order_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc201_order_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc201_order_detail"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tc201_order_detail"."is_reduced_tax" IS '軽減税率適用商品';
COMMENT
ON COLUMN "public"."tc201_order_detail"."set_sub_id" IS 'セット商品ID';
COMMENT
ON COLUMN "public"."tc201_order_detail"."bundled_flg" IS '同梱物フラグ';
COMMENT
ON COLUMN "public"."tc201_order_detail"."tax_flag" IS '税区分';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_barcode" IS '管理バーコード';
COMMENT
ON COLUMN "public"."tc201_order_detail"."product_kubun" IS '商品区分(0:通常商品、9:商品)';
COMMENT
ON COLUMN "public"."tc201_order_detail"."option_price" IS 'オプション金額';
COMMENT
ON TABLE "public"."tc201_order_detail" IS '受注明細テーブル';

CREATE TABLE "public"."tc202_order_history"
(
    "history_id"      int4                                       NOT NULL,
    "client_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "total_cnt"       int4,
    "success_cnt"     int4,
    "failure_cnt"     int4,
    "import_datetime" timestamp(6),
    "note"            varchar(255) COLLATE "pg_catalog"."default",
    "biko01"          varchar(100) COLLATE "pg_catalog"."default",
    "biko02"          varchar(100) COLLATE "pg_catalog"."default",
    "biko03"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_user"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_user"        varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int4 DEFAULT 0,
    CONSTRAINT "tc202_order_history_PKC" PRIMARY KEY ("history_id")
);
ALTER TABLE "public"."tc202_order_history" OWNER TO "prologi";
CREATE UNIQUE INDEX "tc202_order_history_PKI" ON "public"."tc202_order_history" USING btree (
    "history_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc202_order_history"."history_id" IS '受注取込履歴ID';
COMMENT
ON COLUMN "public"."tc202_order_history"."client_id" IS '顧客管理番号';
COMMENT
ON COLUMN "public"."tc202_order_history"."total_cnt" IS '取込件数';
COMMENT
ON COLUMN "public"."tc202_order_history"."success_cnt" IS '成功件数';
COMMENT
ON COLUMN "public"."tc202_order_history"."failure_cnt" IS '失敗件数';
COMMENT
ON COLUMN "public"."tc202_order_history"."import_datetime" IS '取込時間';
COMMENT
ON COLUMN "public"."tc202_order_history"."note" IS 'メモ';
COMMENT
ON COLUMN "public"."tc202_order_history"."biko01" IS '備考1';
COMMENT
ON COLUMN "public"."tc202_order_history"."biko02" IS '備考2';
COMMENT
ON COLUMN "public"."tc202_order_history"."biko03" IS '備考3';
COMMENT
ON COLUMN "public"."tc202_order_history"."ins_user" IS '作成者';
COMMENT
ON COLUMN "public"."tc202_order_history"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc202_order_history"."upd_user" IS '更新者';
COMMENT
ON COLUMN "public"."tc202_order_history"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc202_order_history"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tc202_order_history" IS '受注取込履歴テーブル';

CREATE TABLE "public"."tc203_order_client"
(
    "id"              int4                                       NOT NULL,
    "client_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_url"      varchar(100) COLLATE "pg_catalog"."default",
    "api_name"        varchar COLLATE "pg_catalog"."default",
    "api_key"         varchar(200) COLLATE "pg_catalog"."default",
    "password"        varchar(200) COLLATE "pg_catalog"."default",
    "token"           varchar(50) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "template"        varchar(100) COLLATE "pg_catalog"."default",
    "identification"  varchar(10) COLLATE "pg_catalog"."default",
    "hostname"        varchar(100) COLLATE "pg_catalog"."default",
    "shipment_status" int4,
    "order_status"    int4,
    "delivery_status" int4,
    "stock_status"    int4                                       DEFAULT 0,
    "bikou1"          varchar(100) COLLATE "pg_catalog"."default",
    "bikou2"          varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int2,
    "access_token"    varchar COLLATE "pg_catalog"."default",
    "refresh_token"   varchar COLLATE "pg_catalog"."default",
    "sponsor_id"      varchar(10) COLLATE "pg_catalog"."default",
    "expire_date"     date,
    CONSTRAINT "tc203_order_client_PKC" PRIMARY KEY ("id", "client_id")
);
ALTER TABLE "public"."tc203_order_client" OWNER TO "prologi";
CREATE UNIQUE INDEX "tc203_order_client_pki" ON "public"."tc203_order_client" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc203_order_client"."id" IS 'id:1: shopify';
COMMENT
ON COLUMN "public"."tc203_order_client"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc203_order_client"."client_url" IS '店舗URL';
COMMENT
ON COLUMN "public"."tc203_order_client"."api_name" IS 'アプリ名';
COMMENT
ON COLUMN "public"."tc203_order_client"."api_key" IS 'ライセンスキー';
COMMENT
ON COLUMN "public"."tc203_order_client"."password" IS 'パスワード';
COMMENT
ON COLUMN "public"."tc203_order_client"."token" IS '認証トークン';
COMMENT
ON COLUMN "public"."tc203_order_client"."template" IS 'テンプレート';
COMMENT
ON COLUMN "public"."tc203_order_client"."identification" IS '店舗識別コード';
COMMENT
ON COLUMN "public"."tc203_order_client"."hostname" IS 'ホスト名';
COMMENT
ON COLUMN "public"."tc203_order_client"."shipment_status" IS '出庫依頼:0:自動ない , 1:自動連携';
COMMENT
ON COLUMN "public"."tc203_order_client"."order_status" IS '受注取込:0:自動ない , 1:自動連携';
COMMENT
ON COLUMN "public"."tc203_order_client"."delivery_status" IS '出荷通知:0:自動ない , 1:自動連携';
COMMENT
ON COLUMN "public"."tc203_order_client"."stock_status" IS '在庫連携:0:自動ない , 1:自動連携';
COMMENT
ON COLUMN "public"."tc203_order_client"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."tc203_order_client"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."tc203_order_client"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."tc203_order_client"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc203_order_client"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc203_order_client"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc203_order_client"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc203_order_client"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tc203_order_client"."access_token" IS 'アクセストークン(未利用)';
COMMENT
ON COLUMN "public"."tc203_order_client"."refresh_token" IS 'リフレッシュトークン(未利用)';
COMMENT
ON COLUMN "public"."tc203_order_client"."sponsor_id" IS '依頼主ID';
COMMENT
ON COLUMN "public"."tc203_order_client"."expire_date" IS '有効期限日付';
COMMENT
ON TABLE "public"."tc203_order_client" IS '店舗受注API連携表';

CREATE TABLE "public"."tc204_order_template"
(
    "template_cd"    int4                                       NOT NULL,
    "client_id"      varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "company_id"     varchar(20) COLLATE "pg_catalog"."default",
    "template_nm"    varchar(100) COLLATE "pg_catalog"."default",
    "template"       text COLLATE "pg_catalog"."default",
    "constant"       text COLLATE "pg_catalog"."default",
    "encoding"       varchar(20) COLLATE "pg_catalog"."default",
    "delimiter"      varchar(5) COLLATE "pg_catalog"."default",
    "data"           text COLLATE "pg_catalog"."default",
    "ins_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"       timestamp(6),
    "upd_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"       timestamp(6),
    "del_flg"        int4,
    "identification" varchar(10) COLLATE "pg_catalog"."default",
    CONSTRAINT "tc204_order_template_PKC" PRIMARY KEY ("template_cd", "client_id")
);
ALTER TABLE "public"."tc204_order_template" OWNER TO "prologi";
CREATE UNIQUE INDEX "tc204_order_template_PKI" ON "public"."tc204_order_template" USING btree (
    "template_cd" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc204_order_template"."template_cd" IS 'テンプレートID';
COMMENT
ON COLUMN "public"."tc204_order_template"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc204_order_template"."company_id" IS 'EC店舗ID';
COMMENT
ON COLUMN "public"."tc204_order_template"."template_nm" IS 'テンプレートネーム';
COMMENT
ON COLUMN "public"."tc204_order_template"."template" IS 'お客様のCSVテンプレート';
COMMENT
ON COLUMN "public"."tc204_order_template"."constant" IS '固定値';
COMMENT
ON COLUMN "public"."tc204_order_template"."encoding" IS '文字コード';
COMMENT
ON COLUMN "public"."tc204_order_template"."delimiter" IS '区切り文字';
COMMENT
ON COLUMN "public"."tc204_order_template"."data" IS 'テンプレートデータ';
COMMENT
ON COLUMN "public"."tc204_order_template"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc204_order_template"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tc204_order_template"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc204_order_template"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tc204_order_template"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tc204_order_template"."identification" IS '識別子';
COMMENT
ON TABLE "public"."tc204_order_template" IS '受注テンプレート明細';

CREATE TABLE "public"."tc205_order_company"
(
    "company_id"   varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "company_name" varchar(100) COLLATE "pg_catalog"."default",
    "template"     text COLLATE "pg_catalog"."default",
    "del_flg"      int4,
    CONSTRAINT "tc205_order_company_PKC" PRIMARY KEY ("company_id")
);
ALTER TABLE "public"."tc205_order_company" OWNER TO "prologi";
CREATE UNIQUE INDEX "tc205_order_company_PKI" ON "public"."tc205_order_company" USING btree (
    "company_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc205_order_company"."company_id" IS 'EC店舗ID';
COMMENT
ON COLUMN "public"."tc205_order_company"."company_name" IS 'EC店舗名称';
COMMENT
ON COLUMN "public"."tc205_order_company"."template" IS 'EC店舗模板';
COMMENT
ON COLUMN "public"."tc205_order_company"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tc205_order_company" IS 'EC店舗受注テンプレート';

CREATE TABLE "public"."tc206_order_ftp"
(
    "id"            int4 NOT NULL,
    "client_id"     varchar(10) COLLATE "pg_catalog"."default",
    "ftp_host"      varchar(200) COLLATE "pg_catalog"."default",
    "ftp_user"      varchar(50) COLLATE "pg_catalog"."default",
    "ftp_passwd"    varchar(50) COLLATE "pg_catalog"."default",
    "ftp_path"      varchar(200) COLLATE "pg_catalog"."default",
    "ftp_file"      varchar(200) COLLATE "pg_catalog"."default",
    "template"      varchar(10) COLLATE "pg_catalog"."default",
    "warehouses"    varchar(100) COLLATE "pg_catalog"."default",
    "get_send_flag" int4,
    "ins_date"      timestamp(6),
    "upd_date"      timestamp(6),
    CONSTRAINT "tc206_order_ftp_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."tc206_order_ftp" OWNER TO "prologi";
CREATE UNIQUE INDEX "tc206_order_ftp_PKI" ON "public"."tc206_order_ftp" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tc206_order_ftp"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."ftp_host" IS 'FTPホスト名';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."ftp_user" IS 'FTPユーザ名';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."ftp_passwd" IS 'パスワード';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."ftp_path" IS '取得先Path';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."ftp_file" IS 'ファイル';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."template" IS 'テンプレート';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."warehouses" IS '倉庫';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."get_send_flag" IS '取得フラグ';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."tc206_order_ftp"."upd_date" IS '更新日';
COMMENT
ON TABLE "public"."tc206_order_ftp" IS 'FTP連携設定';

CREATE TABLE "public"."tc207_order_error"
(
    "order_error_no" int4                                       NOT NULL,
    "client_id"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "outer_order_no" varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
    "history_id"     varchar(25) COLLATE "pg_catalog"."default",
    "status"         int4 DEFAULT 0,
    "error_msg"      text COLLATE "pg_catalog"."default",
    "bikou1"         varchar(100) COLLATE "pg_catalog"."default",
    "bikou2"         varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"       timestamp(6),
    "upd_usr"        varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"       timestamp(6),
    "del_flg"        int4 DEFAULT 0
);
ALTER TABLE "public"."tc207_order_error" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tc207_order_error"."order_error_no" IS '受注失敗管理ID';
COMMENT
ON COLUMN "public"."tc207_order_error"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc207_order_error"."outer_order_no" IS '外部受注番号';
COMMENT
ON COLUMN "public"."tc207_order_error"."history_id" IS '取込履歴管理ID';
COMMENT
ON COLUMN "public"."tc207_order_error"."status" IS 'ステータス:0:未確認 1:確認済';
COMMENT
ON COLUMN "public"."tc207_order_error"."error_msg" IS 'エラーメッセージ';
COMMENT
ON COLUMN "public"."tc207_order_error"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."tc207_order_error"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."tc207_order_error"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."tc207_order_error"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc207_order_error"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."tc207_order_error"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc207_order_error"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."tc207_order_error"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tc207_order_error" IS '受注失敗管理表';

CREATE TABLE "public"."tc207_order_s3"
(
    "id"            int4 NOT NULL,
    "client_id"     varchar(10) COLLATE "pg_catalog"."default",
    "bucket"        varchar(100) COLLATE "pg_catalog"."default",
    "password1"     varchar(100) COLLATE "pg_catalog"."default",
    "password2"     varchar(100) COLLATE "pg_catalog"."default",
    "folder"        varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"      timestamp(6),
    "upd_date"      timestamp(6),
    "upload_folder" varchar(100) COLLATE "pg_catalog"."default",
    CONSTRAINT "tc207_order_s3_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."tc207_order_s3" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tc207_order_s3"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."tc207_order_s3"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc207_order_s3"."bucket" IS 'S3パケット';
COMMENT
ON COLUMN "public"."tc207_order_s3"."password1" IS 'パスワード1';
COMMENT
ON COLUMN "public"."tc207_order_s3"."password2" IS 'パスワード2';
COMMENT
ON COLUMN "public"."tc207_order_s3"."folder" IS '取込先Path';
COMMENT
ON COLUMN "public"."tc207_order_s3"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."tc207_order_s3"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."tc207_order_s3"."upload_folder" IS '格納先Path';
COMMENT
ON TABLE "public"."tc207_order_s3" IS 'S3連携設定(オプション機能)';

CREATE TABLE "public"."tc208_order_cancel"
(
    "order_cancel_no"  int4                                       NOT NULL,
    "client_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "outer_order_no"   varchar(32) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id" varchar(20) COLLATE "pg_catalog"."default",
    "status"           int4 DEFAULT 0,
    "bikou1"           varchar(100) COLLATE "pg_catalog"."default",
    "bikou2"           varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"           varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"         timestamp(6),
    "upd_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"         timestamp(6),
    "del_flg"          int4 DEFAULT 0
);
ALTER TABLE "public"."tc208_order_cancel" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tc208_order_cancel"."order_cancel_no" IS 'キャセンル管理ID';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."outer_order_no" IS '外部受注番号';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."status" IS '確認状況:0:未確認 1:確認済';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."bikou1" IS '備考01';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."bikou2" IS '備考02';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."bikou3" IS '備考03';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."ins_date" IS '作成日';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."upd_date" IS '更新日';
COMMENT
ON COLUMN "public"."tc208_order_cancel"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tc208_order_cancel" IS '外部受注キャセンル管理';

CREATE TABLE "public"."tc209_csv_template"
(
    "template_cd"  int4 NOT NULL,
    "client_id"    varchar(20) COLLATE "pg_catalog"."default",
    "warehouse_cd" varchar(20) COLLATE "pg_catalog"."default",
    "template_nm"  varchar(100) COLLATE "pg_catalog"."default",
    "data"         text COLLATE "pg_catalog"."default",
    "del_flg"      int4,
    "yoto_id"      int4
);
ALTER TABLE "public"."tc209_csv_template" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tc209_csv_template"."template_cd" IS '管理ID';
COMMENT
ON COLUMN "public"."tc209_csv_template"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tc209_csv_template"."warehouse_cd" IS '倉庫CD';
COMMENT
ON COLUMN "public"."tc209_csv_template"."template_nm" IS 'テンプレート名';
COMMENT
ON COLUMN "public"."tc209_csv_template"."data" IS '設定CSV項目';
COMMENT
ON COLUMN "public"."tc209_csv_template"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tc209_csv_template"."yoto_id" IS '用途 1:明細単位 2:伝票単位';
COMMENT
ON TABLE "public"."tc209_csv_template" IS 'CSV出力テンプレート';

CREATE TABLE "public"."tc209_setting_template"
(
    "template_cd"  int4 NOT NULL,
    "warehouse_cd" varchar(20) COLLATE "pg_catalog"."default",
    "client_id"    varchar(20) COLLATE "pg_catalog"."default",
    "template_nm"  varchar(100) COLLATE "pg_catalog"."default",
    "yoto_id"      varchar(3) COLLATE "pg_catalog"."default",
    "encoding"     varchar(10) COLLATE "pg_catalog"."default",
    "constant"     text COLLATE "pg_catalog"."default",
    "data"         text COLLATE "pg_catalog"."default",
    "ins_usr"      varchar(10) COLLATE "pg_catalog"."default",
    "ins_date"     date,
    "upd_usr"      varchar(10) COLLATE "pg_catalog"."default",
    "upd_date"     date,
    "del_flg"      int4 DEFAULT 0,
    CONSTRAINT "tc209_setting_template_pkey" PRIMARY KEY ("template_cd")
);
ALTER TABLE "public"."tc209_setting_template" OWNER TO "prologi";
COMMENT
ON TABLE "public"."tc209_setting_template" IS 'エクスポートCSV管理';

CREATE TABLE "public"."tc210_csv_yoto"
(
    "yoto_id"  int4 NOT NULL,
    "template" text COLLATE "pg_catalog"."default",
    "del_flg"  int4
);
ALTER TABLE "public"."tc210_csv_yoto" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tc210_csv_yoto"."yoto_id" IS '用途ID';
COMMENT
ON COLUMN "public"."tc210_csv_yoto"."template" IS 'テンプレートCSV項目';
COMMENT
ON COLUMN "public"."tc210_csv_yoto"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tc210_csv_yoto" IS 'CSV項目管理';

CREATE TABLE "public"."tc210_setting_yoto"
(
    "yoto_id"  varchar(3) COLLATE "pg_catalog"."default" NOT NULL,
    "name"     varchar COLLATE "pg_catalog"."default",
    "template" text COLLATE "pg_catalog"."default",
    "del_flg"  int4,
    CONSTRAINT "tc210_setting_yoto_pkey" PRIMARY KEY ("yoto_id")
);
ALTER TABLE "public"."tc210_setting_yoto" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tc210_setting_yoto"."yoto_id" IS '用途ID';
COMMENT
ON COLUMN "public"."tc210_setting_yoto"."name" IS '用途名';
COMMENT
ON COLUMN "public"."tc210_setting_yoto"."template" IS 'テンプレート';
COMMENT
ON COLUMN "public"."tc210_setting_yoto"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tc210_setting_yoto" IS 'テンプレートマスタ';

CREATE TABLE "public"."tw110_warehousing_result"
(
    "warehouse_cd"           varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"              varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehousing_plan_id"    varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "request_date"           timestamp(6),
    "warehousing_plan_date"  date,
    "warehousing_type"       varchar(10) COLLATE "pg_catalog"."default",
    "inspection_type"        varchar(10) COLLATE "pg_catalog"."default",
    "search_tag"             varchar(30) COLLATE "pg_catalog"."default",
    "warehousing_status_wh"  varchar(10) COLLATE "pg_catalog"."default",
    "arrived_date"           date,
    "inspection_date"        timestamp(6),
    "warehousing_date"       timestamp(6),
    "payment_type"           varchar(10) COLLATE "pg_catalog"."default",
    "freight_collect_amount" int4,
    "unit_number"            int2,
    "tracking_no"            varchar(30) COLLATE "pg_catalog"."default",
    "product_kind_plan_cnt"  int4,
    "product_plan_total"     int4,
    "product_kind_cnt"       int4,
    "product_total"          int4,
    "ins_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"               timestamp(6),
    "upd_usr"                varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"               timestamp(6),
    "del_flg"                int2,
    CONSTRAINT "tw110_warehousing_result_PKC" PRIMARY KEY ("warehouse_cd", "client_id", "warehousing_plan_id")
);
ALTER TABLE "public"."tw110_warehousing_result" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw110_warehousing_result_PKI" ON "public"."tw110_warehousing_result" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehousing_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."warehousing_plan_id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."request_date" IS '入庫依頼日';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."warehousing_plan_date" IS '入庫予定日';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."warehousing_type" IS '入庫タイプ';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."inspection_type" IS '検品タイプ';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."search_tag" IS '検索キーワード';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."warehousing_status_wh" IS '入庫ステータス';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."arrived_date" IS '着荷日';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."inspection_date" IS '検品開始日';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."warehousing_date" IS '検品処理日';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."payment_type" IS '支払方法';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."freight_collect_amount" IS '着払金額';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."unit_number" IS '個口数';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."tracking_no" IS '問合せ番号';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."product_kind_plan_cnt" IS '入庫依頼商品種類数';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."product_plan_total" IS '入庫依頼商品数計';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."product_kind_cnt" IS '入庫実績商品種類数';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."product_total" IS '入庫実績商品数計';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw110_warehousing_result"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw110_warehousing_result" IS '入庫実績テーブル:入庫実績';

CREATE TABLE "public"."tw111_warehousing_result_detail"
(
    "warehouse_cd"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"           varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehousing_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"          varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_plan_cnt"    int4,
    "product_cnt"         int4,
    "product_size_cd"     varchar(10) COLLATE "pg_catalog"."default",
    "product_weight"      float4,
    "weigth_unit"         varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"            timestamp(6),
    "upd_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"            timestamp(6),
    "del_flg"             int2,
    CONSTRAINT "tw111_warehousing_result_detail_PKC" PRIMARY KEY ("warehouse_cd", "client_id", "warehousing_plan_id", "product_id")
);
ALTER TABLE "public"."tw111_warehousing_result_detail" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw111_warehousing_result_detail_PKI" ON "public"."tw111_warehousing_result_detail" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehousing_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."warehousing_plan_id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."product_plan_cnt" IS '入庫依頼数';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."product_cnt" IS '入庫実績数';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."product_size_cd" IS '商品サイズコード';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."product_weight" IS '商品重量';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."weigth_unit" IS '重量単位';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw111_warehousing_result_detail"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw111_warehousing_result_detail" IS '入庫実績明細テーブル:入庫実績SKU別';

CREATE TABLE "public"."tw112_warehousing_result_unknown"
(
    "warehouse_cd"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"           varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehousing_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "sub_id"              int2                                       NOT NULL,
    "product_id"          varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_cnt"         int4,
    "product_size_cd"     varchar(10) COLLATE "pg_catalog"."default",
    "location"            varchar(30) COLLATE "pg_catalog"."default",
    "hold_reason"         varchar(100) COLLATE "pg_catalog"."default",
    "memo"                varchar(100) COLLATE "pg_catalog"."default",
    "item_img"            varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"            timestamp(6),
    "upd_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"            timestamp(6),
    "del_flg"             int2,
    CONSTRAINT "tw112_warehousing_result_unknown_PKC" PRIMARY KEY ("warehouse_cd", "client_id", "warehousing_plan_id", "sub_id")
);
ALTER TABLE "public"."tw112_warehousing_result_unknown" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw112_warehousing_result_unknown_PKI" ON "public"."tw112_warehousing_result_unknown" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehousing_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "sub_id" "pg_catalog"."int2_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."warehousing_plan_id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."sub_id" IS '不明品枝番';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."product_cnt" IS '入庫実績数';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."product_size_cd" IS '商品サイズコード';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."location" IS '位置';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."hold_reason" IS '保留理由';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."memo" IS '倉庫メモ';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."item_img" IS '画像パス';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw112_warehousing_result_unknown"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw112_warehousing_result_unknown" IS '入庫実績不明品テーブル:入庫実績不明品';

CREATE TABLE "public"."tw113_warehousing_location_detail"
(
    "client_id"           varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehousing_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"          varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "location_id"         varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_cnt"         int4,
    "ins_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"            timestamp(6),
    "upd_usr"             varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"            timestamp(6),
    "del_flg"             int2,
    "lot_no"              varchar(30) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "bestbefore_date"     date,
    "shipping_flag"       int4                                       DEFAULT 0,
    CONSTRAINT "tw113_warehousing_location_detail_PKC" PRIMARY KEY ("client_id", "warehousing_plan_id", "product_id", "location_id")
);
ALTER TABLE "public"."tw113_warehousing_location_detail" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw113_warehousing_location_detail_PKI" ON "public"."tw113_warehousing_location_detail" USING btree (
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehousing_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "location_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."warehousing_plan_id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."location_id" IS 'ロケーションID';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."product_cnt" IS '入庫実績数';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."lot_no" IS 'ロット番号';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."bestbefore_date" IS '賞味期限/在庫保管期限';
COMMENT
ON COLUMN "public"."tw113_warehousing_location_detail"."shipping_flag" IS '出荷フラグ';
COMMENT
ON TABLE "public"."tw113_warehousing_location_detail" IS '入庫作業ロケ明細';

CREATE TABLE "public"."tw200_shipment"
(
    "shipment_plan_id"           varchar(20) COLLATE "pg_catalog"."default"  NOT NULL,
    "client_id"                  varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "warehouse_cd"               varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "request_date"               timestamp(6),
    "shipment_plan_date"         timestamp(6),
    "delivery_plan_date"         timestamp(6),
    "form"                       int4,
    "postcode"                   varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "surname"                    varchar(100) COLLATE "pg_catalog"."default",
    "phone"                      varchar(20) COLLATE "pg_catalog"."default",
    "prefecture"                 varchar(10) COLLATE "pg_catalog"."default"  NOT NULL,
    "address1"                   varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "address2"                   varchar(100) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "company"                    varchar(50) COLLATE "pg_catalog"."default",
    "division"                   varchar(30) COLLATE "pg_catalog"."default",
    "email"                      varchar(100) COLLATE "pg_catalog"."default",
    "shipment_status"            int4                                        DEFAULT 2,
    "status_message"             varchar(120) COLLATE "pg_catalog"."default",
    "inspection_type"            varchar(10) COLLATE "pg_catalog"."default",
    "identifier"                 varchar(30) COLLATE "pg_catalog"."default",
    "delivery_tracking_nm"       text COLLATE "pg_catalog"."default",
    "order_no"                   varchar(100) COLLATE "pg_catalog"."default",
    "product_kind_plan_cnt"      int4,
    "product_plan_total"         int4,
    "total_price"                numeric(20, 0),
    "subtotal_amount"            varchar(30) COLLATE "pg_catalog"."default",
    "delivery_charge"            int4,
    "handling_charge"            int4,
    "discount_amount"            int4,
    "total_amount"               numeric(30, 0),
    "cushioning_unit"            varchar(1) COLLATE "pg_catalog"."default",
    "gift_wrapping_unit"         varchar(1) COLLATE "pg_catalog"."default",
    "gift_sender_name"           varchar(30) COLLATE "pg_catalog"."default",
    "bundled_items"              varchar(100) COLLATE "pg_catalog"."default",
    "shipping_email"             varchar(100) COLLATE "pg_catalog"."default",
    "delivery_note_type"         varchar(10) COLLATE "pg_catalog"."default",
    "price_on_delivery_note"     int4,
    "message"                    text COLLATE "pg_catalog"."default",
    "sponsor_id"                 varchar(10) COLLATE "pg_catalog"."default",
    "label_note"                 varchar(50) COLLATE "pg_catalog"."default",
    "shipping_date"              timestamp(6),
    "tax"                        numeric(20, 0),
    "total_with_normal_tax"      int4,
    "total_with_reduced_tax"     int4,
    "delivery_carrier"           varchar(10) COLLATE "pg_catalog"."default",
    "delivery_time_slot"         varchar(10) COLLATE "pg_catalog"."default",
    "delivery_date"              timestamp(6),
    "cash_on_delivery"           int4                                        DEFAULT 0,
    "total_for_cash_on_delivery" int4,
    "tax_for_cash_on_delivery"   int4,
    "delivery_method"            varchar(30) COLLATE "pg_catalog"."default",
    "box_delivery"               int4,
    "fragile_item"               int4,
    "invoice_special_notes"      varchar(150) COLLATE "pg_catalog"."default",
    "instructions_special_notes" varchar(150) COLLATE "pg_catalog"."default",
    "international"              int4                                        DEFAULT 0,
    "delivery_service"           varchar(20) COLLATE "pg_catalog"."default",
    "currency_code"              varchar(20) COLLATE "pg_catalog"."default",
    "size_cd"                    varchar(10) COLLATE "pg_catalog"."default",
    "insurance"                  int4                                        DEFAULT 0,
    "pdf_name"                   varchar(200) COLLATE "pg_catalog"."default",
    "pdf_confirm_img"            varchar(200) COLLATE "pg_catalog"."default",
    "ins_usr"                    varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"                   timestamp(6),
    "upd_usr"                    varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"                   timestamp(6),
    "del_flg"                    int4                                        DEFAULT 0,
    "bikou2"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou4"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou5"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou6"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou7"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou8"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou9"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou10"                    varchar(100) COLLATE "pg_catalog"."default",
    "bikou1"                     varchar(100) COLLATE "pg_catalog"."default",
    "bikou_flg"                  int4,
    "boxes"                      int4,
    "file"                       varchar(200) COLLATE "pg_catalog"."default",
    "finish_flg"                 int4                                        DEFAULT 0,
    "payment_method"             int4,
    "photography_flg"            int4                                        DEFAULT 0,
    "file2"                      varchar(255) COLLATE "pg_catalog"."default",
    "file3"                      varchar(255) COLLATE "pg_catalog"."default",
    "file4"                      varchar(255) COLLATE "pg_catalog"."default",
    "file5"                      varchar(255) COLLATE "pg_catalog"."default",
    "bill_barcode"               varchar(50) COLLATE "pg_catalog"."default",
    "order_company"              varchar(50) COLLATE "pg_catalog"."default",
    "order_division"             varchar(30) COLLATE "pg_catalog"."default",
    "order_zip_code1"            varchar(10) COLLATE "pg_catalog"."default",
    "order_zip_code2"            varchar(10) COLLATE "pg_catalog"."default",
    "order_todoufuken"           varchar(10) COLLATE "pg_catalog"."default",
    "order_address1"             varchar(100) COLLATE "pg_catalog"."default",
    "order_address2"             varchar(100) COLLATE "pg_catalog"."default",
    "order_family_name"          varchar(50) COLLATE "pg_catalog"."default",
    "order_first_name"           varchar(50) COLLATE "pg_catalog"."default",
    "order_family_kana"          varchar(50) COLLATE "pg_catalog"."default",
    "order_first_kana"           varchar(50) COLLATE "pg_catalog"."default",
    "order_phone_number1"        varchar(20) COLLATE "pg_catalog"."default",
    "order_phone_number2"        varchar(20) COLLATE "pg_catalog"."default",
    "order_phone_number3"        varchar(20) COLLATE "pg_catalog"."default",
    "order_mail"                 varchar(100) COLLATE "pg_catalog"."default",
    "order_gender"               int2,
    "payment_id"                 varchar(50) COLLATE "pg_catalog"."default",
    "order_datetime"             timestamp(6),
    "buy_id"                     varchar(30) COLLATE "pg_catalog"."default",
    "buy_cnt"                    int4,
    "next_delivery_date"         date,
    "memo"                       text COLLATE "pg_catalog"."default",
    "delivery_url"               varchar(200) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "receipt_url"                varchar(200) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    "surname_kana"               varchar(100) COLLATE "pg_catalog"."default" DEFAULT NULL:: character varying,
    "related_order_no"           text COLLATE "pg_catalog"."default",
    "freight"                    int4,
    "packing"                    int4,
    CONSTRAINT "tw200_shipment_PKC" PRIMARY KEY ("shipment_plan_id", "client_id", "warehouse_cd")
);
ALTER TABLE "public"."tw200_shipment" OWNER TO "prologi";
CREATE INDEX "tw200_shipment_pki" ON "public"."tw200_shipment" USING btree (
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_status" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw200_shipment"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tw200_shipment"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw200_shipment"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw200_shipment"."request_date" IS '出庫依頼日';
COMMENT
ON COLUMN "public"."tw200_shipment"."shipment_plan_date" IS '出庫予定日';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_plan_date" IS '到着予定日';
COMMENT
ON COLUMN "public"."tw200_shipment"."form" IS '配送先形態:1:会社 ， 2: 个人';
COMMENT
ON COLUMN "public"."tw200_shipment"."postcode" IS '郵便番号';
COMMENT
ON COLUMN "public"."tw200_shipment"."surname" IS '氏名';
COMMENT
ON COLUMN "public"."tw200_shipment"."phone" IS '電話番号';
COMMENT
ON COLUMN "public"."tw200_shipment"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."tw200_shipment"."address1" IS '住所';
COMMENT
ON COLUMN "public"."tw200_shipment"."address2" IS 'マンション・ビル名';
COMMENT
ON COLUMN "public"."tw200_shipment"."company" IS '会社名';
COMMENT
ON COLUMN "public"."tw200_shipment"."division" IS '部署';
COMMENT
ON COLUMN "public"."tw200_shipment"."email" IS 'メールアドレス';
COMMENT
ON COLUMN "public"."tw200_shipment"."shipment_status" IS '1.出庫ステータス:1: 確認待ち 11: 出荷承認失敗 2: 入庫待ち 3:出荷待ち 4: 出荷作業中 41: 出庫承認待ち 42: 出庫承認済み 5:検品中 6: 出庫保留中 7:検品済み 8:出荷済み 9:入金待ち 90:返品処理';
COMMENT
ON COLUMN "public"."tw200_shipment"."status_message" IS 'ステータス理由';
COMMENT
ON COLUMN "public"."tw200_shipment"."inspection_type" IS '検品タイプ';
COMMENT
ON COLUMN "public"."tw200_shipment"."identifier" IS '出庫識別番号';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_tracking_nm" IS '伝票番号';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_no" IS '注文番号';
COMMENT
ON COLUMN "public"."tw200_shipment"."product_kind_plan_cnt" IS '出庫依頼商品種類数';
COMMENT
ON COLUMN "public"."tw200_shipment"."product_plan_total" IS '出庫依頼商品数計';
COMMENT
ON COLUMN "public"."tw200_shipment"."total_price" IS '商品合計金額';
COMMENT
ON COLUMN "public"."tw200_shipment"."subtotal_amount" IS '納品書 小計';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_charge" IS '納品書 配送料';
COMMENT
ON COLUMN "public"."tw200_shipment"."handling_charge" IS '納品書 手数料';
COMMENT
ON COLUMN "public"."tw200_shipment"."discount_amount" IS '納品書 割引額';
COMMENT
ON COLUMN "public"."tw200_shipment"."total_amount" IS '納品書 合計';
COMMENT
ON COLUMN "public"."tw200_shipment"."cushioning_unit" IS '緩衝材単位:0: なし 1:注文単位   2:商品単位';
COMMENT
ON COLUMN "public"."tw200_shipment"."gift_wrapping_unit" IS 'ギフトラッピング単位:0: なし 1:注文単位   2:商品単位';
COMMENT
ON COLUMN "public"."tw200_shipment"."gift_sender_name" IS 'ギフト贈り主氏名';
COMMENT
ON COLUMN "public"."tw200_shipment"."bundled_items" IS '同梱物の商品ID or 商品コード';
COMMENT
ON COLUMN "public"."tw200_shipment"."shipping_email" IS '配送先連絡メール';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_note_type" IS '明細書の同梱設定';
COMMENT
ON COLUMN "public"."tw200_shipment"."price_on_delivery_note" IS '明細書への金額印字指定';
COMMENT
ON COLUMN "public"."tw200_shipment"."message" IS '明細書メッセージ';
COMMENT
ON COLUMN "public"."tw200_shipment"."sponsor_id" IS 'ご依頼主ID';
COMMENT
ON COLUMN "public"."tw200_shipment"."label_note" IS '品名';
COMMENT
ON COLUMN "public"."tw200_shipment"."shipping_date" IS '出荷希望日';
COMMENT
ON COLUMN "public"."tw200_shipment"."tax" IS '税';
COMMENT
ON COLUMN "public"."tw200_shipment"."total_with_normal_tax" IS '総計通常税率';
COMMENT
ON COLUMN "public"."tw200_shipment"."total_with_reduced_tax" IS '総計軽減税率';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_carrier" IS '配送会社';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_time_slot" IS '配達希望時間帯';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_date" IS '配達希望日';
COMMENT
ON COLUMN "public"."tw200_shipment"."cash_on_delivery" IS '代金引換指定';
COMMENT
ON COLUMN "public"."tw200_shipment"."total_for_cash_on_delivery" IS '代金引換総計';
COMMENT
ON COLUMN "public"."tw200_shipment"."tax_for_cash_on_delivery" IS '代金引換消費税';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_method" IS '配送便指定';
COMMENT
ON COLUMN "public"."tw200_shipment"."box_delivery" IS '不在時宅配ボックス';
COMMENT
ON COLUMN "public"."tw200_shipment"."fragile_item" IS '割れ物注意';
COMMENT
ON COLUMN "public"."tw200_shipment"."invoice_special_notes" IS '送り状特記事項';
COMMENT
ON COLUMN "public"."tw200_shipment"."instructions_special_notes" IS '出荷指示書特記事項';
COMMENT
ON COLUMN "public"."tw200_shipment"."international" IS '海外発送指定:0:無 1:有';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_service" IS '海外発送用配送サービス';
COMMENT
ON COLUMN "public"."tw200_shipment"."currency_code" IS '海外発送用通貨コード';
COMMENT
ON COLUMN "public"."tw200_shipment"."size_cd" IS 'サイズＣＤ';
COMMENT
ON COLUMN "public"."tw200_shipment"."insurance" IS '海外発送用損害保証制度の加入希望:0:無 1:有';
COMMENT
ON COLUMN "public"."tw200_shipment"."pdf_name" IS 'PDF名';
COMMENT
ON COLUMN "public"."tw200_shipment"."pdf_confirm_img" IS 'PDF確認画像';
COMMENT
ON COLUMN "public"."tw200_shipment"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw200_shipment"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw200_shipment"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw200_shipment"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw200_shipment"."del_flg" IS '削除フラグ:0：利用中　1:削除済';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou4" IS '備考4';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou5" IS '備考5';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou6" IS '備考6';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou7" IS '備考7';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou8" IS '備考8';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou9" IS '備考9';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou10" IS '備考10';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."tw200_shipment"."bikou_flg" IS '備考内容显示flg';
COMMENT
ON COLUMN "public"."tw200_shipment"."boxes" IS '個口数';
COMMENT
ON COLUMN "public"."tw200_shipment"."file" IS '添付ファイル';
COMMENT
ON COLUMN "public"."tw200_shipment"."finish_flg" IS 'API連携フラグ　0:未連携 1：連携済み';
COMMENT
ON COLUMN "public"."tw200_shipment"."payment_method" IS '支払方法';
COMMENT
ON COLUMN "public"."tw200_shipment"."photography_flg" IS '撮影確認  0：未確認 1：確認';
COMMENT
ON COLUMN "public"."tw200_shipment"."bill_barcode" IS '請求コード';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_company" IS '注文者会社名';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_division" IS '注文者部署';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_zip_code1" IS '注文者郵便番号1';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_zip_code2" IS '注文者郵便番号2';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_todoufuken" IS '注文者住所都道府県';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_address1" IS '注文者住所郡市区';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_address2" IS '注文者詳細住所';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_family_name" IS '注文者姓';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_first_name" IS '注文者名';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_family_kana" IS '注文者姓カナ';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_first_kana" IS '注文者名カナ';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_phone_number1" IS '注文者電話番号1';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_phone_number2" IS '注文者電話番号2';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_phone_number3" IS '注文者電話番号3';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_mail" IS '注文者メールアドレス';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_gender" IS '注文者性別';
COMMENT
ON COLUMN "public"."tw200_shipment"."payment_id" IS '取引ID(GMO)';
COMMENT
ON COLUMN "public"."tw200_shipment"."order_datetime" IS '受注日時';
COMMENT
ON COLUMN "public"."tw200_shipment"."buy_id" IS '定期購入ID';
COMMENT
ON COLUMN "public"."tw200_shipment"."buy_cnt" IS '定期購入回数';
COMMENT
ON COLUMN "public"."tw200_shipment"."next_delivery_date" IS '次回お届け予定日';
COMMENT
ON COLUMN "public"."tw200_shipment"."memo" IS '購入者備考欄';
COMMENT
ON COLUMN "public"."tw200_shipment"."delivery_url" IS '納品書URL';
COMMENT
ON COLUMN "public"."tw200_shipment"."receipt_url" IS '領収書URL';
COMMENT
ON COLUMN "public"."tw200_shipment"."surname_kana" IS '配送先名前フリガナ';
COMMENT
ON COLUMN "public"."tw200_shipment"."related_order_no" IS 'Qoo10関連注文番号';
COMMENT
ON COLUMN "public"."tw200_shipment"."freight" IS 'NTM-运费';
COMMENT
ON COLUMN "public"."tw200_shipment"."packing" IS 'NTM-捆包费';
COMMENT
ON TABLE "public"."tw200_shipment" IS '出荷依頼管理テーブル';

CREATE TABLE "public"."tw201_shipment_detail"
(
    "id"                 int4                                       NOT NULL,
    "warehouse_cd"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"          varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id"   varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"         varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "is_reduced_tax"     int4                                                DEFAULT 0,
    "cushioning_type"    varchar(50) COLLATE "pg_catalog"."default",
    "gift_wrapping_type" varchar(200) COLLATE "pg_catalog"."default",
    "product_plan_cnt"   int4,
    "unit_price"         int4,
    "price"              numeric(20, 0),
    "set_sub_id"         int4,
    "set_cnt"            int4,
    "ins_usr"            varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"           timestamp(6),
    "upd_usr"            varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"           timestamp(6),
    "del_flg"            int4                                                DEFAULT 0,
    "gift_wrapping_note" text COLLATE "pg_catalog"."default",
    "tax_flag"           int4,
    "reserve_status"     int4,
    "reserve_cnt"        int4                                                DEFAULT 0,
    "product_sub_id"     int4                                       NOT NULL DEFAULT 0,
    "barcode"            varchar(20) COLLATE "pg_catalog"."default",
    "name"               varchar(300) COLLATE "pg_catalog"."default",
    "code"               varchar(100) COLLATE "pg_catalog"."default",
    "options"            text COLLATE "pg_catalog"."default",
    "kubun"              int4                                                DEFAULT 0,
    "option_price"       int4,
    "serial_no"          text COLLATE "pg_catalog"."default",
    CONSTRAINT "tw201_shipment_detail_pkey" PRIMARY KEY ("id")
);
ALTER TABLE "public"."tw201_shipment_detail" OWNER TO "prologi";
CREATE INDEX "tw201_shipment_detail_PKI" ON "public"."tw201_shipment_detail" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "tw201_shipment_detail_pkc" ON "public"."tw201_shipment_detail" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
CREATE UNIQUE INDEX "tw201_shipment_detail_pki" ON "public"."tw201_shipment_detail" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."id" IS '管理ID';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."is_reduced_tax" IS '軽減税率適用商品:0: 無 1:有';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."cushioning_type" IS '緩衝材種別';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."gift_wrapping_type" IS 'ギフトラッピングタイプ';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."product_plan_cnt" IS '出庫依頼数';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."unit_price" IS '商品単価';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."price" IS '商品総額';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."set_sub_id" IS 'セット商品ID';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."set_cnt" IS 'セット数';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."del_flg" IS '削除フラグ:0:利用中 1:削除済';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."gift_wrapping_note" IS 'ラッピング備考';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."tax_flag" IS '税率';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."reserve_status" IS '引当ステータス';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."reserve_cnt" IS '引当数';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."product_sub_id" IS 'マクロ明細区分';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."barcode" IS '商品管理コード';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."name" IS '商品名';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."code" IS '商品コード';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."options" IS '商品オプション';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."kubun" IS '商品区分:(0:通常商品、1:同梱物、2:セット商品、9:仮登録)';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."option_price" IS 'オプション金額';
COMMENT
ON COLUMN "public"."tw201_shipment_detail"."serial_no" IS 'シリアル番号';
COMMENT
ON TABLE "public"."tw201_shipment_detail" IS '出庫明細テーブル';

CREATE TABLE "public"."tw210_shipment_result"
(
    "work_id"         int4                                       NOT NULL,
    "warehouse_cd"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "work_name"       varchar(50) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_status" varchar(1) COLLATE "pg_catalog"."default"  NOT NULL,
    "total_cnt"       int4,
    "yobi"            varchar(100) COLLATE "pg_catalog"."default",
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int2,
    CONSTRAINT "tw210_shipment_result_PKC" PRIMARY KEY ("work_id", "warehouse_cd")
);
ALTER TABLE "public"."tw210_shipment_result" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw210_shipment_result_PKI" ON "public"."tw210_shipment_result" USING btree (
    "work_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw210_shipment_result"."work_id" IS '作業管理ID';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."work_name" IS '作業管理';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."shipment_status" IS '処理ステータス: 1:作業中　2:完了';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."total_cnt" IS '作業件数';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."yobi" IS '備考';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw210_shipment_result"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw210_shipment_result" IS '出庫作業管理テーブル:出庫実績管理';

CREATE TABLE "public"."tw211_shipment_result_detail"
(
    "work_id"          int4                                       NOT NULL,
    "warehouse_cd"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "size_cd"          varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"         timestamp(6),
    "upd_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"         timestamp(6),
    "del_flg"          int2,
    CONSTRAINT "tw211_shipment_result_detail_PKC" PRIMARY KEY ("work_id", "warehouse_cd", "client_id", "shipment_plan_id")
);
ALTER TABLE "public"."tw211_shipment_result_detail" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw211_shipment_result_detail_PKI" ON "public"."tw211_shipment_result_detail" USING btree (
    "work_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."work_id" IS '作業管理ID';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."size_cd" IS 'サイズ';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw211_shipment_result_detail"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw211_shipment_result_detail" IS '出庫作業明細:出庫依頼SKU別';

CREATE TABLE "public"."tw212_shipment_location_detail"
(
    "id"                   int4                                       NOT NULL,
    "warehouse_cd"         varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"            varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id"     varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"           varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "location_id"          varchar(10) COLLATE "pg_catalog"."default",
    "status"               int4,
    "product_plan_cnt"     int4,
    "inventory_cnt"        int4,
    "reserve_cnt"          int4,
    "ins_usr"              varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"             timestamp(6),
    "upd_usr"              varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"             timestamp(6),
    "del_flg"              int2,
    "total_picking_status" int4                                       DEFAULT 0,
    "lot_no"               varchar(30) COLLATE "pg_catalog"."default" DEFAULT '':: character varying,
    CONSTRAINT "tw212_shipment_location_detail_PKC" PRIMARY KEY ("id", "warehouse_cd", "client_id", "shipment_plan_id", "product_id")
);
ALTER TABLE "public"."tw212_shipment_location_detail" OWNER TO "prologi";
CREATE INDEX "tw212_shipment_location_detail_PKI" ON "public"."tw212_shipment_location_detail" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "location_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int2_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."id" IS 'ID';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."location_id" IS 'ロケーションID';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."status" IS 'ステータス';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."product_plan_cnt" IS '出庫依頼数';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."inventory_cnt" IS '実在庫数';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."reserve_cnt" IS '引当数';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."total_picking_status" IS 'ピッキング確認  0：未確認 1：確認';
COMMENT
ON COLUMN "public"."tw212_shipment_location_detail"."lot_no" IS 'ロット番号';
COMMENT
ON TABLE "public"."tw212_shipment_location_detail" IS '出庫作業ロケ明細';

CREATE TABLE "public"."tw213_shipment_location_detail_history"
(
    "id"               int4                                       NOT NULL,
    "warehouse_cd"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "location_id"      varchar(10) COLLATE "pg_catalog"."default",
    "status"           varchar(10) COLLATE "pg_catalog"."default",
    "product_plan_cnt" int4,
    "inventory_cnt"    int4,
    "reserve_cnt"      int4,
    "biko"             varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"         timestamp(6),
    "upd_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"         timestamp(6),
    "del_flg"          int2,
    CONSTRAINT "tw213_shipment_location_detail_history_PKC" PRIMARY KEY ("id", "warehouse_cd", "client_id", "shipment_plan_id", "product_id")
);
ALTER TABLE "public"."tw213_shipment_location_detail_history" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw213_shipment_location_detail_history_PKI" ON "public"."tw213_shipment_location_detail_history" USING btree (
    "id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."id" IS 'ID';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."location_id" IS 'ロケーションID';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."status" IS 'ステータス';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."product_plan_cnt" IS '出庫依頼数';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."inventory_cnt" IS '実在庫数';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."reserve_cnt" IS '引当数';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."biko" IS '備考';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw213_shipment_location_detail_history"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw213_shipment_location_detail_history" IS '出庫作業ロケ明細履歴';

CREATE TABLE "public"."tw214_total_picking"
(
    "total_picking_id" int4                                       NOT NULL,
    "warehouse_cd"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "user_id"          varchar(10) COLLATE "pg_catalog"."default",
    "start_date"       timestamp(6),
    "end_date"         timestamp(6),
    "ins_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"         date,
    "upd_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"         date,
    "del_flg"          int4 DEFAULT 0,
    CONSTRAINT "tw214_total_picking_PKC" PRIMARY KEY ("total_picking_id", "warehouse_cd")
);
ALTER TABLE "public"."tw214_total_picking" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw214_total_picking_PKI" ON "public"."tw214_total_picking" USING btree (
    "total_picking_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw214_total_picking"."total_picking_id" IS 'トータルピッキングID';
COMMENT
ON COLUMN "public"."tw214_total_picking"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw214_total_picking"."user_id" IS '作業者';
COMMENT
ON COLUMN "public"."tw214_total_picking"."start_date" IS '開始時間';
COMMENT
ON COLUMN "public"."tw214_total_picking"."end_date" IS '終了時間';
COMMENT
ON COLUMN "public"."tw214_total_picking"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw214_total_picking"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw214_total_picking"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw214_total_picking"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw214_total_picking"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw214_total_picking" IS 'トータルピッキング';

CREATE TABLE "public"."tw215_total_picking_detail"
(
    "total_picking_id" int4                                       NOT NULL,
    "warehouse_cd"     varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "shipment_plan_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_plan_cnt" int4,
    "ins_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"         date,
    "upd_usr"          varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"         date,
    "del_flg"          int4 DEFAULT 0,
    CONSTRAINT "tw215_total_picking_detail_PKC" PRIMARY KEY ("total_picking_id", "warehouse_cd", "client_id",
                                                             "shipment_plan_id", "product_id")
);
ALTER TABLE "public"."tw215_total_picking_detail" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw215_total_picking_detail_PKI" ON "public"."tw215_total_picking_detail" USING btree (
    "total_picking_id" "pg_catalog"."int4_ops" ASC NULLS LAST,
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "shipment_plan_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."total_picking_id" IS 'トータルピッキングID';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."shipment_plan_id" IS '出庫依頼ID';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."product_id" IS '商品Id';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."product_plan_cnt" IS '商品个数';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw215_total_picking_detail"."del_flg" IS '削除フラグ';
COMMENT
ON TABLE "public"."tw215_total_picking_detail" IS 'トータルピッキング詳細';

CREATE TABLE "public"."tw216_delivery_fare"
(
    "fare_id"        int4                                        NOT NULL,
    "agent_id"       varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "method"         varchar(100) COLLATE "pg_catalog"."default" NOT NULL,
    "region"         varchar(32) COLLATE "pg_catalog"."default"  NOT NULL,
    "actual_price"   int4                                        NOT NULL DEFAULT 0,
    "original_price" int4                                        NOT NULL DEFAULT 0,
    "ins_usr"        varchar(10) COLLATE "pg_catalog"."default",
    "ins_date"       timestamp(6),
    "upd_usr"        varchar(10) COLLATE "pg_catalog"."default",
    "upd_date"       timestamp(6),
    "del_flg"        int4                                        NOT NULL DEFAULT 0,
    CONSTRAINT "tw216_delivery_fare_pkc" PRIMARY KEY ("fare_id")
);
ALTER TABLE "public"."tw216_delivery_fare" OWNER TO "prologi";
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."fare_id" IS '管理ID';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."agent_id" IS '配送業者';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."method" IS '配送方法';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."region" IS '都道府県';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."actual_price" IS '価格';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."original_price" IS '原価';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw216_delivery_fare"."del_flg" IS '削除フラグ:0:利用中 1:削除済';
COMMENT
ON TABLE "public"."tw216_delivery_fare" IS '配送運賃';

CREATE TABLE "public"."tw300_stock"
(
    "stock_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"       varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "available_cnt"   int4                                       NOT NULL,
    "requesting_cnt"  int4                                       NOT NULL,
    "inventory_cnt"   int4                                       NOT NULL,
    "replenish_cnt"   int4,
    "product_size_cd" varchar(10) COLLATE "pg_catalog"."default",
    "product_weight"  float4,
    "weigth_unit"     varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int2,
    "store_cnt"       int4 DEFAULT 0,
    "info"            varchar(100) COLLATE "pg_catalog"."default",
    "bikou1"          varchar(100) COLLATE "pg_catalog"."default",
    "bikou2"          varchar(100) COLLATE "pg_catalog"."default",
    "bikou3"          varchar(100) COLLATE "pg_catalog"."default",
    "not_delivery"    int4 DEFAULT 0,
    CONSTRAINT "tw300_stock_PKC" PRIMARY KEY ("stock_id")
);
ALTER TABLE "public"."tw300_stock" OWNER TO "prologi";
CREATE INDEX "tw300_stock_PKI" ON "public"."tw300_stock" USING btree (
    "warehouse_cd" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "client_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "product_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "del_flg" "pg_catalog"."int2_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw300_stock"."stock_id" IS '在庫ID';
COMMENT
ON COLUMN "public"."tw300_stock"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw300_stock"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw300_stock"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw300_stock"."available_cnt" IS '理論在庫数';
COMMENT
ON COLUMN "public"."tw300_stock"."requesting_cnt" IS '出庫依頼中数';
COMMENT
ON COLUMN "public"."tw300_stock"."inventory_cnt" IS '実在庫数';
COMMENT
ON COLUMN "public"."tw300_stock"."replenish_cnt" IS '補充設定在庫数';
COMMENT
ON COLUMN "public"."tw300_stock"."product_size_cd" IS '商品サイズコード';
COMMENT
ON COLUMN "public"."tw300_stock"."product_weight" IS '商品重量';
COMMENT
ON COLUMN "public"."tw300_stock"."weigth_unit" IS '重量単位';
COMMENT
ON COLUMN "public"."tw300_stock"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw300_stock"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw300_stock"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw300_stock"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw300_stock"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tw300_stock"."store_cnt" IS '店舗側在庫数';
COMMENT
ON COLUMN "public"."tw300_stock"."info" IS 'コメント';
COMMENT
ON COLUMN "public"."tw300_stock"."bikou1" IS '備考1';
COMMENT
ON COLUMN "public"."tw300_stock"."bikou2" IS '備考2';
COMMENT
ON COLUMN "public"."tw300_stock"."bikou3" IS '備考3';
COMMENT
ON COLUMN "public"."tw300_stock"."not_delivery" IS '不可配送数';
COMMENT
ON TABLE "public"."tw300_stock" IS '在庫管理テーブル:SKU別商品在庫数管理';

CREATE TABLE "public"."tw301_stock_history"
(
    "history_id" varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "plan_id"    varchar(20) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_id" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "type"       int4                                       NOT NULL,
    "quantity"   int4,
    "ins_user"   varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"   timestamp(6),
    "upd_user"   varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"   timestamp(6),
    "del_flg"    int4 DEFAULT 0,
    "before_num" int4 DEFAULT 0,
    "after_num"  int4 DEFAULT 0,
    "info"       varchar(100) COLLATE "pg_catalog"."default",
    CONSTRAINT "tw301_stock_history_PKC" PRIMARY KEY ("history_id")
);
ALTER TABLE "public"."tw301_stock_history" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw301_stock_history_PKI" ON "public"."tw301_stock_history" USING btree (
    "history_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw301_stock_history"."history_id" IS '在庫履歴ID';
COMMENT
ON COLUMN "public"."tw301_stock_history"."plan_id" IS '依頼ID';
COMMENT
ON COLUMN "public"."tw301_stock_history"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw301_stock_history"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw301_stock_history"."type" IS 'タイプ:1:入庫 2:出庫 11:入庫調整 22:出庫調整';
COMMENT
ON COLUMN "public"."tw301_stock_history"."quantity" IS '数量';
COMMENT
ON COLUMN "public"."tw301_stock_history"."ins_user" IS '作成者';
COMMENT
ON COLUMN "public"."tw301_stock_history"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw301_stock_history"."upd_user" IS '更新者';
COMMENT
ON COLUMN "public"."tw301_stock_history"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw301_stock_history"."del_flg" IS '削除フラグ';
COMMENT
ON COLUMN "public"."tw301_stock_history"."before_num" IS '変更前アイテム数';
COMMENT
ON COLUMN "public"."tw301_stock_history"."after_num" IS '備考';
COMMENT
ON TABLE "public"."tw301_stock_history" IS '在庫履歴テーブル';

CREATE TABLE "public"."tw302_stock_management"
(
    "manage_id"    int4                                       NOT NULL,
    "warehouse_cd" varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "client_id"    varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "product_cnt"  int4,
    "state"        int4,
    "check_date"   date,
    "user_id"      varchar COLLATE "pg_catalog"."default",
    "start_date"   date,
    "end_date"     date,
    "update_date"  date,
    CONSTRAINT "tw302_stock_management_PKC" PRIMARY KEY ("manage_id")
);
ALTER TABLE "public"."tw302_stock_management" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw302_stock_management_PKI" ON "public"."tw302_stock_management" USING btree (
    "manage_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw302_stock_management"."manage_id" IS '管理ID';
COMMENT
ON COLUMN "public"."tw302_stock_management"."warehouse_cd" IS '倉庫CD';
COMMENT
ON COLUMN "public"."tw302_stock_management"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw302_stock_management"."product_cnt" IS '商品点数';
COMMENT
ON COLUMN "public"."tw302_stock_management"."state" IS 'ステータス';
COMMENT
ON COLUMN "public"."tw302_stock_management"."check_date" IS '棚卸し年月';
COMMENT
ON COLUMN "public"."tw302_stock_management"."user_id" IS '作業者ID';
COMMENT
ON COLUMN "public"."tw302_stock_management"."start_date" IS '作業開始日';
COMMENT
ON COLUMN "public"."tw302_stock_management"."end_date" IS '作業終了日';
COMMENT
ON COLUMN "public"."tw302_stock_management"."update_date" IS '更新日';
COMMENT
ON TABLE "public"."tw302_stock_management" IS '棚卸し在庫管理表:棚卸し在庫管理表';

CREATE TABLE "public"."tw303_stock_detail"
(
    "detail_id"   int4                                       NOT NULL,
    "manage_id"   int4                                       NOT NULL,
    "product_id"  varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "stock_count" int4,
    "count"       int4,
    "update_date" date,
    "user_id"     varchar(10) COLLATE "pg_catalog"."default",
    CONSTRAINT "tw303_stock_detail_PKC" PRIMARY KEY ("detail_id")
);
ALTER TABLE "public"."tw303_stock_detail" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw303_stock_detail_PKI" ON "public"."tw303_stock_detail" USING btree (
    "detail_id" "pg_catalog"."int4_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw303_stock_detail"."detail_id" IS '明細管理ID';
COMMENT
ON COLUMN "public"."tw303_stock_detail"."manage_id" IS '管理ID';
COMMENT
ON COLUMN "public"."tw303_stock_detail"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw303_stock_detail"."stock_count" IS '在庫数（理論在庫数）';
COMMENT
ON COLUMN "public"."tw303_stock_detail"."count" IS '実在庫数';
COMMENT
ON COLUMN "public"."tw303_stock_detail"."update_date" IS '更新日';
COMMENT
ON COLUMN "public"."tw303_stock_detail"."user_id" IS '更新者';
COMMENT
ON TABLE "public"."tw303_stock_detail" IS '棚卸し在庫明細表:棚卸し在庫明細表';

CREATE TABLE "public"."tw304_stock_day"
(
    "stock_date"      varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "stock_id"        varchar(10) COLLATE "pg_catalog"."default" NOT NULL,
    "warehouse_cd"    varchar(10) COLLATE "pg_catalog"."default",
    "client_id"       varchar(10) COLLATE "pg_catalog"."default",
    "product_id"      varchar(10) COLLATE "pg_catalog"."default",
    "available_cnt"   int4,
    "requesting_cnt"  int4,
    "inventory_cnt"   int4,
    "replenish_cnt"   int4,
    "product_size_cd" varchar(10) COLLATE "pg_catalog"."default",
    "product_weight"  float4,
    "weigth_unit"     varchar(10) COLLATE "pg_catalog"."default",
    "ins_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "ins_date"        timestamp(6),
    "upd_usr"         varchar(100) COLLATE "pg_catalog"."default",
    "upd_date"        timestamp(6),
    "del_flg"         int4,
    CONSTRAINT "tw304_stock_day_PKC" PRIMARY KEY ("stock_date", "stock_id")
);
ALTER TABLE "public"."tw304_stock_day" OWNER TO "prologi";
CREATE UNIQUE INDEX "tw304_stock_day_PKI" ON "public"."tw304_stock_day" USING btree (
    "stock_date" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST,
    "stock_id" COLLATE "pg_catalog"."default" "pg_catalog"."text_ops" ASC NULLS LAST
    );
COMMENT
ON COLUMN "public"."tw304_stock_day"."stock_date" IS '在庫処理日';
COMMENT
ON COLUMN "public"."tw304_stock_day"."stock_id" IS '在庫ID';
COMMENT
ON COLUMN "public"."tw304_stock_day"."warehouse_cd" IS '倉庫コード';
COMMENT
ON COLUMN "public"."tw304_stock_day"."client_id" IS '店舗ID';
COMMENT
ON COLUMN "public"."tw304_stock_day"."product_id" IS '商品ID';
COMMENT
ON COLUMN "public"."tw304_stock_day"."available_cnt" IS '理論在庫数';
COMMENT
ON COLUMN "public"."tw304_stock_day"."requesting_cnt" IS '出庫依頼中数';
COMMENT
ON COLUMN "public"."tw304_stock_day"."inventory_cnt" IS '実在庫数';
COMMENT
ON COLUMN "public"."tw304_stock_day"."replenish_cnt" IS '補充設定在庫数';
COMMENT
ON COLUMN "public"."tw304_stock_day"."product_size_cd" IS '商品サイズコード';
COMMENT
ON COLUMN "public"."tw304_stock_day"."product_weight" IS '商品重量';
COMMENT
ON COLUMN "public"."tw304_stock_day"."weigth_unit" IS '重量単位';
COMMENT
ON COLUMN "public"."tw304_stock_day"."ins_usr" IS '作成者';
COMMENT
ON COLUMN "public"."tw304_stock_day"."ins_date" IS '作成日時';
COMMENT
ON COLUMN "public"."tw304_stock_day"."upd_usr" IS '更新者';
COMMENT
ON COLUMN "public"."tw304_stock_day"."upd_date" IS '更新日時';
COMMENT
ON COLUMN "public"."tw304_stock_day"."del_flg" IS '削除フラグ';

CREATE VIEW "public"."product_stock_view" AS
SELECT mc100.warehouse_cd,
       (SELECT mw400_warehouse.warehouse_nm
        FROM mw400_warehouse
        WHERE mw400_warehouse.warehouse_cd::text = mc100.warehouse_cd::text) AS warehouse_nm,
    mc100.client_id,
    ( SELECT ms201_client.client_nm
           FROM ms201_client
          WHERE ms201_client.client_id::text = mc100.client_id::text) AS client_nm,
    ( SELECT mw404_location.wh_location_nm
           FROM mw404_location
          WHERE mw404_location.location_id::text = mw405.location_id::text AND mw404_location.warehouse_cd::text = tw300.warehouse_cd::text) AS wh_location_nm,
    mc100.product_id,
    mc100.name,
    mc100.code,
    tw300.inventory_cnt,
    tw300.requesting_cnt,
    tw300.available_cnt
   FROM tw300_stock tw300
     LEFT JOIN mc100_product mc100 ON tw300.client_id::text = mc100.client_id::text AND tw300.warehouse_cd::text = mc100.warehouse_cd::text AND tw300.product_id::text = mc100.product_id::text AND mc100.del_flg = 0
     LEFT JOIN mw405_product_location mw405 ON mw405.client_id::text = tw300.client_id::text AND mw405.product_id::text = tw300.product_id::text AND mc100.del_flg = 0
  ORDER BY tw300.warehouse_cd, mc100.client_id, mw405.location_id, mc100.product_id;
ALTER TABLE "public"."product_stock_view" OWNER TO "prologi";

