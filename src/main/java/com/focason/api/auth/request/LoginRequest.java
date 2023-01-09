/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.auth.request;



import javax.validation.constraints.NotNull;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;


@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class LoginRequest
{
    /**
     * ユーザ名
     */
    @NotNull(message = "username can not be null.")
    private String username;

    /**
     * パスワード
     */
    @NotNull(message = "password can not be null.")
    private String password;
}
