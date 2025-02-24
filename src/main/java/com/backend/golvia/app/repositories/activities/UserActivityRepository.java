package com.backend.golvia.app.repositories.activities;

import com.backend.golvia.app.entities.UserActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;


@Repository
public interface UserActivityRepository extends JpaRepository<UserActivity, Long> {

    @Query(value = """
        SELECT COUNT(DISTINCT user_identifier)
        FROM user_activities
        WHERE timestamp >= :startOfDay AND timestamp < :endOfDay
      """, nativeQuery = true)
    long countDailyActiveUsers(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u WHERE u.dateCreated >= :startOfDay AND u.dateCreated < :endOfDay")
        long countNewUsersToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);


    @Query(value = """
    SELECT COUNT(DISTINCT user_identifier)
    FROM user_activities
    WHERE timestamp >= :startOfWeek AND timestamp < :endOfWeek
  """, nativeQuery = true)
    long countWeeklyActiveUsers(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u WHERE u.dateCreated >= :startOfWeek AND u.dateCreated < :endOfWeek")
    long countNewUsersThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek, @Param("endOfWeek") LocalDateTime endOfWeek);

    @Query(value = """
    SELECT COUNT(DISTINCT user_identifier)
    FROM user_activities
    WHERE timestamp >= :startOfMonth AND timestamp < :endOfMonth
  """, nativeQuery = true)
    long countMonthlyActiveUsers(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);

    @Query("SELECT COUNT(DISTINCT u.id) FROM User u WHERE u.dateCreated >= :startOfMonth AND u.dateCreated < :endOfMonth")
    long countNewUsersThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth, @Param("endOfMonth") LocalDateTime endOfMonth);


}

