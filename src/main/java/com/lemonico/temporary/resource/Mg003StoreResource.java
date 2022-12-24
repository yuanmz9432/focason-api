/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package com.lemonico.temporary.resource;



import com.lemonico.core.attribute.ID;
import com.lemonico.entity.Mg003StoreEntity;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 店舗管理テーブルリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class Mg003StoreResource
{

    /** */
    private final ID<Mg003StoreEntity> id;

    /** 店舗コード */
    private final String storeCode;

    /** 店舗名称 */
    private final String storeName;

    /** 店舗表示名称 */
    private final String storeDisplayName;

    /** 店舗タイプ（1: 本番店舗 2: 開発店舗） */
    private final Short storeType;

    /** ステータス（1: 利用中 2: 利用停止 3: 準備中） */
    private final Short status;

    /** 契約開始日 */
    private final LocalDate contractStartDate;

    /** 契約終了日 */
    private final LocalDate contractEndDate;

    /** 事業形態（1: 法人 2: 個人） */
    private final Short businessFormType;

    /** 法人番号 */
    private final String corporateNumber;

    /** 国・地域 */
    private final String country;

    /** 郵便番号 */
    private final String zip;

    /** 都道府県 */
    private final String prefecture;

    /** 市区町村 */
    private final String municipality;

    /** 住所1 */
    private final String address1;

    /** 住所2 */
    private final String address2;

    /** 電話番号 */
    private final String phone;

    /** 携帯電話 */
    private final String mobile;

    /** FAX */
    private final String fax;

    /** 連絡メールアドレス */
    private final String email;

    /** 担当部署 */
    private final String section;

    /** ロゴ */
    private final String logo;

    /** 店舗ホームページURL */
    private final String homePageUrl;

    /** ログイン許可IP */
    private final String loginPermissionIp;

    /** 品名タイプ（1: 商品情報 2: 依頼主情報） */
    private final Short descriptionType;

    /** 端数タイプ（1: 切り捨て 2: 切り上げ 3: 四捨五入） */
    private final Short fractionType;

    /** 商品価格表示タイプ（1: 税込 2: 税抜） */
    private final Short priceDisplayType;

    /** 備考 */
    private final String memo;

    /** 作成者 */
    private final String createdBy;

    /** 作成日時 */
    private final LocalDateTime createdAt;

    /** 更新者 */
    private final String modifiedBy;

    /** 更新日時 */
    private final LocalDateTime modifiedAt;

    /** 削除フラグ（0: 未削除 1: 削除済） */
    private final Short isDeleted;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity 店舗管理テーブルエンティティ
     */
    public Mg003StoreResource(Mg003StoreEntity entity) {
        this.id = entity.getId();
        this.storeCode = entity.getStoreCode();
        this.storeName = entity.getStoreName();
        this.storeDisplayName = entity.getStoreDisplayName();
        this.storeType = entity.getStoreType();
        this.status = entity.getStatus();
        this.contractStartDate = entity.getContractStartDate();
        this.contractEndDate = entity.getContractEndDate();
        this.businessFormType = entity.getBusinessFormType();
        this.corporateNumber = entity.getCorporateNumber();
        this.country = entity.getCountry();
        this.zip = entity.getZip();
        this.prefecture = entity.getPrefecture();
        this.municipality = entity.getMunicipality();
        this.address1 = entity.getAddress1();
        this.address2 = entity.getAddress2();
        this.phone = entity.getPhone();
        this.mobile = entity.getMobile();
        this.fax = entity.getFax();
        this.email = entity.getEmail();
        this.section = entity.getSection();
        this.logo = entity.getLogo();
        this.homePageUrl = entity.getHomePageUrl();
        this.loginPermissionIp = entity.getLoginPermissionIp();
        this.descriptionType = entity.getDescriptionType();
        this.fractionType = entity.getFractionType();
        this.priceDisplayType = entity.getPriceDisplayType();
        this.memo = entity.getMemo();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 店舗管理テーブルエンティティ
     */
    public Mg003StoreEntity toEntity() {
        return Mg003StoreEntity.builder()
            .id(id)
            .storeCode(storeCode)
            .storeName(storeName)
            .storeDisplayName(storeDisplayName)
            .storeType(storeType)
            .status(status)
            .contractStartDate(contractStartDate)
            .contractEndDate(contractEndDate)
            .businessFormType(businessFormType)
            .corporateNumber(corporateNumber)
            .country(country)
            .zip(zip)
            .prefecture(prefecture)
            .municipality(municipality)
            .address1(address1)
            .address2(address2)
            .phone(phone)
            .mobile(mobile)
            .fax(fax)
            .email(email)
            .section(section)
            .logo(logo)
            .homePageUrl(homePageUrl)
            .loginPermissionIp(loginPermissionIp)
            .descriptionType(descriptionType)
            .fractionType(fractionType)
            .priceDisplayType(priceDisplayType)
            .memo(memo)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
