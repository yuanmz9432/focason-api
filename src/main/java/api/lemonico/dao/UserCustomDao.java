package api.lemonico.dao;

import api.lemonico.annotation.InjectConfig;
import api.lemonico.entity.User;
import org.seasar.doma.Dao;
import org.seasar.doma.Select;
import org.springframework.stereotype.Component;

import java.util.List;

@Dao
@InjectConfig
@Component(value = "fileInfoCustomDao")
public interface UserCustomDao {

//    @Select
//    User selectUserByUserName(String userName);
//
//    @Select
//    User selectUserByUserId(Integer userId);
//
    @Select
    User selectAccountByEmail(String email);
//
//    @Select
//    List<Address> selectAddressesByUserId(Integer userId);
//
//    @Select
//    List<Address> selectAddressesByUserIdAndAddressId(Integer userId, Integer addressId);
}
