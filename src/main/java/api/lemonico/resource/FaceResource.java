/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.Face;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 顔リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class FaceResource
{

    /** フェスの自動採番ID */
    private final ID<Face> id;

    /** クレクションID */
    private final Integer collectionId;

    /** Amazon Rekognition が顔に割り当てる一意の識別子 */
    private final String faceId;

    /** Amazon Rekognition が入力画像に割り当てる一意の識別子 */
    private final String imageId;

    /** イメージの名前 */
    private final String externalImageId;

    /** 作成者 */
    private final String createdBy;

    /** 作成日時 */
    private final LocalDateTime createdAt;

    /** 更新者 */
    private final String modifiedBy;

    /** 更新日時 */
    private final LocalDateTime modifiedAt;

    /** 削除フラグ */
    private final Integer isDeleted;

    /**
     * 指定したエンティティを使用して、リソースを構築します。
     *
     * @param entity 顔エンティティ
     */
    public FaceResource(Face entity) {
        this.id = entity.getId();
        this.collectionId = entity.getCollectionId();
        this.faceId = entity.getFaceId();
        this.imageId = entity.getImageId();
        this.externalImageId = entity.getExternalImageId();
        this.createdBy = entity.getCreatedBy();
        this.createdAt = entity.getCreatedAt();
        this.modifiedBy = entity.getModifiedBy();
        this.modifiedAt = entity.getModifiedAt();
        this.isDeleted = entity.getIsDeleted();
    }

    /**
     * リソースをエンティティに変換します。
     *
     * @return 顔エンティティ
     */
    public Face toEntity() {
        return Face.builder()
            .id(id)
            .collectionId(collectionId)
            .faceId(faceId)
            .imageId(imageId)
            .externalImageId(externalImageId)
            .build();
    }
}
