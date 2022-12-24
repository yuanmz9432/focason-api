CREATE TABLE "public"."es001_yamato"
(
    "id"                    serial,
    "store_code"            varchar(255),
    "origin_client_code"    varchar(255),
    "billing_category_code" varchar(255),
    "management_code"       varchar(255),
    "memo"                  text,
    "created_by"            varchar(255),
    "created_at"            timestamp,
    "modified_by"           varchar(255),
    "modified_at"           timestamp,
    "is_deleted"            int2 DEFAULT 0,
    CONSTRAINT "pk_es001_yamato_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es001_yamato"."id" IS 'ヤマト連携情報ID';
COMMENT
ON COLUMN "public"."es001_yamato"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es001_yamato"."origin_client_code" IS '外部顧客コード';
COMMENT
ON COLUMN "public"."es001_yamato"."billing_category_code" IS 'ご請求先分類コード';
COMMENT
ON COLUMN "public"."es001_yamato"."management_code" IS '運賃管理番号';
COMMENT
ON COLUMN "public"."es001_yamato"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es001_yamato"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es001_yamato"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es001_yamato"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es001_yamato"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es001_yamato"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es001_yamato" IS 'ヤマト連携情報管理テーブル';

CREATE TABLE "public"."es002_sagawa"
(
    "id"                 serial,
    "store_code"         varchar(255),
    "origin_client_code" varchar(255),
    "packing_code"       varchar(255),
    "memo"               text,
    "created_by"         varchar(255),
    "created_at"         timestamp,
    "modified_by"        varchar(255),
    "modified_at"        timestamp,
    "is_deleted"         int2 DEFAULT 0,
    CONSTRAINT "pk_es002_sagewa_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es002_sagawa"."id" IS '佐川急便連携情報IID';
COMMENT
ON COLUMN "public"."es002_sagawa"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es002_sagawa"."origin_client_code" IS '外部顧客コード';
COMMENT
ON COLUMN "public"."es002_sagawa"."packing_code" IS '荷姿コード';
COMMENT
ON COLUMN "public"."es002_sagawa"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es002_sagawa"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es002_sagawa"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es002_sagawa"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es002_sagawa"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es002_sagawa"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es002_sagawa" IS '佐川急便連携情報管理テーブル';

CREATE TABLE "public"."es003_fukuyama"
(
    "id"             serial,
    "store_code"     varchar(255),
    "deliverer_code" varchar(255),
    "memo"           text,
    "created_by"     varchar(255),
    "created_at"     timestamp,
    "modified_by"    varchar(255),
    "modified_at"    timestamp,
    "is_deleted"     int2 DEFAULT 0,
    CONSTRAINT "pk_es003_fukuyama_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es003_fukuyama"."id" IS '福山運輸連携情報ID';
COMMENT
ON COLUMN "public"."es003_fukuyama"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es003_fukuyama"."deliverer_code" IS '荷送人コード';
COMMENT
ON COLUMN "public"."es003_fukuyama"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es003_fukuyama"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es003_fukuyama"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es003_fukuyama"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es003_fukuyama"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es003_fukuyama"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es003_fukuyama" IS '福山運輸連携情報管理テーブル';

CREATE TABLE "public"."es004_seino"
(
    "id"             serial,
    "store_code"     varchar(255),
    "deliverer_code" varchar(255),
    "memo"           text,
    "created_by"     varchar(255),
    "created_at"     timestamp,
    "modified_by"    varchar(255),
    "modified_at"    timestamp,
    "is_deleted"     int2 DEFAULT 0,
    CONSTRAINT "pk_es004_seino_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es004_seino"."id" IS '西濃運輸連携情報ID';
COMMENT
ON COLUMN "public"."es004_seino"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es004_seino"."deliverer_code" IS '荷送人コード';
COMMENT
ON COLUMN "public"."es004_seino"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es004_seino"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es004_seino"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es004_seino"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es004_seino"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es004_seino"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es004_seino" IS '西濃運輸連携情報管理テーブル';

CREATE TABLE "public"."es100_makeshop"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es100_makeshop_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es100_makeshop"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es100_makeshop"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es100_makeshop"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es100_makeshop"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es100_makeshop"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es100_makeshop"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es100_makeshop"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es100_makeshop" IS 'MakeShop連携情報管理テーブル';

CREATE TABLE "public"."es101_colorme"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es101_colorme_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es101_colorme"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es101_colorme"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es101_colorme"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es101_colorme"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es101_colorme"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es101_colorme"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es101_colorme"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es101_colorme" IS 'ColorMe連携情報管理テーブル';

CREATE TABLE "public"."es102_rakuten"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es102_rakuten_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es102_rakuten"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es102_rakuten"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es102_rakuten"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es102_rakuten"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es102_rakuten"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es102_rakuten"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es102_rakuten"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es102_rakuten" IS 'Rekuten連携情報管理テーブル';

CREATE TABLE "public"."es103_yahoo"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es103_yahoo_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es103_yahoo"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es103_yahoo"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es103_yahoo"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es103_yahoo"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es103_yahoo"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es103_yahoo"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es103_yahoo"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es103_yahoo" IS 'Yahoo連携情報管理テーブル';

CREATE TABLE "public"."es104_qoo10"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es104_qoo10_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es104_qoo10"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es104_qoo10"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es104_qoo10"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es104_qoo10"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es104_qoo10"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es104_qoo10"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es104_qoo10"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es104_qoo10" IS 'Qoo10連携情報管理テーブル';

CREATE TABLE "public"."es105_eccube"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es105_eccube_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es105_eccube"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es105_eccube"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es105_eccube"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es105_eccube"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es105_eccube"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es105_eccube"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es105_eccube"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es105_eccube" IS 'EC-Cube連携情報管理テーブル';

CREATE TABLE "public"."es106_ecforce"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es106_ecforce_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es106_ecforce"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es106_ecforce"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es106_ecforce"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es106_ecforce"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es106_ecforce"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es106_ecforce"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es106_ecforce"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es106_ecforce" IS 'EC-Force連携情報管理テーブル';

CREATE TABLE "public"."es107_base"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es107_base_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es107_base"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es107_base"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es107_base"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es107_base"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es107_base"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es107_base"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es107_base"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es107_base" IS 'BASE連携情報管理テーブル';

CREATE TABLE "public"."es108_shopify"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es108_shopify_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es108_shopify"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es108_shopify"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es108_shopify"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es108_shopify"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es108_shopify"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es108_shopify"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es108_shopify"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es108_shopify" IS 'Shopify連携情報管理テーブル';

CREATE TABLE "public"."es109_next_engine"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_es109_next_engine_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."es109_next_engine"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."es109_next_engine"."memo" IS '備考';
COMMENT
ON COLUMN "public"."es109_next_engine"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."es109_next_engine"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."es109_next_engine"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."es109_next_engine"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."es109_next_engine"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."es109_next_engine" IS 'Next-Engine連携情報管理テーブル';

CREATE TABLE "public"."fc001_notice"
(
    "id"          serial,
    "notice_to"   varchar(255),
    "is_read"     varchar(255),
    "start_at"    varchar(255),
    "end_at"      varchar(255),
    "detail"      varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_fc001_notice_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."fc001_notice"."id" IS '通知ID';
COMMENT
ON COLUMN "public"."fc001_notice"."notice_to" IS '通知先';
COMMENT
ON COLUMN "public"."fc001_notice"."is_read" IS '既読フラグ（1: 既読 2: 未読）';
COMMENT
ON COLUMN "public"."fc001_notice"."start_at" IS '掲載日時';
COMMENT
ON COLUMN "public"."fc001_notice"."end_at" IS '終了日時';
COMMENT
ON COLUMN "public"."fc001_notice"."detail" IS '通知内容';
COMMENT
ON COLUMN "public"."fc001_notice"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc001_notice"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc001_notice"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc001_notice"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc001_notice"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc001_notice"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."fc001_notice" IS '通知機能管理テーブル';

CREATE TABLE "public"."fc002_macro"
(
    "id"               serial,
    "macro_code"       varchar(255),
    "marco_name"       varchar(255),
    "affiliation_type" int2,
    "affiliation_code" varchar(255),
    "use_type"         int2,
    "judgement_type"   int2,
    "status"           int2,
    "start_at"         timestamp,
    "end_at"           timestamp,
    "action_category"  varchar(255),
    "action_detail"    varchar(255),
    "memo"             text,
    "created_by"       varchar(255),
    "created_at"       timestamp,
    "modified_by"      varchar(255),
    "modified_at"      timestamp,
    "is_deleted"       int2 DEFAULT 0,
    CONSTRAINT "pk_fc002_macro_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_macro_code" UNIQUE ("macro_code")
);
COMMENT
ON COLUMN "public"."fc002_macro"."id" IS '店舗マクロID';
COMMENT
ON COLUMN "public"."fc002_macro"."macro_code" IS 'マクロコード';
COMMENT
ON COLUMN "public"."fc002_macro"."marco_name" IS 'マクロ名称';
COMMENT
ON COLUMN "public"."fc002_macro"."affiliation_type" IS '所属タイプ';
COMMENT
ON COLUMN "public"."fc002_macro"."affiliation_code" IS '所属コード';
COMMENT
ON COLUMN "public"."fc002_macro"."use_type" IS '利用区分（1: 出荷依頼新規＆更新 2: 引き当て）';
COMMENT
ON COLUMN "public"."fc002_macro"."judgement_type" IS '判断タイプ（1: 全満足 2: いずれ満足）';
COMMENT
ON COLUMN "public"."fc002_macro"."status" IS 'ステータス（1: 利用中 2: 利用停止 3: 準備中）';
COMMENT
ON COLUMN "public"."fc002_macro"."start_at" IS '利用開始日';
COMMENT
ON COLUMN "public"."fc002_macro"."end_at" IS '利用終了日';
COMMENT
ON COLUMN "public"."fc002_macro"."action_category" IS 'アクションカテゴリ';
COMMENT
ON COLUMN "public"."fc002_macro"."action_detail" IS 'アクション詳細';
COMMENT
ON COLUMN "public"."fc002_macro"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc002_macro"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc002_macro"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc002_macro"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc002_macro"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc002_macro"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_macro_code" ON "public"."fc002_macro" IS '一意的なマクロコード';
COMMENT
ON TABLE "public"."fc002_macro" IS 'マクロ機能管理テーブル';

CREATE TABLE "public"."fc003_macro_detail"
(
    "id"              serial,
    "macro_code"      varchar(255),
    "judgement_type"  varchar(255),
    "judgement_item"  varchar(255),
    "judgement_value" varchar(255),
    "memo"            text,
    "created_by"      varchar(255),
    "created_at"      timestamp,
    "modified_by"     varchar(255),
    "modified_at"     timestamp,
    "is_deleted"      int2 DEFAULT 0,
    CONSTRAINT "pk_fc003_macro_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."fc003_macro_detail"."id" IS 'マクロ明細ID';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."macro_code" IS 'マクロコード';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."judgement_type" IS '判断標準タイプ';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."judgement_item" IS '判断項目';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."judgement_value" IS '判断値';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc003_macro_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."fc003_macro_detail" IS 'マクロ機能明細管理テーブル';

CREATE TABLE "public"."fc004_csv_template"
(
    "id"            serial,
    "template_code" varchar(20),
    "store_code"    varchar(20),
    "template_name" varchar(255),
    "type"          int2,
    "category"      int2,
    "delimiter"     varchar(255),
    "charset"       varchar(255),
    "memo"          text,
    "created_by"    varchar(255),
    "created_at"    timestamp,
    "modified_by"   varchar(255),
    "modified_at"   timestamp,
    "is_deleted"    int2 DEFAULT 0,
    CONSTRAINT "pk_fc004_csv_template_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_template_code" UNIQUE ("template_code")
);
COMMENT
ON COLUMN "public"."fc004_csv_template"."id" IS 'CSVテンプレートID';
COMMENT
ON COLUMN "public"."fc004_csv_template"."template_code" IS 'テンプレートコード';
COMMENT
ON COLUMN "public"."fc004_csv_template"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."fc004_csv_template"."template_name" IS 'テンプレート名称';
COMMENT
ON COLUMN "public"."fc004_csv_template"."type" IS 'タイプ（1: インポート 2:  エクスポート）';
COMMENT
ON COLUMN "public"."fc004_csv_template"."category" IS 'カテゴリ（11: 受注依頼新規 21: 商品新規）';
COMMENT
ON COLUMN "public"."fc004_csv_template"."delimiter" IS '区切り文字';
COMMENT
ON COLUMN "public"."fc004_csv_template"."charset" IS '文字コード';
COMMENT
ON COLUMN "public"."fc004_csv_template"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc004_csv_template"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc004_csv_template"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc004_csv_template"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc004_csv_template"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc004_csv_template"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_template_code" ON "public"."fc004_csv_template" IS '一意的なテンプレートコード';
COMMENT
ON TABLE "public"."fc004_csv_template" IS 'CSVテンプレート管理テーブル';

CREATE TABLE "public"."fc004_ftp"
(
    "id"          serial,
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_fc004_ftp_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."fc004_ftp"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc004_ftp"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc004_ftp"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc004_ftp"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc004_ftp"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc004_ftp"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."fc004_ftp" IS 'FTP連携情報管理テーブル';

CREATE TABLE "public"."fc005_csv_template_detail"
(
    "id"             serial,
    "template_code"  varchar(255),
    "header"         varchar(255),
    "reference_type" int2,
    "reference"      varchar(255),
    "fixed_value"    varchar(255),
    "memo"           text,
    "created_by"     varchar(255),
    "created_at"     timestamp,
    "modified_by"    varchar(255),
    "modified_at"    timestamp,
    "is_deleted"     int2 DEFAULT 0,
    CONSTRAINT "pk_fc005_csv_template_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."id" IS 'テンプレート明細ID';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."template_code" IS 'テンプレートコード';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."header" IS 'ヘッダー';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."reference_type" IS '参照タイプ（1: 固定値 2: 参照項目）';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."reference" IS '参照項目';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."fixed_value" IS '固定値';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc005_csv_template_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."fc005_csv_template_detail" IS 'CSVテンプレート明細管理テーブル';

CREATE TABLE "public"."fc005_smartcat"
(
    "id"          serial,
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_fc005_smartcat_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."fc005_smartcat"."memo" IS '備考';
COMMENT
ON COLUMN "public"."fc005_smartcat"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."fc005_smartcat"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."fc005_smartcat"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."fc005_smartcat"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."fc005_smartcat"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."fc005_smartcat" IS 'スマートCAT管理テーブル';

CREATE TABLE "public"."mg001_group"
(
    "id"          serial,
    "group_code"  varchar(20) NOT NULL,
    "group_name"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_mg001_group_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_group_code" UNIQUE ("group_code")
);
COMMENT
ON COLUMN "public"."mg001_group"."group_code" IS 'グループコード';
COMMENT
ON COLUMN "public"."mg001_group"."group_name" IS 'グループ名称';
COMMENT
ON COLUMN "public"."mg001_group"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg001_group"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg001_group"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg001_group"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg001_group"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg001_group"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_group_code" ON "public"."mg001_group" IS '一意的なグループコード';
COMMENT
ON TABLE "public"."mg001_group" IS 'グループ管理テーブル';

CREATE TABLE "public"."mg002_warehouse"
(
    "id"                  serial,
    "warehouse_code"      varchar(20) NOT NULL,
    "group_code"          varchar(20) NOT NULL,
    "warehouse_name"      varchar(255),
    "warehouse_type"      int2 DEFAULT 2,
    "status"              int2 DEFAULT 3,
    "contract_start_date" date,
    "contract_end_date"   date,
    "country"             varchar(255),
    "zip"                 varchar(255),
    "prefecture"          varchar(255),
    "municipality"        varchar(255),
    "address_1"           varchar(255),
    "address_2"           varchar(255),
    "phone"               varchar(255),
    "mobile"              varchar(255),
    "fax"                 varchar(255),
    "section"             varchar(255),
    "email"               varchar(255),
    "logo"                varchar(255),
    "home_page_url"       varchar(255),
    "memo"                text,
    "created_by"          varchar(255),
    "created_at"          timestamp,
    "modified_by"         varchar(255),
    "modified_at"         timestamp,
    "is_deleted"          int2 DEFAULT 0,
    CONSTRAINT "pk_mg002_warehouse_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg002_warehouse"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."mg002_warehouse"."group_code" IS 'グループコード';
COMMENT
ON COLUMN "public"."mg002_warehouse"."warehouse_name" IS '倉庫名称';
COMMENT
ON COLUMN "public"."mg002_warehouse"."warehouse_type" IS '倉庫タイプ（1: 本番倉庫 2: 開発倉庫）';
COMMENT
ON COLUMN "public"."mg002_warehouse"."status" IS 'ステータス（1: 利用中 2: 利用停止 3: 準備中）';
COMMENT
ON COLUMN "public"."mg002_warehouse"."contract_start_date" IS '契約開始日';
COMMENT
ON COLUMN "public"."mg002_warehouse"."contract_end_date" IS '契約終了日';
COMMENT
ON COLUMN "public"."mg002_warehouse"."country" IS '国・地域';
COMMENT
ON COLUMN "public"."mg002_warehouse"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."mg002_warehouse"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."mg002_warehouse"."municipality" IS '市区町村';
COMMENT
ON COLUMN "public"."mg002_warehouse"."address_1" IS '住所1';
COMMENT
ON COLUMN "public"."mg002_warehouse"."address_2" IS '住所2';
COMMENT
ON COLUMN "public"."mg002_warehouse"."phone" IS '電話番号';
COMMENT
ON COLUMN "public"."mg002_warehouse"."mobile" IS '携帯電話';
COMMENT
ON COLUMN "public"."mg002_warehouse"."fax" IS 'FAX';
COMMENT
ON COLUMN "public"."mg002_warehouse"."section" IS '担当部署';
COMMENT
ON COLUMN "public"."mg002_warehouse"."email" IS 'メールアドレス';
COMMENT
ON COLUMN "public"."mg002_warehouse"."logo" IS 'ロゴ';
COMMENT
ON COLUMN "public"."mg002_warehouse"."home_page_url" IS 'ホームページURL';
COMMENT
ON COLUMN "public"."mg002_warehouse"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg002_warehouse"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg002_warehouse"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg002_warehouse"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg002_warehouse"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg002_warehouse"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."mg002_warehouse" IS '倉庫管理テーブル';

CREATE TABLE "public"."mg003_store"
(
    "id"                  serial,
    "store_code"          varchar(20) NOT NULL,
    "store_name"          varchar(255),
    "store_display_name"  varchar(255),
    "store_type"          int2 DEFAULT 2,
    "status"              int2 DEFAULT 3,
    "contract_start_date" date,
    "contract_end_date"   date,
    "business_form_type"  int2 DEFAULT 1,
    "corporate_number"    varchar(255),
    "country"             varchar(255),
    "zip"                 varchar(255),
    "prefecture"          varchar(255),
    "municipality"        varchar(255),
    "address_1"           varchar(255),
    "address_2"           varchar(255),
    "phone"               varchar(255),
    "mobile"              varchar(255),
    "fax"                 varchar(255),
    "email"               varchar(255),
    "section"             varchar(255),
    "logo"                varchar(255),
    "home_page_url"       varchar(255),
    "login_permission_ip" varchar(255),
    "description_type"    int2 DEFAULT 1,
    "fraction_type"       int2 DEFAULT 1,
    "price_display_type"  int2 DEFAULT 1,
    "memo"                text,
    "created_by"          varchar(255),
    "created_at"          timestamp,
    "modified_by"         varchar(255),
    "modified_at"         timestamp,
    "is_deleted"          int2 DEFAULT 0,
    CONSTRAINT "pk_mg003_store_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_store_code" UNIQUE ("store_code")
);
COMMENT
ON COLUMN "public"."mg003_store"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."mg003_store"."store_name" IS '店舗名称';
COMMENT
ON COLUMN "public"."mg003_store"."store_display_name" IS '店舗表示名称';
COMMENT
ON COLUMN "public"."mg003_store"."store_type" IS '店舗タイプ（1: 本番店舗 2: 開発店舗）';
COMMENT
ON COLUMN "public"."mg003_store"."status" IS 'ステータス（1: 利用中 2: 利用停止 3: 準備中）';
COMMENT
ON COLUMN "public"."mg003_store"."contract_start_date" IS '契約開始日';
COMMENT
ON COLUMN "public"."mg003_store"."contract_end_date" IS '契約終了日';
COMMENT
ON COLUMN "public"."mg003_store"."business_form_type" IS '事業形態（1: 法人 2: 個人）';
COMMENT
ON COLUMN "public"."mg003_store"."corporate_number" IS '法人番号';
COMMENT
ON COLUMN "public"."mg003_store"."country" IS '国・地域';
COMMENT
ON COLUMN "public"."mg003_store"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."mg003_store"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."mg003_store"."municipality" IS '市区町村';
COMMENT
ON COLUMN "public"."mg003_store"."address_1" IS '住所1';
COMMENT
ON COLUMN "public"."mg003_store"."address_2" IS '住所2';
COMMENT
ON COLUMN "public"."mg003_store"."phone" IS '電話番号';
COMMENT
ON COLUMN "public"."mg003_store"."mobile" IS '携帯電話';
COMMENT
ON COLUMN "public"."mg003_store"."fax" IS 'FAX';
COMMENT
ON COLUMN "public"."mg003_store"."email" IS '連絡メールアドレス';
COMMENT
ON COLUMN "public"."mg003_store"."section" IS '担当部署';
COMMENT
ON COLUMN "public"."mg003_store"."logo" IS 'ロゴ';
COMMENT
ON COLUMN "public"."mg003_store"."home_page_url" IS '店舗ホームページURL';
COMMENT
ON COLUMN "public"."mg003_store"."login_permission_ip" IS 'ログイン許可IP';
COMMENT
ON COLUMN "public"."mg003_store"."description_type" IS '品名タイプ（1: 商品情報 2: 依頼主情報）';
COMMENT
ON COLUMN "public"."mg003_store"."fraction_type" IS '端数タイプ（1: 切り捨て 2: 切り上げ 3: 四捨五入）';
COMMENT
ON COLUMN "public"."mg003_store"."price_display_type" IS '商品価格表示タイプ（1: 税込 2: 税抜）';
COMMENT
ON COLUMN "public"."mg003_store"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg003_store"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg003_store"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg003_store"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg003_store"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg003_store"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_store_code" ON "public"."mg003_store" IS '一意的なストアコード';
COMMENT
ON TABLE "public"."mg003_store" IS '店舗管理テーブル';

CREATE TABLE "public"."mg004_user"
(
    "id"               serial,
    "user_code"        varchar(20) NOT NULL,
    "username"         varchar(255),
    "email"            varchar(255),
    "password"         varchar(255),
    "is_accept_notice" varchar(255) DEFAULT 1,
    "memo"             text,
    "created_by"       varchar(255),
    "created_at"       timestamp,
    "modified_by"      varchar(255),
    "modified_at"      timestamp,
    "is_deleted"       int2         DEFAULT 0,
    CONSTRAINT "pk_mg004_user_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_user_code" UNIQUE ("user_code")
);
COMMENT
ON COLUMN "public"."mg004_user"."user_code" IS 'ユーザーコード';
COMMENT
ON COLUMN "public"."mg004_user"."username" IS 'ユーザー名称';
COMMENT
ON COLUMN "public"."mg004_user"."email" IS 'メールアドレス（ログインのユーザー名として扱う）';
COMMENT
ON COLUMN "public"."mg004_user"."password" IS 'パスワード';
COMMENT
ON COLUMN "public"."mg004_user"."is_accept_notice" IS '通知受取フラグ（1: 受取する  2: 受取しない）';
COMMENT
ON COLUMN "public"."mg004_user"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg004_user"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg004_user"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg004_user"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg004_user"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg004_user"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_user_code" ON "public"."mg004_user" IS '一意的なユーザーコード';
COMMENT
ON TABLE "public"."mg004_user" IS 'ユーザー管理テーブル';

CREATE TABLE "public"."mg100_user_relation"
(
    "id"               serial,
    "user_code"        varchar(20) NOT NULL,
    "username"         varchar(255),
    "affiliation_type" varchar(255),
    "affiliation_code" varchar(255),
    "memo"             text,
    "created_by"       varchar(255),
    "created_at"       timestamp,
    "modified_by"      varchar(255),
    "modified_at"      timestamp,
    "is_deleted"       int2 DEFAULT 0,
    CONSTRAINT "pk_mg100_user_relation_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg100_user_relation"."user_code" IS 'ユーザーコード';
COMMENT
ON COLUMN "public"."mg100_user_relation"."username" IS 'ユーザー名称';
COMMENT
ON COLUMN "public"."mg100_user_relation"."affiliation_type" IS '所属タイプ（1: グループ 2: 倉庫 3: 店舗 4: 管理側）';
COMMENT
ON COLUMN "public"."mg100_user_relation"."affiliation_code" IS '所属コード';
COMMENT
ON COLUMN "public"."mg100_user_relation"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg100_user_relation"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg100_user_relation"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg100_user_relation"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg100_user_relation"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg100_user_relation"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."mg100_user_relation" IS 'ユーザー所属関係管理テーブル';

CREATE TABLE "public"."mg101_store_relation"
(
    "id"               serial,
    "store_code"       varchar(255) NOT NULL,
    "affiliation_type" varchar(255) NOT NULL,
    "affiliation_code" varchar(255) NOT NULL,
    "memo"             text,
    "created_by"       varchar(255),
    "created_at"       timestamp,
    "modified_by"      varchar(255),
    "modified_at"      timestamp,
    "is_deleted"       int2 DEFAULT 0,
    CONSTRAINT "pk_mg101_store_relation_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg101_store_relation"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."mg101_store_relation"."affiliation_type" IS '所属タイプ（1: 倉庫 2: グループ）';
COMMENT
ON COLUMN "public"."mg101_store_relation"."affiliation_code" IS '所属コード';
COMMENT
ON COLUMN "public"."mg101_store_relation"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg101_store_relation"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg101_store_relation"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg101_store_relation"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg101_store_relation"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg101_store_relation"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."mg101_store_relation" IS '店舗所属関係管理テーブル';

CREATE TABLE "public"."mg200_delivery_mapping"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_mg200_delivery_mapping_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg200_delivery_mapping"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg200_delivery_mapping"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg200_delivery_mapping"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg200_delivery_mapping"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg200_delivery_mapping"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg200_delivery_mapping"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."mg200_delivery_mapping" IS '店鋪配送方法マッピング管理テーブル';

CREATE TABLE "public"."mg201_delivery_period_mapping"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_mg201_delivery_period_mapping_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg201_delivery_period_mapping"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg201_delivery_period_mapping"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg201_delivery_period_mapping"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg201_delivery_period_mapping"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg201_delivery_period_mapping"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg201_delivery_period_mapping"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."mg201_delivery_period_mapping" IS '店鋪配送時間帯マッピング管理テーブル';

CREATE TABLE "public"."mg202_delivery_info"
(
    "id"          serial,
    "store_code"  varchar(255),
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_mg202_delivery_info_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg202_delivery_info"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg202_delivery_info"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg202_delivery_info"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg202_delivery_info"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg202_delivery_info"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg202_delivery_info"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';

CREATE TABLE "public"."mg203_sender"
(
    "id"                  serial,
    "store_code"          varchar(20) NOT NULL,
    "sender_code"         varchar(20),
    "sender_name"         varchar(255),
    "sender_name_kana"    varchar(255),
    "is_default"          int2 DEFAULT 1,
    "company"             varchar(255),
    "section"             varchar(255),
    "zip"                 varchar(255),
    "prefecture"          varchar(255),
    "municipality"        varchar(255),
    "address_1"           varchar(255),
    "address_2"           varchar(255),
    "phone"               varchar(255),
    "mobile"              varchar(255),
    "memo"                text,
    "fax"                 varchar(255),
    "email"               varchar(255),
    "inquiry_type"        int2 DEFAULT 1,
    "inquiry_url"         varchar(255),
    "invoice_logo"        varchar(255),
    "invoice_description" varchar(255),
    "notification"        varchar(255),
    "created_by"          varchar(255),
    "created_at"          timestamp,
    "modified_by"         varchar(255),
    "modified_at"         timestamp,
    "is_deleted"          int2 DEFAULT 0,
    CONSTRAINT "pk_mg203_sender_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."mg203_sender"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."mg203_sender"."sender_code" IS '依頼主コード';
COMMENT
ON COLUMN "public"."mg203_sender"."sender_name" IS '依頼主名称';
COMMENT
ON COLUMN "public"."mg203_sender"."sender_name_kana" IS '依頼主名称_カナ';
COMMENT
ON COLUMN "public"."mg203_sender"."is_default" IS 'ディフォルトフラグ（1: ディフォルト 2: 非ディフォルト）';
COMMENT
ON COLUMN "public"."mg203_sender"."company" IS '会社名称';
COMMENT
ON COLUMN "public"."mg203_sender"."section" IS '担当部署';
COMMENT
ON COLUMN "public"."mg203_sender"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."mg203_sender"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."mg203_sender"."municipality" IS '市区町村';
COMMENT
ON COLUMN "public"."mg203_sender"."address_1" IS '住所1';
COMMENT
ON COLUMN "public"."mg203_sender"."address_2" IS '住所2';
COMMENT
ON COLUMN "public"."mg203_sender"."phone" IS '電話番号';
COMMENT
ON COLUMN "public"."mg203_sender"."mobile" IS '携帯電話';
COMMENT
ON COLUMN "public"."mg203_sender"."memo" IS '備考';
COMMENT
ON COLUMN "public"."mg203_sender"."fax" IS 'FAX';
COMMENT
ON COLUMN "public"."mg203_sender"."email" IS 'メールアドレス';
COMMENT
ON COLUMN "public"."mg203_sender"."inquiry_type" IS '問い合わせタイプ（1: 電話番号 2: メールアドレス 3: 問い合わせフォーム）';
COMMENT
ON COLUMN "public"."mg203_sender"."inquiry_url" IS '問い合わせURL';
COMMENT
ON COLUMN "public"."mg203_sender"."invoice_logo" IS '納品書ロゴ';
COMMENT
ON COLUMN "public"."mg203_sender"."invoice_description" IS '納品書記載内容';
COMMENT
ON COLUMN "public"."mg203_sender"."notification" IS '発送通知内容';
COMMENT
ON COLUMN "public"."mg203_sender"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."mg203_sender"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."mg203_sender"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."mg203_sender"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."mg203_sender"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."mg203_sender" IS '依頼主管理テーブル';

CREATE TABLE "public"."ms001_authority"
(
    "id"          serial,
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_ms001_authority" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."ms001_authority"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms001_authority"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."ms001_authority"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."ms001_authority"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."ms001_authority"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."ms001_authority"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';

CREATE TABLE "public"."ms002_function"
(
    "id"            serial,
    "function_code" varchar(255),
    "function_name" varchar(255),
    "memo"          text,
    "created_by"    varchar(255),
    "created_at"    timestamp,
    "modified_by"   varchar(255),
    "modified_at"   timestamp,
    "is_deleted"    int2 DEFAULT 0,
    CONSTRAINT "pk_ms002_function_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."ms002_function"."id" IS '機能ID';
COMMENT
ON COLUMN "public"."ms002_function"."function_code" IS '機能コード';
COMMENT
ON COLUMN "public"."ms002_function"."function_name" IS '機能名称';
COMMENT
ON COLUMN "public"."ms002_function"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms002_function"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."ms002_function"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."ms002_function"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."ms002_function"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."ms002_function"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."ms002_function" IS '機能マスタ';

CREATE TABLE "public"."ms003_address"
(
    "id"                serial,
    "address_code"      varchar(255),
    "zip"               varchar(255),
    "prefecture"        varchar(255),
    "prefecture_kana"   varchar(255),
    "municipality"      varchar(255),
    "municipality_kana" varchar(255),
    "town"              varchar(255),
    "town_kana"         varchar(255),
    "memo"              text,
    "created_by"        varchar(255),
    "created_at"        timestamp,
    "modified_by"       varchar(255),
    "modified_at"       timestamp,
    "is_deleted"        int2 DEFAULT 0,
    CONSTRAINT "pk_ms003_address_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."ms003_address"."id" IS '住所ID';
COMMENT
ON COLUMN "public"."ms003_address"."address_code" IS '住所コード';
COMMENT
ON COLUMN "public"."ms003_address"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."ms003_address"."prefecture" IS '都道府県';
COMMENT
ON COLUMN "public"."ms003_address"."prefecture_kana" IS '都道府県_カナ';
COMMENT
ON COLUMN "public"."ms003_address"."municipality" IS '市区町村';
COMMENT
ON COLUMN "public"."ms003_address"."municipality_kana" IS '市区町村_カナ';
COMMENT
ON COLUMN "public"."ms003_address"."town" IS '町名';
COMMENT
ON COLUMN "public"."ms003_address"."town_kana" IS '町名_カナ';
COMMENT
ON COLUMN "public"."ms003_address"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms003_address"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."ms003_address"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."ms003_address"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."ms003_address"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."ms003_address"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."ms003_address" IS '住所マスタ';

CREATE TABLE "public"."ms004_delivery_period"
(
    "id"                   serial,
    "delivery_period_code" varchar(255),
    "delivery_period_name" varchar(255),
    "memo"                 text,
    "created_by"           varchar(255),
    "created_at"           timestamp,
    "modified_by"          varchar(255),
    "modified_at"          timestamp,
    "is_deleted"           int2 DEFAULT 0,
    CONSTRAINT "pk_ms004_delivery_period_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."ms004_delivery_period"."id" IS '配送時間帯ID';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."delivery_period_code" IS '配送時間帯コード';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."delivery_period_name" IS '配送時間帯名称';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."ms004_delivery_period"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."ms004_delivery_period" IS '配送時間帯マスタ';

CREATE TABLE "public"."ms005_delivery"
(
    "id"                    serial,
    "delivery_code"         varchar(255),
    "delivery_company_name" varchar(255),
    "delivery_company_code" varchar(255),
    "delivery_method"       varchar(255),
    "memo"                  text,
    "created_by"            varchar(255),
    "created_at"            timestamp,
    "modified_by"           varchar(255),
    "modified_at"           timestamp,
    "is_deleted"            int2 DEFAULT 0,
    CONSTRAINT "pk_ms005_delivery_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."ms005_delivery"."id" IS '配送情報ID';
COMMENT
ON COLUMN "public"."ms005_delivery"."delivery_code" IS '配送情報コード';
COMMENT
ON COLUMN "public"."ms005_delivery"."delivery_company_name" IS '配送会社名称';
COMMENT
ON COLUMN "public"."ms005_delivery"."delivery_company_code" IS '配送会社コード';
COMMENT
ON COLUMN "public"."ms005_delivery"."delivery_method" IS '配送方法';
COMMENT
ON COLUMN "public"."ms005_delivery"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms005_delivery"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."ms005_delivery"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."ms005_delivery"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."ms005_delivery"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."ms005_delivery"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."ms005_delivery" IS '配送情報マスタ';

CREATE TABLE "public"."ms006_address"
(
    "address_code"      int8 NOT NULL DEFAULT 0,
    "prefecture_code"   int4          DEFAULT NULL,
    "municipality_code" int4          DEFAULT NULL,
    "town_code"         int8          DEFAULT NULL,
    "zip"               varchar(8)    DEFAULT NULL,
    "is_office"         int4          DEFAULT 0,
    "is_deleted"        int4          DEFAULT 0,
    "prefecture_name"   varchar(255)  DEFAULT NULL,
    "prefecture_kana"   varchar(255)  DEFAULT NULL,
    "municipality_name" varchar(255)  DEFAULT NULL,
    "municipality_kana" varchar(255)  DEFAULT NULL,
    "town_name"         varchar(255)  DEFAULT NULL,
    "town_kana"         varchar(255)  DEFAULT NULL,
    "town_memo"         varchar(255)  DEFAULT NULL,
    "kyoto_street"      varchar(255)  DEFAULT NULL,
    "block_name"        varchar(255)  DEFAULT NULL,
    "block_kana"        varchar(255)  DEFAULT NULL,
    "memo"              varchar(255)  DEFAULT NULL,
    "office_name"       varchar(255)  DEFAULT NULL,
    "office_kana"       varchar(255)  DEFAULT NULL,
    "office_address"    varchar(255)  DEFAULT NULL,
    "new_address_code"  varchar(255)  DEFAULT NULL,
    CONSTRAINT "pk_ms006_address_id" PRIMARY KEY ("address_code")
);
COMMENT
ON COLUMN "public"."ms006_address"."address_code" IS '住所コード';
COMMENT
ON COLUMN "public"."ms006_address"."prefecture_code" IS '都道府県コード';
COMMENT
ON COLUMN "public"."ms006_address"."municipality_code" IS '市区町村コード';
COMMENT
ON COLUMN "public"."ms006_address"."town_code" IS '町域コード';
COMMENT
ON COLUMN "public"."ms006_address"."zip" IS '郵便番号';
COMMENT
ON COLUMN "public"."ms006_address"."is_office" IS '事業所フラグ（0: 事業所であり 1: 事業所ではない）';
COMMENT
ON COLUMN "public"."ms006_address"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON COLUMN "public"."ms006_address"."prefecture_name" IS '都道府県';
COMMENT
ON COLUMN "public"."ms006_address"."prefecture_kana" IS '都道府県カナ';
COMMENT
ON COLUMN "public"."ms006_address"."municipality_name" IS '市区町村';
COMMENT
ON COLUMN "public"."ms006_address"."municipality_kana" IS '市区町村カナ';
COMMENT
ON COLUMN "public"."ms006_address"."town_name" IS '町域';
COMMENT
ON COLUMN "public"."ms006_address"."town_kana" IS '町域カナ';
COMMENT
ON COLUMN "public"."ms006_address"."town_memo" IS '町域補足';
COMMENT
ON COLUMN "public"."ms006_address"."kyoto_street" IS '京都通り名';
COMMENT
ON COLUMN "public"."ms006_address"."block_name" IS '字丁目';
COMMENT
ON COLUMN "public"."ms006_address"."block_kana" IS '字丁目カナ';
COMMENT
ON COLUMN "public"."ms006_address"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms006_address"."office_name" IS '事業所名';
COMMENT
ON COLUMN "public"."ms006_address"."office_kana" IS '事業所カナ';
COMMENT
ON COLUMN "public"."ms006_address"."office_address" IS '事業所住所';
COMMENT
ON COLUMN "public"."ms006_address"."new_address_code" IS '新住所コード';
COMMENT
ON TABLE "public"."ms006_address" IS '住所マスタ';

CREATE TABLE "public"."ms006_operation"
(
    "id"             serial,
    "operation_code" varchar(255),
    "operation_name" varchar(255),
    "memo"           text,
    "created_by"     varchar(255),
    "created_at"     timestamp,
    "modified_by"    varchar(255),
    "modified_at"    timestamp,
    "is_deleted"     int2 DEFAULT 0,
    CONSTRAINT "pk_ms006_operation_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."ms006_operation"."id" IS '操作ID';
COMMENT
ON COLUMN "public"."ms006_operation"."operation_code" IS '操作コード';
COMMENT
ON COLUMN "public"."ms006_operation"."operation_name" IS '操作名称';
COMMENT
ON COLUMN "public"."ms006_operation"."memo" IS '備考';
COMMENT
ON COLUMN "public"."ms006_operation"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."ms006_operation"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."ms006_operation"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."ms006_operation"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."ms006_operation"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."ms006_operation" IS '操作マスタ';

CREATE TABLE "public"."od001_order"
(
    "id"                             serial,
    "order_code"                     varchar(20)  NOT NULL,
    "store_code"                     varchar(255) NOT NULL,
    "warehouse_code"                 varchar(255) NOT NULL,
    "order_history_code"             varchar(255) NOT NULL,
    "sender_code"                    varchar(20),
    "es_code"                        varchar(20),
    "order_status"                   int2,
    "order_type"                     int2,
    "order_datetime"                 timestamp,
    "import_datetime"                timestamp,
    "shipment_code"                  varchar(20),
    "shipment_request_datetime"      timestamp,
    "shipment_desired_date"          date,
    "shipment_estimated_date"        date,
    "delivery_desired_date"          date,
    "delivery_desired_period"        varchar(255),
    "origin_delivery_desired_period" varchar(255),
    "delivery_method"                varchar(255),
    "origin_delivery_method"         varchar(255),
    "payment_method"                 varchar(255),
    "total_price"                    int4,
    "total_tax"                      int4,
    "commission_fee"                 int4,
    "cash_on_delivery"               int4,
    "product_total_price"            int4,
    "delivery_fee"                   int4,
    "other_fee"                      int4,
    "membership_number"              int4,
    "orderer_zip"                    varchar(255),
    "orderer_prefecture"             varchar(255),
    "orderer_municipality"           varchar(255),
    "orderer_address1"               varchar(255),
    "orderer_address2"               varchar(255),
    "orderer_name"                   varchar(255),
    "orderer_name_kana"              varchar(255),
    "orderer_phone"                  varchar(255),
    "orderer_email"                  varchar(255),
    "orderer_gender"                 int2,
    "receiver_zip"                   varchar(255),
    "receiver_prefecture"            varchar(255),
    "receiver_municipality"          varchar(255),
    "receiver_address1"              varchar(255),
    "receiver_address2"              varchar(255),
    "receiver_name"                  varchar(255),
    "receiver_name_kana"             varchar(255),
    "receiver_phone"                 varchar(255),
    "receiver_email"                 varchar(255),
    "receiver_gender"                int2,
    "receiving_method"               varchar(255),
    "request_gift"                   varchar(255),
    "order_origin_form"              int2,
    "order_requester"                int2,
    "shipment_remarks"               varchar(255),
    "delivery_slip_remarks"          varchar(255),
    "orderer_remarks"                varchar(255),
    "other_remarks"                  varchar(255),
    "gmo_transaction_id"             int4,
    "subscription_id"                int4,
    "subscription_count"             int4,
    "next_subscription_date"         date,
    "memo"                           text,
    "created_by"                     varchar(255),
    "created_at"                     timestamp,
    "modified_by"                    varchar(255),
    "modified_at"                    timestamp,
    "is_deleted"                     int2 DEFAULT 0,
    CONSTRAINT "pk_od001_order_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_order_code" UNIQUE ("order_code")
);
COMMENT
ON COLUMN "public"."od001_order"."id" IS '受注依頼ID';
COMMENT
ON COLUMN "public"."od001_order"."order_code" IS '受注依頼コード';
COMMENT
ON COLUMN "public"."od001_order"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."od001_order"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."od001_order"."order_history_code" IS '受注取込履歴コード';
COMMENT
ON COLUMN "public"."od001_order"."sender_code" IS '依頼主コード';
COMMENT
ON COLUMN "public"."od001_order"."es_code" IS '外部連携情報コード';
COMMENT
ON COLUMN "public"."od001_order"."order_status" IS '受注ステータス（1: 出荷依頼完了 2: 出荷依頼待ち 3: 受注キャンセル済）';
COMMENT
ON COLUMN "public"."od001_order"."order_type" IS '受注タイプ（1: 通常 2: 後払い 3: 代引）';
COMMENT
ON COLUMN "public"."od001_order"."order_datetime" IS '注文日時';
COMMENT
ON COLUMN "public"."od001_order"."import_datetime" IS '受注取込日時';
COMMENT
ON COLUMN "public"."od001_order"."shipment_code" IS '出荷依頼コード';
COMMENT
ON COLUMN "public"."od001_order"."shipment_request_datetime" IS '出荷依頼日時';
COMMENT
ON COLUMN "public"."od001_order"."shipment_desired_date" IS '出荷希望日';
COMMENT
ON COLUMN "public"."od001_order"."shipment_estimated_date" IS '出荷予定日';
COMMENT
ON COLUMN "public"."od001_order"."delivery_desired_date" IS '配送希望日';
COMMENT
ON COLUMN "public"."od001_order"."delivery_desired_period" IS '配送希望時間帯';
COMMENT
ON COLUMN "public"."od001_order"."origin_delivery_desired_period" IS '外部配送希望時間帯';
COMMENT
ON COLUMN "public"."od001_order"."delivery_method" IS '配送方法';
COMMENT
ON COLUMN "public"."od001_order"."origin_delivery_method" IS '外部配送方法';
COMMENT
ON COLUMN "public"."od001_order"."payment_method" IS '支払方法';
COMMENT
ON COLUMN "public"."od001_order"."total_price" IS '総金額';
COMMENT
ON COLUMN "public"."od001_order"."total_tax" IS '総税金';
COMMENT
ON COLUMN "public"."od001_order"."commission_fee" IS '手数料';
COMMENT
ON COLUMN "public"."od001_order"."cash_on_delivery" IS '代引請求金額';
COMMENT
ON COLUMN "public"."od001_order"."product_total_price" IS '商品総金額';
COMMENT
ON COLUMN "public"."od001_order"."delivery_fee" IS '配送料';
COMMENT
ON COLUMN "public"."od001_order"."other_fee" IS 'その他金額';
COMMENT
ON COLUMN "public"."od001_order"."membership_number" IS '会員番号';
COMMENT
ON COLUMN "public"."od001_order"."orderer_zip" IS '注文者郵便番号';
COMMENT
ON COLUMN "public"."od001_order"."orderer_prefecture" IS '注文者都道府県';
COMMENT
ON COLUMN "public"."od001_order"."orderer_municipality" IS '注文者市区町村';
COMMENT
ON COLUMN "public"."od001_order"."orderer_address1" IS '注文者住所1';
COMMENT
ON COLUMN "public"."od001_order"."orderer_address2" IS '注文者住所2';
COMMENT
ON COLUMN "public"."od001_order"."orderer_name" IS '注文者姓名';
COMMENT
ON COLUMN "public"."od001_order"."orderer_name_kana" IS '注文者姓名カナ';
COMMENT
ON COLUMN "public"."od001_order"."orderer_phone" IS '注文者電話番号';
COMMENT
ON COLUMN "public"."od001_order"."orderer_email" IS '注文者メールアドレス';
COMMENT
ON COLUMN "public"."od001_order"."orderer_gender" IS '注文者性別(1: 男性 2: 女性 3: 未知)';
COMMENT
ON COLUMN "public"."od001_order"."receiver_zip" IS '配送先郵便番号';
COMMENT
ON COLUMN "public"."od001_order"."receiver_prefecture" IS '配送先都道府県';
COMMENT
ON COLUMN "public"."od001_order"."receiver_municipality" IS '配送先市区町村';
COMMENT
ON COLUMN "public"."od001_order"."receiver_address1" IS '配送先住所1';
COMMENT
ON COLUMN "public"."od001_order"."receiver_address2" IS '配送先住所2';
COMMENT
ON COLUMN "public"."od001_order"."receiver_name" IS '配送先姓名';
COMMENT
ON COLUMN "public"."od001_order"."receiver_name_kana" IS '配送先姓名カナ';
COMMENT
ON COLUMN "public"."od001_order"."receiver_phone" IS '配送先電話番号';
COMMENT
ON COLUMN "public"."od001_order"."receiver_email" IS '配送先メールアドレス';
COMMENT
ON COLUMN "public"."od001_order"."receiver_gender" IS '配送先性別(1: 男性 2: 女性 3: 未知)';
COMMENT
ON COLUMN "public"."od001_order"."receiving_method" IS '受取方法';
COMMENT
ON COLUMN "public"."od001_order"."request_gift" IS 'ギフト配送希望';
COMMENT
ON COLUMN "public"."od001_order"."order_origin_form" IS '注文元形態（1: 法人 2: 個人）';
COMMENT
ON COLUMN "public"."od001_order"."order_requester" IS '注文依頼元（1: 依頼主 2: 注文者）';
COMMENT
ON COLUMN "public"."od001_order"."shipment_remarks" IS '出荷依頼特記事項';
COMMENT
ON COLUMN "public"."od001_order"."delivery_slip_remarks" IS '送り状特記事項';
COMMENT
ON COLUMN "public"."od001_order"."orderer_remarks" IS '購入者特記事項';
COMMENT
ON COLUMN "public"."od001_order"."other_remarks" IS 'その他特記事項';
COMMENT
ON COLUMN "public"."od001_order"."gmo_transaction_id" IS 'GMO取引ID';
COMMENT
ON COLUMN "public"."od001_order"."subscription_id" IS '定期購入ID';
COMMENT
ON COLUMN "public"."od001_order"."subscription_count" IS '定期購入回数';
COMMENT
ON COLUMN "public"."od001_order"."next_subscription_date" IS '次回お届け予定日';
COMMENT
ON COLUMN "public"."od001_order"."memo" IS '備考';
COMMENT
ON COLUMN "public"."od001_order"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."od001_order"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."od001_order"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."od001_order"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."od001_order"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_order_code" ON "public"."od001_order" IS '一意的な注文コード';
COMMENT
ON TABLE "public"."od001_order" IS '受注依頼管理テーブル';

CREATE TABLE "public"."od002_order_detail"
(
    "id"                  serial,
    "order_detail_code"   varchar(20)  NOT NULL,
    "order_code"          varchar(20)  NOT NULL,
    "store_code"          varchar(255) NOT NULL,
    "warehouse_code"      varchar(255) NOT NULL,
    "product_code"        varchar(255) NOT NULL,
    "origin_product_code" varchar(255),
    "product_name"        varchar(255),
    "product_type"        int2,
    "tax_excluded_price"  int4,
    "tax_included_price"  int4,
    "tax"                 int4,
    "tax_rate"            int4,
    "count"               int4,
    "options"             varchar(255),
    "barcode"             varchar(255),
    "inspection_type"     int2,
    "wrapping_type"       int2,
    "wrapping_title"      varchar(255),
    "wrapping_price"      int4,
    "memo"                text,
    "created_by"          varchar(255),
    "created_at"          timestamp,
    "modified_by"         varchar(255),
    "modified_at"         timestamp,
    "is_deleted"          int2 DEFAULT 0,
    CONSTRAINT "pk_od002_order_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."od002_order_detail"."id" IS '受注依頼明細ID';
COMMENT
ON COLUMN "public"."od002_order_detail"."order_detail_code" IS '受注依頼明細コード';
COMMENT
ON COLUMN "public"."od002_order_detail"."order_code" IS '受注依頼コード';
COMMENT
ON COLUMN "public"."od002_order_detail"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."od002_order_detail"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."od002_order_detail"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."od002_order_detail"."origin_product_code" IS '外部商品コード';
COMMENT
ON COLUMN "public"."od002_order_detail"."product_name" IS '商品名称';
COMMENT
ON COLUMN "public"."od002_order_detail"."product_type" IS '商品タイプ（1: 通常商品 2:セット商品 3: 同梱物 4: 仮商品）';
COMMENT
ON COLUMN "public"."od002_order_detail"."tax_excluded_price" IS '税抜単価';
COMMENT
ON COLUMN "public"."od002_order_detail"."tax_included_price" IS '税込単価';
COMMENT
ON COLUMN "public"."od002_order_detail"."tax" IS '税金';
COMMENT
ON COLUMN "public"."od002_order_detail"."tax_rate" IS '税率';
COMMENT
ON COLUMN "public"."od002_order_detail"."count" IS '個数';
COMMENT
ON COLUMN "public"."od002_order_detail"."options" IS 'オプション';
COMMENT
ON COLUMN "public"."od002_order_detail"."barcode" IS '管理バーコード';
COMMENT
ON COLUMN "public"."od002_order_detail"."inspection_type" IS '検品タイプ';
COMMENT
ON COLUMN "public"."od002_order_detail"."wrapping_type" IS 'ラッピングタイプ';
COMMENT
ON COLUMN "public"."od002_order_detail"."wrapping_title" IS 'ラッピングタイトル';
COMMENT
ON COLUMN "public"."od002_order_detail"."wrapping_price" IS 'ラッピング価格';
COMMENT
ON COLUMN "public"."od002_order_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."od002_order_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."od002_order_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."od002_order_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."od002_order_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."od002_order_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."od002_order_detail" IS '受注依頼明細管理テーブル';

CREATE TABLE "public"."od003_order_history"
(
    "id"                serial,
    "history_code"      varchar(255),
    "store_code"        varchar(20),
    "es_code"           varchar(20),
    "import_type"       int2,
    "import_datetime"   timestamp,
    "csv_template_code" varchar(20),
    "condition"         varchar(255),
    "total_count"       int4,
    "new_order_count"   int4,
    "imported_count"    int4,
    "failure_count"     int4,
    "memo"              text,
    "created_by"        varchar(255),
    "created_at"        timestamp,
    "modified_by"       varchar(255),
    "modified_at"       timestamp,
    "is_deleted"        int2 DEFAULT 0,
    CONSTRAINT "pk_od003_order_history_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_history_code" UNIQUE ("history_code")
);
COMMENT
ON COLUMN "public"."od003_order_history"."id" IS '受注取込履歴ID';
COMMENT
ON COLUMN "public"."od003_order_history"."history_code" IS '受注取込履歴コード';
COMMENT
ON COLUMN "public"."od003_order_history"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."od003_order_history"."es_code" IS '外部連携情報コード';
COMMENT
ON COLUMN "public"."od003_order_history"."import_type" IS '取込タイプ（1: バッチ 2: API 3: CSVアップロード）';
COMMENT
ON COLUMN "public"."od003_order_history"."import_datetime" IS '取込日時';
COMMENT
ON COLUMN "public"."od003_order_history"."csv_template_code" IS 'CSVテンプレートコード';
COMMENT
ON COLUMN "public"."od003_order_history"."condition" IS '取込条件';
COMMENT
ON COLUMN "public"."od003_order_history"."total_count" IS '取込総件数';
COMMENT
ON COLUMN "public"."od003_order_history"."new_order_count" IS '新規受注件数';
COMMENT
ON COLUMN "public"."od003_order_history"."imported_count" IS '取込済件数';
COMMENT
ON COLUMN "public"."od003_order_history"."failure_count" IS '異常件数';
COMMENT
ON COLUMN "public"."od003_order_history"."memo" IS '備考';
COMMENT
ON COLUMN "public"."od003_order_history"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."od003_order_history"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."od003_order_history"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."od003_order_history"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."od003_order_history"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_history_code" ON "public"."od003_order_history" IS '一意的な受注取込履歴コード';
COMMENT
ON TABLE "public"."od003_order_history" IS '受注取込履歴管理テーブル';

CREATE TABLE "public"."od004_order_error"
(
    "id"                serial,
    "history_id"        int4,
    "origin_order_code" varchar(255),
    "status"            int2,
    "detail"            varchar(255),
    "memo"              text,
    "created_by"        varchar(255),
    "created_at"        timestamp,
    "modified_by"       varchar(255),
    "modified_at"       timestamp,
    "is_deleted"        int2 DEFAULT 0,
    CONSTRAINT "pk_od004_order_error_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."od004_order_error"."id" IS '受注取込異常ID';
COMMENT
ON COLUMN "public"."od004_order_error"."history_id" IS '受注取込履歴ID';
COMMENT
ON COLUMN "public"."od004_order_error"."origin_order_code" IS '外部受注依頼コード';
COMMENT
ON COLUMN "public"."od004_order_error"."status" IS 'ステータス（1: 対応待ち 2: 対応中 3: 対応完了）';
COMMENT
ON COLUMN "public"."od004_order_error"."detail" IS '異常詳細';
COMMENT
ON COLUMN "public"."od004_order_error"."memo" IS '備考';
COMMENT
ON COLUMN "public"."od004_order_error"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."od004_order_error"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."od004_order_error"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."od004_order_error"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."od004_order_error"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."od004_order_error" IS '受注取込異常管理テーブル';

CREATE TABLE "public"."pd001_product"
(
    "id"                 serial,
    "product_code"       varchar(20) NOT NULL,
    "store_code"         varchar(20) NOT NULL,
    "warehouse_code"     varchar(20) NOT NULL,
    "product_name_jp"    varchar(255),
    "product_name_en"    varchar(255),
    "tax_excluded_price" int4,
    "tax_included_price" int4,
    "tax"                int4,
    "tax_rate"           int2,
    "product_type"       int2,
    "product_category"   varchar(255),
    "options"            varchar(255),
    "tags"               varchar(255),
    "description"        varchar(255),
    "product_image_1"    varchar(255),
    "product_image_2"    varchar(255),
    "product_image_3"    varchar(255),
    "qr_code_url"        varchar(255),
    "cost_price"         int4,
    "weight"             float8,
    "size_code"          int2,
    "origin_country"     varchar(255),
    "barcode"            varchar(255),
    "inspection_type"    int2,
    "is_archived"        int2,
    "memo"               varchar(255),
    "created_by"         varchar(255),
    "created_at"         timestamp,
    "modified_by"        varchar(255),
    "modified_at"        timestamp,
    "is_deleted"         int2,
    CONSTRAINT "pk_pd001_product_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_product_code" UNIQUE ("product_code")
);
COMMENT
ON COLUMN "public"."pd001_product"."id" IS '商品ID';
COMMENT
ON COLUMN "public"."pd001_product"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."pd001_product"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."pd001_product"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."pd001_product"."product_name_jp" IS '商品名称_日本語';
COMMENT
ON COLUMN "public"."pd001_product"."product_name_en" IS '商品名称_英語';
COMMENT
ON COLUMN "public"."pd001_product"."tax_excluded_price" IS '税抜単価';
COMMENT
ON COLUMN "public"."pd001_product"."tax_included_price" IS '税込単価';
COMMENT
ON COLUMN "public"."pd001_product"."tax" IS '税金';
COMMENT
ON COLUMN "public"."pd001_product"."tax_rate" IS '税率';
COMMENT
ON COLUMN "public"."pd001_product"."product_type" IS '商品タイプ（1: 通常商品 2: セット商品 3: 同梱物 4: 仮商品）';
COMMENT
ON COLUMN "public"."pd001_product"."product_category" IS '商品種類';
COMMENT
ON COLUMN "public"."pd001_product"."options" IS 'オプション';
COMMENT
ON COLUMN "public"."pd001_product"."tags" IS 'タグ';
COMMENT
ON COLUMN "public"."pd001_product"."description" IS '品名';
COMMENT
ON COLUMN "public"."pd001_product"."product_image_1" IS '商品イメージ1';
COMMENT
ON COLUMN "public"."pd001_product"."product_image_2" IS '商品イメージ2';
COMMENT
ON COLUMN "public"."pd001_product"."product_image_3" IS '商品イメージ3';
COMMENT
ON COLUMN "public"."pd001_product"."qr_code_url" IS 'QRコードURL';
COMMENT
ON COLUMN "public"."pd001_product"."cost_price" IS '仕入れ単価';
COMMENT
ON COLUMN "public"."pd001_product"."weight" IS '重量';
COMMENT
ON COLUMN "public"."pd001_product"."size_code" IS 'サイズコード';
COMMENT
ON COLUMN "public"."pd001_product"."origin_country" IS '製造国';
COMMENT
ON COLUMN "public"."pd001_product"."barcode" IS '管理バーコード';
COMMENT
ON COLUMN "public"."pd001_product"."inspection_type" IS '検品タイプ（1: 通常検品 2: シリアル検品）';
COMMENT
ON COLUMN "public"."pd001_product"."is_archived" IS 'アーカイブフラグ（1: アーカイブされた  2: アーカイブされない）';
COMMENT
ON COLUMN "public"."pd001_product"."memo" IS '備考';
COMMENT
ON COLUMN "public"."pd001_product"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."pd001_product"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."pd001_product"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."pd001_product"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."pd001_product"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_product_code" ON "public"."pd001_product" IS '一意的な商品コード';
COMMENT
ON TABLE "public"."pd001_product" IS '商品管理テーブル';

CREATE TABLE "public"."pd002_product_detail"
(
    "id"             serial,
    "product_code"   varchar(255) NOT NULL,
    "store_code"     varchar(255) NOT NULL,
    "warehouse_code" varchar(255) NOT NULL,
    "product_type"   varchar(255),
    "count"          int4,
    "created_by"     varchar(255),
    "created_at"     timestamp,
    "modified_by"    varchar(255),
    "modified_at"    timestamptz,
    "is_deleted"     int2,
    CONSTRAINT "pk_pd002_product_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."pd002_product_detail"."id" IS '商品明細ID';
COMMENT
ON COLUMN "public"."pd002_product_detail"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."pd002_product_detail"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."pd002_product_detail"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."pd002_product_detail"."product_type" IS '商品タイプ';
COMMENT
ON COLUMN "public"."pd002_product_detail"."count" IS '個数';
COMMENT
ON COLUMN "public"."pd002_product_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."pd002_product_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."pd002_product_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."pd002_product_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."pd002_product_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."pd002_product_detail" IS '商品明細管理テーブル';

CREATE TABLE "public"."pd003_product_mapping"
(
    "id"                  int4         NOT NULL,
    "product_code"        varchar(20)  NOT NULL,
    "origin_product_code" varchar(255) NOT NULL,
    "store_code"          varchar(20),
    "warehouse_code"      varchar(20),
    "created_by"          varchar(255),
    "created_at"          timestamptz,
    "modified_by"         varchar(255),
    "modified_at"         timestamp,
    "is_deleted"          int2,
    CONSTRAINT "pk_pd003_product_mapping_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."pd003_product_mapping"."id" IS '商品マッピングID';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."origin_product_code" IS '外部商品コード';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."pd003_product_mapping"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."pd003_product_mapping" IS '商品マッピング管理テーブル';

CREATE TABLE "public"."sp001_shipment"
(
    "id"                            serial,
    "shipment_code"                 varchar(20),
    "store_code"                    varchar(20),
    "warehouse_code"                varchar(20),
    "order_code"                    varchar(255),
    "origin_order_code"             varchar(255),
    "sender_code"                   varchar(20),
    "status"                        int2,
    "order_datetime"                timestamp,
    "shipment_request_datetime"     timestamp,
    "shipment_estimated_date"       date,
    "delivery_estimated_date"       date,
    "product_category_count"        int4,
    "product_total_count"           int4,
    "delivery_desired_date"         date,
    "delivery_desired_period"       varchar(255),
    "delivery_company"              varchar(255),
    "delivery_company_code"         varchar(255),
    "delivery_method"               varchar(255),
    "payment_method"                varchar(255),
    "total_price"                   int4,
    "total_tax"                     int4,
    "commission_fee"                int4,
    "cash_on_delivery"              int4,
    "product_total_price"           int4,
    "delivery_fee"                  int4,
    "other_fee"                     int4,
    "tracking_number"               varchar(255),
    "is_tracking_number_sent"       int2,
    "is_invoice_included"           int2,
    "is_invoice_printed_with_price" int2,
    "invoice_description"           varchar(255),
    "invoice_total_price"           int4,
    "invoice_product_total_price"   int4,
    "invoice_delivery_fee"          int4,
    "invoice_commission_fee"        int4,
    "invoice_adjustment_fee"        int4,
    "invoice_pdf_identifier"        varchar(255),
    "receipt_pdf_identifier"        varchar(255),
    "description"                   varchar(255),
    "inspection_type"               int4,
    "billing_code"                  varchar(255),
    "gmo_transaction_id"            varchar(255),
    "subscription_id"               varchar(255),
    "subscription_count"            int4,
    "next_subscription_date"        date,
    "is_oversea_shipment"           int2,
    "oversea_delivery_company"      varchar(255),
    "oversea_delivery_company_code" varchar(255),
    "oversea_delivery_method"       varchar(255),
    "oversea_currency_code"         varchar(255),
    "is_join_oversea_insurance"     int2,
    "receiving_method"              varchar(255),
    "gift_wrapping_count"           int4,
    "gift_sent_by"                  varchar(255),
    "box_count"                     int4,
    "box_size_code"                 varchar(255),
    "is_photograph_confirmed"       int2,
    "attachment_file1"              varchar(255),
    "attachment_file2"              varchar(255),
    "attachment_file3"              varchar(255),
    "attachment_file4"              varchar(255),
    "attachment_file5"              varchar(255),
    "orderer_zip"                   varchar(255),
    "orderer_prefecture"            varchar(255),
    "orderer_municipality"          varchar(255),
    "orderer_address1"              varchar(255),
    "orderer_address2"              varchar(255),
    "orderer_name"                  varchar(255),
    "orderer_name_kana"             varchar(255),
    "orderer_phone"                 varchar(255),
    "orderer_email"                 varchar(255),
    "orderer_gender"                int2,
    "receiver_zip"                  varchar(255),
    "receiver_prefecture"           varchar(255),
    "receiver_municipality"         varchar(255),
    "receiver_address1"             varchar(255),
    "receiver_address2"             varchar(255),
    "receiver_name"                 varchar(255),
    "receiver_name_kana"            varchar(255),
    "receiver_phone"                varchar(255),
    "receiver_email"                varchar(255),
    "receiver_gender"               int2,
    "shipment_remarks"              varchar(255),
    "delivery_slip_remarks"         varchar(255),
    "orderer_remarks"               varchar(255),
    "other_remarks"                 varchar(255),
    "memo"                          text,
    "created_by"                    varchar(255),
    "created_at"                    timestamp,
    "modified_by"                   varchar(255),
    "modified_at"                   timestamp,
    "is_deleted"                    int2 DEFAULT 0,
    CONSTRAINT "pk_sp001_shipment_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_shipment_code" UNIQUE ("shipment_code")
);
COMMENT
ON COLUMN "public"."sp001_shipment"."id" IS '出荷依頼ID';
COMMENT
ON COLUMN "public"."sp001_shipment"."shipment_code" IS '出荷依頼コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."order_code" IS '受注依頼コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."origin_order_code" IS '外部受注依頼コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."sender_code" IS '依頼主コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."status" IS 'ステータス（1: 確認待ち 2: 出荷待ち）';
COMMENT
ON COLUMN "public"."sp001_shipment"."order_datetime" IS '注文日時';
COMMENT
ON COLUMN "public"."sp001_shipment"."shipment_request_datetime" IS '出荷依頼日時';
COMMENT
ON COLUMN "public"."sp001_shipment"."shipment_estimated_date" IS '出荷予定日';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_estimated_date" IS '到着予定日';
COMMENT
ON COLUMN "public"."sp001_shipment"."product_category_count" IS '商品種類数';
COMMENT
ON COLUMN "public"."sp001_shipment"."product_total_count" IS '商品総個数';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_desired_date" IS '配送希望日';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_desired_period" IS '配送希望時間帯';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_company" IS '配送会社';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_company_code" IS '配送会社コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_method" IS '配送方法';
COMMENT
ON COLUMN "public"."sp001_shipment"."payment_method" IS '支払方法';
COMMENT
ON COLUMN "public"."sp001_shipment"."total_price" IS '総金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."total_tax" IS '税金';
COMMENT
ON COLUMN "public"."sp001_shipment"."commission_fee" IS '手数料';
COMMENT
ON COLUMN "public"."sp001_shipment"."cash_on_delivery" IS '代引請求金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."product_total_price" IS '商品総金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_fee" IS '配送料';
COMMENT
ON COLUMN "public"."sp001_shipment"."other_fee" IS 'その他金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."tracking_number" IS '伝票番号';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_tracking_number_sent" IS '伝票番号連携フラグ（1: 連携済 0: 未連携）';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_invoice_included" IS '納品書同梱フラグ（1: 同梱する 0: 同梱しない）';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_invoice_printed_with_price" IS '納品書金額印字フラグ（1: 印字する 0: 印字しない）';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_description" IS '納品書記載内容';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_total_price" IS '納品書総金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_product_total_price" IS '納品書商品総金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_delivery_fee" IS '納品書配送料';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_commission_fee" IS '納品書手数料';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_adjustment_fee" IS '納品書調整金額';
COMMENT
ON COLUMN "public"."sp001_shipment"."invoice_pdf_identifier" IS '納品書PDF識別子';
COMMENT
ON COLUMN "public"."sp001_shipment"."receipt_pdf_identifier" IS '領収書PDF識別子';
COMMENT
ON COLUMN "public"."sp001_shipment"."description" IS '品名';
COMMENT
ON COLUMN "public"."sp001_shipment"."inspection_type" IS '検品タイプ（1: 通常検品 2: シリアル検品）';
COMMENT
ON COLUMN "public"."sp001_shipment"."billing_code" IS '請求コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."gmo_transaction_id" IS 'GMO取引ID';
COMMENT
ON COLUMN "public"."sp001_shipment"."subscription_id" IS '定期購入ID';
COMMENT
ON COLUMN "public"."sp001_shipment"."subscription_count" IS '定期購入回数';
COMMENT
ON COLUMN "public"."sp001_shipment"."next_subscription_date" IS '次回お届け予定日';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_oversea_shipment" IS '海外配送フラグ（1: 利用する 0: 利用しない）';
COMMENT
ON COLUMN "public"."sp001_shipment"."oversea_delivery_company" IS '海外配送会社';
COMMENT
ON COLUMN "public"."sp001_shipment"."oversea_delivery_company_code" IS '海外配送会社コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."oversea_delivery_method" IS '海外配送方法';
COMMENT
ON COLUMN "public"."sp001_shipment"."oversea_currency_code" IS '海外配送通貨コード';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_join_oversea_insurance" IS '海外配送損害保証加入希望（1: 希望する 0: 希望しない）';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiving_method" IS '受取方法';
COMMENT
ON COLUMN "public"."sp001_shipment"."gift_wrapping_count" IS 'ギフトラッピング個数';
COMMENT
ON COLUMN "public"."sp001_shipment"."gift_sent_by" IS 'ギフト送り主氏名';
COMMENT
ON COLUMN "public"."sp001_shipment"."box_count" IS '個口数';
COMMENT
ON COLUMN "public"."sp001_shipment"."box_size_code" IS '箱サイズコード';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_photograph_confirmed" IS '撮影確認フラグ（1: 確認済 0: 未確認）';
COMMENT
ON COLUMN "public"."sp001_shipment"."attachment_file1" IS '添付ファイル1';
COMMENT
ON COLUMN "public"."sp001_shipment"."attachment_file2" IS '添付ファイル2';
COMMENT
ON COLUMN "public"."sp001_shipment"."attachment_file3" IS '添付ファイル3';
COMMENT
ON COLUMN "public"."sp001_shipment"."attachment_file4" IS '添付ファイル4';
COMMENT
ON COLUMN "public"."sp001_shipment"."attachment_file5" IS '添付ファイル5';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_zip" IS '注文者郵便番号';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_prefecture" IS '注文者都道府県';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_municipality" IS '注文者市区町村';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_address1" IS '注文者住所1';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_address2" IS '注文者住所2';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_name" IS '注文者姓名';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_name_kana" IS '注文者姓名_カナ';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_phone" IS '注文者電話番号';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_email" IS '注文者メールアドレス';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_gender" IS '注文者性別（1: 男性 2: 女性 3: 未知）';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_zip" IS '配送先郵便番号';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_prefecture" IS '配送先都道府県';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_municipality" IS '配送先市区町村';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_address1" IS '配送先住所1';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_address2" IS '配送先住所2';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_name" IS '配送先姓名';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_name_kana" IS '配送先姓名_カナ';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_phone" IS '配送先電話番号';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_email" IS '配送先メールアドレス';
COMMENT
ON COLUMN "public"."sp001_shipment"."receiver_gender" IS '配送先性別（1: 男性 2: 女性 3: 未知）';
COMMENT
ON COLUMN "public"."sp001_shipment"."shipment_remarks" IS '出荷依頼特記事項';
COMMENT
ON COLUMN "public"."sp001_shipment"."delivery_slip_remarks" IS '送り状特記事項';
COMMENT
ON COLUMN "public"."sp001_shipment"."orderer_remarks" IS '購入者特記事項';
COMMENT
ON COLUMN "public"."sp001_shipment"."other_remarks" IS 'その他特記事項';
COMMENT
ON COLUMN "public"."sp001_shipment"."memo" IS '備考';
COMMENT
ON COLUMN "public"."sp001_shipment"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."sp001_shipment"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."sp001_shipment"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."sp001_shipment"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."sp001_shipment"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_shipment_code" ON "public"."sp001_shipment" IS '一意的な出荷コード';
COMMENT
ON TABLE "public"."sp001_shipment" IS '出荷依頼管理テーブル';

CREATE TABLE "public"."sp002_shipment_detail"
(
    "id"                   serial,
    "shipment_detail_code" varchar(20) NOT NULL,
    "shipment_code"        varchar(20) NOT NULL,
    "store_code"           varchar(20),
    "warehouse_code"       varchar(20),
    "product_code"         varchar(20),
    "reservation_status"   int2,
    "reserved_count"       int4,
    "tax_excluded_price"   int4,
    "tax_included_price"   int4,
    "individual_tax"       int4,
    "total_tax"            int4,
    "tax_rate"             int4,
    "count"                int4,
    "product_total_price"  int4,
    "barcode"              varchar(255),
    "options"              varchar(255),
    "serial_number"        varchar(255),
    "description"          varchar(255),
    "is_fragile_item"      int2,
    "cushioning_count"     int4,
    "cushioning_type"      int2,
    "inspection_type"      int2,
    "memo"                 text,
    "created_by"           varchar(255),
    "created_at"           timestamp,
    "modified_by"          varchar(255),
    "modified_at"          timestamp,
    "is_deleted"           int2 DEFAULT 0,
    CONSTRAINT "sp002_shipment_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."id" IS '出荷依頼明細ID';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."shipment_detail_code" IS '出荷依頼明細コード';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."shipment_code" IS '出荷依頼コード';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."reservation_status" IS '引当ステータス（1: 引当待ち 2: 引当済）';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."reserved_count" IS '引当済個数';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."tax_excluded_price" IS '税抜単価';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."tax_included_price" IS '税込単価';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."individual_tax" IS '個別税金';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."total_tax" IS '総税金';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."tax_rate" IS '税率';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."count" IS '個数';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."product_total_price" IS '総金額';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."barcode" IS '管理バーコード';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."options" IS 'オプション';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."serial_number" IS 'シリアル番号';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."description" IS '品名';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."is_fragile_item" IS '壊れやすい物';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."cushioning_count" IS '緩衝材数量';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."cushioning_type" IS '緩衝材タイプ';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."inspection_type" IS '検品タイプ（1: 通常検品 2: シリアル検品）';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."sp002_shipment_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."sp002_shipment_detail" IS '出荷依頼明細管理テーブル';

CREATE TABLE "public"."sp003_shipment_history"
(
    "id"                 serial,
    "shipment_code"      varchar(255),
    "store_code"         varchar(255),
    "warehouse_code"     varchar(255),
    "operator"           varchar(255),
    "operation_code"     varchar(255),
    "operation_datetime" timestamp,
    "memo"               text,
    "created_by"         varchar(255),
    "created_at"         timestamp,
    "modified_by"        varchar(255),
    "modified_at"        timestamp,
    "is_deleted"         int2 DEFAULT 0,
    CONSTRAINT "pk_sp003_shipment_history_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."sp003_shipment_history"."id" IS '出荷依頼履歴ID';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."shipment_code" IS '出荷依頼コード';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."operator" IS '作業者';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."operation_code" IS '作業コード';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."operation_datetime" IS '作業時間';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."memo" IS '備考';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."sp003_shipment_history"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."sp003_shipment_history" IS '出荷依頼履歴管理テーブル';

CREATE TABLE "public"."sp100_picking"
(
    "id"           serial,
    "picking_code" varchar(255),
    "memo"         text,
    "created_by"   varchar(255),
    "created_at"   timestamp,
    "modified_by"  varchar(255),
    "modified_at"  timestamp,
    "is_deleted"   int2 DEFAULT 0,
    CONSTRAINT "pk_sp100_picking_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_picking_code" UNIQUE ("picking_code")
);
COMMENT
ON COLUMN "public"."sp100_picking"."memo" IS '備考';
COMMENT
ON COLUMN "public"."sp100_picking"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."sp100_picking"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."sp100_picking"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."sp100_picking"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."sp100_picking"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_picking_code" ON "public"."sp100_picking" IS '一意的なピッキングコード';
COMMENT
ON TABLE "public"."sp100_picking" IS '出荷ピッキング作業管理テーブル';

CREATE TABLE "public"."sp101_picking_detail"
(
    "id"           serial,
    "picking_code" varchar(255),
    "memo"         text,
    "created_by"   varchar(255),
    "created_at"   timestamp,
    "modified_by"  varchar(255),
    "modified_at"  timestamp,
    "is_deleted"   int2 DEFAULT 0,
    CONSTRAINT "pk_sp101_picking_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."sp101_picking_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."sp101_picking_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."sp101_picking_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."sp101_picking_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."sp101_picking_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."sp101_picking_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."sp101_picking_detail" IS '出荷ピッキング作業詳細管理テーブル';

CREATE TABLE "public"."st001_stock"
(
    "id"                  serial,
    "store_code"          varchar(255),
    "warehouse_code"      varchar(255),
    "product_code"        varchar(255),
    "actual_stock"        varchar(255),
    "allocated_stock"     varchar(255),
    "dead_stock"          varchar(255),
    "replenishment_stock" varchar(255),
    "size_code"           int2,
    "weight"              float8,
    "memo"                text,
    "created_by"          varchar(255),
    "created_at"          timestamp,
    "modified_by"         varchar(255),
    "modified_at"         timestamp,
    "is_deleted"          int2 DEFAULT 0,
    CONSTRAINT "pk_st001_stock_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."st001_stock"."id" IS '在庫ID';
COMMENT
ON COLUMN "public"."st001_stock"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."st001_stock"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."st001_stock"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."st001_stock"."actual_stock" IS '実際在庫数';
COMMENT
ON COLUMN "public"."st001_stock"."allocated_stock" IS '出荷依頼中数';
COMMENT
ON COLUMN "public"."st001_stock"."dead_stock" IS '出荷依頼不可数';
COMMENT
ON COLUMN "public"."st001_stock"."replenishment_stock" IS '補充設定在庫数';
COMMENT
ON COLUMN "public"."st001_stock"."size_code" IS 'サイズコード';
COMMENT
ON COLUMN "public"."st001_stock"."weight" IS '重量';
COMMENT
ON COLUMN "public"."st001_stock"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st001_stock"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st001_stock"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st001_stock"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st001_stock"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st001_stock"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."st001_stock" IS '在庫管理テーブル';

CREATE TABLE "public"."st002_stock_history"
(
    "id"                     serial,
    "store_code"             varchar(255),
    "warehouse_code"         varchar(255),
    "product_code"           varchar(255),
    "operation_code"         int2,
    "operation_datetime"     timestamp,
    "operator"               varchar,
    "count_before_operation" int4,
    "count_after_operation"  int4,
    "count"                  int4,
    "memo"                   text,
    "created_by"             varchar(255),
    "created_at"             timestamp,
    "modified_by"            varchar(255),
    "modified_at"            timestamp,
    "is_deleted"             int2 DEFAULT 0,
    CONSTRAINT "pk_st002_stock_history_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."st002_stock_history"."id" IS '在庫履歴ID';
COMMENT
ON COLUMN "public"."st002_stock_history"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."st002_stock_history"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."st002_stock_history"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."st002_stock_history"."operation_code" IS '作業コード（1: 入荷 2: 出荷 3: 在庫移動）';
COMMENT
ON COLUMN "public"."st002_stock_history"."operation_datetime" IS '作業日時';
COMMENT
ON COLUMN "public"."st002_stock_history"."operator" IS '作業担当';
COMMENT
ON COLUMN "public"."st002_stock_history"."count_before_operation" IS '操作前数量';
COMMENT
ON COLUMN "public"."st002_stock_history"."count_after_operation" IS '操作後数量';
COMMENT
ON COLUMN "public"."st002_stock_history"."count" IS '操作個数';
COMMENT
ON COLUMN "public"."st002_stock_history"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st002_stock_history"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st002_stock_history"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st002_stock_history"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st002_stock_history"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st002_stock_history"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."st002_stock_history" IS '在庫履歴管理テーブル';

CREATE TABLE "public"."st100_location"
(
    "id"             serial,
    "location_code"  varchar(255),
    "location_name"  varchar(255),
    "warehouse_code" varchar(255),
    "memo"           text,
    "created_by"     varchar(255),
    "created_at"     timestamp,
    "modified_by"    varchar(255),
    "modified_at"    timestamp,
    "is_deleted"     int2 DEFAULT 0,
    CONSTRAINT "pk_st100_location_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_location_code" UNIQUE ("location_code")
);
COMMENT
ON COLUMN "public"."st100_location"."id" IS 'ロケーションID';
COMMENT
ON COLUMN "public"."st100_location"."location_code" IS 'ロケーションコード';
COMMENT
ON COLUMN "public"."st100_location"."location_name" IS 'ロケーション名称';
COMMENT
ON COLUMN "public"."st100_location"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."st100_location"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st100_location"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st100_location"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st100_location"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st100_location"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st100_location"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_location_code" ON "public"."st100_location" IS '一意的なロケーションコード';
COMMENT
ON TABLE "public"."st100_location" IS 'ロケーション管理テーブル';

CREATE TABLE "public"."st101_location_detail"
(
    "id"              serial,
    "location_code"   varchar(255),
    "product_code"    varchar(255),
    "actual_stock"    varchar(255),
    "allocated_stock" varchar(255),
    "dead_stock"      varchar(255),
    "expiration_date" date,
    "memo"            text,
    "created_by"      varchar(255),
    "created_at"      timestamp,
    "modified_by"     varchar(255),
    "modified_at"     timestamp,
    "is_deleted"      int2 DEFAULT 0,
    CONSTRAINT "pk_st101_location_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."st101_location_detail"."id" IS 'ロケーション明細ID';
COMMENT
ON COLUMN "public"."st101_location_detail"."location_code" IS 'ロケーションコード';
COMMENT
ON COLUMN "public"."st101_location_detail"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."st101_location_detail"."actual_stock" IS '実在庫数';
COMMENT
ON COLUMN "public"."st101_location_detail"."allocated_stock" IS '出荷依頼中数';
COMMENT
ON COLUMN "public"."st101_location_detail"."dead_stock" IS '出荷不可数';
COMMENT
ON COLUMN "public"."st101_location_detail"."expiration_date" IS '賞味期限';
COMMENT
ON COLUMN "public"."st101_location_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st101_location_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st101_location_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st101_location_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st101_location_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st101_location_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."st101_location_detail" IS 'ロケーション明細管理テーブル';

CREATE TABLE "public"."st102_location_history"
(
    "id"          serial,
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "pk_st102_location_history_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."st102_location_history"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st102_location_history"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st102_location_history"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st102_location_history"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st102_location_history"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st102_location_history"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';

CREATE TABLE "public"."st200_stocktaking"
(
    "id"               serial,
    "stocktaking_code" varchar(255),
    "memo"             text,
    "created_by"       varchar(255),
    "created_at"       timestamp,
    "modified_by"      varchar(255),
    "modified_at"      timestamp,
    "is_deleted"       int2 DEFAULT 0,
    CONSTRAINT "pk_st200_stocktaking_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_stocktaking_code" UNIQUE ("stocktaking_code")
);
COMMENT
ON COLUMN "public"."st200_stocktaking"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st200_stocktaking"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st200_stocktaking"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st200_stocktaking"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st200_stocktaking"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st200_stocktaking"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_stocktaking_code" ON "public"."st200_stocktaking" IS '一意的な棚卸作業コード';
COMMENT
ON TABLE "public"."st200_stocktaking" IS '棚卸作業管理テーブル';

CREATE TABLE "public"."st201_stocktaking_detail"
(
    "id"               serial,
    "stocktaking_code" varchar(255),
    "memo"             text,
    "created_by"       varchar(255),
    "created_at"       timestamp,
    "modified_by"      varchar(255),
    "modified_at"      timestamp,
    "is_deleted"       int2 DEFAULT 0,
    CONSTRAINT "pk_st201_stocktaking_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."st201_stocktaking_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."st201_stocktaking_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."st201_stocktaking_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."st201_stocktaking_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."st201_stocktaking_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."st201_stocktaking_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."st201_stocktaking_detail" IS '棚卸作業詳細管理テーブル';

CREATE TABLE "public"."template"
(
    "id"          serial,
    "memo"        text,
    "created_by"  varchar(255),
    "created_at"  timestamp,
    "modified_by" varchar(255),
    "modified_at" timestamp,
    "is_deleted"  int2 DEFAULT 0,
    CONSTRAINT "_copy_2_copy_1_copy_14" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."template"."memo" IS '備考';
COMMENT
ON COLUMN "public"."template"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."template"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."template"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."template"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."template"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';

CREATE TABLE "public"."wh001_warehousing"
(
    "id"                            serial,
    "warehousing_code"              varchar(20),
    "store_code"                    varchar(20),
    "warehouse_code"                varchar(20),
    "warehousing_request_date"      date,
    "warehousing_estimated_date"    date,
    "arrival_date"                  date,
    "inspection_type"               int2,
    "inspection_start_date"         timestamp,
    "inspection_end_date"           timestamp,
    "tracking_number"               varchar(255) NOT NULL,
    "product_kind_requested_amount" int4,
    "product_requested_amount"      int4,
    "product_kind_actual_amount"    varchar(255),
    "product_actual_amount"         varchar(255),
    "status"                        int2,
    "memo"                          text,
    "created_by"                    varchar(255),
    "created_at"                    timestamp,
    "modified_by"                   varchar(255),
    "modified_at"                   timestamp,
    "is_deleted"                    int2 DEFAULT 0,
    CONSTRAINT "pk_wh001_warehousing_id" PRIMARY KEY ("id"),
    CONSTRAINT "uk_warehousing_code" UNIQUE ("warehousing_code")
);
COMMENT
ON COLUMN "public"."wh001_warehousing"."id" IS '入庫依頼ID';
COMMENT
ON COLUMN "public"."wh001_warehousing"."warehousing_code" IS '入庫依頼コード';
COMMENT
ON COLUMN "public"."wh001_warehousing"."store_code" IS '店舗コード';
COMMENT
ON COLUMN "public"."wh001_warehousing"."warehouse_code" IS '倉庫コード';
COMMENT
ON COLUMN "public"."wh001_warehousing"."warehousing_request_date" IS '入庫依頼日';
COMMENT
ON COLUMN "public"."wh001_warehousing"."warehousing_estimated_date" IS '入庫予定日';
COMMENT
ON COLUMN "public"."wh001_warehousing"."arrival_date" IS '着荷日';
COMMENT
ON COLUMN "public"."wh001_warehousing"."inspection_type" IS '検品タイプ（1: 通常検品 2: シリアル検品）';
COMMENT
ON COLUMN "public"."wh001_warehousing"."inspection_start_date" IS '検品開始日時';
COMMENT
ON COLUMN "public"."wh001_warehousing"."inspection_end_date" IS '検品終了日時';
COMMENT
ON COLUMN "public"."wh001_warehousing"."tracking_number" IS '伝票番号';
COMMENT
ON COLUMN "public"."wh001_warehousing"."product_kind_requested_amount" IS '依頼商品種類総数';
COMMENT
ON COLUMN "public"."wh001_warehousing"."product_requested_amount" IS '依頼商品総数';
COMMENT
ON COLUMN "public"."wh001_warehousing"."product_kind_actual_amount" IS '実績商品種類総数';
COMMENT
ON COLUMN "public"."wh001_warehousing"."product_actual_amount" IS '実績商品総数';
COMMENT
ON COLUMN "public"."wh001_warehousing"."status" IS 'ステータス（1: 入庫まち 2: 検品中 3: 入庫取消 4: 入庫完了 5: 一時保存）';
COMMENT
ON COLUMN "public"."wh001_warehousing"."memo" IS '備考';
COMMENT
ON COLUMN "public"."wh001_warehousing"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."wh001_warehousing"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."wh001_warehousing"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."wh001_warehousing"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."wh001_warehousing"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON CONSTRAINT "uk_warehousing_code" ON "public"."wh001_warehousing" IS '一意的な入荷コード';
COMMENT
ON TABLE "public"."wh001_warehousing" IS '入庫依頼管理テーブル';

CREATE TABLE "public"."wh002_warehousing_detail"
(
    "id"                          serial,
    "warehousing_code"            varchar(20),
    "product_code"                varchar(20),
    "location_code"               varchar(255),
    "lot_number"                  int4,
    "warehousing_estimated_count" int4,
    "warehousing_actual_count"    int4,
    "expiration_date"             date,
    "is_shipment_available"       int2,
    "status"                      int2,
    "memo"                        text,
    "created_by"                  varchar(255),
    "created_at"                  timestamp,
    "modified_by"                 varchar(255),
    "modified_at"                 timestamp,
    "is_deleted"                  int2 DEFAULT 0,
    CONSTRAINT "pk_wh002_warehousing_detail_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."id" IS '入庫依頼明細ID';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."warehousing_code" IS '入庫依頼コード';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."product_code" IS '商品コード';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."location_code" IS 'ロケーションコード';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."lot_number" IS 'ロット番号';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."warehousing_estimated_count" IS '入庫予定数';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."warehousing_actual_count" IS '入庫実績数';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."expiration_date" IS '賞味期間';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."is_shipment_available" IS '出荷可否（1: 正常出荷 2: 出荷不可）';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."status" IS 'ステータス（1: 入庫待ち 2: 検品中 3: 入庫取消 4: 入庫完了 5: 一時保存）';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."memo" IS '備考';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."wh002_warehousing_detail"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."wh002_warehousing_detail" IS '入庫依頼明細管理テーブル';

CREATE TABLE "public"."wh003_warehousing_history"
(
    "id"                         serial,
    "warehousing_code"           varchar(20),
    "warehousing_request_date"   date,
    "warehousing_estimated_date" date,
    "inspection_type"            int2,
    "operation_type"             varchar(255),
    "count"                      int4,
    "memo"                       text,
    "created_by"                 varchar(255),
    "created_at"                 timestamp,
    "modified_by"                varchar(255),
    "modified_at"                timestamp,
    "is_deleted"                 int2 DEFAULT 0,
    CONSTRAINT "pk_wh003_warehousing_history_id" PRIMARY KEY ("id")
);
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."id" IS '入庫依頼履歴ID';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."warehousing_code" IS '入庫依頼コード';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."warehousing_request_date" IS '入庫依頼日';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."warehousing_estimated_date" IS '入庫予定日';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."inspection_type" IS '検品タイプ（1: 通常検品 2: シリアル検品）';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."operation_type" IS '入庫操作区分（1: 入庫開始 2: 検品完了 3: 入庫完了 4: 一時保存）';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."count" IS '操作個数';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."memo" IS '備考';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."created_by" IS '作成者';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."created_at" IS '作成日時';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."modified_by" IS '更新者';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."modified_at" IS '更新日時';
COMMENT
ON COLUMN "public"."wh003_warehousing_history"."is_deleted" IS '削除フラグ（0: 未削除 1: 削除済）';
COMMENT
ON TABLE "public"."wh003_warehousing_history" IS '入庫依頼履歴管理テーブル';

ALTER TABLE "public"."es001_yamato"
    ADD CONSTRAINT "fk_es001_yamato_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es002_sagawa"
    ADD CONSTRAINT "fk_es002_sagawa_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es003_fukuyama"
    ADD CONSTRAINT "fk_es003_fukuyama_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es004_seino"
    ADD CONSTRAINT "fk_es004_seino_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es100_makeshop"
    ADD CONSTRAINT "fk_es100_makeshop_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es101_colorme"
    ADD CONSTRAINT "fk_es101_colorme_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es102_rakuten"
    ADD CONSTRAINT "fk_es102_rakuten_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es103_yahoo"
    ADD CONSTRAINT "fk_es103_yahoo_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es104_qoo10"
    ADD CONSTRAINT "fk_es104_qoo10_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es105_eccube"
    ADD CONSTRAINT "fk_es105_eccube_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es106_ecforce"
    ADD CONSTRAINT "fk_es106_ecforce_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es107_base"
    ADD CONSTRAINT "fk_es107_base_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es108_shopify"
    ADD CONSTRAINT "fk_es108_shopify_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."es109_next_engine"
    ADD CONSTRAINT "fk_es109_next_engine_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."fc003_macro_detail"
    ADD CONSTRAINT "fk_fc003_macro_detail_fc002_macro_1" FOREIGN KEY ("macro_code") REFERENCES "public"."fc002_macro" ("macro_code");
ALTER TABLE "public"."fc004_csv_template"
    ADD CONSTRAINT "fk_ms004_csv_template_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."fc005_csv_template_detail"
    ADD CONSTRAINT "fk_ms005_csv_template_detail_ms004_csv_template_1" FOREIGN KEY ("template_code") REFERENCES "public"."fc004_csv_template" ("template_code");
ALTER TABLE "public"."mg002_warehouse"
    ADD CONSTRAINT "fk_mg003_warehouse_mg003_warehouse_1" FOREIGN KEY ("group_code") REFERENCES "public"."mg001_group" ("group_code");
ALTER TABLE "public"."mg100_user_relation"
    ADD CONSTRAINT "fk_mg100_user_relation_mg100_user_relation_1" FOREIGN KEY ("user_code") REFERENCES "public"."mg004_user" ("user_code");
ALTER TABLE "public"."mg101_store_relation"
    ADD CONSTRAINT "fk_mg101_client_relation_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."mg200_delivery_mapping"
    ADD CONSTRAINT "fk_mg200_delivery_mapping_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."mg201_delivery_period_mapping"
    ADD CONSTRAINT "fk_mg201_delivery_period_mapping_mg001_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."mg202_delivery_info"
    ADD CONSTRAINT "fk_mg202_delivery_info_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."mg203_sender"
    ADD CONSTRAINT "fk_mg005_sponsor_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."od001_order"
    ADD CONSTRAINT "fk_od001_order_od003_order_history_1" FOREIGN KEY ("order_history_code") REFERENCES "public"."od003_order_history" ("history_code");
ALTER TABLE "public"."od001_order"
    ADD CONSTRAINT "fk_od001_order_mg003_store_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."od002_order_detail"
    ADD CONSTRAINT "fk_od002_order_detail_od001_order_1" FOREIGN KEY ("order_code") REFERENCES "public"."od001_order" ("order_code");
ALTER TABLE "public"."od004_order_error"
    ADD CONSTRAINT "fk_od004_order_error_od003_order_history_1" FOREIGN KEY ("history_id") REFERENCES "public"."od003_order_history" ("id");
ALTER TABLE "public"."pd001_product"
    ADD CONSTRAINT "fk_pd001_product_mg001_client_1" FOREIGN KEY ("store_code") REFERENCES "public"."mg003_store" ("store_code");
ALTER TABLE "public"."pd002_product_detail"
    ADD CONSTRAINT "fk_pd002_product_detail_pd002_product_detail_1" FOREIGN KEY ("product_code") REFERENCES "public"."pd001_product" ("product_code");
ALTER TABLE "public"."pd003_product_mapping"
    ADD CONSTRAINT "fk_pd003_product_mapping_pd003_product_mapping_1" FOREIGN KEY ("product_code") REFERENCES "public"."pd001_product" ("product_code");
ALTER TABLE "public"."sp002_shipment_detail"
    ADD CONSTRAINT "fk_sp002_shipment_detail_sp001_shipment_1" FOREIGN KEY ("shipment_code") REFERENCES "public"."sp001_shipment" ("shipment_code");
ALTER TABLE "public"."sp003_shipment_history"
    ADD CONSTRAINT "fk_sp003_shipment_history_sp001_shipment_1" FOREIGN KEY ("shipment_code") REFERENCES "public"."sp001_shipment" ("shipment_code");
ALTER TABLE "public"."sp101_picking_detail"
    ADD CONSTRAINT "fk_sp101_picking_detail_sp100_picking_1" FOREIGN KEY ("picking_code") REFERENCES "public"."sp100_picking" ("picking_code");
ALTER TABLE "public"."st101_location_detail"
    ADD CONSTRAINT "fk_st101_location_detail_st100_location_1" FOREIGN KEY ("location_code") REFERENCES "public"."st100_location" ("location_code");
ALTER TABLE "public"."st201_stocktaking_detail"
    ADD CONSTRAINT "fk_st200_stocktaking_copy_1_st200_stocktaking_1" FOREIGN KEY ("stocktaking_code") REFERENCES "public"."st200_stocktaking" ("stocktaking_code");
ALTER TABLE "public"."wh002_warehousing_detail"
    ADD CONSTRAINT "fk_wh002_warehousing_detail_wh001_warehousing_1" FOREIGN KEY ("warehousing_code") REFERENCES "public"."wh001_warehousing" ("warehousing_code");
ALTER TABLE "public"."wh003_warehousing_history"
    ADD CONSTRAINT "fk_wh003_warehousing_history_wh001_warehousing_1" FOREIGN KEY ("warehousing_code") REFERENCES "public"."wh001_warehousing" ("warehousing_code");

