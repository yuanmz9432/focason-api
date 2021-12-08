/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.entity;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.LcEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.seasar.doma.*;

/**
 * 顔エンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
@Table(name = "face")
public class Face extends LcEntity
{
    /** フェスの自動採番ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<Face> id;
    /** クレクションID */
    @Column(name = "collection_id")
    Integer collectionId;
    /** Amazon Rekognition が顔に割り当てる一意の識別子 */
    @Column(name = "face_id")
    String faceId;
    /** Amazon Rekognition が入力画像に割り当てる一意の識別子 */
    @Column(name = "image_id")
    String imageId;
    /** イメージの名前 */
    @Column(name = "external_image_id")
    String externalImageId;
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
