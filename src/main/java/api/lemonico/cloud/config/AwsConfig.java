package api.lemonico.cloud.config;



import api.lemonico.cloud.props.AwsProps;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/**
 * AWSサービス接続設定
 *
 * @since 1.0
 */
@EnableConfigurationProperties(AwsProps.class)
@Profile("!default")
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
     * AWS環境上のRekognitionクライアント接続
     *
     * @return {@link RekognitionClient}
     */
    @Lazy
    @Bean
    @ConditionalOnMissingBean
    public RekognitionClient rekClient() {
        return RekognitionClient.builder()
            .region(Region.of(awsProps.getRegion()))
            .build();
    }
}
