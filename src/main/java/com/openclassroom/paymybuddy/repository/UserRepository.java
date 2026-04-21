package com.openclassroom.paymybuddy.repository;

import com.openclassroom.paymybuddy.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository JPA pour la gestion des utilisateurs.
 * <p>Permet la persistance, la récupération et certaines requêtes complexes liées aux utilisateurs
 * et à leurs relations d'amis (via l'entité {@link User} et la table de relation {@ode assoc_user}).</p>
 *
 * <p>Hérite de {@link CrudRepository} pour bénéficier des méthodes de base (save, findById, delete, etc.).</p>
 */
@Repository
public interface UserRepository extends CrudRepository <User, Integer> {

    /**
     * Récupère un utilisateur à partir de son email.
     * <p>La recherche est effectuée sur le champ {@code email} de l'entité {@link User}</p>
     *
     * @param email l'email de l'utilisateur recherché (non {@code null})
     * @return un {@link Optional} contenant l'utilisateur si trouvé, vide sinon
     */
    public Optional<User> findByEmail(String email);

    /**
     * Récupère un utilisateur avec sa liste d'amis (chargée en une seule requête).
     * <p>Utilise une jointure LAZY/FETCH pour charger également la collection {@code friends}
     * afin d'éviter le problème du lazy loading hors d'une session ouvert.</p>
     *
     * @param email l'email de l'utilisateur recherché (non {@code null})
     * @return un {@link Optional} contenant l'utilisateur avec ses amis si trouvé, vide sinon
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends WHERE u.email = :email")
    Optional<User> findByEmailWithFriends(@Param("email") String email);

    /**
     * Vérifie l'existence d'une relation d'amitié entre deux utilisateurs.
     *
     * <p> Interogge la table de relation {@code assoc_user} pour compter le nombre de lignes
     * où l'utilisateur donné et son ami apparaissent dans ou l'autre des champs {@code id_user1} et {@code id_user}
     * </p>
     *
     * @param userId l'identifiant de l'utilisateur ( non {@code null})
     * @param friendId l'identifiant de l'ami (non {@code null})
     * @return le nombre de relations trouvées (1 s'il y a une relation, 0 sinon)
     */
    @Query(value= "SELECT count(*) FROM assoc_user WHERE (id_user1 = :userId AND id_user2 = :friendId) OR (id_user1 = :friendId AND id_user2 = :userId)", nativeQuery = true)
        int verifyRelation(int userId, int friendId);

}
