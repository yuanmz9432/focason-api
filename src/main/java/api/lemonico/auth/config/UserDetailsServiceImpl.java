/*
 * Copyright 2021 Lemonico Co.,Ltd. AllRights Reserved.
 */
package api.lemonico.auth.config;



import api.lemonico.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class UserDetailsServiceImpl implements UserDetailsService
{

    private final UserService service;

    @Override
    public UserDetails loadUserByUsername(String email) {
        return service.getLoginUserByEmail(email);
    }

}
