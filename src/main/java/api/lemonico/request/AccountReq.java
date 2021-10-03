package api.lemonico.request;

import lombok.Data;
import lombok.NonNull;

/**
 * アカウント登録Bean
 */
@Data
public class AccountReq {

    /**
     * ユニークID
     */
    @NonNull
    private String uid;

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
     * 性別
     */
    @NonNull
    private Integer sex;

    /**
     * 年
     */
    @NonNull
    private Integer year;

    /**
     * 月
     */
    @NonNull
    private Integer month;

    /**
     * 日
     */
    @NonNull
    private Integer day;

    /**
     * メールアドレス
     */
    @NonNull
    private String email;

    /**
     * 有効フラグ
     */
    @NonNull
    private Integer emailVerified;
}
