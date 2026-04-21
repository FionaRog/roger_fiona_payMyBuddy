package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * DTO de réponse représentant une transaction enregistrée.
 *
 * Expose les informations utiles à l'affichage d'une transaction :
 * le pseudo de l'expéditeur et du destinataire, la description,
 * le montant et la date de transaction.
 */
@Getter
@Setter
public class TransactionResponseDto {

    /**
     * Pseudo de l'expéditeur.
     */
    private String senderUsername;

    /**
     * Pseudo du destinataire.
     */
    private String receiverUsername;

    /**
     * Description de la transaction.
     */
    private String description;

    /**
     * Montant de la transaction.
     */
    private double amount;

    /**
     * Date et heure de la transaction.
     */
    private LocalDateTime dateTransaction;
}
