package api.lemonico.info;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfo implements Serializable {

    private User user;
//    private List<Address> addresses;
//
//    public UserInfo(User user, List<Address> addresses){
//        this.user = user;
//        this.addresses = addresses;
//    }
}
