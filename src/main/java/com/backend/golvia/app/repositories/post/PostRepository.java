package com.backend.golvia.app.repositories.post;

import com.backend.golvia.app.entities.Post;
import com.backend.golvia.app.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findByUserId(Long userId);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.user WHERE p.id = :postId")
    Optional<Post> findPostWithUserById(@Param("postId") Long postId);

    @Query("SELECT p FROM Post p ORDER BY p.dateCreated DESC")
    Page<Post> findAll(Pageable pageable);

    long countByUser_Email(String email);

    void deleteAllByUser(User user);

    List<Post> findByUser(User user);

    @Query("SELECT COUNT(m) FROM Post p JOIN p.mediaUrls m WHERE m.type = 'video'")
    long countTotalVideos();

    @Query("SELECT COUNT(m) FROM Post p JOIN p.mediaUrls m WHERE m.type = 'video' AND p.dateCreated >= :startOfDay AND p.dateCreated < :endOfDay")
    long countVideosUploadedToday(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);
}
