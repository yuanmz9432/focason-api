/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.resource;



import com.focason.api.core.attribute.ID;
import com.focason.api.user.entity.UserAuthorityEntity;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ユーザ権限リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class UserAuthorityResource
{

    /** 自動採番ID */
    private final ID<UserAuthorityEntity> id;

    /** UUID */
    private final String uuid;

    /** 権限コード */
    private final String authorityCode;

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
     * @param entity ユーザ権限エンティティ
     */
    public UserAuthorityResource(UserAuthorityEntity entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.authorityCode = entity.getAuthorityCode();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ユーザ権限エンティティ
     */
    public UserAuthorityEntity toEntity() {
        return UserAuthorityEntity.builder()
            .id(id)
            .uuid(uuid)
            .authorityCode(authorityCode)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
