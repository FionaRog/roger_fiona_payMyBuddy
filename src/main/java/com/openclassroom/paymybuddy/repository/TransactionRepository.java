package com.openclassroom.paymybuddy.repository;

import com.openclassroom.paymybuddy.model.Transaction;
import com.openclassroom.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository JPA pour la gestion des transactions.
 *
 * <p> Permet la persistance et la récupération des entités {@link Transaction}
 * via les méthodes de bases héritées de {@link CrudRepository}, ainsi que
 * des requêtes dérivées basées sur l'expéditeur et le destinaire.</p>
 */
@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Integer> {

    /**
     * Récupère toutes les transactions envoyées par un utilisateur donné.
     *
     * <p> La recherche s'effectue à partir de l'entité {@link User} passée en paramètre
     * et correspond aux transactions pour lesquelles cet utilisateur est l'expéditeur.
     * </p>
     *
     * @param sender l'utilisateur expéditeur des transactions recherchées (non {@code null})
     * @return une liste de {@link Transaction} envoyées par cet utilisateur
     */
    List<Transaction> findBySender(User sender);

    /**
     * Récupère toutes les transactions reçues par un utilisateur donné.
     *
     * <p> La recherche s'effectue à partir de l'entité {@link User} passée en paramètre
     * et correspond aux transactions pour lesquelles cet utilisateur est le destinataire.
     * </p>
     *
     * @param receiver l'utilisateur destinataire des transactions recherchées (non {@code null})
     * @return une liste de {@link Transaction} reçues par cet utilisateur
     */
    List<Transaction> findByReceiver(User receiver);
}
