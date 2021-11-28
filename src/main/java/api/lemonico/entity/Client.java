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
 * クライアントエンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "client")
public class Client extends LcEntity
{
    /** クライアントの自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<Client> id;
    /** ユーザーコード */
    @Column(name = "client_code")
    String clientCode;
    /** 姓 */
    @Column(name = "first_name")
    String firstName;
    /** 名 */
    @Column(name = "last_name")
    String lastName;
    /** メールアドレス（ログインID） */
    @Column(name = "email")
    String email;
    /** パスワード */
    @Column(name = "password")
    String password;
    /** ステータス（1:通常 2:ブラックユーザー 3:退会） */
    @Column(name = "status")
    Integer status;
}
