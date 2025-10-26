package stephenowinoh.spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import stephenowinoh.spring.security.MyUserDetailsService;
import stephenowinoh.spring.security.token.JwtAuthenticationFilter;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class securityConfiguration {

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(registry -> {
                    // ✅ PUBLIC ENDPOINTS - NO AUTHENTICATION REQUIRED
                    registry.requestMatchers(
                            "/home",
                            "/register/**",
                            "/authenticate"
                    ).permitAll();

                    //ADMIN-ONLY ENDPOINTS
                    registry.requestMatchers("/admin/**").hasRole("ADMIN");

                    //CLIENT-ONLY ENDPOINTS
                    registry.requestMatchers("/client/**").hasRole("CLIENT");

                    //TAILOR-ONLY ENDPOINTS
                    registry.requestMatchers("/tailor/**").hasRole("TAILOR");

                    //TAILORS CAN CREATE/UPDATE/DELETE THEIR SERVICES AND GALLERIES
                    registry.requestMatchers(
                            "/api/services",
                            "/api/services/*",
                            "/api/galleries",
                            "/api/galleries/*"
                    ).hasAnyRole("TAILOR", "ADMIN");

                    // ALL OTHER ENDPOINTS REQUIRE AUTHENTICATION
                    // This includes:
                    // - /api/tailors/** (view tailors, profiles) - any authenticated user
                    // - /api/follows/** (follow/unfollow) - any authenticated user
                    // - Any other API endpoints - any authenticated user
                    registry.anyRequest().authenticated();
                })
                .authenticationProvider(authenticationProvider(userDetailsService, passwordEncoder()))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService(){
        return userDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(List<AuthenticationProvider> authenticationProviders) {
        return new ProviderManager(authenticationProviders);
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
