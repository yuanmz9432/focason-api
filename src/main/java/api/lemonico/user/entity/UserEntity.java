/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.entity;



import api.lemonico.core.attribute.ID;
import api.lemonico.core.entity.LcEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.seasar.doma.*;

/**
 * ユーザエンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "user")
public class UserEntity extends LcEntity
{
    /** 自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<UserEntity> id;
    /** UUID */
    @Column(name = "uuid")
    String uuid;
    /** ユーザ名 */
    @Column(name = "username")
    String username;
    /** 性別（１：男性、２：女性、９：不明） */
    @Column(name = "gender")
    Integer gender;
    /** メールアドレス（ログインID） */
    @Column(name = "email")
    String email;
    /** パスワード */
    @Column(name = "password")
    String password;
    /** ステータス（１：有効、０：無効） */
    @Column(name = "status")
    Integer status;
    /** タイプ（１：本番、２：デモ、９：スーパーユーザ） */
    @Column(name = "type")
    Integer type;
    /** 電話番号 */
    @Column(name = "phone")
    String phone;
    /** 郵便番号 */
    @Column(name = "zip")
    String zip;
    /** 都道府県 */
    @Column(name = "prefecture")
    Integer prefecture;
    /** 市区町村 */
    @Column(name = "municipality")
    String municipality;
    /** 字・町目 */
    @Column(name = "address1")
    String address1;
    /** 部屋 */
    @Column(name = "address2")
    String address2;
    /** 作成者 */
    @Column(name = "created_by")
    String createdBy;
    /** 作成日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;
    /** 更新者 */
    @Column(name = "modified_by")
    String modifiedBy;
    /** 更新日時 */
    @Column(name = "modified_at")
    LocalDateTime modifiedAt;
    /** 削除フラグ（0: 未削除 1: 削除済） */
    @Column(name = "is_deleted")
    Integer isDeleted;
}
