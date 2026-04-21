package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.exception.FriendAlreadyAddedException;
import com.openclassroom.paymybuddy.exception.InvalidOperationException;
import com.openclassroom.paymybuddy.exception.UserAlreadyExistsException;
import com.openclassroom.paymybuddy.exception.UserNotFoundException;
import com.openclassroom.paymybuddy.mapper.UserMapper;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implémentation du service de gestion des utilisateurs.
 *
 * Ce service permet de :
 * <ul>
 *      <li>récupérer la liste de tous les utilisateurs,</li>
 *      <li>récupérer un utilisateur et ses amis,</li>
 *      <li>récupérer le profil d'un utilisateur (sous forme de DTO),</li>
 *      <li>ajouter un nouvel utilisateur avec encodage de son mot de passe,</li>
 *      <li>ajouter un ami à un utilisateur,</li>
 *      <li>mettre à jour le mot de passe, le pseudo ou le solde d'un utilisateur.</li>
 * </ul>
 *
 *
 */
//Ajout de la javadoc
//Ajout de logger info, error, debug
//Gestion des erreurs côté front
//v1 ajout
//OK @transactionnal mettre readonly ou @ttransactionnal au dessus des méthodes
@Service
public class UserService implements IUserService {
    /**
     * Repository permettant la persistance et la récupération des utilisateurs.
     */
    @Autowired
    private UserRepository userRepository;

    /**
     * Encodeur de mot de passe utilisé pour sécuriser les mots de passe avant sauvegarde en base.
     */
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Mapper permettant la conversion entre l'entité {@link User} et le
     * DTO {@link UserResponseDto}.
     */
    @Autowired
    private UserMapper userMapper;

    // a supprimer ?
    /**
     * Récupère tout les utilisateurs présents en base de données.
     *
     * @return un {@link Iterable} contenant tous les utilisateurs
     */
    public Iterable<User> getUsers() {
        return userRepository.findAll();
    }

    /**
     * Récupère un utilisateur avec sa liste d'amis à partir de son email.
     *
     * @param email l'email de l'utilisateur recherché (non {@code null})
     * @return l'entité {@link User} correspondante, incluant sa liste d'amis
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    public User getUserWithFriends(String email) {
        return userRepository.findByEmailWithFriends(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Récupère le profil d'un utilisateur sous forme de DTO à partir de son email.
     *
     * @param email l'email de l'utilisateur dont on souhaite obtenir le profil (non {@code null})
     * @return un {@link UserResponseDto} représentant le profil de l'utilisateur
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    public UserResponseDto getUserProfile(String email) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        return userMapper.toDto(user);
    }

    /**
     * Récupère la liste des amis (utilisateurs) d'un utilisateur donné par son email.
     *
     * @param email l'email de l'utilisateur dont on souhaite obtenir la liste d'amis (non {@code null})
     * @return une liste de {@link User} représentant les amis de l'utilisateur
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet email
     */
    public List<User> getFriendUsernames(String email) {
        User user = userRepository.findByEmailWithFriends(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return user.getFriends();
    }

    /**
     * Ajoute un nouvel utilisateur en base de données.
     * <p>Le mot de passe est encodé avant la sauvegarde.</p>
     *
     * @param user l'entité {@link User} à ajouter (non {@code null})
     * @return l'utilisateur après sauvegarde avec un identifiant unique généré
     * @throws UserAlreadyExistsException si un utilisateur avec cet email existe déjà
     */
    @Transactional
    public User addUser (User user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserAlreadyExistsException("Email already used");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    /**
     * Ajoute un ami à la liste d'amis d'un utilisateur.
     *
     * @param userEmail l'email de l'utilisateur qui ajoute un ami (non {@code null})
     * @param friendEmail l'email de l'utilisateur à ajouter en tant qu'ami (non {@ode null})
     * @throws UserNotFoundException si l'utilisateur ou l'ami n'existe pas
     * @throws InvalidOperationException si l'utilisateur tente de s'ajouter lui-même
     * @throws FriendAlreadyAddedException si l'ami est déjà dans la liste d'amis
     */
    @Transactional
    public void addFriend(String userEmail, String friendEmail) {
        User user = userRepository.findByEmail(userEmail).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        User friend = userRepository.findByEmail(friendEmail).
                orElseThrow(() -> new UserNotFoundException("Friend not found"));

        if(user.getEmail().equals(friend.getEmail())) {
            throw new InvalidOperationException("You cannot add yourself");
        }

        if (userRepository.verifyRelation(user.getId(), friend.getId()) > 0) {
            throw new FriendAlreadyAddedException("Friend already in your contacts");
        }

        user.getFriends().add(friend);

        userRepository.save(user);
    }

    /**
     * Met à jour le mot de passe d'un utilisateur.
     * <p>Le nouveau mot de passe est encodé et sauvegardé.</p>
     * @param email l'email de l'utilisateur dont on met à jour le mot de passe (non {@code null})
     * @param requestDto le DTO contenant le mot de passe actuel, le nouveau mot de passe et sa confirmation (non {@code null})
     * @throws UserNotFoundException si l'utilisateur n'existe pas
     * @throws InvalidOperationException si :
     *      <ul>
     *          <li>le mot de passe actuel est incorrect,</li>
     *          <li>le nouveau mot de passe est vide,</li>
     *          <li>le nouveau mot de passe ne correspond pas à la confirmation,</li>
     *          <li>le nouveau mot de passe est identique au mot de passe actuel.</li>
     *      </ul>
     */
    @Transactional
    public void updatePassword(String email, UpdatePasswordRequestDto requestDto) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new InvalidOperationException("Incorrect password");
        }

        if (requestDto.getNewPassword() == null || requestDto.getNewPassword().isBlank()) {
            throw new InvalidOperationException("New password cannot be empty");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            throw new InvalidOperationException("New password does not match the confirmation");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            throw new InvalidOperationException("New password is the same as your current password");
        }

        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    /**
     * Met à jour le pseudo d'un utilisateur.
     * <p>Le nouveau pseudo est simplement affecté, l'entité est ensuite sauvegardée.</p>
     *
     * @param email l'email de l'utilisateur dont on met à jour le pseudo (non {@ode null})
     * @param username le nouveau pseudo de l'utilisateur (non {@code null})
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet email
     * @throws InvalidOperationException si le nouveau pseudo est vide
     */
    @Transactional
    public void updateUsername(String email, String username) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        if(username == null || username.isEmpty()) {
            throw new InvalidOperationException("Username cannot be null");
        }
        user.setUsername(username);
        userRepository.save(user);
    }

    /**
     * Met à jour le solde d'un utilisateur en ajoutant ou retranchant un montant donné.
     *
     * @param email l'email de l'utilisateur dont on met à jour le solde (non {@code null})
     * @param amount le montant à ajouter (peut-être négatif pour retrait)
     * @throws UserNotFoundException si aucun utilisateur n'est trouvé avec cet email
     * @throws InvalidOperationException si le solde résultant est inférieur à 0
     */
    @Transactional
    public void updateBalance(String email, double amount) {
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new UserNotFoundException("User not found"));

        double newBalance = user.getBalance() + amount;

        if(newBalance < 0) {
            throw new InvalidOperationException("Balance cannot be negative"); };

        user.setBalance(newBalance);
        userRepository.save(user);
    }
}
