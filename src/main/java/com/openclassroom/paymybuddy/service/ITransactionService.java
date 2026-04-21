package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.TransactionRequestDto;
import com.openclassroom.paymybuddy.dto.TransactionResponseDto;
import com.openclassroom.paymybuddy.service.impl.TransactionService;

import java.util.List;

/**
 * Interface de service dédiée à la gestion des transactions entre utilisateurs.
 * <p>
 * Définit les opérations principales :
 * <ul>
 *   <li>ajout d'une nouvelle transaction (envoi d'argent d'un expéditeur à un destinataire),</li>
 *   <li>récupération de l'historique des transactions d'un utilisateur.</li>
 * </ul>
 * <p>
 * Ces opérations sont implémentées par la classe {@link TransactionService}.
 */
public interface ITransactionService {

/**
 * Ajoute une nouvelle transaction entre deux utilisateurs.
 *
 * @param senderEmail l'email de l'utilisateur expéditeur (non {@code null})
 * @param requestDto  le DTO contenant les informations de la transaction
 *                    (non {@code null})
 * @return un {@link TransactionResponseDto} représentant la transaction enregistrée
 */
    TransactionResponseDto addTransaction(String senderEmail, TransactionRequestDto requestDto);

    /**
     * Récupère la liste de toutes les transactions (envoyées et reçues)
     * associées à un utilisateur identifié par son email.
     *
     * @param email l'email de l'utilisateur dont on souhaite obtenir l'historique
     *              des transactions (non {@code null})
     * @return une liste de {@link TransactionResponseDto} représentant toutes
     *         les transactions envoyées et reçues par l'utilisateur
     */
    List<TransactionResponseDto> getUserTransactions(String email);
}
