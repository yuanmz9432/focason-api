/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.entity;



import api.lemonico.core.attribute.ID;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.seasar.doma.*;

/**
 * 倉庫ストア関連情報エンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "warehouse_store")
public class WarehouseStoreEntity extends LcEntity
{
    /** 自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<WarehouseStoreEntity> id;
    /** 倉庫コード */
    @Column(name = "warehouse_code")
    String warehouseCode;
    /** ストアコード */
    @Column(name = "store_code")
    String storeCode;
    /** 作成者 */
    @Column(name = "created_by")
    String createdBy;
    /** 作成日時 */
    @Column(name = "created_at")
    LocalDateTime createdAt;
    /** 更新者 */
    @Column(name = "modified_by")
    String modifiedBy;
    /** 更新日時 */
    @Column(name = "modified_at")
    LocalDateTime modifiedAt;
    /** 削除フラグ（0: 未削除 1: 削除済） */
    @Column(name = "is_deleted")
    Byte isDeleted;
}
