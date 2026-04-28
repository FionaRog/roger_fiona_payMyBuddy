package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.service.IBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Contrôleur gérant les opérations de dépôt et de retrait sur le solde d'un utilisateur.
 *
 * <p> Permet à un utilisateur connecté d'ajouter ou de retirer des fonds sur son propre compte.</p>
 */
@Controller
@Slf4j
public class BalanceController {

    private final IBalanceService balanceService;

    /**
     * Construit le contrôleur avec le service de gestion de solde.
     *
     * @param balanceService service de gestion de solde
     */
    public BalanceController(IBalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Effectue un dépôt sur le solde de l'utilisateur connecté.
     *
     * Le montant indiqué est ajouté au solde du compte de l'utilisateur.
     * En cas de succès, un message de confirmation est ajouté aux attributs flash
     * et l'utilisateur est redirigé vers sa page de profil.
     *
     * @param amount le montant à déposer (doit être strictement supérieur à 0)
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @param redirectAttributes attributs flash utilisés après redirection
     * @return une redirection vers la page de profil
     */
    @PostMapping("/profile/deposit")
    public String deposit(@RequestParam double amount,
                          Authentication authentication,
                          RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        log.info("POST_BALANCE_DEPOSIT_INIT - Appel pour l'utilisateur={}", email);

        balanceService.deposit(email, amount);

        redirectAttributes.addFlashAttribute("successMessage", "Montant ajouté avec succès.");

        log.info("POST_BALANCE_DEPOSIT_SUCCESS - Dépôt de {} effectué pour l'utilisateur={}", amount, email);
        return "redirect:/profile";
    }

/**
 * Effectue un retrait sur le solde de l'utilisateur connecté.
 *
 * Le montant indiqué est soustrait du solde du compte de l'utilisateur.
 * Le montant doit être strictement positif, et le solde après retrait
 * ne doit pas devenir négatif. En cas de succès, un message de confirmation
 * est ajouté aux attributs flash et l'utilisateur est redirigé vers sa
 * page de profil.
 *
 * @param amount le montant à retirer (doit être strictement supérieur à 0)
 * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
 * @param redirectAttributes attributs flash utilisés après redirection
 * @return une redirection vers la page de profil
 */
 @PostMapping("/profile/withdraw")
    public String withdraw(@RequestParam double amount,
                           Authentication authentication,
                           RedirectAttributes redirectAttributes) {

        String email = authentication.getName();
        log.info("POST_BALANCE_WITHDRAW_INIT - Appel pour l'utilisateur={}", email);

        balanceService.withdraw(email, amount);

        redirectAttributes.addFlashAttribute("successMessage", "Montant retiré avec succès.");

        log.info("POST_BALANCE_WITHDRAW_SUCCESS - Retrait de {} effectué pour l'utilisateur={}", amount, email);
         return "redirect:/profile";
    }
}
