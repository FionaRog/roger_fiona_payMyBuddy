package com.openclassroom.paymybuddy.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;

    //rendre unique
    @Column(name="email")
    private String email;

    @Column(name="username")
    private String username;

    //sécurisé le mdp
    @Column(name="password")
    private String password;

    //unidirectionnelle ou bidirectionnelle mappedBy=?.
    @ManyToMany(
            fetch = FetchType.LAZY,
            cascade = { CascadeType.PERSIST,
                        CascadeType.MERGE}  )

    @JoinTable(
            name="assoc_user",
            joinColumns = @JoinColumn(name="id_user1"),
            inverseJoinColumns = @JoinColumn(name="id_user2")
    )
    private List<User> users = new ArrayList<>();

}
