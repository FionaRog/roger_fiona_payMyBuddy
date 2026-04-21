package com.openclassroom.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un utilisateur de l'application.
 * <p>
 * Un utilisateur possède un email unique, un nom d'utilisateur, un mot de passe
 * encodé, un solde et une liste d'amis. Cette entité est persistée dans la table
 * {@code user}.
 * <p>
 * La relation d'amitié est modélisée par une association unidirectionnelle
 * de type {@link ManyToMany} via la table de jointure {@code assoc_user}.
 */
@Getter
@Setter
@Entity
@Table(name="user")
public class User {

    /**
     * Identifiant unique de l'utilisateur.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    /**
     * Adresse email de l'utilisateur servant d'identifiant de connexion.
     * Doit être unique et ne peut être nulle.
     */
    @Column(unique = true, nullable = false)
    private String email;

    /**
     * Nom d'utilisateur affiché dans l'application.
     * Ne peut être nul.
     */
    @Column(nullable = false)
    private String username;

    /**
     * Mot de passe de l'utilisateur.
     * Il est stocké encodé en base de données et ne peut être nul.
     */
    @Column(nullable = false)
    private String password;

    /**
     * Solde du compte de l'utilisateur.
     * Représente le montant disponible pour effectuer des transactions.
     * La valeur par défaut attribué est {@code 0.00}.
     */
    @Column(nullable = false)
    private double balance = 0.00;

    /**
     * Liste des amis de l'utilisateur.
     *
     * Relation unidirectionnelle de type {@link ManyToMany} stockée dans la table
     * de jointure {@code assoc_user}. L'utilisateur courant est relié à ses amis
     * via la colonne {@code id_user1}, et l'ami via {@code id_user2}.
     *
     * Le chargement est différé ({@link FetchType#LAZY}) et les opérations de persistance/mis à jour
     * sont propagées grâce aux casacdes {@code PERSIST} et {@code MERGE}.
     */
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST,
                        CascadeType.MERGE}  )
    @JoinTable(
            name="assoc_user",
            joinColumns = @JoinColumn(name="id_user1"),
            inverseJoinColumns = @JoinColumn(name="id_user2")
    )
    private List<User> friends = new ArrayList<>();
}
