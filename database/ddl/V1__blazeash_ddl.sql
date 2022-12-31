DROP TABLE IF EXISTS `blazeash`.`authority`;
DROP TABLE IF EXISTS `blazeash`.`company`;
DROP TABLE IF EXISTS `blazeash`.`product_detail`;
DROP TABLE IF EXISTS `blazeash`.`product_header`;
DROP TABLE IF EXISTS `blazeash`.`store`;
DROP TABLE IF EXISTS `blazeash`.`store_dependent`;
DROP TABLE IF EXISTS `blazeash`.`user`;
DROP TABLE IF EXISTS `blazeash`.`user_authority`;
DROP TABLE IF EXISTS `blazeash`.`user_department`;
DROP TABLE IF EXISTS `blazeash`.`warehouse`;

CREATE TABLE `blazeash`.`authority`  (
  `id` int(11) NOT NULL COMMENT '自動採番ID',
  `authority_code` varchar(255) NOT NULL COMMENT '権限コード',
  `authority_name` varchar(255) NULL COMMENT '権限名称',
  `created_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '権限マスタ' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`company`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `company_code` varchar(255) NOT NULL COMMENT '会社コード',
  `company_name` varchar(255) NULL COMMENT '会社名称',
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '会社' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`product_detail`  (
  `id` int(11) NOT NULL COMMENT '自動採番ID',
  `spu` varchar(255) NOT NULL COMMENT '商品SPUコード',
  `sku` varchar(255) NOT NULL COMMENT '商品SKUコード',
  `name` varchar(255) NULL COMMENT '商品名称',
  `created_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品詳細' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`product_header`  (
  `id` int(11) NOT NULL COMMENT '自動採番ID',
  `spu` varchar(255) NOT NULL COMMENT '商品SPUコード',
  `name` varchar(255) NULL COMMENT '商品名称',
  `type` int(2) NOT NULL COMMENT '商品タイプ（）',
  `created_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '商品ヘッダ' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`store`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `store_code` varchar(255) NOT NULL COMMENT 'ストアコード',
  `store_name` varchar(255) NOT NULL COMMENT 'ストア名称',
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ストア' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`store_dependent`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `store_code` varchar(255) NOT NULL COMMENT 'ストアコード',
  `warehouse_code` varchar(255) NOT NULL COMMENT '倉庫コード',
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ストア所属' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`user`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `uuid` varchar(36) NOT NULL COMMENT 'UUID',
  `username` varchar(255) NULL COMMENT 'ユーザ名',
  `gender` int(11) NULL DEFAULT 9 COMMENT '性別（１：男性、２：女性、９：不明）',
  `email` varchar(255) NOT NULL COMMENT 'メールアドレス（ログインID）',
  `password` varchar(255) NOT NULL COMMENT 'パスワード',
  `status` int(11) NULL DEFAULT 1 COMMENT 'ステータス（１：有効、０：無効）',
  `type` int(11) NOT NULL COMMENT 'タイプ（１：管理者、２：スタッフ、９：スーパーユーザ）',
  `created_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ユーザ' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`user_authority`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `uuid` varchar(36) NOT NULL COMMENT 'UUID',
  `authority_code` varchar(255) NOT NULL COMMENT '権限コード',
  `created_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ユーザ権限' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`user_department`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `uuid` varchar(36) NOT NULL COMMENT 'UUID',
  `department_code` varchar(255) NOT NULL COMMENT '部署コード',
  `department_type`  int NOT NULL COMMENT '部署タイプ',
  `role_type` int(11) NOT NULL COMMENT 'ロールタイプ（１：管理者、２：スタッフ）',
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = 'ユーザ部署' ROW_FORMAT = Dynamic;

CREATE TABLE `blazeash`.`warehouse`  (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '自動採番ID',
  `warehouse_code` varchar(255) NOT NULL COMMENT '倉庫コード',
  `warehouse_name` varchar(255) NOT NULL COMMENT '倉庫名称',
  `company_code` varchar(255) NOT NULL COMMENT '会社コード',
  `created_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '作成者',
  `created_at` datetime NOT NULL COMMENT '作成日時',
  `modified_by` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'admin' COMMENT '更新者',
  `modified_at` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日時',
  `is_deleted`  int NOT NULL COMMENT '削除フラグ（0: 未削除 1: 削除済）',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci COMMENT = '倉庫' ROW_FORMAT = Dynamic;

