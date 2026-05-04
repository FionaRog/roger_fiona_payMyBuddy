package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Contrôleur chargé de l'inscription des nouveaux utilisateurs.
 *
 * <p>Reçoit les informations du formulaire d'inscription, crée un nouvel utilisateur
 * et le persiste via le service utilisateur après encodage du mot de passe.</p>
 */
@Slf4j
@Controller
public class RegisterController {

    private final IUserService userService;

    private final PasswordEncoder passwordEncoder;

    /**
     * Construit le contrôleur avec le service utilisateur et l'encodeur de mot de passe.
     *
     * @param userService service métier des utilisateurs
     * @param passwordEncoder encodeur de mot de passe utilisé pour sécuriser le mot de passe
     */
    public RegisterController(IUserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Traite la soumission du formulaire d'inscription.
     *
     * Crée un nouvel utilisateur avec un solde initial à zéro, encode son mot de passe,
     * puis l'enregistre en base de données.
     *
     * @param email adresse email du nouvel utilisateur
     * @param username pseudo choisi
     * @param password mot de passe en clair saisi lors de l'inscription
     * @return une redirection vers la page de connexion
     */
    @PostMapping("/register")
    public String register(@RequestParam String email, @RequestParam String username, @RequestParam String password) {

        log.info("POST_REGISTER_INIT - Appel d'inscription");
        log.debug("POST_REGISTER_PARAMS - Email={}", email);

        User user = new User();
        user.setEmail(email);
        user.setPassword(password);
        user.setUsername(username);
        user.setBalance(0.0);

        userService.addUser(user);

        log.info("POST_REGISTER_SUCCESS - Inscription réalisée pour l'utilisateur={}", email);
        return "redirect:/login";
    }
}
