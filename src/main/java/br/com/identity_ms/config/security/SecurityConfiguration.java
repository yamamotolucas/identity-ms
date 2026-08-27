package br.com.identity_ms.config.security;

import br.com.identity_ms.config.filters.token.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    public SecurityConfiguration(CustomAccessDeniedHandler customAccessDeniedHandler) {
        this.customAccessDeniedHandler = customAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, TokenFilter tokenFilter) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .authorizeHttpRequests(authorizeRequests -> authorizeRequests

                        //SWAGGER
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger.yaml").permitAll()

                        //AUTH
                        .requestMatchers(HttpMethod.POST, "/v1/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/v1/auth/register").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/auth/validate").permitAll()

                        //OAUTH2
                        .requestMatchers(HttpMethod.GET, "/v1/auth/oauth2/**").permitAll()

                        //USER
                        .requestMatchers(HttpMethod.POST, "/v1/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/v1/users/me").hasAnyRole("ADMIN", "CUSTOMER", "BARBER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/users/update").hasAnyRole("ADMIN", "CUSTOMER", "BARBER")

                        // BARBER
                        .requestMatchers(HttpMethod.GET, "/v1/barber/findAll").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/barber/findById/*").permitAll()
                        .requestMatchers(HttpMethod.GET, "/v1/barber/me").hasRole("BARBER")
                        .requestMatchers(HttpMethod.PATCH, "/v1/barber/*").hasRole("BARBER")

                        .anyRequest()
                        .authenticated())
                .addFilterBefore(
                        tokenFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    //ADMIN > MANAGER > BARBER > CUSTOMER
    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy(
                "ROLE_ADMIN > ROLE_MANAGER\n" +
                        "ROLE_MANAGER > ROLE_BARBER\n" +
                        "ROLE_BARBER > ROLE_CUSTOMER"
        );
    }
}
