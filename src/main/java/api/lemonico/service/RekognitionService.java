package api.lemonico.service;



import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.model.*;

@Builder
@AllArgsConstructor
public class RekognitionService
{
    private final RekognitionClient rekClient;

    private final static String S3_PATH_SEPARATOR = "/";
    private String bucketName;
    private String prefix;

    private String getPrefix() {
        return prefix != null && !prefix.isEmpty() ? prefix + S3_PATH_SEPARATOR : "";
    }

    public void getLabelsFromImage(final String objectKey) {
        S3Object s3Object = S3Object.builder()
            .bucket(bucketName)
            .name(getPrefix() + objectKey).build();

        Image image = Image.builder()
            .s3Object(s3Object)
            .build();

        DetectLabelsRequest detectLabelsRequest = DetectLabelsRequest.builder()
            .image(image)
            .maxLabels(10)
            .build();

        DetectLabelsResponse labelsResponse = rekClient.detectLabels(detectLabelsRequest);
        List<Label> labels = labelsResponse.labels();
        for (Label label : labels) {
            System.out.println(label.name() + ": " + label.confidence());
        }
    }
}
