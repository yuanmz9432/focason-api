package api.lemonico.rekognition.resource;



import api.lemonico.core.attribute.ID;
import api.lemonico.fileTransfer.entity.FileTransfer;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 視覚分析リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Builder(toBuilder = true)
@Data
@NoArgsConstructor
public class RekognitionResource
{
    /**
     * ファイル転送ID
     */
    private ID<FileTransfer> id;
}
