package api.lemonico.resource;



import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Base64リソース
 *
 * @since 1.0.0
 */
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class Base64Resource
{
    /** ターゲットコード */
    @NonNull
    private final String originalInput;
    /** エンコードした文字列 */
    private final String encodedString;
    /** ディコードした文字列 */
    private final String decodedString;
}
