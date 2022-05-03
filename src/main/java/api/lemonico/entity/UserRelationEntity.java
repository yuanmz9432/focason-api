/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.entity;



import api.lemonico.core.attribute.ID;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.seasar.doma.*;

/**
 * ユーザー所属情報エンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "user_relation")
public class UserRelationEntity extends LcEntity
{
    /** 自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<UserRelationEntity> id;
    /** UUID */
    @Column(name = "uuid")
    String uuid;
    /** 所属タイプ（1: ストア 2: 倉庫 3: 管理） */
    @Column(name = "relation_type")
    Byte relationType;
    /** 所属コード */
    @Column(name = "relation_code")
    String relationCode;
    /** ロール（1: 管理者 2: 作業者 3: メンテナー） */
    @Column(name = "role")
    Integer role;
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
