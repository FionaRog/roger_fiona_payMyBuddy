package com.openclassroom.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return
                http
                        .authorizeHttpRequests(auth -> {
                            auth.requestMatchers("/login", "/register").permitAll();
                            auth.requestMatchers("/css/**", "/js/**").permitAll();
                            auth.anyRequest().authenticated();
                        })
                        .formLogin(form -> form
                                .loginPage("/login").loginProcessingUrl("/login")
                                .usernameParameter("email").passwordParameter("password")
                                .defaultSuccessUrl("/transactions", true)
                                .failureUrl("/login?error")
                                .permitAll()
                        )
                        .logout(logout -> logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/login?logout")
                        )
                        .build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
