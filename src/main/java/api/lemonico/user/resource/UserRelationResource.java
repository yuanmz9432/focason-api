/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.user.entity.UserRelationEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ユーザー所属情報リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class UserRelationResource
{

    /** 自動採番ID */
    private final ID<UserRelationEntity> id;

    /** UUID */
    private final String uuid;

    /** 所属タイプ（1: ストア 2: 倉庫 3: 管理） */
    private final Byte relationType;

    /** 所属コード */
    private final String relationCode;

    /** ロール */
    private final Integer role;

    /** 作成者 */
    private final String createdBy;

    /** 作成日時 */
    private final LocalDateTime createdAt;

    /** 更新者 */
    private final String modifiedBy;

    /** 更新日時 */
    private final LocalDateTime modifiedAt;

    /** 削除フラグ（0: 未削除 1: 削除済） */
    private final Integer isDeleted;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity ユーザー所属情報エンティティ
     */
    public UserRelationResource(UserRelationEntity entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.relationType = entity.getRelationType();
        this.relationCode = entity.getRelationCode();
        this.role = entity.getRole();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ユーザー所属情報エンティティ
     */
    public UserRelationEntity toEntity() {
        return UserRelationEntity.builder()
            .id(id)
            .uuid(uuid)
            .relationType(relationType)
            .relationCode(relationCode)
            .role(role)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
