/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.file_transfer.entity;



import api.lemonico.core.attribute.ID;
import api.lemonico.core.attribute.LcEntity;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import lombok.With;
import org.seasar.doma.*;

/**
 * フィアル転送エンティティ
 *
 * @since 1.0.0
 */
@Entity(immutable = true)
@Value
@EqualsAndHashCode(callSuper = false)
@Builder(toBuilder = true)
@With
public class FileTransfer extends LcEntity
{

    /** ファイル転送ID */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    ID<FileTransfer> id;

    /**  */
    @Column(name = "user_id")
    Integer userId;

    /** バケット名 */
    @Column(name = "bucket_name")
    String bucketName;

    /** ファイル名 */
    @Column(name = "file_name")
    String fileName;

    /** ファイル種類（1:CSV 2:PDF） */
    @Column(name = "file_type")
    Integer fileType;

    /** 転送タイプ（1:インポート 2:エクスポート） */
    @Column(name = "transfer_type")
    Integer transferType;

    /** ステータス（1:未転送 2:転送済） */
    @Column(name = "status")
    Integer status;

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
