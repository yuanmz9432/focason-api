package com.lemonico.cloud.service;



import com.lemonico.core.enums.FileType;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDate;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

/**
 * S3サービスクラス
 */
@Builder
@AllArgsConstructor
public class S3Service
{
    private final static String S3_PATH_SEPARATOR = "/";
    private final S3Presigner s3Presigner;
    private final S3Client s3Client;
    private final String bucketName;
    private final String prefix;
    private final long preSignedUrlValidMinutes;

    /**
     * ファイル読み込む処理
     *
     * @param path 対象ファイルパス
     * @return {@link ByteBuffer}
     */
    private static ByteBuffer getByteBuffer(String path) {
        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(fileContent);
    }

    /**
     * ファイル読み込む処理
     *
     * @param multipartFile 対象ファイル自体
     * @return {@link ByteBuffer}
     */
    private static ByteBuffer getByteBuffer(MultipartFile multipartFile) {
        byte[] fileContent = new byte[0];
        try {
            fileContent = multipartFile.getBytes();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ByteBuffer.wrap(fileContent);
    }

    /**
     * ファイルタイプと店舗IDを指定して、ファイルパスを生成する
     * 例:
     * PDF: BUCKET_NAME/prefix/2021-12/pdf/SL001/
     * CSV: BUCKET_NAME/prefix/2021-12/csv/SL001/
     * CREDENTIAL: BUCKET_NAME/prefix/credential/SL001/
     * IMAGE: BUCKET_NAME/prefix/image/SL001/
     * ※ 店舗IDを指定しないと、アップロードされたファイルを【SL999】フォルダーに格納する
     *
     * @param fileType ファイルタイプ（PDF, CSV, CREDENTIAL, IMAGE）
     * @param clientId 店舗ID
     * @return ファイルパス
     */
    private String getFilePath(final FileType fileType, String clientId) {
        final int year = LocalDate.now().getYear();
        final int monthValue = LocalDate.now().getMonthValue();
        StringBuilder path = new StringBuilder();
        switch (fileType) {
            case IMAGE:
            case CREDENTIAL:
                path.append(fileType.getName()).append(S3_PATH_SEPARATOR);
                break;
            case CSV:
            case PDF:
            case UNKNOWN:
                path.append(year).append("-").append(monthValue);
                path.append(S3_PATH_SEPARATOR).append(fileType.getName()).append(S3_PATH_SEPARATOR);
                break;
            default:
        }
        // 店舗IDが存在しなければ、SL999フォルダーに格納する。
        if (Strings.isBlank(clientId)) {
            clientId = "SL999";
        }
        path.append(clientId).append(S3_PATH_SEPARATOR);

        return !Strings.isBlank(prefix) ? prefix + S3_PATH_SEPARATOR + path : path.toString();
    }

    /**
     * 証明付きURL PUTリクエスト作成
     *
     * @param objectKey S3オブジェクトキー
     * @return {@link PresignedPutObjectRequest}
     */
    private PresignedPutObjectRequest getPresignedPutObjectRequest(String objectKey, FileType fileType,
        String clientId) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(getFilePath(fileType, clientId) + objectKey)
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
    private PresignedGetObjectRequest getPresignedGetObjectRequest(String objectKey, FileType fileType,
        String clientId) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(getFilePath(fileType, clientId) + objectKey)
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
    public boolean doesObjectExist(String objectKey, FileType fileType, String clientId) {

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(getFilePath(fileType, clientId) + objectKey)
            .build();
        ResponseInputStream<GetObjectResponse> responseInputStream = null;
        try {
            responseInputStream = s3Client.getObject(getObjectRequest);
        } catch (NoSuchKeyException ex) {
            // S3バケットにキーと一致するファイルが存在しない場合
            return false;
        } catch (S3Exception e) {
            throw new NoSuchElementException();
        } finally {
            if (responseInputStream != null) {
                responseInputStream.abort();
            }
        }
        return true;
    }

    /**
     * ファイルダウンロード用の証明付きURL作成
     *
     * @param objectKey S3オブジェクトキー
     * @return 署名付きURL or S3にファイルが存在しない場合はNULL
     */
    public URL generateGetUrl(String objectKey, FileType fileType, String clientId) {

        if (doesObjectExist(objectKey, fileType, clientId)) {
            return getPresignedGetObjectRequest(objectKey, fileType, clientId).url();
        }
        return null;
    }

    /**
     * ファイルアップロード用の証明付きURL作成
     *
     * @param objectKey S3オブジェクトキー
     * @return 署名付きURL
     */
    public URL generatePutUrl(String objectKey, FileType fileType, String clientId) {
        return getPresignedPutObjectRequest(objectKey, fileType, clientId).url();
    }

    /**
     * ファイルアップロード用の証明付きURL作成
     * fileNameからnewFileNameにファイル名を変更してアップロードする。
     *
     * @param objectKey S3オブジェクトキー
     * @param newFileName 新しいファイル名
     * @return 署名付きURL
     */
    public URL generatePutUrl(String objectKey, String newFileName, FileType fileType, String clientId) {
        return getPresignedPutObjectRequest(changeObjectKeyByNewFileName(objectKey, newFileName), fileType, clientId)
            .url();
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
    private PresignedGetObjectRequest getPresignedGetObjectRequest(String objectKey, String fileName, FileType fileType,
        String clientId) {
        GetObjectRequest objectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(getFilePath(fileType, clientId) + objectKey)
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
    public URL generateGetUrl(String objectKey, String fileName, FileType fileType, String clientId) {
        if (doesObjectExist(objectKey, fileType, clientId)) {
            return getPresignedGetObjectRequest(objectKey, fileName, fileType, clientId).url();
        }
        return null;
    }

    /**
     * ファイルをS3から削除
     *
     * @param objectKey S3オブジェクトキー
     */
    public void deleteObject(String objectKey, FileType fileType, String clientId) {
        if (doesObjectExist(objectKey, fileType, clientId)) {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(getFilePath(fileType, clientId) + objectKey)
                .build();
            s3Client.deleteObject(deleteObjectRequest);
        }
    }

    /**
     * ファイルをS3にアップロード
     *
     * @param objectKey S3オブジェクトキー
     */
    public void putObject(String objectKey, String path, FileType fileType, String clientId) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(getFilePath(fileType, clientId) + objectKey)
            .build();

        s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(getByteBuffer(path)));
    }

    /**
     * ファイルをS3にアップロード
     *
     * @param objectKey S3オブジェクトキー
     */
    public void putObject(String objectKey, MultipartFile multipartFile, FileType fileType, String clientId) {
        PutObjectRequest objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(getFilePath(fileType, clientId) + objectKey)
            .build();

        s3Client.putObject(objectRequest, RequestBody.fromByteBuffer(getByteBuffer(multipartFile)));
    }
}
