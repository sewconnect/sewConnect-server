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
import io.jsonwebtoken.ExpiredJwtException;

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

                // ✅ ENHANCED: Check if token is valid (not expired)
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
                    // ✅ Token is invalid or expired
                    if (requestPath.contains("/api/chat")) {
                        System.out.println("Token validation FAILED");
                    }

                    // ✅ Send 401 Unauthorized response
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Token expired or invalid\", \"message\": \"Please log in again\"}");
                    return; // Don't continue filter chain
                }
            } else if (requestPath.contains("/api/chat")) {
                System.out.println("Username is null or already authenticated");
            }

        } catch (ExpiredJwtException e) {
            // ✅ HANDLE EXPIRED JWT SPECIFICALLY
            System.err.println("🔴 JWT Token expired: " + e.getMessage());

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\": \"Token expired\", \"message\": \"Your session has expired. Please log in again.\"}");
            return; // Don't continue filter chain

        } catch (Exception e) {
            // ✅ HANDLE OTHER JWT EXCEPTIONS
            if (requestPath.contains("/api/chat")) {
                System.err.println("Exception in JWT filter: " + e.getMessage());
                e.printStackTrace();
            }

            // Log the error but continue filter chain for other exceptions
            System.err.println("JWT Filter Exception: " + e.getMessage());
        }

        filterChain.doFilter(request, response);
    }
}