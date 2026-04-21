package com.openclassroom.paymybuddy.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * DTO de réponse représentant le profil d'un utilisateur.
 *
 * Il expose les informations suivantes : pseudo, email et solde.
 */
@Getter
@Setter
public class UserResponseDto {

    /**
     * Pseudo de l'utilisateur.
     */
    private String username;

    /**
     * Email de l'utilisateur.
     */
    private String email;

    /**
     * Solde du compte de l'utilisateur.
     */
    private double balance;
}
