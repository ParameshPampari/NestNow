package com.nestnow.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )
                .authorizeHttpRequests(auth -> auth

                        // PUBLIC AUTH APIs
                        .requestMatchers("/api/auth/**").permitAll()

                        // SWAGGER / OPENAPI
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**"
                        ).permitAll()

                        // PUBLIC CATEGORY VIEW APIs
                        .requestMatchers(HttpMethod.GET,
                                "/api/categories/**").permitAll()

                        // PUBLIC MARKETPLACE VIEW APIs
                        .requestMatchers(HttpMethod.GET,
                                "/api/services/**").permitAll()

                        .requestMatchers("/api/professionals/profile")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET,
                                "/api/professionals/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/api/reviews/professional/**").permitAll()

                        // ADMIN ONLY
                        .requestMatchers("/api/admin/**")
                        .hasAuthority("ADMIN")

                        .requestMatchers("/api/categories/create")
                        .hasAuthority("ADMIN")

                        .requestMatchers("/api/services/create")
                        .hasAuthority("ADMIN")

                        .requestMatchers("/api/reviews/*/flag")
                        .hasAuthority("ADMIN")

                        // ALL OTHER APIS REQUIRE AUTH
                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        jwtFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }
}
