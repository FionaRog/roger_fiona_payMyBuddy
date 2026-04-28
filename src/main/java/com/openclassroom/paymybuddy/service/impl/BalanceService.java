package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.IBalanceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implémentation du service de gestion de solde.
 * <p>
 * Permet d'effectuer des opérations de dépôt et de retrait sur le solde
 * d'un utilisateur identifié par son email, via l'entité {@link User}
 * et le repository {@link UserRepository}.
 * <p>
 * Toutes les opérations sont exécutées dans une transaction de base de données
 * afin de garantir la cohérence de l'état du solde après chaque modification.
 */
@Slf4j
@Service
public class BalanceService implements IBalanceService {

    /**
     * Repository permettant la récupération et la mise à jour des utilisateurs
     * (y compris leur solde).
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Effectue un dépôt sur le solde d'un utilisateur.
     *
     * @param email  l'email de l'utilisateur dont on souhaite augmenter le solde
     * @param amount le montant à ajouter au solde
     * @throws BusinessException si le montant est invalide ou si l'utilisateur
     *         est introuvable
     */
    @Transactional
    @Override
    public void deposit(String email, double amount) {

        if(amount <= 0) {
            log.warn("DEPOSIT_INVALID_AMOUNT - Montant invalide ({}) pour l'utilisateur={}", amount, email);
            throw new BusinessException("INVALID_AMOUNT", "Le montant doit être supérieur à 0");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Utilisateur introuvable"));

        double newBalance = user.getBalance() + amount;
        log.info("DEPOSIT_SUCCESS - Dépôt de {} sur le compte de l'utilisateur={}, nouveau solde={}",
                amount, email, newBalance);

        user.setBalance(newBalance);
    }

    /**
     * Effectue un retrait sur le solde d'un utilisateur.
     *
     * @param email  l'email de l'utilisateur dont on souhaite réduire le solde
     * @param amount le montant à retirer du solde
     * @throws BusinessException si le montant est invalide, si l'utilisateur
     *         est introuvable ou si le solde est insuffisant
     */
    @Transactional
    @Override
    public void withdraw(String email, double amount) {

        if(amount <= 0) {
            log.warn("WITHDRAW_INVALID_AMOUNT - Montant invalide ({}) pour l'utilisateur={}", amount, email);
            throw new BusinessException("INVALID_AMOUNT", "Le montant doit être supérieur à 0");
        }
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND", "Utilisateur introuvable"));

        if(user.getBalance() < amount) {
            log.warn("WITHDRAW_INSUFFICIENT_BALANCE - Solde={}, montant={}, pour l'utilisateur={}",
                    user.getBalance(), amount, email);
            throw new BusinessException("INVALID_OPERATION", "Solde insuffisant");
        }

        double newBalance = user.getBalance() - amount;
        log.info("WITHDRAW_SUCCESS - Retrait de {} sur le compte de l'utilisateur={}, nouveau solde={}",
                amount, email, newBalance);

        user.setBalance(newBalance);
    }

}
