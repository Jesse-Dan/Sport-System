package com.backend.golvia.app.repositories.auth;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.User;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    List<User> findByEmailNotIn(ArrayList<String> strings);

    @Query("""
    SELECT u 
    FROM User u 
    WHERE u.email NOT IN (
        SELECT c.fromEmail 
        FROM Connection c 
        WHERE c.toEmail = :email
        UNION
        SELECT c.toEmail 
        FROM Connection c 
        WHERE c.fromEmail = :email
    )
""")
    List<User> findUnconnectedUsers(@Param("email") String email);


    @Query("""
    SELECT u 
    FROM User u 
    WHERE u.email NOT IN (
        SELECT c.fromEmail 
        FROM Follower c 
        WHERE c.toEmail = :email OR c.fromEmail = :email
        UNION
        SELECT c.toEmail 
        FROM Follower c 
        WHERE c.fromEmail = :email OR c.toEmail = :email
    )
""")
    List<User> findUnfollowedUsers(@Param("email") String email);



    @Query("SELECT u FROM User u " +
            "WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<User> searchUsers(@Param("query") String query);

    List<User> findByEmailIn(List<String> emails);



   @NotNull
   List<User> findAll();

//this was made to delete already existing users foreign key, i will delete this after implementation
    @Modifying
    @Query(value = "DELETE u1 FROM users u1 INNER JOIN users u2 ON u1.email = u2.email WHERE u1.id > u2.id", nativeQuery = true)
    void deleteDuplicateUsersByEmail();

    @Modifying
    @Query(value = "ALTER TABLE users ADD CONSTRAINT unique_email UNIQUE (email)", nativeQuery = true)
    void enforceUniqueConstraintOnEmail();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM fan_interactions " +
            "WHERE fan_id IN (SELECT f1.id FROM fans f1 " +
            "INNER JOIN fans f2 " +
            "ON f1.email = f2.email " +
            "WHERE f1.id > f2.id)", nativeQuery = true)
    void deleteDuplicateFanReferences();

    @Modifying
    @Transactional
    @Query(value = "DELETE f1 FROM fans f1 " +
            "INNER JOIN fans f2 " +
            "ON f1.email = f2.email " +
            "WHERE f1.id > f2.id", nativeQuery = true)
    void deleteDuplicateFans();

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM fan_favorite_athletes " +
            "WHERE fan_id IN (SELECT f1.id FROM fans f1 " +
            "INNER JOIN fans f2 " +
            "ON f1.email = f2.email " +
            "WHERE f1.id > f2.id)", nativeQuery = true)
    void deleteDuplicateFanFavoriteAthletes();

    User getUserByEmail(String email);
}
