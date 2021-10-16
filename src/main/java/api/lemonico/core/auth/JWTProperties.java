package api.lemonico.core.auth;



import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class JWTProperties
{

    // トークンシークレット
    @Value("${jwt.secret}")
    private String secret;

    // アクセストークン有効期間
    @Value("${jwt.access_token_expires_in}")
    private Long accessTokenExpiresIn;

    // リフレッシュトークン有効期間
    @Value("${jwt.refresh_token_expires_in}")
    private Long refreshTokenExpiresIn;

    // アクセストークンヘッダー
    @Value("${jwt.access_token_header}")
    private String accessTokenHeader;

    // リフレッシュトークンヘッダー
    @Value("${jwt.refresh_token_header}")
    private String refreshTokenHeader;
}
