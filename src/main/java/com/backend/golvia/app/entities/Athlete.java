package com.backend.golvia.app.entities;

import java.time.LocalDateTime;
import java.util.Optional;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.backend.golvia.app.dtos.profile.AthleteDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "athletes")
public class Athlete {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(nullable = false, length = 150)
    private int user;

    @Column(nullable = false, length = 150)
    private String email;

    @Column(nullable = true, length = 150)
    private String address;

    @Column(nullable = true, length = 100)
    private String dateOfBirth;

    @Column(nullable = false)
    private int yearsOfExperience;

    @Column(nullable = false, length = 100)
    private String height;

    @Column(nullable = false, length = 100)
    private String weight;

    @Column(nullable = false, length = 255)
    private String biography;

    @Column(nullable = true, length = 200)
    private String currentClub;

    @Column(nullable = false, length = 100)
    private String preferredPosition;

    @Column(nullable = false, length = 100)
    private String preferredFoot;

    @Column(nullable = false, length = 100)
    private String preferredClub;


    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Profession profession;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime dateCreated;

    @UpdateTimestamp
    private LocalDateTime dateUpdated;

    public enum Profession {
        Footballer,
        Basketballer,
        Athletes
    }

    // Default Constructor
    public Athlete() {
        super();
    }

    // Full Constructor
    public Athlete(Long id, int user, String userEmail, String address, String dateOfBirth, int yearsOfExperience,
                   String height, String weight, String biography, String currentClub, String preferredPosition,
                   String preferredFoot, String preferredClub, String email, Profession profession,
                   LocalDateTime dateCreated, LocalDateTime dateUpdated) {
        this.id = id;
        this.user = user;
        this.email = userEmail;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.yearsOfExperience = yearsOfExperience;
        this.height = height;
        this.weight = weight;
        this.biography = biography;
        this.currentClub = currentClub;
        this.preferredPosition = preferredPosition;
        this.preferredFoot = preferredFoot;
        this.preferredClub = preferredClub;
        this.profession = profession;
        this.dateCreated = dateCreated;
        this.dateUpdated = dateUpdated;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String userEmail) {
        this.email = userEmail;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getCurrentClub() {
        return currentClub;
    }

    public void setCurrentClub(String currentClub) {
        this.currentClub = currentClub;
    }

    public String getPreferredPosition() {
        return preferredPosition;
    }

    public void setPreferredPosition(String preferredPosition) {
        this.preferredPosition = preferredPosition;
    }

    public String getPreferredFoot() {
        return preferredFoot;
    }

    public void setPreferredFoot(String preferredFoot) {
        this.preferredFoot = preferredFoot;
    }

    public String getPreferredClub() {
        return preferredClub;
    }

    public void setPreferredClub(String preferredClub) {
        this.preferredClub = preferredClub;
    }

    public Profession getProfession() {
        return profession;
    }

    public void setProfession(Profession profession) {
        this.profession = profession;
    }

    public LocalDateTime getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(LocalDateTime dateCreated) {
        this.dateCreated = dateCreated;
    }

    public LocalDateTime getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(LocalDateTime dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    @Override
    public String toString() {
        return "Athlete{" +
                "id=" + id +
                ", user=" + user +
                ", userEmail='" + email + '\'' +
                ", address='" + address + '\'' +
                ", dateOfBirth='" + dateOfBirth + '\'' +
                ", yearsOfExperience=" + yearsOfExperience +
                ", height='" + height + '\'' +
                ", weight='" + weight + '\'' +
                ", biography='" + biography + '\'' +
                ", currentClub='" + currentClub + '\'' +
                ", preferredPosition='" + preferredPosition + '\'' +
                ", preferredFoot='" + preferredFoot + '\'' +
                ", preferredClub='" + preferredClub + '\'' +
                ", profession=" + profession +
                ", dateCreated=" + dateCreated +
                ", dateUpdated=" + dateUpdated +
                '}';
    }

    public Optional<AthleteDto> map(Object object) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'map'");
    }
}
