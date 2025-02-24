package com.backend.golvia.app.entities;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Entity
@Table(name = "fans")
public class Fan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 150)
    private int user;

    @Setter
    @Getter
    @Column(nullable = false, length = 150)
    private String email;

    @Column(length = 50)
    private String city;

    @Column(name = "profile_picture_url")
    private String profilePictureUrl;

    @ElementCollection
    @CollectionTable(name = "fan_favorite_sports", joinColumns = @JoinColumn(name = "fan_id"))
    @Column(name = "favorite_sport", length = 255)
    private List<String> favoriteSports;

    @ElementCollection
    @CollectionTable(name = "fan_favorite_athletes", joinColumns = @JoinColumn(name = "fan_id"))
    @Column(name = "favorite_athlete", length = 255)
    private List<String> favoriteAthletes;

    @ElementCollection
    @CollectionTable(name = "fan_notification_preferences", joinColumns = @JoinColumn(name = "fan_id"))
    @Column(name = "notification_preference", length = 255)
    private List<String> notificationPreferences;

    @ElementCollection
    @CollectionTable(name = "fan_interactions", joinColumns = @JoinColumn(name = "fan_id"))
    @Column(name = "interaction", length = 255)
    private List<String> interactions;

    @ElementCollection
    @CollectionTable(name = "fan_purchased_items", joinColumns = @JoinColumn(name = "fan_id"))
    @Column(name = "purchased_item", length = 255)
    private List<String> purchasedItems;


    public int getId() {
        return user;
    }

    public void setId(int user) {
        this.user = user;
    }

}
