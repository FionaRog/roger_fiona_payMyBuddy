package com.openclassroom.paymybuddy.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Gestion globale des exceptions métier de l'application.
 *
 * <p>Intercepte toutes les {@link BusinessException} levées par les services
 * et redirige l'utilisateur vers la page des transactions avec un message d'erreur.</p>
 */
@Slf4j
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
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {

        log.info("BUSINESS_EXCEPTION_CAUGHT - Code={}, Message={}", ex.getErrorCode(), ex.getMessage());

        redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());

        String requestURI = request.getRequestURI();

        if(requestURI.startsWith("/transactions")) {
            return "redirect:/transactions";
        }
        if(requestURI.startsWith("/friends")) {
            return "redirect:/friends";
        }
        if(requestURI.startsWith("/profile")) {
            return "redirect:/profile";
        }
        if(requestURI.startsWith("/register")) {
            return "redirect:/register";
        }
        return "redirect:/login";
    }

    /**
     * Gère les exceptions non anticipées.
     *
     * Pour des raisons de sécurité, le message technique n’est pas affiché à l’utilisateur.
     * Seul un message générique est exposé côté frontend.
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, RedirectAttributes redirectAttributes) {
        log.error("UNEXPECTED_EXCEPTION_CAUGHT - Erreur non gérée : " + ex.getMessage(), ex);

        redirectAttributes.addFlashAttribute("errorMessage",
                "Une erreur interne s'est produite. Veuillez réessayer.");

        return "redirect:/transactions";
    }
}

