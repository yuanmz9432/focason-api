package com.lemonico.core.shiro;



import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWTトークンマッチング処理
 *
 * @since 1.0.0
 */
public class JwtCredentialsMatcher implements CredentialsMatcher
{

    private final static Logger logger = LoggerFactory.getLogger(CredentialsMatcher.class);

    @Override
    public boolean doCredentialsMatch(AuthenticationToken authenticationToken, AuthenticationInfo authenticationInfo) {
        final String token = authenticationToken.getCredentials().toString();
        final JwtToken jwtToken = (JwtToken) authenticationToken;
        final String warehouseCd = JwtUtils.getClaimFiled((String) jwtToken.getCredentials(), "warehouse_cd");
        final String clientId = ((JwtToken) authenticationToken).getClientId();
        try {
            JWTVerifier verifier;
            if (warehouseCd == null) {
                verifier = JWT.require(Algorithm.HMAC256(JwtUtils.SECRET)).withClaim("client_id", clientId).build();
            } else {
                verifier =
                    JWT.require(Algorithm.HMAC256(JwtUtils.SECRET)).withClaim("warehouse_cd", warehouseCd).build();
            }
            verifier.verify(token);
            return true;
        } catch (JWTVerificationException e) {
            logger.error(e.getMessage());
        }
        return false;
    }
}
