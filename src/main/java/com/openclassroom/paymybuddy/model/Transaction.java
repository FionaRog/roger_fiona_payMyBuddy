package com.openclassroom.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name="transaction")
public class Transaction {

    //changer le nom id dans la table de bdd?
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int transactionId;

    @Column(name="sender")
    private User sender;

    @Column(name="receiver")
    private User receiver;

    @Column(name="description")
    private String description;

    @Column(name="amount")
    private double amount;

    //bon format de date ?
    @Column(name="date_transaction")
    private Date dateTransaction;

    @OneToMany(
            fetch = FetchType.LAZY,
            cascade = {
                    CascadeType.MERGE,
                    CascadeType.PERSIST
            })
    @JoinColumn(name="sender")
    List<User> senders = new ArrayList<>();
    @JoinColumn(name="receiver")
    List<User> receivers = new ArrayList<>();
}
