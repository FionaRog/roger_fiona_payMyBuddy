package com.openclassroom.paymybuddy.repository;

import com.openclassroom.paymybuddy.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository <User, Integer> {

    public Optional<User> findByEmail(String email);

}
