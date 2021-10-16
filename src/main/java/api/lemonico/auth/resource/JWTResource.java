/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
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

    /**
     * 有効期間
     */
    private final Date expirationTime;
}
