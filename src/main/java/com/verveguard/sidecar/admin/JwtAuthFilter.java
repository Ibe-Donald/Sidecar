package com.verveguard.sidecar.admin;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 1. Check if they are carrying the badge in the HTTP "Authorization" header
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // If there is no header, or it doesn't start with "Bearer ", let them pass to the next gate.
        // (They might be trying to hit the open /api/v1/transactions/verify endpoint, which is fine).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extract the token by removing the "Bearer " prefix (the first 7 characters)
        jwt = authHeader.substring(7);
        username = jwtService.extractUsername(jwt);

        // 3. If the badge has a name, and they aren't already authenticated in this exact moment...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // 4. Validate the badge
            if (jwtService.isTokenValid(jwt, username)) {

                // For this sidecar, we create a simple UserDetails object in memory
                UserDetails userDetails = new User(username, "", new ArrayList<>());

                // 5. Open the door! Tell Spring Security this user is officially authenticated
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 6. Continue processing the request
        filterChain.doFilter(request, response);
    }
}