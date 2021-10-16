package api.lemonico.auth.resource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class JWTResource {
    /**
     * アクセストークン
     */
    private final String accessToken;

    private final Date expirationTime;
}
