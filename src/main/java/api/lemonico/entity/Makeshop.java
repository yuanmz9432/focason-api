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
 * メーカーショップAPIエンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "makeshop")
public class Makeshop extends LcEntity
{
    /** メーカーショップ自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<Makeshop> id;
    /** API名称 */
    @Column(name = "api_name")
    String apiName;
    /** ショップID */
    @Column(name = "shop_id")
    String shopId;
    /** 認証コード */
    @Column(name = "auth_code")
    String authCode;
    /** 読取期間 */
    @Column(name = "period")
    Integer period;
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
    /** 削除フラグ */
    @Column(name = "is_deleted")
    Integer isDeleted;
}
