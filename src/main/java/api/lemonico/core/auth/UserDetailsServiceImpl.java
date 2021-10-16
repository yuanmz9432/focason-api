package api.lemonico.core.auth;



import api.lemonico.customer.service.CustomerService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService
{

    private final CustomerService service;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return service.getLoginUserByEmail(email);
    }

}
