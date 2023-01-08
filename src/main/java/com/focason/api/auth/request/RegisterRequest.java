package com.focason.api.auth.request;



import java.util.List;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegisterRequest
{

    /** ユーザ名 */
    @NotNull(message = "Username can not be null.")
    private final String username;

    /** 性別（１：男性、２：女性、９：不明） */
    @NotNull(message = "Gender can not be null.")
    @Max(1)
    private final Integer gender;

    /** メールアドレス（ログインID） */
    @NotNull(message = "Email can not be null.")
    @Email(message = "Email is not right.")
    private final String email;

    /** パスワード */
    @NotNull(message = "Password can not be null.")
    private final String password;

    /** ステータス（１：有効、０：無効） */
    @NotNull(message = "Status can not be null.")
    private final Integer status;

    /** タイプ（１：本番、２：デモ、９：スーパーユーザ） */
    @NotNull(message = "Type can not be null.")
    private final Integer type;

    /** ユーザーの権限リスト */
    @NotNull(message = "Authorities can not be null.")
    private final List<String> authorities;
}
