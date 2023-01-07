package com.focason.api.cloud.config;



import com.focason.api.cloud.props.AwsProps;
import com.focason.api.cloud.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWSサービスディフォルトクライアント設定
 *
 * @since 1.0
 */
@EnableConfigurationProperties(AwsProps.class)
@RequiredArgsConstructor
@Configuration
public class AwsDefaultServiceConfig
{
    /**
     * AWSプロパティ
     */
    private final AwsProps awsProps;

    @Bean
    public S3Service s3Service(S3Presigner s3Presigner, S3Client s3Client) {
        var props = awsProps.getS3().get("file-upload");
        return S3Service.builder()
            .s3Presigner(s3Presigner)
            .s3Client(s3Client)
            .prefix(props.getPrefix())
            .bucketName(props.getBucketName())
            .preSignedUrlValidMinutes(props.getPreSignedUrlValidMinutes())
            .build();
    }
}
