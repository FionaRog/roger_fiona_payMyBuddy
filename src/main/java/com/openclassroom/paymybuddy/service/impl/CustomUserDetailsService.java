package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation personalisée de {@link UserDetailsService} pour Spring Security.
 * <p> Permet de charger un utilisateur par son email (utilisé comme identifiant) et
 * de lui attribuer un rôle par défaut {@code ROLE_USER}.
 * Cette classe est utilisée par Spring Security lors de l'authentification.</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Charge un utilisateur à partir de son email et retourne un objet {@link UserDetails}
     * compatible avec Spring Security.
     * <p> L'email fourni est utilisé comme "username" dans l'authentification.
     * L'utilisateur est affécté au rôle {@code ROLE_USER}.</p>
     *
     * @param username l'email de l'utilisateur à charger (non {@code null})
     * @return une instance de {@link UserDetails} représentant l'utilisateur chargé
     * @throws UsernameNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        com.openclassroom.paymybuddy.model.User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(user.getEmail(), user.getPassword(), authorities);
    }
}
