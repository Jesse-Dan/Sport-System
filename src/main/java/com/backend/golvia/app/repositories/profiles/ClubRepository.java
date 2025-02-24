package com.backend.golvia.app.repositories.profiles;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.Club;

@Repository
public interface ClubRepository extends JpaRepository<Club, Long>, JpaSpecificationExecutor<Club> {

    @Query(value = "SELECT * FROM clubs WHERE country = :country", nativeQuery = true)
    List<Club> findByCountry(@Param("country") String country);

    @Query(value = "SELECT * FROM clubs WHERE competition_level = :level", nativeQuery = true)
    List<Club> findByCompetitionLevel(@Param("level") String level);

    @Query(value = "SELECT * FROM clubs WHERE recruitment_areas LIKE %:area%", nativeQuery = true)
    List<Club> findByRecruitmentArea(@Param("area") String area);

    @Query(value = "SELECT * FROM clubs WHERE club_name LIKE %:name%", nativeQuery = true)
    List<Club> findByClubNameContaining(@Param("name") String name);

    @Query(value = "SELECT * FROM clubs ORDER BY id DESC LIMIT :limit", nativeQuery = true)
    List<Club> findMostRecentlyAdded(@Param("limit") int limit);

    @Query(value = "SELECT * FROM clubs WHERE city = :city", nativeQuery = true)
    List<Club> findByCity(@Param("city") String city);

    @Query(value = "SELECT * FROM clubs WHERE contact_email = :email", nativeQuery = true)
    Club findByContactEmail(@Param("email") String email);

    @Query(value = "SELECT COUNT(*) FROM clubs WHERE competition_level = :level", nativeQuery = true)
    int countByCompetitionLevel(@Param("level") String level);

    @Query(value = "SELECT * FROM clubs WHERE club_name = :name", nativeQuery = true)
    Club findByExactName(@Param("name") String name);

    @Query(value = "SELECT * FROM clubs WHERE website LIKE %:url%", nativeQuery = true)
    List<Club> findByWebsiteContaining(@Param("url") String url);

    List<Club> findAll();

    @Query("SELECT c FROM Club c WHERE LOWER(c.clubName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.city) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(c.contactPersonName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Club> searchClubs(@Param("query") String query);

    boolean existsByContactEmail(String contactEmail);

    @Modifying
    @Query(value = "DELETE t1 FROM clubs t1 INNER JOIN clubs t2 ON t1.contact_email = t2.contact_email WHERE t1.id > t2.id", nativeQuery = true)
    void deleteDuplicateTeamsByContactEmail();
}