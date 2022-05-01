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
 * 倉庫ストア関連情報エンティティ
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
    /**
     * 所属コード
     * 倉庫コードの場合、倉庫関連のストア情報がすべて参照できる
     * ストアコードの場合、対象ストアの情報のみ参照できる
     */
    @Column(name = "relation_code")
    String relationCode;
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
    /** 所属タイプ（1: ストア 2: 倉庫 3: 管理） */
    @Column(name = "relation_type")
    Byte relationType;
}
