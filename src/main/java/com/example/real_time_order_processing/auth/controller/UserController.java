package com.example.real_time_order_processing.auth.controller;

import com.example.real_time_order_processing.auth.request.AuthRequest;
import com.example.real_time_order_processing.auth.entity.UserInfo;
import com.example.real_time_order_processing.auth.service.JwtService;
import com.example.real_time_order_processing.auth.service.UserInfoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class UserController
{
    private final UserInfoService userInfoService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder encoder;

    @GetMapping("/welcome")
    public String welcome()
    {
        return "Welcome to your website.";
    }

    @PostMapping("/addNewUser")
    public String addNewUser(@RequestBody UserInfo userInfo)
    {
        return userInfoService.addUser(userInfo);
    }


    @PostMapping("/generateToken")
    public String authenticateAndGetToken(@RequestBody AuthRequest authRequest)
    {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        if (authentication.isAuthenticated())
        {
            return jwtService.generateToken(authRequest.getUsername());
        }
        else
        {
            throw new UsernameNotFoundException("Invalid user request!");
        }
    }

}
