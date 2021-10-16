/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.entity;



import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcEntity;
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
public class User extends LcEntity
{

    /** ユーザーID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<User> id;

    /** 姓 */
    @Column(name = "first_name")
    String firstName;

    /** 名 */
    @Column(name = "last_name")
    String lastName;

    /** メールアドレス */
    @Column(name = "email")
    String email;

    /** パスワード */
    @Column(name = "password")
    String password;

    /** ステータス（1:通常 2:ブラックユーザー 3:退会） */
    @Column(name = "status")
    Integer status;

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
