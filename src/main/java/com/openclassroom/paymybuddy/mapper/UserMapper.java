package com.openclassroom.paymybuddy.mapper;

import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.model.User;
import org.mapstruct.Mapper;

/**
 * Mapper MapStruct chargé de convertir un utilisateur en DTO de réponse.
 *
 * Permet d'exposer une vue profil d'un utilisateur via {@link UserResponseDto}.
 *
 * L'implémentation est générée automatiquement par MapStruct.
 */
 @Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Convertit une entité {@link User} en DTO de réponse.
     *
     * @param user l'utilisateur à convertir
     * @return un {@link UserResponseDto} contenant les informations profil de l'utilisateur
     */
    UserResponseDto toDto (User user);
}
