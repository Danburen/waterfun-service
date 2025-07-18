package org.waterwood.waterfunservice.confirguation;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableWebSecurity(debug=false)
public class SecurityConfig {
    private final PublicKey jwtVerificationKey;

    public SecurityConfig(PublicKey jwtVerificationKey) {
        this.jwtVerificationKey = jwtVerificationKey;
    }
    @Bean
    @Order(1)
    public SecurityFilterChain loginFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors->cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        .requireCsrfProtectionMatcher(request ->
                                !request.getMethod().equalsIgnoreCase("GET"))
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                .securityMatcher("/api/auth/**")
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                )
                .formLogin(form -> form.disable());
        return http.build();
    }
    @Bean
    @Order(2)
    public SecurityFilterChain resourceFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/resource/**")
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .csrf(csrf -> csrf.disable());
        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors-> cors.configurationSource(corsConfigurationSource()))
                .securityMatcher("/api/content/**")
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .oauth2ResourceServer(oauth2-> oauth2.jwt(jwt-> jwt.decoder(jwtDecoder())))
                .formLogin(form-> form.disable());
        return http.build();
    }

    @Bean
    @Order(4)
    public SecurityFilterChain redisTokenFilterChain(HttpSecurity http) throws Exception {
        http.securityMatcher("/api/admin/**")
                .cors(cors-> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth->auth.anyRequest().authenticated())
                .formLogin(form-> form.disable());
        return http.build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:5173", "http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withPublicKey((RSAPublicKey) jwtVerificationKey).build();
    }
}