package com.openclassroom.paymybuddy.controller;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.ITransactionService;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * Contrôleur chargé de la gestion des transactions.
 *
 * <p>Permet de créer une transaction et d'afficher l'historique des transactions
 * de l'utilisateur connecté avec un filtre optionnel.</p>
 */
@Controller
public class TransactionController {

    private final ITransactionService transactionService;

    private final IUserService userService;

    /**
     * Construit le contrôleur avec les services nécessaires aux transactions
     * et aux informations utilisateur.
     *
     * @param transactionService service métier des transactions
     * @param userService service métier des utilisateurs
     */
    public TransactionController(ITransactionService transactionService, IUserService userService) {
        this.transactionService = transactionService;
        this.userService = userService;
    }

    /**
     * Crée une nouvelle transaction à partir des données du formulaire.
     *
     * L'expéditeur est l'utilisateur connecté. En cas de succès, un message flash
     * est ajouté et l'utilisateur est redirigé vers la page des transactions.
     *
     * @param requestDto DTO contenant les informations de la transaction à créer
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @param redirectAttributes attributs flash utilisés pour transmettre un message après redirection
     * @return une redirection vers la page des transactions
     */
    @PostMapping("/transactions/add")
    public String addTransaction(
            @ModelAttribute TransactionRequestDto requestDto,
            Authentication authentication, RedirectAttributes redirectAttributes) {

        String senderEmail = authentication.getName();
        transactionService.addTransaction(senderEmail, requestDto);

        redirectAttributes.addFlashAttribute("successMessage", "Transaction créée");

        return "redirect:/transactions";
    }

    /**
     * Affiche l'historique des transactions de l'utilisateur connecté.
     *
     * Un filtre optionnel permet d'afficher toutes les transactions, uniquement
     * celles envoyées ou uniquement celles reçues.
     *
     * @param filter type de filtre à appliquer ({@code all}, {@code sent} ou {@code received})
     * @param model modèle Spring utilisé pour exposer les données à la vue
     * @param authentication objet d'authentification Spring Security contenant l'utilisateur connecté
     * @return le nom de la vue {@code transactions}
     */
    @GetMapping("/transactions")
    public String getTransactions(@RequestParam(defaultValue = "all") String filter, Model model, Authentication authentication) {

        String email = authentication.getName();

        List<TransactionResponseDto> transactions = transactionService.getUserTransactions(email);
        List<User> friends = userService.getFriendUsernames(email);

        String currentUsername = userService.getUserProfile(email).getUsername();

        List<TransactionResponseDto> filteredTransactions = transactions;

        if("sent".equals(filter)) {
            filteredTransactions = transactions.stream()
                    .filter(transaction -> transaction.getSenderUsername().equals(currentUsername))
                    .toList();
        } else if("received".equals(filter)) {
            filteredTransactions = transactions.stream()
                    .filter(transaction -> transaction.getReceiverUsername().equals(currentUsername))
                    .toList();
        }

        model.addAttribute("transactions", filteredTransactions);
        model.addAttribute("friends", friends);
        model.addAttribute("transactionRequestDto", new TransactionRequestDto());
        model.addAttribute("selectedFilter", filter);


        return "transactions";
    }
}
