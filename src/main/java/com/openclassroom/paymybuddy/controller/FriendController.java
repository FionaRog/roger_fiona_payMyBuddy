package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/*
Contrôleur chargé de l'affichage et de la gestion des amis.

<p> Permet à l'utilisateur d'ajouter un ami. </p>
 */
@Slf4j
@Controller
public class FriendController {

    private final IUserService userService;

    /**
     * Construit le contrôleur avec le service utilisateur nécessaire à la gestion des amis.
     *
     * @param userService service métier des utilisateurs
     */
    public FriendController(IUserService userService) {
        this.userService = userService;
    }

    /**
     * Ajoute un utilisateur à la liste d'amis de l'utilisateur connecté.
     *
     * L'email de l'utilisateur à ajouter est fourni par le formulaire.
     * L'utilisateur connecté est récupéré depuis l'authentification Spring Security.
     * En cas de succès, un message de confirmation est ajouté dans les attributs flash.
     *
     * @param email l'email de l'utilisateur à ajouter comme ami
     * @param authentication objet d'authentification contenant l'utilisateur connecté
     * @param redirectAttributes attributs flash utilisés pour transmettre un message après redirection
     * @return une redirection vers la page {@code friends}
     */
    @PostMapping("/friends/add")
    public String addFriend(@RequestParam String email, Authentication authentication, RedirectAttributes redirectAttributes) {

        String currentUserEmail = authentication.getName();
        log.info("POST_FRIENDS_ADD_INIT - Appel de l'endpoint /friends/add pour l'utilisateur={}", currentUserEmail);

        userService.addFriend(currentUserEmail, email);

        redirectAttributes.addFlashAttribute("successMessage", "Relation ajoutée");

        log.info("POST_FRIENDS_ADD_SUCCESS - Relation {} ajoutée par l'utilisateur={}", email, currentUserEmail);
        return "redirect:/friends";
    }

    /**
     * Affiche la page d'ajout d'amis.
     *
     * @return le nom de la vue {@code friends}
     */
    @GetMapping("/friends")
    public String showFriendPage() {
        log.info("GET_FRIENDS_PAGE_INIT - Affichage de la page d'ajout d'amis (friends)");
        return "friends";
    }
}
