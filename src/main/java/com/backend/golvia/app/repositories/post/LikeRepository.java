package com.backend.golvia.app.repositories.post;

import com.backend.golvia.app.entities.Like;
import com.backend.golvia.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByPostIdAndUserId(Long postId, Long userId);
    int countByPostId(Long postId);
    List<Like> findByPostId(Long postId);
    void deleteAllByUser(User user);

    @Query("SELECT COUNT(l) FROM Like l")
    long countTotalLikes();


    @Query("SELECT COUNT(l) FROM Like l WHERE l.dateCreated >= :startOfDay AND l.dateCreated < :endOfDay")
    long countLikesReceivedToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
