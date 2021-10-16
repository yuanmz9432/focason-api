/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.config;

public class TokenDetailImpl implements TokenDetail
{

    private final String email;

    public TokenDetailImpl(String email) {
        this.email = email;
    }

    @Override
    public String getEmail() {
        return this.email;
    }

}
