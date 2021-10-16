/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.config;



import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class JWTGenerator
{

    private final JWTProperties properties;

    /**
     * 有効期間が過ぎたのかどうかをチェックする
     *
     * @param claims 要求
     * @return 有効期間が過ぎた場合はFalse, 逆にTrue.
     */
    public boolean isAccessTokenExpired(Claims claims) {
        return claims.getExpiration().before(new Date(System.currentTimeMillis()));
    }

    /**
     * アクセストークンからclaimsリソースを取得
     *
     * @param accessToken アクセストークン
     * @return Claimsリソース
     */
    public Claims getClaims(String accessToken) {
        return Jwts.parser()
            .setSigningKey(Base64Util.encode(properties.getSecret()))
            .parseClaimsJws(accessToken)
            .getBody();
    }

    /**
     * アクセストークンを生成する
     * {@link Claims} のパラメータを設定する
     * <p>
     * iss (issuer)：签发人
     * exp (expiration time)：过期时间
     * sub (subject)：主题
     * aud (audience)：受众
     * nbf (Not Before)：生效时间
     * iat (Issued At)：签发时间
     * jti (JWT ID)：编号
     *
     * @param sub サブジェクト
     * @param exp 有効期限
     * @return アクセストークン
     */
    public String generateAccessToken(String sub, Date exp) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("sub", sub);
        payload.put("exp", exp);
        payload.put("iat", new Date(System.currentTimeMillis()));
        return Jwts.builder()
            .setClaims(payload)
            .setExpiration(exp)
            .signWith(SignatureAlgorithm.HS256, Base64Util.encode(properties.getSecret())).compact();
    }

    /**
     * 有効期間（application.ymlファイルに設定 単位：秒）を生成する。
     *
     * @return Date
     */
    public Date generateExpirationTime() {
        return new Date(System.currentTimeMillis() + properties.getAccessTokenExpiresIn() * 1000);
    }

}
