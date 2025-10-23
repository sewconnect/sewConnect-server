package stephenowinoh.spring.security.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import stephenowinoh.spring.security.MyUserDetailsService;

@Configuration
@EnableWebSecurity
public class securityConfiguration {

    @Autowired
    MyUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests(registry -> {
            registry.requestMatchers("/home").permitAll();
            registry.requestMatchers("/admin/**").hasRole("ADMIN");
            registry.requestMatchers("/user/**").hasRole("USER");
            registry.anyRequest().authenticated();

        })
                .formLogin(formLogin -> formLogin.permitAll())
                .build();
    }

//    @Bean
//    public UserDetailsService userDetailsService() {
//        UserDetails normalUser = User.builder()
//                .username("stephen")
//                .password("$2a$12$QFy6WBM/ONKlDcbGgg.Ed.FfPNga87cLGiURi3fokPgQj2H2.KmNC")
//                .roles("USER")
//                .build();
//
//        UserDetails adminUser = User.builder()
//                .username("steve")
//                .password("$2a$12$DSf./jsQHB9t7U3Uv7OIyOwkhTTte5w682a6qUkhJ3B6IAOSZqTfS")
//                .roles("ADMIN", "USER")
//                .build();
//        return new InMemoryUserDetailsManager(normalUser, adminUser);
//    }


    @Bean
    public UserDetailsService userDetailsService(){
        return userDetailsService;

    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {

        // Provide the UserDetailsService to the constructor
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);

        // Use the setter for the password encoder
        provider.setPasswordEncoder(passwordEncoder);

        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();

    }
}


