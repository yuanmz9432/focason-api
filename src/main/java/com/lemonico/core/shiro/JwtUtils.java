package com.lemonico.core.shiro;



import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * JWTツール
 *
 * @since 1.0.0
 */
public class JwtUtils
{

    /**
     * 有効期限は8時間である
     */
    private static final long EXPIRE_TIME = 8 * 60 * 60 * 1000;

    /**
     * 秘密鍵
     */
    public static final String SECRET = "SECRET_VALUE";

    /**
     * メール署名の有効期限は5分である
     */
    private static final long EMAIL_EXPIRE_TIME = 5 * 60 * 1000;

    /**
     * リクエストヘッダー
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * トークンが正しいかどうかを検証する
     */
    public static boolean verify(String token, String client_id, String secret) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTVerifier verifier = JWT.require(algorithm).withClaim("clientId", client_id).build();
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException exception) {
            return false;
        }
    }

    /**
     * @Param * @param: token
     * @param: filed
     * @description: トークンのカスタマイズ情報を取得する。secret復号しなくても取得できる
     * @return: java.lang.String
     */
    public static String getClaimFiled(String token, String filed) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim(filed).asString();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * @Param: login_nm
     * @param: secret
     * @param: yoto
     * @param: user_id
     * @description: 登录生成TOKEN
     * @return: java.lang.String
     * @date: 2020/9/21
     */
    public static String sign(String secret, String user_id) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withClaim("user_id", user_id)
                .withExpiresAt(date).sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    /**
     * @Param: secret
     * @param: loginId
     * @param: status
     * @description: 注册生成token
     * @return: java.lang.String
     * @date: 2020/9/21
     */
    public static String sign(String secret, String loginId, Integer status) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create()
                .withClaim("login_id", loginId)
                .withExpiresAt(date).sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    /**
     * @Param: client_id
     * @param: login_nm
     * @param: secret
     * @param: yoto
     * @param: user_id
     * @description: 署名を生成する
     * @return: java.lang.String
     */
    public static String sign(String client_id, String login_nm, String secret, String yoto, String user_id,
        String login_id) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create().withClaim("client_id", client_id)
                .withClaim("login_nm", login_nm)
                .withClaim("yoto", yoto)
                .withClaim("user_id", user_id)
                .withClaim("login_id", login_id)
                .withExpiresAt(date).sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    /**
     * @Param * @param: warehouse_cd
     * @param: login_nm
     * @param: secret
     * @param: client_id
     * @param: yoto
     * @param: user_id
     * @description: 署名を生成する
     * @return: java.lang.String
     */
    public static String sign(String warehouse_cd, String login_nm, String secret,
        Integer status, String yoto, String user_id, String login_id) {
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create().withClaim("warehouse_cd", warehouse_cd)
                .withClaim("login_nm", login_nm)
                .withClaim("status", status)
                .withClaim("yoto", yoto)
                .withClaim("user_id", user_id)
                .withClaim("login_id", login_id)
                .withExpiresAt(date).sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    /**
     * @Param * @param: email
     * @param: secret
     * @description: メールアドレスの署名を生成する
     * @return: java.lang.String
     */
    public static String signEmail(String email, String secret) {
        try {
            Date date = new Date(System.currentTimeMillis() + EMAIL_EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.create().withClaim("email", email).withExpiresAt(date).sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }

    /**
     * @Param * @param: token
     * @description: トークンの発行時間を取得する
     * @return: java.util.Date
     */
    public static Date getIssuedAt(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getIssuedAt();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    /**
     * @Param: token
     * @description: トークンの有効期限が切れているかどうかを検証する
     * @return: boolean
     */
    public static boolean isTokenExpired(String token) {
        Date now = Calendar.getInstance().getTime();
        DecodedJWT jwt = JWT.decode(token);
        return jwt.getExpiresAt().before(now);
    }

    /**
     * @Param: token
     * @param: secret
     * @description: トークンの期限切れ時間を更新する
     * @return: java.lang.String
     */
    public static String refreshTokenExpired(String token, String secret) {
        DecodedJWT jwt = JWT.decode(token);
        Map<String, Claim> claims = jwt.getClaims();
        try {
            Date date = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWTCreator.Builder builer = JWT.create().withExpiresAt(date);
            for (Map.Entry<String, Claim> entry : claims.entrySet()) {
                builer.withClaim(entry.getKey(), entry.getValue().asString());
            }
            return builer.sign(algorithm);
        } catch (JWTCreationException e) {
            return null;
        }
    }
}
