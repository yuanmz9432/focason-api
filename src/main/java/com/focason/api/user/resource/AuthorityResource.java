/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.user.resource;



import com.focason.api.core.attribute.ID;
import com.focason.api.user.entity.AuthorityEntity;
import java.time.LocalDateTime;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 権限マスタリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class AuthorityResource
{

    /** 自動採番ID */
    private final ID<AuthorityEntity> id;

    /** 権限コード */
    private final String authorityCode;

    /** 権限名称 */
    private final String authorityName;

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
     * @param entity 権限マスタエンティティ
     */
    public AuthorityResource(AuthorityEntity entity) {
        this.id = entity.getId();
        this.authorityCode = entity.getAuthorityCode();
        this.authorityName = entity.getAuthorityName();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 権限マスタエンティティ
     */
    public AuthorityEntity toEntity() {
        return AuthorityEntity.builder()
            .id(id)
            .authorityCode(authorityCode)
            .authorityName(authorityName)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
