package com.openclassroom.paymybuddy.service;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.service.impl.UserService;

import java.util.List;

/**
 * Interface de service dédiée à la gestion des utilisateurs.
 * <p>
 * Définit les opérations principales possibles sur les utilisateurs :
 * <ul>
 *   <li>lecture de la liste des utilisateurs,</li>
 *   <li>récupération de profils et d'amis,</li>
 *   <li>ajout d'utilisateur et d'amis,</li>
 *   <li>mise à jour du mot de passe, du pseudo et du solde.</li>
 * </ul>
 * <p>
 * Ces opérations sont implémentées par la classe {@link UserService}.
 */
public interface IUserService {

    /**
     * Récupère tous les utilisateurs présents en base de données.
     *
     * @return un {@link Iterable} contenant tous les utilisateurs
     */
    Iterable<User> getUsers();

    /**
     * Récupère le profil d'un utilisateur sous forme de DTO à partir de son email.
     *
     * @param email l'email de l'utilisateur dont on souhaite obtenir le profil
     *              (non {@code null})
     * @return un {@link UserResponseDto} représentant le profil de l'utilisateur
     */
    UserResponseDto getUserProfile(String email);

    /**
     * Ajoute un nouvel utilisateur en base de données.
     *
     * @param user l'entité {@link User} à ajouter (non {@code null})
     * @return l'utilisateur après sauvegarde avec identifiant généré
     */
    User addUser (User user);

    /**
 * Ajoute un ami à la liste d'amis d'un utilisateur.
 *
 * @param userEmail   l'email de l'utilisateur qui ajoute un ami (non {@code null})
 * @param friendEmail l'email de l'utilisateur à ajouter en tant qu'ami (non {@code null})
 */
    void addFriend(String userEmail, String friendEmail);

    /**
 * Récupère un utilisateur avec sa liste d'amis à partir de son email.
 *
 * @param email l'email de l'utilisateur recherché (non {@code null})
 * @return l'entité {@link User} correspondante, incluant sa liste d'amis
 */
    User getUserWithFriends(String email);

    /**
 * Récupère la liste des amis (utilisateurs) d'un utilisateur donné par son email.
 *
 * @param email l'email de l'utilisateur dont on souhaite obtenir la liste d'amis (non {@code null})
 * @return une liste de {@link User} représentant les amis de l'utilisateur
 */
    List<User> getFriendUsernames(String email);

    /**
 * Met à jour le mot de passe d'un utilisateur.
 *
 * @param email l'email de l'utilisateur dont on met à jour le mot de passe (non {@code null})
 * @param updatePasswordRequestDto le DTO contenant le mot de passe actuel, le nouveau mot de passe
 *                                 et sa confirmation (non {@code null})
 */
    void updatePassword(String email, UpdatePasswordRequestDto updatePasswordRequestDto);

    /**
 * Met à jour le pseudo d'un utilisateur.
 *
 * @param email l'email de l'utilisateur dont on met à jour le pseudo (non {@code null})
 * @param username  le nouveau pseudo (non {@code null})
 */
    void updateUsername(String email, String username);

    /**
 * Met à jour le solde d'un utilisateur en ajoutant ou retranchant un montant donné.
 *
 * @param email  l'email de l'utilisateur dont on met à jour le solde (non {@code null})
 * @param Amount le montant à ajouter (peut être négatif pour retrait)
 */
    void updateBalance(String email, double Amount);
}
