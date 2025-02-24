package com.backend.golvia.app.repositories.interactions;

import com.backend.golvia.app.entities.Connection;
import com.backend.golvia.app.entities.Follower;
//import com.backend.golvia.app.enums.String;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    @Query(value = "SELECT * FROM follows WHERE from_email = :followerEmail AND to_email = :followeeEmail", nativeQuery = true)
    Follower findByFromEmailAndToEmail(@Param("followerEmail") String followerEmail,
                                       @Param("followeeEmail") String followeeEmail);

    @Query(value = "SELECT * FROM follows WHERE from_email = :userEmail", nativeQuery = true)
    List<Follower> findByFromEmail(@Param("userEmail") String userEmail);

    @Query(value = "SELECT * FROM follows WHERE to_email = :userEmail", nativeQuery = true)
    List<Follower> findByToEmail(@Param("userEmail") String userEmail);

    @Query(value = "SELECT * FROM follows WHERE from_email = :fromEmail AND follow_status = :status", nativeQuery = true)
    List<Follower> findByFromEmailAndStatus(@Param("fromEmail") String fromEmail,
                                            @Param("status") String status);

    @Query(value = "SELECT * FROM follows WHERE to_email = :toEmail AND follow_status = :status", nativeQuery = true)
    List<Follower> findByToEmailAndStatus(@Param("toEmail") String toEmail,
                                          @Param("status") String status);

    @Query(value = "SELECT COUNT(*) FROM follows WHERE to_email = :toEmail", nativeQuery = true)
    long countByToEmail(@Param("toEmail") String toEmail);

    Follower findByFromEmailAndToEmailOrToEmailAndFromEmail(@NotBlank(message = "Sorry, email is required") String email, String referenceEmail, @NotBlank(message = "Sorry, email is required") String email1, String referenceEmail1);
}
