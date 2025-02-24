package com.backend.golvia.app.repositories.profiles;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.Fan;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface FanRepository extends JpaRepository<Fan, Long>, JpaSpecificationExecutor<Fan> {

    @Query(value = "SELECT * FROM fans WHERE email = :email", nativeQuery = true)
    Fan findByEmail(@Param("email") String email);

    @Query(value = "SELECT * FROM fans WHERE country = :country", nativeQuery = true)
    List<Fan> findByCountry(@Param("country") String country);

    @Query(value = "SELECT * FROM fans WHERE favorite_sports LIKE %:sport%", nativeQuery = true)
    List<Fan> findByFavoriteSport(@Param("sport") String sport);

    @Query(value = "SELECT * FROM fans WHERE favorite_athletes LIKE %:athlete%", nativeQuery = true)
    List<Fan> findByFavoriteAthlete(@Param("athlete") String athlete);

    @Query(value = "SELECT * FROM fans WHERE username = :username", nativeQuery = true)
    Fan findByUsername(@Param("username") String username);

    @Query(value = "SELECT * FROM fans ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Fan> findMostRecentlyRegistered(@Param("limit") int limit);

    @Query(value = "SELECT * FROM fans WHERE city = :city", nativeQuery = true)
    List<Fan> findByCity(@Param("city") String city);

    @Query(value = "SELECT * FROM fans WHERE notification_preferences LIKE %:preference%", nativeQuery = true)
    List<Fan> findByNotificationPreference(@Param("preference") String preference);

    @Query(value = "SELECT COUNT(*) FROM fans WHERE favorite_sports LIKE %:sport%", nativeQuery = true)
    int countByFavoriteSport(@Param("sport") String sport);

    @Query(value = "SELECT * FROM fans WHERE purchased_items LIKE %:item%", nativeQuery = true)
    List<Fan> findByPurchasedItem(@Param("item") String item);

    @Query(value = "SELECT * FROM fans WHERE username LIKE %:username%", nativeQuery = true)
    List<Fan> findByUsernameContaining(@Param("username") String username);

    @Query("SELECT f FROM Fan f WHERE LOWER(f.email) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(f.city) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Fan> searchFans(@Param("query") String query);

    boolean existsByEmail(String email);

    @Modifying
    @Query(value = "DELETE f1 FROM fans f1 INNER JOIN fans f2 ON f1.email = f2.email WHERE f1.id > f2.id", nativeQuery = true)
    void deleteDuplicateFansByEmail();



}
