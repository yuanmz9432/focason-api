/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.WarehouseEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 倉庫情報リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class WarehouseResource
{

    /** 自動採番ID */
    private final ID<WarehouseEntity> id;

    /** 倉庫コード */
    private final String warehouseCode;

    /** 倉庫名称 */
    private final String warehouseName;

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

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity 倉庫情報エンティティ
     */
    public WarehouseResource(WarehouseEntity entity) {
        this.id = entity.getId();
        this.warehouseCode = entity.getWarehouseCode();
        this.warehouseName = entity.getWarehouseName();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 倉庫情報エンティティ
     */
    public WarehouseEntity toEntity() {
        return WarehouseEntity.builder()
            .id(id)
            .warehouseCode(warehouseCode)
            .warehouseName(warehouseName)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}