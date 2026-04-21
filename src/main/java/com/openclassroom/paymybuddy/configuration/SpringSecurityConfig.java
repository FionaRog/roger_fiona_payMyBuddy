package com.openclassroom.paymybuddy.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuration de la sécurité de l'application avec Spring Security.
 *
 * <p>Définit les règles d'accès aux ressources, le formulaire de connexion personnalisé,
 * la gestion de la déconnexion et l'encodeur de mot de passe utilisé pour stocker
 * les mots de passe de manière sécurisée.</p>
 */
@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

    /**
     * Définit la chaîne de filtres de sécurité de l'application.
     *
     * <p>Les pages {@code /login} et {@code /register} ainsi que les ressources statiques
     * ({@code /css/**}, {@code /js/**}) sont accessibles sans authentification.
     * Toutes les autres requêtes nécessitent une authentification.</p>
     *
     * <p>Le formulaire de connexion utilise l'email comme identifiant utilisateur,
     * redirige vers {@code /transactions} en cas de succès, et vers
     * {@code /login?error} en cas d'échec.</p>
     *
     * <p>La déconnexion est exposée via {@code /logout} et redirige vers
     * {@code /login?logout} après succès.</p>
     *
     * @param http objet de configuration HTTP fourni par Spring Security
     * @return la chaîne de filtres de sécurité configurée
     * @throws Exception si une erreur survient lors de la configuration de la sécurité
     */
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

    /**
     * Fournit l'encodeur de mot de passe utilisé par l'application.
     *
     * {@link BCryptPasswordEncoder} utilise l'algorithme BCrypt, conçu pour
     * stocker les mots de passe sous forme hachée afin d'améliorer la sécurité.
     *
     * @return une instance de {@link PasswordEncoder} basée sur BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
