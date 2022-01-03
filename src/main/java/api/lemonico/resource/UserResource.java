/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.User;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * ユーザーリソース
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

    /** ユーザー自動採番ID */
    private final ID<User> id;

    /** ユーザーコード（UUID） */
    private final String userCode;

    /** 姓 */
    private final String firstName;

    /** 名 */
    private final String lastName;

    /** 性別（1:男性 2:女性） */
    private final Integer gender;

    /** メールアドレス（ログインID） */
    private final String email;

    /** パスワード */
    private final String password;

    /** タイプ（1:シルバー 2:ゴールド 3:プレミアム 4:退会） */
    private final Integer type;

    /** 電話番号 */
    private final String phone;

    /** 個人ページ */
    private final String url;

    /** 個人ページ画像 */
    private final String profileImage;

    /** 郵便番号 */
    private final String zip;

    /** 都道府県 */
    private final Integer prefecture;

    /** 市区町村 */
    private final String municipality;

    /** 字・町目 */
    private final String address;

    /** ビル */
    private final String building;

    /** おすすめ情報読込 */
    private final Integer subscribe;

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
     * @param entity ユーザーエンティティ
     */
    public UserResource(User entity) {
        this.id = entity.getId();
        this.userCode = entity.getUserCode();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.gender = entity.getGender();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.type = entity.getType();
        this.phone = entity.getPhone();
        this.url = entity.getUrl();
        this.profileImage = entity.getProfileImage();
        this.zip = entity.getZip();
        this.prefecture = entity.getPrefecture();
        this.municipality = entity.getMunicipality();
        this.address = entity.getAddress();
        this.building = entity.getBuilding();
        this.subscribe = entity.getSubscribe();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return ユーザーエンティティ
     */
    public User toEntity() {
        return User.builder()
            .id(id)
            .userCode(userCode)
            .firstName(firstName)
            .lastName(lastName)
            .gender(gender)
            .email(email)
            .password(password)
            .type(type)
            .phone(phone)
            .url(url)
            .profileImage(profileImage)
            .zip(zip)
            .prefecture(prefecture)
            .municipality(municipality)
            .address(address)
            .building(building)
            .subscribe(subscribe)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
