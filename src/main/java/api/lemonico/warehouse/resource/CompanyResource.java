/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.warehouse.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.warehouse.entity.CompanyEntity;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 会社リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class CompanyResource
{

    /** 自動採番ID */
    private final ID<CompanyEntity> id;

    /** 会社コード */
    private final String companyCode;

    /** 会社名称 */
    private final String companyName;

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
     * @param entity 会社エンティティ
     */
    public CompanyResource(CompanyEntity entity) {
        this.id = entity.getId();
        this.companyCode = entity.getCompanyCode();
        this.companyName = entity.getCompanyName();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 会社エンティティ
     */
    public CompanyEntity toEntity() {
        return CompanyEntity.builder()
            .id(id)
            .companyCode(companyCode)
            .companyName(companyName)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
