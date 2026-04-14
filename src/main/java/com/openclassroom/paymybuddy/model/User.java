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
    @Column(name = "id", nullable = false)
    private int id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private double balance = 0.00;

    //unidirectionnelle
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
