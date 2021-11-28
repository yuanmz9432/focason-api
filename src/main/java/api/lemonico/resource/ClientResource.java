/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.Client;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * クライアントリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class ClientResource
{

    /** クライアントの自動採番ID */
    private final ID<Client> id;

    /** ユーザーコード */
    private final String clientCode;

    /** 姓 */
    private final String firstName;

    /** 名 */
    private final String lastName;

    /** メールアドレス（ログインID） */
    private final String email;

    /** パスワード */
    private final String password;

    /** ステータス（1:通常 2:ブラックユーザー 3:退会） */
    private final Integer status;

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
     * @param entity クライアントエンティティ
     */
    public ClientResource(Client entity) {
        this.id = entity.getId();
        this.clientCode = entity.getClientCode();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.email = entity.getEmail();
        this.password = entity.getPassword();
        this.status = entity.getStatus();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return クライアントエンティティ
     */
    public Client toEntity() {
        return Client.builder()
            .id(id)
            .clientCode(clientCode)
            .firstName(firstName)
            .lastName(lastName)
            .email(email)
            .password(password)
            .status(status)
            .build();
    }
}
