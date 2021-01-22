package api.lemonico.model;

import api.lemonico.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;

@Entity
@Data
@EqualsAndHashCode(callSuper=false)
public class User extends BaseEntity {

    /** 用户ID */
    @Column(name = "id")
    private Integer userId;

    /** 用户名 */
    @Column(name = "name")
    private String userName;

    /** 邮箱账号 */
    @Column(name = "email")
    private String email;
}
