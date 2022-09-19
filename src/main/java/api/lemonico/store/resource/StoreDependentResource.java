/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.store.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.store.entity.StoreDependentEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ストア所属リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class StoreDependentResource
{

    /** 自動採番ID */
    private final ID<StoreDependentEntity> id;

    /** ストアコード */
    private final String storeCode;

    /** 倉庫コード */
    private final String warehouseCode;

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
     * @param entity ストア所属エンティティ
     */
    public StoreDependentResource(StoreDependentEntity entity) {
        this.id = entity.getId();
        this.storeCode = entity.getStoreCode();
        this.warehouseCode = entity.getWarehouseCode();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ストア所属エンティティ
     */
    public StoreDependentEntity toEntity() {
        return StoreDependentEntity.builder()
            .id(id)
            .storeCode(storeCode)
            .warehouseCode(warehouseCode)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
