package stephenowinoh.spring.security.token;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import stephenowinoh.spring.security.MyUserDetailsService;

import java.io.IOException;

@Configuration
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String requestPath = request.getRequestURI();

        // Skip JWT filter for WebSocket endpoints - authentication happens in STOMP interceptor
        if (requestPath.startsWith("/ws")) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");

        // Debug logging for chat endpoints
        if (requestPath.contains("/api/chat")) {
            System.out.println("=== JWT Filter for Chat Endpoint ===");
            System.out.println("Path: " + requestPath);
            System.out.println("Auth Header: " + (authHeader != null ? "Present (Bearer...)" : "MISSING"));
        }

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            if (requestPath.contains("/api/chat")) {
                System.out.println("No Bearer token found, continuing filter chain");
            }
            filterChain.doFilter(request, response);
            return;
        }

        try {
            String jwt = authHeader.substring(7);
            String username = jwtService.extractUsername(jwt);

            if (requestPath.contains("/api/chat")) {
                System.out.println("Extracted username: " + username);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = myUserDetailService.loadUserByUsername(username);

                if (userDetails != null && jwtService.isTokenValid(jwt)) {
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    if (requestPath.contains("/api/chat")) {
                        System.out.println("Authentication SUCCESS for: " + username);
                        System.out.println("Authorities: " + userDetails.getAuthorities());
                    }
                } else {
                    if (requestPath.contains("/api/chat")) {
                        System.out.println("Token validation FAILED");
                    }
                }
            } else if (requestPath.contains("/api/chat")) {
                System.out.println("Username is null or already authenticated");
            }
        } catch (Exception e) {
            if (requestPath.contains("/api/chat")) {
                System.err.println("Exception in JWT filter: " + e.getMessage());
                e.printStackTrace();
            }
        }

        filterChain.doFilter(request, response);
    }
}