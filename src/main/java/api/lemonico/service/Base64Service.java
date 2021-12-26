package api.lemonico.service;



import api.lemonico.resource.Base64Resource;
import java.util.Base64;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Base64サービス
 *
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class Base64Service
{
    /**
     * エンコード処理
     *
     * @param resource {@link Base64Resource}
     * @return {@link Base64Resource}
     */
    public Base64Resource encode(Base64Resource resource) {
        if (Objects.isNull(resource))
            return null;
        String originalInput = resource.getOriginalInput();
        String encodedString = Base64.getEncoder().encodeToString(originalInput.trim().getBytes());

        return resource.withEncodedString(encodedString);
    }

    /**
     * ディコード処理
     *
     * @param resource {@link Base64Resource}
     * @return {@link Base64Resource}
     */
    public Base64Resource decode(Base64Resource resource) {
        if (Objects.isNull(resource))
            return null;
        byte[] decodedBytes = Base64.getDecoder().decode(resource.getOriginalInput().trim());
        String decodedString = new String(decodedBytes);

        return resource.withDecodedString(decodedString);
    }
}
