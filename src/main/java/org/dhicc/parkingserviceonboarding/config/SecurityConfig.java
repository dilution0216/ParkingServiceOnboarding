//package org.dhicc.parkingserviceonboarding.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//import static org.springframework.security.config.Customizer.withDefaults;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화 (필요 시 활성화 가능)
//                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers(
//                                "/swagger-ui/**", "/v3/api-docs/**", "/swagger-resources/**", "/webjars/**"
//                        ).permitAll() // Swagger 관련 URL 인증 없이 허용
//                        .anyRequest().authenticated()
//                )
//                .httpBasic(withDefaults())
//                .formLogin(withDefaults());
//
//        return http.build();
//    }
//}
