package com.example.demo;

import com.example.demo.Handler.CustomAccessDeniedHandler;
import com.example.demo.Handler.CustomAuthenticationFailureHandler;
import com.example.demo.Repository.UserRepository;
import com.example.demo.Service.UserSecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.HttpSecurityBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
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
    private final UserRepository userRepository;


    public SecurityConfig(CustomAuthenticationFailureHandler failureHandler, CustomAccessDeniedHandler accessDeniedHandler, UserSecurityService userSecurityService,UserRepository userRepository) {
        this.failureHandler = failureHandler;
        this.accessDeniedHandler = accessDeniedHandler;
        this.userSecurityService = userSecurityService;
        this.userRepository = userRepository;
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
                .formLogin(formLogin->formLogin
                        .loginPage("/login")
                        .defaultSuccessUrl("/main",true)
                        .permitAll()
                        .failureHandler(failureHandler)
                        .usernameParameter("userId")
                        .passwordParameter("userPwd")
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
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(userSecurityService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserSecurityService userSecurityService){
        return userSecurityService;
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CustomAuthenticationProvider customAuthenticationProvider() {
        return new CustomAuthenticationProvider(userSecurityService, passwordEncoder(), userRepository);
    }

}
