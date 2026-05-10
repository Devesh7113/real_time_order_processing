package com.example.real_time_order_processing.auth.service;

import com.example.real_time_order_processing.auth.dto.AdminUserSummaryDTO;
import com.example.real_time_order_processing.auth.entity.UserInfo;
import com.example.real_time_order_processing.auth.repository.UserInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminUserService
{
    private final UserInfoRepository userInfoRepository;

    public List<AdminUserSummaryDTO> searchByEmailFragment(String query)
    {
        String q = query != null ? query.trim() : "";
        if (q.length() < 2)
        {
            throw new IllegalArgumentException("Enter at least 2 characters to search by email.");
        }
        return userInfoRepository.findTop30ByEmailContainingIgnoreCaseOrderByEmailAsc(q).stream()
                .map(u -> new AdminUserSummaryDTO(
                        u.getId(),
                        u.getEmail(),
                        u.getUserName(),
                        u.getRoles()
                ))
                .toList();
    }

    @Transactional
    public String grantAdmin(String email)
    {
        String e = email != null ? email.trim() : "";
        if (e.isBlank())
        {
            throw new IllegalArgumentException("Email is required.");
        }
        UserInfo user = userInfoRepository.findByEmail(e)
                .orElseThrow(() -> new IllegalArgumentException("No user found with that email."));
        user.setRoles(RoleAuthorityMapper.grantAdminRolesCsv(user.getRoles()));
        userInfoRepository.save(user);
        return "Admin role granted. The user must sign in again to receive a token with ROLE_ADMIN.";
    }
}
