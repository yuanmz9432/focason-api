package api.lemonico.cloud.service;



import api.lemonico.core.attribute.LcErrorCode;
import api.lemonico.core.exception.LcException;
import java.net.URL;
import java.time.Duration;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Builder
@AllArgsConstructor
public class S3Service
{
    private final static String S3_PATH_SEPARATOR = "/";
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private String bucketName;
    private String prefix;
    private long preSignedUrlValidMinutes;

    private String getPrefix() {
        return prefix != null && !prefix.isEmpty() ? prefix + S3_PATH_SEPARATOR : "";
    }

    /**
     * 証明付きURL PUTリクエスト作成
     *
     * @param objectKey S3オブジェクトキー
     * @return {@link PresignedPutObjectRequest}
     */
    private PresignedPutObjectRequest getPresignedPutObjectRequest(String objectKey) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(getPrefix() + objectKey)
            .build();

        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(preSignedUrlValidMinutes))
            .putObjectRequest(objectRequest)
            .build();

        return s3Presigner.presignPutObject(presignRequest);
    }

    /**
     * 証明付きURL GETリクエスト作成
     *
     * @param objectKey S3オブジェクトキー
     * @return {@link PresignedGetObjectRequest}
     */
    private PresignedGetObjectRequest getPresignedGetObjectRequest(String objectKey) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(getPrefix() + objectKey)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(preSignedUrlValidMinutes))
            .getObjectRequest(objectRequest)
            .build();

        return s3Presigner.presignGetObject(presignRequest);
    }

    /**
     * S3内にオブジェクトキーのファイル存在確認
     *
     * @param objectKey S3オブジェクトキー
     * @return S3内のファイル存在有無
     */
    public boolean doesObjectExist(String objectKey) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(getPrefix() + objectKey)
            .build();
        try {
            s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException ex) {
            // S3バケットにキーと一致するファイルが存在しない場合
            return false;
        } catch (S3Exception e) {
            throw new LcException(LcErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return true;
    }

    /**
     * ファイルダウンロード用の証明付きURL作成
     *
     * @param objectKey S3オブジェクトキー
     * @return 署名付きURL or S3にファイルが存在しない場合はNULL
     */
    public URL generateGetUrl(String objectKey) {
        if (doesObjectExist(objectKey)) {
            return getPresignedGetObjectRequest(objectKey).url();
        }
        return null;
    }

    /**
     * ファイルアップロード用の証明付きURL作成
     *
     * @param objectKey S3オブジェクトキー
     * @return 署名付きURL
     */
    public URL generatePutUrl(String objectKey) {
        return getPresignedPutObjectRequest(objectKey).url();
    }

    /**
     * ファイルアップロード用の証明付きURL作成
     * fileNameからnewFileNameにファイル名を変更してアップロードする。
     *
     * @param objectKey S3オブジェクトキー
     * @param newFileName 新しいファイル名
     * @return 署名付きURL
     */
    public URL generatePutUrl(String objectKey, String newFileName) {
        return getPresignedPutObjectRequest(changeObjectKeyByNewFileName(objectKey, newFileName)).url();
    }

    /**
     * S3オブジェクトキーからファイル名を変更する。
     *
     * @param objectKey S3オブジェクトキー
     * @param newFileName 新しいファイル名
     * @return 署名付きURL
     */
    private String changeObjectKeyByNewFileName(String objectKey, String newFileName) {
        String[] objectKeyList = objectKey.split(S3_PATH_SEPARATOR);
        Optional<String> extension = Optional.ofNullable(objectKeyList[objectKeyList.length - 1])
            .filter(f -> f.contains("."))
            .map(f -> f.substring(f.lastIndexOf(".") + 1));
        objectKeyList[objectKeyList.length - 1] = extension.map(s -> newFileName + "." + s).orElse(newFileName);
        return String.join(S3_PATH_SEPARATOR, objectKeyList);
    }

    /**
     * 証明付きURL GETリクエスト作成
     *
     * @param objectKey S3オブジェクトキー
     * @param fileName ファイル名
     * @return {@link PresignedGetObjectRequest}
     */
    private PresignedGetObjectRequest getPresignedGetObjectRequest(String objectKey, String fileName) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(getPrefix() + objectKey)
            .responseContentDisposition("attachment;filename=" + fileName)
            .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(preSignedUrlValidMinutes))
            .getObjectRequest(objectRequest)
            .build();
        return s3Presigner.presignGetObject(presignRequest);
    }

    /**
     * ファイルダウンロード用の証明付きURL作成
     *
     * @param objectKey S3オブジェクトキー
     * @param fileName ファイル名
     * @return 署名付きURL or S3にファイルが存在しない場合はNULL
     */
    public URL generateGetUrl(String objectKey, String fileName) {
        if (doesObjectExist(objectKey)) {
            return getPresignedGetObjectRequest(objectKey, fileName).url();
        }
        return null;
    }

    /**
     * ファイルをS3から削除
     *
     * @param objectKey S3オブジェクトキー
     */
    public void deleteObject(String objectKey) {
        if (doesObjectExist(objectKey)) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(getPrefix() + objectKey)
                .build();
            s3Client.deleteObject(deleteObjectRequest);
        }
    }
}
