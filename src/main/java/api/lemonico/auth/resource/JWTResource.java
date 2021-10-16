package api.lemonico.auth.resource;



import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder(toBuilder = true)
public class JWTResource
{
    /**
     * アクセストークン
     */
    private final String accessToken;

    private final Date expirationTime;
}
