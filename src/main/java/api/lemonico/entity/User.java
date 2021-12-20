/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.entity;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.LcEntity;
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
@Table(name = "User")
public class User extends LcEntity
{
    /** クライアントの自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<User> id;
    /** ユーザーコード */
    @Column(name = "user_code")
    String userCode;
    /** 姓 */
    @Column(name = "first_name")
    String firstName;
    /** 名 */
    @Column(name = "last_name")
    String lastName;
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
    /** 画像 */
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
    @Column(name = "subscribe")
    Integer subscribe;
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
    /** 削除フラグ（退会から一定時間経過後に削除状態になる） */
    @Column(name = "is_deleted")
    Integer isDeleted;
}
