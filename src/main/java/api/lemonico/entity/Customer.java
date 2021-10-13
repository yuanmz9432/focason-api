/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.entity;



import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.seasar.doma.*;

/**
 * customerのエンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
public class Customer extends LcEntity
{

    /** ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<Customer> id;

    /** UUID */
    @Column(name = "uuid")
    String uuid;

    /** 姓 */
    @Column(name = "first_name")
    String firstName;

    /** 名 */
    @Column(name = "last_name")
    String lastName;

    /** 性別 */
    @Column(name = "sex")
    Integer sex;

    /** 生年月日（ハイフォンなし） */
    @Column(name = "birthday")
    String birthday;

    /** メールアドレス */
    @Column(name = "email")
    String email;

    /**  */
    @Column(name = "created_by")
    String createdBy;

    /**  */
    @Column(name = "created_at")
    LocalDateTime createdAt;

    /**  */
    @Column(name = "modified_by")
    String modifiedBy;

    /**  */
    @Column(name = "modified_at")
    LocalDateTime modifiedAt;

    /**  */
    @Column(name = "is_deleted")
    Integer isDeleted;
}
