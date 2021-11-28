/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.core.attribute;



import api.lemonico.core.handler.LcEntityListenerHandler;
import lombok.*;
import org.seasar.doma.Entity;

@Entity(listener = LcEntityListenerHandler.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class LcEntity
{
    // /** 作成者 */
    // @Column(name = "created_by")
    // String createdBy;
    // /** 作成日時 */
    // @Column(name = "created_at")
    // LocalDateTime createdAt;
    // /** 更新者 */
    // @Column(name = "modified_by")
    // String modifiedBy;
    // /** 更新日時 */
    // @Column(name = "modified_at")
    // LocalDateTime modifiedAt;
    // /** 削除フラグ */
    // @Column(name = "is_deleted")
    // Integer isDeleted;
}
