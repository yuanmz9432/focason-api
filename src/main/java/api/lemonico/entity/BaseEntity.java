package api.lemonico.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.seasar.doma.Column;
import org.seasar.doma.Entity;
import org.seasar.doma.Version;

import java.time.LocalDateTime;

@Entity(listener = BaseListener.class)
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BaseEntity {
    @Version
    @Column(name = "version_no")
    Integer versionNo;
    @Column(name = "create_by")
    String createBy;
    @Column(name = "create_time")
    LocalDateTime createTime;
    @Column(name = "update_by")
    String updateBy;
    @Column(name = "update_time")
    LocalDateTime updateTime;
    @Column(name = "is_actived")
    Integer isActived;
}
