/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.Customer;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * customerのエンティティ
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class CustomerResource
{

    /** ID */
    private final ID<Customer> id;

    /** UUID */
    private final String uuid;

    /** 姓 */
    private final String firstName;

    /** 名 */
    private final String lastName;

    /** 性別 */
    private final Integer sex;

    /** 生年月日（ハイフォンなし） */
    private final String birthday;

    /** メールアドレス */
    private final String email;

    /**  */
    private final String createdBy;

    /**  */
    private final LocalDateTime createdAt;

    /**  */
    private final String modifiedBy;

    /**  */
    private final LocalDateTime modifiedAt;

    /**  */
    private final Integer isDeleted;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity Customerエンティティ
     */
    public CustomerResource(Customer entity) {
        this.id = entity.getId();
        this.uuid = entity.getUuid();
        this.firstName = entity.getFirstName();
        this.lastName = entity.getLastName();
        this.sex = entity.getSex();
        this.birthday = entity.getBirthday();
        this.email = entity.getEmail();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return Customerエンティティ
     */
    public Customer toEntity() {
        return Customer.builder()
            .id(id)
            .uuid(uuid)
            .firstName(firstName)
            .lastName(lastName)
            .sex(sex)
            .birthday(birthday)
            .email(email)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
