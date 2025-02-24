package com.backend.golvia.app.repositories.post;

import com.backend.golvia.app.entities.Comment;
import com.backend.golvia.app.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPostId(Long postId);
    int countByPostId(Long postId);
    void deleteAllByUser(User user);
   // List<Comment> findByParentContentId(Long parentContentId);
}
