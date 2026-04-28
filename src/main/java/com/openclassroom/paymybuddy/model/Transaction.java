package com.openclassroom.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Entité représentant une transaction financière entre deux utilisateurs.
 *
 * <p>Une transaction contient un expéditeur, un destinataire, un montant,
 * une description facultative et une date de création.
 * Elle est persistée dans la table {@code transaction}</p>
 */
@Getter
@Setter
@Entity
@Table(name="transaction")
public class Transaction {

    /**
     * Identifiant unique de la transaction.
     * Généré automatiquement par la base de données.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int transactionId;

    /**
     * Utilisateur à l'origine de la transaction.
     * Correspond à la colonne {@code sender}, cette association est obligatoire.
     */
    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender")
    private User sender;

    /**
     * Utilisateur destinataire de la transaction.
     * Correspond à la colonne {@code receiver}, cette association est obligatoire.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver")
    private User receiver;

    /**
     * Description associée à la transaction.
     * Correspond à la colonne {@code description}.
     */
    @Column
    private String description;

    /**
     * Montant transféré lors de la transacation.
     * Doit être positif et ne peut être nul.
     * Correspond à la colonne {@code amount}.
     */
    @Column(nullable = false)
    private double amount;

    /**
     * Montant prélevé par PayMyBuddy lors de chaque transaction.
     * Correspond à la colonne {@code fee}
     */
    @Column(nullable = false)
    private double fee=0.00;

    /**
     * Date et heure auxquelles la transaction est effectuée, ne peut être nulle.
     * Correspond à la colonne {@code date_transacation}.
     */
    @Column(name="date_transaction", nullable = false)
    private LocalDateTime dateTransaction;
}
