package com.openclassroom.paymybuddy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur d'affichage des pages publiques de l'application.
 *
 * Fournit l'accès aux vues de connexion et d'inscription.
 */
@Controller
public class ViewController {

    /**
     * Affiche la page de connexion.
     *
     * @return le nom de la vue {@code login}
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Affiche la page d'inscription.
     *
     * @return le nom de la vue {@code register}
     */
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }
}
