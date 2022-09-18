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
 * ユーザーエンティティ
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
    /** ユーザー名 */
    @Column(name = "username")
    String username;
    /** 性別（1:男性 2:女性） */
    @Column(name = "gender")
    Integer gender;
    /** メールアドレス（ログインID） */
    @Column(name = "email")
    String email;
    /** パスワード */
    @Column(name = "password")
    String password;
    /** タイプ（1:シルバー 2:ゴールド 3:プレミアム 4:退会） */
    @Column(name = "type")
    Integer type;
    /** 電話番号 */
    @Column(name = "phone")
    String phone;
    /** 個人ページ */
    @Column(name = "url")
    String url;
    /** 個人ページ画像 */
    @Column(name = "profile_image")
    String profileImage;
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
    @Column(name = "address")
    String address;
    /** ビル */
    @Column(name = "building")
    String building;
    /** おすすめ情報読込 */
    @Column(name = "is_subscribed")
    Integer isSubscribed;
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