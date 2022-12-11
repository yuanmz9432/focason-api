/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.user.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.store.resource.StoreResource;
import api.lemonico.user.entity.UserEntity;
import api.lemonico.warehouse.resource.WarehouseResource;
import java.time.LocalDateTime;
import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ユーザリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class UserResource
{

    /** 自動採番ID */
    private final ID<UserEntity> id;

    /** UUID */
    private final String uuid;

    /** ユーザ名 */
    @NotNull(message = "Username can not be null.")
    private final String username;

    /** 性別（１：男性、２：女性、９：不明） */
    @NotNull(message = "Gender can not be null.")
    @Max(1)
    private final Integer gender;

    /** メールアドレス（ログインID） */
    @NotNull(message = "Email can not be null.")
    @Email(message = "Email is not right.")
    private final String email;

    /** パスワード */
    private final String password;

    /** ステータス（１：有効、０：無効） */
    private final Integer status;

    /** タイプ（１：本番、２：デモ、９：スーパーユーザ） */
    private final Integer type;

    /** 電話番号 */
    @NotNull(message = "Phone can not be null.")
    @Length(min = 10, max = 11, message = "Phone number is too long.")
    private final String phone;

    @NotNull(message = "Zip can not be null.")
    /** 郵便番号 */
    private final String zip;

    /** 都道府県 */
    private final Integer prefecture;

    /** 市区町村 */
    private final String municipality;

    /** 字・町目 */
    private final String address1;

    /** 部屋 */
    private final String address2;

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

    /** ユーザー所属リスト */
    private final List<UserDepartmentResource> userDepartments;

    /** ユーザー所属のストアリスト */
    private final List<StoreResource> stores;

    /** ユーザー所属の倉庫リスト */
    private final List<WarehouseResource> warehouses;

    /** ユーザーの権限リスト */
    private final List<String> authorities;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity ユーザエンティティ
     */
    public UserResource(UserEntity entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.username = entity.getUsername();
        this.gender = entity.getGender();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.status = entity.getStatus();
        this.type = entity.getType();
        this.phone = entity.getPhone();
        this.zip = entity.getZip();
        this.prefecture = entity.getPrefecture();
        this.municipality = entity.getMunicipality();
        this.address1 = entity.getAddress1();
        this.address2 = entity.getAddress2();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
        this.userDepartments = null;
        this.stores = null;
        this.warehouses = null;
        this.authorities = null;
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ユーザエンティティ
     */
    public UserEntity toEntity() {
        return UserEntity.builder()
            .id(id)
            .uuid(uuid)
            .username(username)
            .gender(gender)
            .email(email)
            .password(password)
            .status(status)
            .type(type)
            .phone(phone)
            .zip(zip)
            .prefecture(prefecture)
            .municipality(municipality)
            .address1(address1)
            .address2(address2)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
