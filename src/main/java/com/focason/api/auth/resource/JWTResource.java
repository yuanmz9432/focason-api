/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.auth.resource;



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
    private final long expiresIn;
}
