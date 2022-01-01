package api.lemonico.resource;



import api.lemonico.entity.Face;
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
    Face face;
}
