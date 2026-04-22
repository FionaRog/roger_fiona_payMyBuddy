package com.openclassroom.paymybuddy.exception;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Gestion globale des exceptions métier de l'application.
 *
 * <p>Intercepte toutes les {@link BusinessException} levées par les services
 * et redirige l'utilisateur vers la page des transactions avec un message d'erreur.</p>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Gère toutes les exceptions métier de l'application.
     *
     * Ajoute un message d'erreur dans les attributs flash afin qu'il soit
     * affiché sur la page de destination après la redirection.
     *
     * @param ex l'exception métier levée
     * @param redirectAttributes attributs flash utilisés après redirection
     * @return une redirection vers la page principale des transactions
     */
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex,
                                          RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        return "redirect:/transactions";
    }

    /**
     * Gère les exceptions non anticipées.
     *
     * Pour des raisons de sécurité, le message technique n’est pas affiché à l’utilisateur.
     * Seul un message générique est exposé côté frontend.
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        System.err.println("Erreur interne non gérée : " + ex.getMessage());
        ex.printStackTrace();

        redirectAttributes.addFlashAttribute("errorMessage",
                "Une erreur interne s'est produite. Veuillez réessayer.");

        return "redirect:/transactions";
    }
}

