package com.openclassroom.paymybuddy.repository;

import com.openclassroom.paymybuddy.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository <User, Integer> {

    public Optional<User> findByEmail(String email);

    public User findByUsername(String username);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.friends WHERE u.email = :email")
    Optional<User> findByEmailWithFriends(@Param("email") String email);

    @Query(value= "SELECT count(id) FROM assoc_user WHERE (id_user1 = :userId AND id_user2 = :friendId) OR (id_user1 = :friendId AND id_user2 = :userId)", nativeQuery = true)
        int verifyRelation(int userId, int friendId);

}
