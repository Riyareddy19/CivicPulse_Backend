
package com.civicpulse.Civicpulse.config;

import com.civicpulse.Civicpulse.service.CustomUserDetailsService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;
import java.util.function.Supplier;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.csrf.CsrfTokenRequestHandler;
import org.springframework.security.web.csrf.XorCsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class SecurityConfig {

    private CsrfTokenRequestHandler spaCsrfTokenRequestHandler() {
        CsrfTokenRequestAttributeHandler plainHandler = new CsrfTokenRequestAttributeHandler();

        XorCsrfTokenRequestAttributeHandler xorHandler = new XorCsrfTokenRequestAttributeHandler();

        return new CsrfTokenRequestHandler() {

            @Override
            public void handle(
                    HttpServletRequest request,
                    HttpServletResponse response,
                    Supplier<CsrfToken> csrfToken) {

                xorHandler.handle(request, response, csrfToken);
            }

            @Override
            public String resolveCsrfTokenValue(
                    HttpServletRequest request,
                    CsrfToken csrfToken) {

                String headerValue = request.getHeader(csrfToken.getHeaderName());

                if (headerValue != null && !headerValue.isBlank()) {
                    return xorHandler.resolveCsrfTokenValue(
                            request, csrfToken);
                }

                return xorHandler.resolveCsrfTokenValue(
                        request, csrfToken);
            }
        };
    }

    @Bean
    public SecurityContextRepository securityContextRepository() {
        return new HttpSessionSecurityContextRepository();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "http://localhost:5174",
                "https://civic-pulse-frontend-eight.vercel.app"));

        configuration.setAllowedMethods(List.of(
                "GET",
                "POST",
                "PATCH",
                "PUT",
                "DELETE",
                "OPTIONS"));

        configuration.setAllowedHeaders(List.of(
                "Content-Type",
                "X-XSRF-TOKEN",
                "X-Requested-With"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            CustomUserDetailsService userDetailsService,
            SecurityContextRepository securityContextRepository)
            throws Exception {

        CookieCsrfTokenRepository csrfTokenRepository = CookieCsrfTokenRepository.withHttpOnlyFalse();

        // Allow the browser to send the CSRF cookie over cross-site HTTPS.
        csrfTokenRepository.setCookieCustomizer(cookie -> cookie.sameSite("None").secure(true));

        http
                .userDetailsService(userDetailsService)

                .cors(cors -> cors.configurationSource(
                        corsConfigurationSource()))

                .csrf(csrf -> csrf
                        .csrfTokenRepository(csrfTokenRepository)
                        .csrfTokenRequestHandler(
                                spaCsrfTokenRequestHandler()))

                .sessionManagement(session -> session
                        .sessionCreationPolicy(
                                SessionCreationPolicy.IF_REQUIRED))

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .securityContext(context -> context
                        .securityContextRepository(
                                securityContextRepository))

                .authorizeHttpRequests(auth -> auth

                        // Public authentication endpoints
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/auth/csrf")
                        .permitAll()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/register",
                                "/api/auth/login")
                        .permitAll()

                        // Require a logged-in user
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/auth/me")
                        .authenticated()

                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/auth/logout")
                        .authenticated()

                        // Deny other authentication endpoints
                        .requestMatchers("/api/auth/**").denyAll()

                        // Admin-only dashboard statistics
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/issues/stats")
                        .hasRole("ADMIN")

                        // Admin-only issue status updates
                        .requestMatchers(
                                HttpMethod.PATCH,
                                "/api/issues/*/status")
                        .hasRole("ADMIN")

                        // Public issue viewing
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/issues",
                                "/api/issues/**")
                        .permitAll()

                        // Creating issues requires authentication
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/issues")
                        .authenticated()

                        // Deny other unlisted API requests
                        .requestMatchers("/api/**").denyAll()

                        .anyRequest().permitAll());

        return http.build();
    }
}
