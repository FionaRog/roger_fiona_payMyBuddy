package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête utilisé pour créer une nouvelle transaction.
 *
 * Contient les informations nécessaires pour effectuer un transfert d'argent entre
 * deux utilisateurs : l'email du destinataire, une description facultative et le montant à transférer.
 */
@Getter
@Setter
public class TransactionRequestDto {

    /**
     * Email de l'utilisateur destinataire de la transaction.
     */
    private String receiverEmail;

    /**
     * Description associée à la transaction.
     */
    private String description;

    /**
     * Montant à transférer.
     */
    private double amount;
}
