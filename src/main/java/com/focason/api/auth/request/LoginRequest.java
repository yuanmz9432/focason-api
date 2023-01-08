/*
 * Copyright 2023 Focason Co.,Ltd. AllRights Reserved.
 */
package com.focason.api.auth.request;



import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoginRequest
{
    @NotNull(message = "username can not be null.")
    private String username;
    @NotNull(message = "password can not be null.")
    private String password;
}
