package com.lemonico.warehouse.resource;



import com.lemonico.core.attribute.ID;
import com.lemonico.entity.Mg002WarehouseEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class WarehouseResource
{
    /** */
    private ID<Mg002WarehouseEntity> id;
    /** 倉庫コード */
    private String warehouseCode;
    /** グループコード */
    private String groupCode;
    /** 倉庫名称 */
    private String warehouseName;
    /** 倉庫タイプ（1: 本番倉庫 2: 開発倉庫） */
    private Short warehouseType;
    /** ステータス（1: 利用中 2: 利用停止 3: 準備中） */
    private Short status;
    /** 契約開始日 */
    private LocalDate contractStartDate;
    /** 契約終了日 */
    private LocalDate contractEndDate;
    /** 国・地域 */
    private String country;
    /** 郵便番号 */
    private String zip;
    /** 都道府県 */
    private String prefecture;
    /** 市区町村 */
    private String municipality;
    /** 住所1 */
    private String address1;
    /** 住所2 */
    private String address2;
    /** 電話番号 */
    private String phone;
    /** 携帯電話 */
    private String mobile;
    /** FAX */
    private String fax;
    /** 担当部署 */
    private String section;
    /** メールアドレス */
    private String email;
    /** ロゴ */
    private String logo;
    /** ホームページURL */
    private String homePageUrl;
    /** 備考 */
    private String memo;
    /** 作成者 */
    private String createdBy;
    /** 作成日時 */
    private LocalDateTime createdAt;
    /** 更新者 */
    private String modifiedBy;
    /** 更新日時 */
    private LocalDateTime modifiedAt;
    /** 削除フラグ（0: 未削除 1: 削除済） */
    private Short isDeleted;

    /** 所管ストア情報 */

    public WarehouseResource(Mg002WarehouseEntity entity) {
        this.id = entity.getId();
        this.warehouseCode = entity.getWarehouseCode();
        this.groupCode = entity.getGroupCode();
        this.warehouseName = entity.getWarehouseName();
        this.warehouseType = entity.getWarehouseType();
        this.status = entity.getStatus();
        this.contractStartDate = entity.getContractStartDate();
        this.contractEndDate = entity.getContractEndDate();
        this.country = entity.getCountry();
        this.zip = entity.getZip();
        this.prefecture = entity.getPrefecture();
        this.municipality = entity.getMunicipality();
        this.address1 = entity.getAddress1();
        this.address2 = entity.getAddress2();
        this.phone = entity.getPhone();
        this.mobile = entity.getMobile();
        this.fax = entity.getFax();
        this.section = entity.getSection();
        this.email = entity.getEmail();
        this.logo = entity.getLogo();
        this.homePageUrl = entity.getHomePageUrl();
        this.memo = entity.getMemo();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }
}
