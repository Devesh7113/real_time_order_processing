package com.example.real_time_order_processing.auth.filter;

import com.example.real_time_order_processing.auth.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter
{
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String userName = null;

        if (authHeader != null && authHeader.startsWith("Bearer"))
        {
            token = authHeader.substring(7).trim();
            if (!token.isEmpty())
            {
                try
                {
                    userName = jwtService.extractUsername(token);
                }
                catch (JwtException ex)
                {
                    writeJsonUnauthorized(response);
                    return;
                }
            }
        }

        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            try
            {
                if (jwtService.validateToken(token, userDetails))
                {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
                else
                {
                    writeJsonUnauthorized(response);
                    return;
                }
            }
            catch (JwtException ex)
            {
                writeJsonUnauthorized(response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private static void writeJsonUnauthorized(HttpServletResponse response) throws IOException
    {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"message\":\"Session expired. Please sign in again.\"}");
    }
}
