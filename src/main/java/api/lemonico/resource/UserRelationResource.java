/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.UserRelationEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 倉庫ストア関連情報リソース
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

    /**
     * 所属コード
     * 倉庫コードの場合、倉庫関連のストア情報がすべて参照できる
     * ストアコードの場合、対象ストアの情報のみ参照できる
     */
    private final String relationCode;

    /** 作成者 */
    private final String createdBy;

    /** 作成日時 */
    private final LocalDateTime createdAt;

    /** 更新者 */
    private final String modifiedBy;

    /** 更新日時 */
    private final LocalDateTime modifiedAt;

    /** 削除フラグ（退会から一定時間経過後に削除状態になる） */
    private final Integer isDeleted;

    /** 所属タイプ（1: ストア 2: 倉庫 3: 管理） */
    private final Byte relationType;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity 倉庫ストア関連情報エンティティ
     */
    public UserRelationResource(UserRelationEntity entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.relationCode = entity.getRelationCode();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
        this.relationType = entity.getRelationType();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 倉庫ストア関連情報エンティティ
     */
    public UserRelationEntity toEntity() {
        return UserRelationEntity.builder()
            .id(id)
            .uuid(uuid)
            .relationCode(relationCode)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .relationType(relationType)
            .build();
    }
}
