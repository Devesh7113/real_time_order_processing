package com.example.real_time_order_processing.auth.service;

import com.example.real_time_order_processing.auth.entity.UserInfo;
import com.example.real_time_order_processing.auth.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserInfoService implements UserDetailsService
{
    private final UserInfoRepository userInfoRepository;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        Optional< UserInfo> userInfo = userInfoRepository.findByEmail(username);

        if(userInfo.isEmpty())
        {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        UserInfo user = userInfo.get();

        List<GrantedAuthority> authorities = List.of(user.getRoles().split(","))
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(user.getEmail(), user.getPassword(), authorities);
    }

    public String addUser(UserInfo userInfo)
    {
        Optional<UserInfo> optionalUserInfo = userInfoRepository.findByNameOrEmail(userInfo.getName(), userInfo.getEmail());

        if(optionalUserInfo.isPresent())
        {
            throw new RuntimeException("Account already present with this user name or email.");
        }

        userInfo.setPassword(encoder.encode(userInfo.getPassword()));
        userInfo.setRoles("ROLE_USER");
        userInfoRepository.save(userInfo);
        return "User added successfully.";
    }
}
