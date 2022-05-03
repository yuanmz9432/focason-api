/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.UserRoleEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ユーザーロール関連情報リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class UserRoleResource
{

    /** 自動採番ID */
    private final ID<UserRoleEntity> id;

    /** ユーザーID */
    private final Integer userId;

    /** ロールID */
    private final Integer roleId;

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
     * @param entity ユーザーロール関連情報エンティティ
     */
    public UserRoleResource(UserRoleEntity entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.roleId = entity.getRoleId();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ユーザーロール関連情報エンティティ
     */
    public UserRoleEntity toEntity() {
        return UserRoleEntity.builder()
            .id(id)
            .userId(userId)
            .roleId(roleId)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
