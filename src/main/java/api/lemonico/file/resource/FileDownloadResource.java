package api.lemonico.file.resource;



import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * フィアルダウンロードリソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class FileDownloadResource
{
    /**
     * ダウンロードURL
     */
    private final String downloadUrl;
}
