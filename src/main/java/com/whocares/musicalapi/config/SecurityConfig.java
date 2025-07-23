package com.whocares.musicalapi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.whocares.musicalapi.security.jwt.JwtAuthenticationFilter;
import com.whocares.musicalapi.security.jwt.JwtTokenProvider;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // 启用CORS配置
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()  // 允许认证相关路径
                        .requestMatchers("/api/musicals").permitAll()  // 允许查看剧目列表
                        .requestMatchers("/api/musicals/**").permitAll()  // 允许查看剧目详情
                        .requestMatchers("/api/theaters").permitAll()  // 允许查看剧场列表
                        .requestMatchers("/api/theaters/**").permitAll()  // 允许查看剧场详情
                        .requestMatchers("/api/reviews/musical/**").permitAll()  // 允许查看评价
                        .requestMatchers("/api/reviews/musical/*/statistics").permitAll()  // 允许查看评价统计
                        .requestMatchers("/api/reviews").permitAll()  // 允许匿名提交评价
                        .requestMatchers("/api/favorites/**").authenticated()  // 收藏相关接口需要认证
                        .requestMatchers("/api/history/**").authenticated()  // 浏览历史相关接口需要认证
                        .requestMatchers("/error").permitAll()         // 允许错误路径
                        .requestMatchers("/actuator/**").permitAll()   // 允许健康检查
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")  // 管理员接口需要ADMIN角色
                        .anyRequest().authenticated()                  // 其他请求需要认证
                )
                .csrf(csrf -> csrf.disable())                         // 禁用CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));  // 无状态会话
        
        // 添加JWT过滤器
        http.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));  // 允许所有来源
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));  // 允许的HTTP方法
        configuration.setAllowedHeaders(Arrays.asList("*"));         // 允许所有请求头
        configuration.setAllowCredentials(true);                     // 允许凭据（如Cookie）

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);     // 对所有路径应用CORS配置
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService);
    }
}