package com.lemonico.cloud.config;



import com.lemonico.cloud.props.AwsProps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.sts.StsClient;

/**
 * AWSサービス接続設定
 */
@EnableConfigurationProperties(AwsProps.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Configuration
public class AwsConfig
{
    /**
     * AWSプロパティ
     */
    private final AwsProps awsProps;

    /**
     * AWS環境上のS3証明付きリクエストクライアント接続
     *
     * @return {@link S3Presigner}
     */
    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.of(awsProps.getRegion()))
            .build();
    }

    /**
     * AWS環境上のS3クライアント接続
     *
     * @return {@link S3Client}
     */
    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public S3Client s3Client() {
        return S3Client.builder()
            .region(Region.of(awsProps.getRegion()))
            .build();
    }

    /**
     * AWS環境上のSTSクライアント接続
     *
     * @return {@link StsClient}
     */
    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public StsClient stsClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
            "AKIAYXUW5PNFOWETK743",
            "vSRBbV1lLdcLCkYk2EqiAz5FcaXit3AMX3d1di2e");
        return StsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
            .build();
    }

}
