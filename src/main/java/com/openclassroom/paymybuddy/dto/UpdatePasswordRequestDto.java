package com.openclassroom.paymybuddy.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO de requête utilisé pour modifier le mot de passe d'un utilisateur.
 *
 * Contient le mot de passe actuel, le nouveau mot de passe et sa confirmation.
 */
@Getter
@Setter
public class UpdatePasswordRequestDto {

    /**
     * Mot de passe actuel de l'utilisateur.
     */
    @NotBlank
    private String currentPassword;

    /**
     * Nouveau mot de passe.
     */
    @NotBlank
    private String newPassword;

    /**
     * Confirmation du nouveau mot de passe.
     */
    @NotBlank
    private String confirmPassword;
}
