package com.example.real_time_order_processing.auth.controller;

import com.example.real_time_order_processing.auth.dto.AdminUserSummaryDTO;
import com.example.real_time_order_processing.auth.dto.GrantAdminRequest;
import com.example.real_time_order_processing.auth.service.AdminUserService;
import com.example.real_time_order_processing.utils.ExceptionUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/users")
public class AdminUserController
{
    private final AdminUserService adminUserService;

    @GetMapping("/search")
    ResponseEntity<?> search(@RequestParam("q") String q)
    {
        try
        {
            List<AdminUserSummaryDTO> list = adminUserService.searchByEmailFragment(q);
            return ResponseEntity.ok(list);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }

    @PostMapping("/grant-admin")
    ResponseEntity<?> grantAdmin(@RequestBody @Valid GrantAdminRequest request)
    {
        try
        {
            String message = adminUserService.grantAdmin(request.getEmail());
            return ResponseEntity.ok(message);
        }
        catch (Exception e)
        {
            return ExceptionUtils.handleException(e);
        }
    }
}
