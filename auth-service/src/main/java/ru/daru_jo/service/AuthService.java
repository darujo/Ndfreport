package ru.daru_jo.service;


import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.daru_jo.dto.jwt.JwtResponse;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;
import ru.daru_jo.model.User;
import ru.daru_jo.utils.JwtTokenUtils;

import java.util.Collection;
import java.util.HashSet;

@Service
public class AuthService implements UserDetailsService {
    private UserService userService;
    private JwtTokenUtils jwtTokenUtils;


    @Autowired
    public void setJwtTokenUtils(JwtTokenUtils jwtTokenUtils) {
        this.jwtTokenUtils = jwtTokenUtils;
    }


    @Autowired
    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public @NonNull UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userService.loadUserByNikName(username);

        if (user.isBlock()) {
            throw new ResourceNotFoundRunTime("Пользователь заблокирован");
        }

        if (!user.getRecovery() && user.getCodeEmail().isEmpty() ) {
            throw new ResourceNotFoundRunTime("Подтвердите почту");
        }

        return new org.springframework.security.core.userdetails.User(user.getNikName(), user.getPassword(),
                mapGrandAuthority()
        );// нужно для спринга
    }

    private Collection<? extends GrantedAuthority> mapGrandAuthority() {
        Collection<SimpleGrantedAuthority> grantedAuthorities;
        grantedAuthorities = new HashSet<>();
//        grantedAuthorities.addAll(rights.stream().map(right -> new SimpleGrantedAuthority(right.getName())).toList());
        return grantedAuthorities;
    }

    public @NonNull User getUser(@NonNull String username) throws UsernameNotFoundException {
        User user = userService.loadUserByNikName(username);
        if (user.isBlock()) {
            throw new ResourceNotFoundRunTime("Пользователь заблокирован");
        }
        user = userService.saveUser(user);
        return user;
    }


    @Transactional
    public String createAuthToken(String userName) {
        User user = getUser(userName);
        return jwtTokenUtils.generateToken(user);
    }

    @Transactional
    public String newToken(String userName) {
        User user = getUser(userName);
        return jwtTokenUtils.generateToken(user);

    }

    @Transactional
    public JwtResponse restorePassword(String nikName, String code) {
        if(userService.restorePassword(nikName,code)){
            return new JwtResponse(newToken(nikName));
        }
        throw  new ResourceNotFoundRunTime("Ссылка не просрочена");
    }
}
