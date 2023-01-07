DROP TABLE IF EXISTS `focason`.`authority`;
DROP TABLE IF EXISTS `focason`.`user`;
DROP TABLE IF EXISTS `focason`.`user_authority`;

CREATE TABLE `focason`.`authority`  (
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

CREATE TABLE `focason`.`user`  (
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

CREATE TABLE `focason`.`user_authority`  (
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

