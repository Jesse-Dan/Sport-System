package com.backend.golvia.app.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;


@Entity
@Table(name = "otpdetails")
public class OtpDetails {
    public String getEmail() {
		return email;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public OtpDetails() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public void setExpiryTime(LocalDateTime expiryTime) {
		this.expiryTime = expiryTime;
	}
	

	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@Column(nullable = false, length = 150)
	private String otp;
	
	@Column(nullable = false, length = 150)
    private String email;
    
    
    @JsonIgnore
    private LocalDateTime expiryTime;



    public OtpDetails(String otp, String email, LocalDateTime expiryTime) {
		super();
		this.otp = otp;
		this.email = email;
		this.expiryTime = expiryTime;
	}

	public String getOtp() {
        return otp;
    }

    public LocalDateTime getExpiryTime() {
        return expiryTime;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryTime);
    }
}
