package com.openclassroom.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name="transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int transactionId;

    @ManyToOne (fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "sender")
    private User sender;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiver")
    private User receiver;

    @Column
    private String description;

    @Column(nullable = false)
    private double amount;

    //bon format de date ?
    @Column(name="date_transaction", nullable = false)
    private LocalDateTime dateTransaction;


}
