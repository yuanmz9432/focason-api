/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.auth.config;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = JwtProps.PREFIX)
public class JwtProps
{
    public static final String PREFIX = "jwt";

    /**
     * トークンシークレット
     */
    private String secret;

    /**
     * アクセストークン有効期間
     */
    private Long accessTokenExpiresIn;

    /**
     * リフレッシュトークン有効期間
     */
    private Long refreshTokenExpiresIn;

    /**
     * リフレッシュトークンヘッダー
     */
    private String refreshTokenHeader;
}
