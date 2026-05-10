package com.example.real_time_order_processing.auth.service;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps the comma-separated {@code user_info.roles} string to Spring Security authorities.
 * <p>
 * {@code hasAuthority("ROLE_ADMIN")} requires the exact prefix {@code ROLE_}. Values like
 * {@code ADMIN} or {@code ROLE_ADMIN } (spaces) would otherwise deny access while the JWT
 * might still show admin in the UI.
 */
public final class RoleAuthorityMapper
{
    private RoleAuthorityMapper()
    {
    }

    public static List<GrantedAuthority> fromRolesCsv(String roles)
    {
        if (roles == null || roles.isBlank())
        {
            return List.of(new SimpleGrantedAuthority("ROLE_USER"));
        }
        List<GrantedAuthority> list = Arrays.stream(roles.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(RoleAuthorityMapper::normalizeRole)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return list.isEmpty() ? List.of(new SimpleGrantedAuthority("ROLE_USER")) : list;
    }

    /**
     * Merges {@code ROLE_ADMIN} into the persisted CSV while keeping existing roles and ensuring {@code ROLE_USER}.
     */
    public static String grantAdminRolesCsv(String currentCsv)
    {
        LinkedHashSet<String> roles = new LinkedHashSet<>();
        if (currentCsv != null && !currentCsv.isBlank())
        {
            Arrays.stream(currentCsv.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .map(RoleAuthorityMapper::normalizeRole)
                    .forEach(roles::add);
        }
        roles.add("ROLE_ADMIN");
        if (!roles.contains("ROLE_USER"))
        {
            roles.add("ROLE_USER");
        }
        return String.join(",", roles);
    }

    private static String normalizeRole(String raw)
    {
        String r = raw.trim();
        if (r.isEmpty())
        {
            return "ROLE_USER";
        }
        String upper = r.toUpperCase();
        if (upper.startsWith("ROLE_"))
        {
            return "ROLE_" + upper.substring("ROLE_".length());
        }
        if ("ADMIN".equals(upper))
        {
            return "ROLE_ADMIN";
        }
        if ("USER".equals(upper))
        {
            return "ROLE_USER";
        }
        return r;
    }
}
