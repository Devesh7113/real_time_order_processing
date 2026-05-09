package com.example.real_time_order_processing.auth.service;

import com.example.real_time_order_processing.auth.entity.UserInfo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserInfoDetails implements UserDetails
{
    private final String userName;
    private final String password;
    private final List<GrantedAuthority> authorities;
    private final String displayName;

    public UserInfoDetails(UserInfo userInfo)
    {
        this.userName = userInfo.getEmail();
        this.password = userInfo.getPassword();
        this.authorities = RoleAuthorityMapper.fromRolesCsv(userInfo.getRoles());
        String registered = userInfo.getUserName();
        this.displayName = registered != null && !registered.isBlank()
                ? registered.trim()
                : userInfo.getEmail();
    }

    public String getDisplayName()
    {
        return displayName;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }
}
