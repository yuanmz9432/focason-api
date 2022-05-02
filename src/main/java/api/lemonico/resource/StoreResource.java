/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.StoreEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ストア情報リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class StoreResource
{

    /** 自動採番ID */
    private final ID<StoreEntity> id;

    /** ストアコード */
    private final String storeCode;

    /** ストア名称 */
    private final String storeName;

    /** プランID */
    private final String planId;

    /** 作成者 */
    private final String createdBy;

    /** 作成日時 */
    private final LocalDateTime createdAt;

    /** 更新者 */
    private final String modifiedBy;

    /** 更新日時 */
    private final LocalDateTime modifiedAt;

    /** 削除フラグ（0: 未削除 1: 削除済） */
    private final Byte isDeleted;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity ストア情報エンティティ
     */
    public StoreResource(StoreEntity entity) {
        this.id = entity.getId();
        this.storeCode = entity.getStoreCode();
        this.storeName = entity.getStoreName();
        this.planId = entity.getPlanId();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ストア情報エンティティ
     */
    public StoreEntity toEntity() {
        return StoreEntity.builder()
            .id(id)
            .storeCode(storeCode)
            .storeName(storeName)
            .planId(planId)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
