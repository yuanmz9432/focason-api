/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.Collection;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * クレクションリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class CollectionResource
{

    /** クレクションの自動採番ID */
    private final ID<Collection> id;

    /** クライアントの自動採番 */
    private final Integer clientId;

    /** クレクションコード */
    private final String collectionCode;

    /** 作成者 */
    private final String createdBy;

    /** 作成日時 */
    private final LocalDateTime createdAt;

    /** 更新者 */
    private final String modifiedBy;

    /** 更新日時 */
    private final LocalDateTime modifiedAt;

    /** 削除フラグ */
    private final Integer isDeleted;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity クレクションエンティティ
     */
    public CollectionResource(Collection entity) {
        this.id = entity.getId();
        this.clientId = entity.getClientId();
        this.collectionCode = entity.getCollectionCode();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return クレクションエンティティ
     */
    public Collection toEntity() {
        return Collection.builder()
            .id(id)
            .clientId(clientId)
            .collectionCode(collectionCode)
            .build();
    }
}
