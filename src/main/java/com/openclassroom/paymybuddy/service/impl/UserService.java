package com.openclassroom.paymybuddy.service.impl;

import com.openclassroom.paymybuddy.dto.UpdatePasswordRequestDto;
import com.openclassroom.paymybuddy.dto.UserResponseDto;
import com.openclassroom.paymybuddy.exception.BusinessException;
import com.openclassroom.paymybuddy.mapper.UserMapper;
import com.openclassroom.paymybuddy.model.User;
import com.openclassroom.paymybuddy.repository.UserRepository;
import com.openclassroom.paymybuddy.service.IUserService;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("GET_USERS - Récupération de tous les utilisateurs");

        Iterable<User> users = userRepository.findAll();

        return users;
    }

    /**
     * Récupère le profil d'un utilisateur sous forme de DTO à partir de son email.
     *
     * @param email l'email de l'utilisateur dont on souhaite obtenir le profil (non {@code null})
     * @return un {@link UserResponseDto} représentant le profil de l'utilisateur
     * @throws BusinessException si aucun utilisateur n'est trouvé avec cet email
     */
    public UserResponseDto getUserProfile(String email) {
        log.info("GET_USER_PROFILE - Récupération du profil utilisateur pour l'email={}", email);
        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        UserResponseDto userDto = userMapper.toDto(user);
        log.debug("GET_USER_PROFILE - Profil utilisateur {}", userDto.getUsername());

        return userDto;
    }

    /**
     * Récupère la liste des amis (utilisateurs) d'un utilisateur donné par son email.
     *
     * @param email l'email de l'utilisateur dont on souhaite obtenir la liste d'amis (non {@code null})
     * @return une liste de {@link User} représentant les amis de l'utilisateur
     * @throws BusinessException si aucun utilisateur n'est trouvé avec cet email
     */
    public List<User> getFriendUsernames(String email) {
        log.info("GET_FRIENDS_LIST - Récupération de la liste des amis pour l'utilisateur email={}", email);
        User user = userRepository.findByEmailWithFriends(email)
                .orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        List<User> friends = user.getFriends();
        log.debug("GET_FRIENDS_LIST - Nombre d'amis trouvés: {}", friends.size());

        return friends;
    }

    /**
     * Ajoute un nouvel utilisateur en base de données.
     * <p>Le mot de passe est encodé avant la sauvegarde.</p>
     *
     * @param user l'entité {@link User} à ajouter (non {@code null})
     * @return l'utilisateur après sauvegarde avec un identifiant unique généré
     * @throws BusinessException si un utilisateur avec cet email existe déjà
     */
    @Transactional
    public User addUser (User user) {

        log.info("ADD_USER_INIT - Tentative d'ajout d'un nouvel utilisateur avec l'email={}", user.getEmail());

        if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            log.warn("ADD_USER_EMAIL_CONFLICT - Email déjà utilisé: {}", user.getEmail());
            throw new BusinessException("USER_ALREADY_EXIST", "Email déjà utilisé");
        }

        log.debug("ADD_USER_ENCODE - Encodage du mot de passe pour l'utilisateur={}", user.getEmail());

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        log.info("Mot de passe encodé pour l'utilisateur email={}", user.getEmail());

        User savedUser = userRepository.save(user);
        log.info("ADD_USER_SUCCESS - Utilisateur ajouté avec succès, id={}", savedUser.getId());
        log.debug("ADD_USER_DETAILS - Nouvel utilisateur: email={}, username={}", savedUser.getEmail(), savedUser.getUsername());

        return savedUser;
    }

    /**
     * Ajoute un ami à la liste d'amis d'un utilisateur.
     *
     * @param userEmail l'email de l'utilisateur qui ajoute un ami (non {@code null})
     * @param friendEmail l'email de l'utilisateur à ajouter en tant qu'ami (non {@ode null})
     * @throws BusinessException si l'utilisateur ou l'ami n'existe pas et si l'utilisateur
     * tente de s'ajouter lui-même ou si l'ami est déjà dans la liste d'amis
     */
    @Transactional
    public void addFriend(String userEmail, String friendEmail) {

        log.info("ADD_FRIEND_INIT - Tentative d'ajout de l'utilisateur {} comme ami de {}", friendEmail, userEmail);

        User user = userRepository.findByEmail(userEmail).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        User friend = userRepository.findByEmail(friendEmail).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Relation introuvable"));

        if(user.getEmail().equals(friend.getEmail())) {
            log.warn("ADD_FRIEND_INVALID_SELF - Tentative d'ajout de soi-même comme ami pour l'utilisateur={}", user.getEmail());
            throw new BusinessException("INVALID_OPERATION", "Vous ne pouvez vous ajouter vous-même");
        }

        if (userRepository.verifyRelation(user.getId(), friend.getId()) > 0) {
            log.warn("ADD_FRIEND_ALREADY_EXISTS - L'utilisateur {} est déjà ami avec {} (relation déjà existante)", user.getEmail(), friend.getEmail());
            throw new BusinessException("FRIEND_ALREADY_ADDED", "Personne déjà dans vos contacts");
        }

        user.getFriends().add(friend);
        log.debug("ADD_FRIEND_UPDATED - Ami {} ajouté à la liste d'amis de {}", friend.getEmail(), user.getEmail());

        userRepository.save(user);
        log.info("ADD_FRIEND_SUCCESS - Ami {} ajouté avec succès pour l'utilisateur={}", friend.getEmail(), user.getEmail());
    }

    /**
     * Met à jour le mot de passe d'un utilisateur.
     * <p>Le nouveau mot de passe est encodé et sauvegardé.</p>
     *
     * @param email l'email de l'utilisateur dont on met à jour le mot de passe (non {@code null})
     * @param requestDto le DTO contenant le mot de passe actuel, le nouveau mot de passe et sa confirmation (non {@code null})
     * @throws BusinessException si :
     *      <ul>
     *          <li>l'utilisateur n'existe pas,</li>
     *          <li>le mot de passe actuel est incorrect,</li>
     *          <li>le nouveau mot de passe est vide,</li>
     *          <li>le nouveau mot de passe ne correspond pas à la confirmation,</li>
     *          <li>le nouveau mot de passe est identique au mot de passe actuel.</li>
     *      </ul>
     */
    @Transactional
    public void updatePassword(String email, UpdatePasswordRequestDto requestDto) {

        log.info("UPDATE_PASSWORD_INIT - Tentative de mise à jour du mot de passe pour l'utilisateur={}", email);

        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            log.warn("UPDATE_PASSWORD_INVALID_CURRENT - Mot de passe actuel incorrect pour l'utilisateur={}", email);
            throw new BusinessException("INVALID_PASSWORD", "Mot de passe incorrect");
        }

        if (requestDto.getNewPassword() == null || requestDto.getNewPassword().isBlank()) {
            log.warn("UPDATE_PASSWORD_INVALID_EMPTY - Tentative de mise à jour du mot de passe vers un mot de passe vide pour l'utilisateur={}", email);
            throw new BusinessException("INVALID_PASSWORD","Le nouveau mot de passe de peut être vide");
        }

        if (!requestDto.getNewPassword().equals(requestDto.getConfirmPassword())) {
            log.warn("UPDATE_PASSWORD_CONFIRMATION_FAILED - Tentative de mise à jour du mot de passe avec confirmation différente pour l'utilisateur={}", email);
            throw new BusinessException("INVALID_PASSWORD","Le nouveau mot de passe ne correspond pas à la confirmation");
        }

        if (passwordEncoder.matches(requestDto.getNewPassword(), user.getPassword())) {
            log.warn("UPDATE_PASSWORD_SAME - Tentative de mise à jour du mot de passe avec le même mot de passe pour l'utilisateur={}", email);
            throw new BusinessException("INVALID_PASSWORD","Le nouveau mot de passe est le même que le mot de passe actuel");
        }

        log.debug("UPDATE_PASSWORD_ENCODE - Encodage du nouveau mot de passe pour l'utilisateur={}", email);

        log.info("UPDATE_PASSWORD_SUCCESS - Mot de passe mis à jour avec succès pour l'utilisateur={}", email);
        user.setPassword(passwordEncoder.encode(requestDto.getNewPassword()));
    }

    /**
     * Met à jour le pseudo d'un utilisateur.
     * <p>Le nouveau pseudo est simplement affecté, l'entité est ensuite sauvegardée.</p>
     *
     * @param email l'email de l'utilisateur dont on met à jour le pseudo (non {@ode null})
     * @param username le nouveau pseudo de l'utilisateur (non {@code null})
     * @throws BusinessException si aucun utilisateur n'est trouvé avec cet email et si
     * le nouveau pseudo est vide
     */
    @Transactional
    public void updateUsername(String email, String username) {

        log.info("UPDATE_USERNAME_INIT - Tentative de mise à jour du username pour l'utilisateur={}", email);

        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        if(username == null || username.isEmpty()) {
            log.warn("UPDATE_USERNAME_INVALID - Tentative de mise à jour du username vers null ou vide pour l'utilisateur={}", email);
            throw new BusinessException("INVALID_OPERATION", "Le username ne peut être null");
        }

        log.debug("UPDATE_USERNAME_BEFORE - Ancien username pour l'utilisateur={} : {}", email, user.getUsername());
        user.setUsername(username);

        log.info("UPDATE_USERNAME_SUCCESS - Pseudonyme mis à jour avec succès pour l'utilisateur={}", email);
        userRepository.save(user);

        log.debug("UPDATE_USERNAME_AFTER - Nouveau username : {}", user.getUsername());
    }

    /**
     * Met à jour le solde d'un utilisateur en ajoutant ou retranchant un montant donné.
     *
     * @param email l'email de l'utilisateur dont on met à jour le solde (non {@code null})
     * @param amount le montant à ajouter (peut-être négatif pour retrait)
     * @throws BusinessException si aucun utilisateur n'est trouvé avec cet email et si
     * le solde résultant est inférieur à 0
     */
    @Transactional
    public void updateBalance(String email, double amount) {

        log.info("UPDATE_BALANCE_INIT - Tentative de mise à jour du solde pour l'utilisateur={} avec un montant={}", email, amount);

        User user = userRepository.findByEmail(email).
                orElseThrow(() -> new BusinessException("USER_NOT_FOUND","Utilisateur introuvable"));

        double newBalance = user.getBalance() + amount;
        log.debug("UPDATE_BALANCE_CALC - Ancien solde={}, montant={}, nouveau solde={}", user.getBalance(), amount, newBalance);

        if(newBalance < 0) {
            log.warn("UPDATE_BALANCE_NEGATIVE - Tentative de mise à jour du solde conduisant à un solde négatif pour l'utilisateur={}, solde={}, montant={}",
                    email, user.getBalance(), amount);
            throw new BusinessException("INVALID_OPERATION", "Le solde ne peut être négatif"); };

        user.setBalance(newBalance);
        userRepository.save(user);
        log.info("UPDATE_BALANCE_SUCCESS - Solde mis à jour avec succès pour l'utilisateur={}, nouveau solde={}", email, newBalance);
    }
}
