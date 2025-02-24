package com.backend.golvia.app.repositories.profiles;


import com.backend.golvia.app.entities.Asset;
import com.backend.golvia.app.entities.Athlete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long>, JpaSpecificationExecutor<Asset> {
    Optional<Asset> findByUserId(Long id);

    void deleteByUserId(Long userId);

    @Query("SELECT a FROM Asset a WHERE a.user.id IN :userIds")
    List<Asset> findAssetsByUserIds(@Param("userIds") List<Long> userIds);

    @Modifying
    @Query(value = "DELETE a1 FROM profile_assets a1 " +
            "INNER JOIN profile_assets a2 " +
            "ON a1.user_id = a2.user_id " +
            "WHERE a1.id > a2.id", nativeQuery = true)
    void deleteDuplicateAssetsByUserId();

    @Modifying
    @Query(value = "ALTER TABLE profile_assets ADD CONSTRAINT unique_user_id UNIQUE (user_id)", nativeQuery = true)
    void enforceUniqueConstraintOnUserId();


}
