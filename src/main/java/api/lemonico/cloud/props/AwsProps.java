package api.lemonico.cloud.props;



import java.util.Map;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = AwsProps.PREFIX)
@Data
public class AwsProps
{
    public static final String PREFIX = "cloud.aws";

    private String region;

    private Map<String, S3Props> s3;
}
