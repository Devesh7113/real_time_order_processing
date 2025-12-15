package com.example.real_time_order_processing.auth.repository;

import com.example.real_time_order_processing.auth.entity.UserInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserInfoRepository extends JpaRepository<UserInfo, Long>
{
    Optional<UserInfo> findByEmail(String email);

    Optional<UserInfo> findByNameOrEmail(String userName, String email);
}
