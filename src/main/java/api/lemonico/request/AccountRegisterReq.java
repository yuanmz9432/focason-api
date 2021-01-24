package api.lemonico.request;

import lombok.Data;
import lombok.NonNull;

/**
 * アカウント登録Bean
 */
@Data
public class AccountRegisterReq {

    /**
     * 姓
     */
    @NonNull
    private String firstName;

    /**
     * 名
     */
    @NonNull
    private String lastName;

    /**
     * メールアドレス
     */
    @NonNull
    private String email;

    /**
     * パスワード
     */
    @NonNull
    private String password;
}
