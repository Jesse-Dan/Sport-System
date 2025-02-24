package com.backend.golvia.app.repositories.auth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.OtpDetails;

import java.time.LocalDateTime;

@Repository
public interface OtpRepository extends JpaRepository<OtpDetails, Long>{
	
	
	 OtpDetails findByEmail(String email);
	
	 @Query(value="SELECT * FROM otpdetails  WHERE email = :email AND otp = :otp",nativeQuery = true)
	 OtpDetails findByEmailAndOtp(String email, String otp);


	@Modifying
	@Transactional
	@Query("DELETE FROM OtpDetails o WHERE o.expiryTime < :currentTime")
	void deleteAllExpired(@Param("currentTime") LocalDateTime currentTime);

}
