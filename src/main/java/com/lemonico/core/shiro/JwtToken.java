package com.lemonico.core.shiro;



import lombok.Data;
import org.apache.shiro.authc.AuthenticationToken;

/**
 * JWTトークン
 *
 * @since 1.0.0
 */
@Data
public class JwtToken implements AuthenticationToken
{

    private final String token;
    private final String clientId;
    private final String warehouseCd;

    public JwtToken(String token) {
        this.token = token;
        this.clientId = JwtUtils.getClaimFiled(token, "client_id");
        this.warehouseCd = JwtUtils.getClaimFiled(token, "warehouse_cd");
    }

    @Override
    public Object getPrincipal() {
        return this.clientId;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
