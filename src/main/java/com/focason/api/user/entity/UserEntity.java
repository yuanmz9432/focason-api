/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.entity;



import com.focason.api.core.attribute.ID;
import com.focason.api.core.entity.FsEntity;
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
public class UserEntity extends FsEntity
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
    /** タイプ（１：管理者、２：スタッフ、９：スーパーユーザ） */
    @Column(name = "type")
    Integer type;
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
