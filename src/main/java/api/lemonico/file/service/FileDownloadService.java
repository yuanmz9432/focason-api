package api.lemonico.file.service;



import api.lemonico.cloud.service.S3Service;
import api.lemonico.file.resource.FileDownloadResource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Service
public class FileDownloadService
{


    /**
     * S3サービス
     */
    private final S3Service service;

    public FileDownloadResource getDownloadUrl() {
        return FileDownloadResource.builder()
            .downloadUrl(service.generateGetUrl("test.pdf").toString()).build();
    }
}
