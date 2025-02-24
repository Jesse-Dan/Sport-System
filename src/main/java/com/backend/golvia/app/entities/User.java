package com.backend.golvia.app.entities;

import java.time.LocalDateTime;
import java.util.List;

import com.backend.golvia.app.enums.ProfileType;
import com.backend.golvia.app.enums.RegistrationType;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;


@Entity
@Table(name = "users")
@ToString(exclude = "password")
@Data
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank(message = "Sorry, first-name is required")
	@Column(nullable = false)
	private String firstName;

	@NotBlank(message = "Sorry, last-name is required")
	@Column(nullable = false)
	private String lastName;

	@NotBlank(message = "Sorry, email is required")
	@Column(nullable = false, unique = true)
	private String email;

	@Enumerated(EnumType.STRING)
	@Column(name = "registation_type", nullable = true)
	private RegistrationType registationType;

	@Column(nullable = true)
	private String country;

	@Enumerated(EnumType.STRING)
	@Column(name = "profile_type",nullable = true)
	private ProfileType profileType;

//	@NotBlank(message = "Sorry, password is required")
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	@Column(nullable = true)
	private String password;

	@Column(nullable = true)
	private String sportType ;


	@Column(nullable = true)
	private String teamName ;


	@JsonIgnore
	@Column(nullable = true)
	private boolean active = false;

	@JsonIgnore
	@Column(nullable = false)
	private boolean deleted = false;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime dateCreated;

	@UpdateTimestamp
	private LocalDateTime dateUpdated;

    public boolean isPresent() {
        return false;
    }
	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Post> posts;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Like> likes;
}