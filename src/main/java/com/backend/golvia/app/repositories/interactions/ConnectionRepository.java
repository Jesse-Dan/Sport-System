package com.backend.golvia.app.repositories.interactions;

import com.backend.golvia.app.entities.Connection;
import com.backend.golvia.app.enums.InteractionStatus;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ConnectionRepository extends JpaRepository<Connection, Long> {

    @Query(value = "SELECT * FROM connections WHERE from_email = :fromEmail AND connection_status = :interactionStatus", nativeQuery = true)
    List<Connection> findByFromEmailAndStatus(@Param("fromEmail") String fromEmail,
                                              @Param("interactionStatus") String interactionStatus);

    @Query(value = "SELECT * FROM connections WHERE to_email = :toEmail AND connection_status = :interactionStatus", nativeQuery = true)
    List<Connection> findByToEmailAndStatus(@Param("toEmail") String toEmail,
                                            @Param("interactionStatus") String interactionStatus);

    @Query(value = "SELECT * FROM connections WHERE from_email = :fromEmail OR to_email = :toEmail", nativeQuery = true)
    List<Connection> findByFromEmailOrToEmail(@Param("fromEmail") String fromEmail,
                                              @Param("toEmail") String toEmail);

    @Query(value = "SELECT * FROM connections WHERE connection_status = :interactionStatus " +
            "AND (from_email = :fromEmail OR to_email = :toEmail)", nativeQuery = true)
    List<Connection> findByStatusAndFromEmailOrToEmail(@Param("interactionStatus") String interactionStatus,
                                                       @Param("fromEmail") String fromEmail,
                                                       @Param("toEmail") String toEmail);

    Optional<Object> findByFromEmailAndToEmailOrToEmailAndFromEmail(@NotBlank(message = "Sorry, email is required") String email, String referenceEmail, @NotBlank(message = "Sorry, email is required") String email1, String referenceEmail1);

    @Query(value = "SELECT * FROM connections WHERE connection_status = :status AND from_email = :fromEmail AND to_email = :toEmail", nativeQuery = true)
    Connection findByStatusAndFromEmailAndToEmail(@Param("status") String status,
                                                  @Param("fromEmail") String fromEmail,
                                                  @Param("toEmail") String toEmail);

    int countByStatusAndFromEmailOrToEmail(InteractionStatus string, String userEmail, String userEmail1);
}
