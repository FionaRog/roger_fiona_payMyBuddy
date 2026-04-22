package com.openclassroom.paymybuddy.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Contrôleur d'affichage des pages publiques de l'application.
 *
 * Fournit l'accès aux vues de connexion et d'inscription.
 */
@Slf4j
@Controller
public class ViewController {

    /**
     * Affiche la page de connexion.
     *
     * @return le nom de la vue {@code login}
     */
    @GetMapping("/login")
    public String showLoginPage() {

        log.info("GET_LOGIN_PAGE - Affichage de la page de connexion");
        return "login";
    }

    /**
     * Affiche la page d'inscription.
     *
     * @return le nom de la vue {@code register}
     */
    @GetMapping("/register")
    public String showRegisterPage() {

        log.info("GET_REGISTER_PAGE - Affichage de la page d'inscription");
        return "register";
    }
}
