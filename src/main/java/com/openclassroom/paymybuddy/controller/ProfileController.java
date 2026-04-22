package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur chagé de l'affichage et de la mise à jour du profil utilisateur.
 *
 * <p>Permet de consulter le profil, de modifier le pseudo, le mot de passe et le solde
 * du compte de l'utilisateur connecté.</p>
 */
@Slf4j
@Controller
public class ProfileController {

    private final IUserService userService;

    /**
     * Construit le contrôleur avec le service utilisateur.
     *
     * @param userService service métier des utilisateurs
     */
    public ProfileController(IUserService userService){
        this.userService = userService;
    }

    /**
     * Affiche la page de profil de l'utilisateur connecté.
     *
     * Le profil est chargé sous forme de DTO et un objet vide de
     * {@link UpdatePasswordRequestDto} est ajouté au modèle pour le formulaire
     * de mise à jour du mot de passe.
     *
     * @param model modèle Spring utilisé pour exposer les données à la vue
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @return le nom de la vue {@code profile}
     */
    @GetMapping("/profile")
    public String getProfile(Model model, Authentication authentication) {
        String email = authentication.getName();
        log.info("GET_PROFILE_INIT - Appel pour l'utilisateur={}", email);
        UserResponseDto userDto = userService.getUserProfile(email);

        model.addAttribute("user", userDto);
        model.addAttribute("updatePasswordRequestDto", new UpdatePasswordRequestDto());

        log.info("GET_PROFILE_SUCCESS - Affichage de la page profile pour l'utilisateur={}", email);
        return "profile";
    }

    /**
     * Met à jour le mot de passe de l'utilisateur connecté.
     *
     * Les valeurs du formulaire sont liées au DTO {@link UpdatePasswordRequestDto}.
     * En cas de succès, un message flash est ajouté avant redirection.
     *
     * @param requestDto objet contenant le mot de passe actuel, le nouveau mot de passe
     *                   et sa confirmation
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @param redirectAttributes attributs flash utilisés pour transmettre un message après redirection
     * @return une redirection vers la page de profil
     */
    @PostMapping("/profile/password")
    public String updatePassword(@ModelAttribute UpdatePasswordRequestDto requestDto,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        log.info("POST_PROFILE_PASSWORD_INIT - Appel pour l'utilisateur={}", email);
        userService.updatePassword(email, requestDto);

        redirectAttributes.addFlashAttribute("successMessage", "Mot de passe mis à jour");

        log.info("POST_PROFILE_PASSWORD_SUCCESS - Mot de passe mis à jour pour l'utilisateur={}", email);
        return "redirect:/profile";
    }

    /**
     * Met à jour le pseudo de l'utilisateur connecté.
     *
     * @param username nouveau pseudo saisi dans le formulaire
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @param redirectAttributes attributs flash utilisés pour transmettre un message après redirection
     * @return une redirection vers la page de profil
     */
    @PostMapping("/profile/username")
    public String updateUsername(@RequestParam String username,
                                 Authentication authentication,
                                 RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        log.info("POST_PROFILE_USERNAME_INIT - Appel pour l'utilisateur={}", email);
        userService.updateUsername(email, username);

        redirectAttributes.addFlashAttribute("successMessage", "Username mis à jour");

        log.info("POST_PROFILE_USERNAME_SUCCESS - Username mis à jour pour l'utilisateur={}", email);
        return "redirect:/profile";
    }

    /**
     * Met à jour le solde de l'utilisateur connecté.
     *
     * Le montant fourni est ajouté au solde courant. Une valeur négative peut
     * être utilisée pour effectuer un retrait.
     *
     * @param amount montant à ajouter ou retirer du solde
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @param redirectAttributes attributs flash utilisés pour transmettre un message après redirection
     * @return une redirection vers la page de profil
     */
    @PostMapping("/profile/balance")
    public String updateBalance(@RequestParam double amount,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        log.info("POST_PROFILE_BALANCE_INIT - Appel pour l'utilisateur={}", email);
        userService.updateBalance(email, amount);

        redirectAttributes.addFlashAttribute("successMessage", "Solde mis à jour");

        log.info("POST_PROFILE_BALANCE_SUCCESS - Solde mis à jour pour l'utilisateur={}", email);
        return "redirect:/profile";
    }
}
