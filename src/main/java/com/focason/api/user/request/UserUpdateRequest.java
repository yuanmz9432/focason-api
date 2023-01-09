package com.focason.api.user.request;



import com.focason.api.core.attribute.ID;
import com.focason.api.user.entity.UserEntity;
import java.util.List;
import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;

@AllArgsConstructor(onConstructor = @__(@Autowired))
@Getter
@Builder(toBuilder = true)
@With
@ToString
public class UserUpdateRequest
{

    /** 自動採番ID */
    private final ID<UserEntity> id;

    /** UUID */
    private final String uuid;

    /** ユーザ名 */
    private final String username;

    /** 性別（１：男性、２：女性、９：不明） */
    private final Integer gender;

    /** メールアドレス（ログインID） */
    private final String email;

    /** パスワード */
    private final String password;

    /** ステータス（１：有効、０：無効） */
    private final Integer status;

    /** タイプ（１：本番、２：デモ、９：スーパーユーザ） */
    private final Integer type;

    /** 削除フラグ（0: 未削除 1: 削除済） */
    private final Integer isDeleted;

    /** ユーザーの権限リスト */
    private final List<String> authorities;
}
