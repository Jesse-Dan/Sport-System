package com.backend.golvia.app.repositories.interactions;

import com.backend.golvia.app.entities.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ChallengeEntryRepository extends JpaRepository<ChallengeEntry, Long> {

    @Query("SELECT c FROM ChallengeEntry c WHERE c.user.email = :email AND c.challenge = :challenge")
    Optional<ChallengeEntry> findByUserEmailAndChallenge(@Param("email") String email, @Param("challenge") Challenge challenge);

    List<ChallengeEntry> findByChallenge(Challenge challenge);

    @Query("SELECT c FROM ChallengeEntry c WHERE c.user.email = :email")
    Optional<ChallengeEntry> findByUserEmail(@Param("email") String email);

    Optional<ChallengeEntry> findByChallengePost(Post attr0);

    Optional<ChallengeEntry> findByChallengeIdAndUserEmail(Long id, String currentUserEmail);

    ChallengeEntry findByChallengeId(Long id);

    @Query(value = """
        SELECT ce.* 
        FROM challenges_entries ce
        JOIN post_tbl p ON ce.challenge_post_id = p.id
        WHERE ce.email = :email AND ce.challenge_post_id = :postId
        """, nativeQuery = true)
    Optional<ChallengeEntry> findByEmailAndPost(@Param("email") String email, @Param("postId") Long postId);}
