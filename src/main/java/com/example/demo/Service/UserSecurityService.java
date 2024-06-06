package com.example.demo.Service;

import com.example.demo.Entity.UserEntity;
import com.example.demo.Repository.UserRepository;
import com.example.demo.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.support.ReflectivePropertyAccessor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.Exceptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserSecurityService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, BadCredentialsException {
        Optional<UserEntity> _siteUser = userRepository.findByUserId(username);
        if (_siteUser.isEmpty()) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 일치하지 않습니다.");

        }


        UserEntity siteuser = _siteUser.get();


        ArrayList<GrantedAuthority> Authorities = new ArrayList<>();
        if ("admin".equals(username)) {
            Authorities.add(new SimpleGrantedAuthority(UserRole.ADMIN.getValue()));
        }else {
            Authorities.add(new SimpleGrantedAuthority(UserRole.USER.getValue()));
        }
        return new User(siteuser.getUserName(),siteuser.getUserPwd(),Authorities);

    }
}
