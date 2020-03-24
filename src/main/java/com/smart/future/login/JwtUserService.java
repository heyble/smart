package com.smart.future.login;

import com.smart.future.user.service.IUserService;
import com.smart.future.user.vo.RoleVO;
import com.smart.future.user.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class JwtUserService implements UserDetailsService {

    @Autowired
    private IUserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationCredentialsNotFoundException {
        UserVO userVO = userService.findUserByPhoneId(Long.valueOf(username));
        if (userVO == null || userVO.getRoles() == null) {
            throw new AuthenticationCredentialsNotFoundException("用户名或者密码不正确");
        }
        String[] roles = userVO.getRoles().stream().map(RoleVO::getCode).toArray(String[]::new);
        return User.builder()
                .username(userVO.getPhoneId().toString())
                .password(userVO.getPassword())
                .roles(roles)
                .build();
    }
}
