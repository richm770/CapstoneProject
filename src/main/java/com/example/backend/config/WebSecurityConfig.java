package com.example.backend.config;

import com.example.backend.repository.UserRepository;
import com.example.backend.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/***
 * The web security configuration class
 * This class configures the security settings for the application
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    /***
     * The filter chain for the security configuration
     * @param http The http security
     * @return The security filter chain
     * @throws Exception If an exception occurs
     */
    @Bean
    @Order(1)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/auth/login", "/error", "/auth/student-signup", "/auth/faculty-signup").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/request/leaveRequest", "/request/courseRegistrationRequest", "/request/housingRequest").hasAuthority("STUDENT")
                        .anyRequest().authenticated()
                )
                .csrf(Customizer.withDefaults())
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(Customizer.withDefaults())
                .formLogin(form -> form
                        .loginPage("/auth/login")
                        .loginProcessingUrl("/auth/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                ).logout(logout -> logout
                        .logoutUrl("/auth/logout")
                        .logoutSuccessUrl("/auth/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    /***
     * The custom user details service
     * @param userRepository The user repository
     * @return The custom user details service
     */
    @Bean
    CustomUserDetailsService customUserDetailsService(UserRepository userRepository) {
        return new CustomUserDetailsService(userRepository);
    }

    /***
     * The password encoder
     * @return The password encoder
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
