package api.lemonico.model;

import api.lemonico.entity.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {

    private User user;

    public UserInfo(User user) {
        this.user = user;
    }
}
