/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.entity.Face;
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
