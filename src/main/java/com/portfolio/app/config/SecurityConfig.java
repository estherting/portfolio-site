package com.portfolio.app.config;

import com.portfolio.app.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Whether the H2 web console is enabled (dev only). Drives the console-specific security relaxations below. */
    @Value("${spring.h2.console.enabled:false}")
    private boolean h2ConsoleEnabled;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService uds, PasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(uds);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> {
                auth
                    .requestMatchers("/admin/login").permitAll()
                    .requestMatchers("/css/**", "/js/**", "/images/**", "/uploads/**").permitAll();
                if (h2ConsoleEnabled) {
                    auth.requestMatchers("/h2-console/**").permitAll();
                }
                auth
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll();
            })
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        // The H2 console is served in a frame and posts without CSRF tokens, so it needs
        // these relaxations — but only when the console is actually enabled (dev). In
        // production (console disabled) the default hardened behaviour applies.
        if (h2ConsoleEnabled) {
            http
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .csrf(csrf -> csrf.ignoringRequestMatchers(new AntPathRequestMatcher("/h2-console/**")));
        }

        return http.build();
    }
}
