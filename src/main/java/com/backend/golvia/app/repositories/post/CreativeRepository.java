package com.backend.golvia.app.repositories.post;

import com.backend.golvia.app.entities.Creative;
import com.backend.golvia.app.entities.Like;
import com.backend.golvia.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CreativeRepository extends JpaRepository<Creative, Long> {
    Optional<Creative> findByPostIdAndUserId(Long postId, Long userId);
    int countByPostId(Long postId);
    List<Creative> findByPostId(Long postId);
    void deleteAllByUser(User user);

    @Query("SELECT COUNT(l) FROM Like l")
    long countTotalLikes();


    @Query("SELECT COUNT(l) FROM Like l WHERE l.dateCreated >= :startOfDay AND l.dateCreated < :endOfDay")
    long countCreativesReceivedToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
