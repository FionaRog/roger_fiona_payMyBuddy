package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.service.impl.BalanceService;

/**
 * Interface de service dédiée à la gestion du solde des utilisateurs.
 * <p>
 * Permet d'ajouter des fonds (dépôt) ou de retirer des fonds (retrait)
 * sur le compte d'un utilisateur identifié par son email.
 * <p>
 *
 * Ces opérations sont implémentées par la classe {@link BalanceService}.
 */
public interface IBalanceService {

/**
 * Effectue un dépôt sur le solde d'un utilisateur.
 *
 * @param email  l'email de l'utilisateur dont on souhaite augmenter le solde
 *               (non {@code null})
 * @param amount le montant à ajouter au solde (doit être strictement supérieur à 0)
 */
    void deposit(String email, double amount);

/**
 * Effectue un retrait sur le solde d'un utilisateur.
 *
 * @param email  l'email de l'utilisateur dont on souhaite réduire le solde
 *               (non {@code null})
 * @param amount le montant à retirer du solde (doit être strictement supérieur à 0)
 */
    void withdraw(String email, double amount);
}
