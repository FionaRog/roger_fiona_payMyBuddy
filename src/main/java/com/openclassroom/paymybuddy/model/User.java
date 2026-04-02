package com.openclassroom.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;

    @Column(name="email")
    private String email;

    @Column(name="username")
    private String username;

    //sécurisé le mdp
    @Column(name="password")
    private String password;
}
