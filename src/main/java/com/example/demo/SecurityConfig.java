package com.example.demo;

import com.example.demo.Handler.CustomAccessDeniedHandler;
import com.example.demo.Handler.CustomAuthenticationFailureHandler;
import com.example.demo.Service.UserSecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    private final CustomAuthenticationFailureHandler failureHandler;

    private final CustomAccessDeniedHandler accessDeniedHandler;
    private final UserSecurityService userSecurityService;

    public SecurityConfig(CustomAuthenticationFailureHandler failureHandler, CustomAccessDeniedHandler accessDeniedHandler, UserSecurityService userSecurityService) {
        this.failureHandler = failureHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userSecurityService = userSecurityService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests((authorizeHttpRequests) -> authorizeHttpRequests
                        .antMatchers("/", "/login", "/signup", "/css/**").permitAll()
                        .anyRequest().authenticated())
                .headers((headers)-> headers
                        .addHeaderWriter(new XFrameOptionsHeaderWriter(
                                XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN
                        )))
                .formLogin((formLogin)->formLogin
                        .loginPage("/login")
                        .usernameParameter("userId")
                        .passwordParameter("userPwd")
                        .defaultSuccessUrl("/main")
                        .permitAll()
                        .failureUrl("/login")
                )
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(accessDeniedHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/main")
                        .permitAll()
                )
        ;
        return http.build();
    }

    @Bean
    AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder, UserDetailsService userDetailsService) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder)
                .and()
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserSecurityService userSecurityService){
        return userSecurityService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
