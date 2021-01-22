package api.lemonico.request;

import lombok.Data;

@Data
public class UserRegisterReq {

    /**
     * 邮箱账号
     */
    private String email;

    /**
     * 密码
     */
    private String password;
}
