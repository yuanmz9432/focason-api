/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.file_transfer.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.file_transfer.entity.FileTransfer;
import java.time.LocalDateTime;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * フィアル転送リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class FileTransferResource
{

    /** ファイル転送ID */
    private final ID<FileTransfer> id;

    /**  */
    private final Integer userId;

    /** バケット名 */
    private final String bucketName;

    /** ファイル名 */
    private final String fileName;

    /** ファイル種類（1:CSV 2:PDF） */
    private final Integer fileType;

    /** 転送タイプ（1:インポート 2:エクスポート） */
    private final Integer transferType;

    /** ステータス（1:未転送 2:転送済） */
    private final Integer status;

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
     * @param entity フィアル転送エンティティ
     */
    public FileTransferResource(FileTransfer entity) {
        this.id = entity.getId();
        this.userId = entity.getUserId();
        this.bucketName = entity.getBucketName();
        this.fileName = entity.getFileName();
        this.fileType = entity.getFileType();
        this.transferType = entity.getTransferType();
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
     * @return フィアル転送エンティティ
     */
    public FileTransfer toEntity() {
        return FileTransfer.builder()
            .id(id)
            .userId(userId)
            .bucketName(bucketName)
            .fileName(fileName)
            .fileType(fileType)
            .transferType(transferType)
            .status(status)
            .createdBy(createdBy)
            .createdAt(createdAt)
            .modifiedBy(modifiedBy)
            .modifiedAt(modifiedAt)
            .isDeleted(isDeleted)
            .build();
    }
}
