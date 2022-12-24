package com.lemonico.auth.resource;



import lombok.Data;

@Data
public class LoginUserResource
{

    /**
     * ユーザー名
     */
    private final String username;
    /**
     * パスワード
     */
    private final String password;

}
