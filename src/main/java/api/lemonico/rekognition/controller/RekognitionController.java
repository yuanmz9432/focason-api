package api.lemonico.rekognition.controller;



import api.lemonico.cloud.service.RekognitionService;
import api.lemonico.cloud.service.S3Service;
import api.lemonico.core.exception.LcEntityNotFoundException;
import api.lemonico.fileTransfer.resource.FileTransferResource;
import api.lemonico.fileTransfer.service.FileTransferService;
import api.lemonico.rekognition.resource.RekognitionResource;
import java.util.Optional;
import javax.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 視覚分析コントローラー
 *
 * @since 1.0.0
 */
@RestController
@Validated
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RekognitionController
{

    /**
     * 視覚分析URI
     */
    private static final String REKOGNITION_URI = "/rekognition";

    private final S3Service s3Service;

    private final RekognitionService rekService;

    private final FileTransferService fileTransferService;

    /**
     * イメージIDを指定して、視覚分析を行う
     *
     * @return ユーザーリソース一覧取得APIレスポンス
     */
    @Validated({
        Default.class
    })
    @PostMapping(REKOGNITION_URI)
    public ResponseEntity<Void> rekognitionHandler(@RequestBody RekognitionResource resource) {
        Optional<FileTransferResource> resourceOptional = fileTransferService.getResource(resource.getId());
        if (resourceOptional.isEmpty()) {
            throw new LcEntityNotFoundException(FileTransferResource.class, resource.getId());
        }
        rekService.getLabelsFromImage(resourceOptional.get().getFileName());
        return ResponseEntity.noContent().build();
    }
}
