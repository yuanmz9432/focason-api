/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.warehouse.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.warehouse.entity.WarehouseStoreEntity;
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
public class WarehouseStoreResource
{

    /** 自動採番ID */
    private final ID<WarehouseStoreEntity> id;

    /** 倉庫コード */
    private final String warehouseCode;

    /** ストアコード */
    private final String storeCode;

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
     * @param entity 倉庫ストア関連情報エンティティ
     */
    public WarehouseStoreResource(WarehouseStoreEntity entity) {
        this.id = entity.getId();
        this.warehouseCode = entity.getWarehouseCode();
        this.storeCode = entity.getStoreCode();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 倉庫ストア関連情報エンティティ
     */
    public WarehouseStoreEntity toEntity() {
        return WarehouseStoreEntity.builder()
            .id(id)
            .warehouseCode(warehouseCode)
            .storeCode(storeCode)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
