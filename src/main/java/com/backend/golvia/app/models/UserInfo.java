package com.backend.golvia.app.models;

import com.backend.golvia.app.entities.Asset;
import com.backend.golvia.app.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class UserInfo<T> {

    private User user;
    private T profile; // Use a generic type for profile
    private Asset asset;
    private Map<String,Object> metadata;

    public UserInfo() {
        // Default constructor
    }


    @Override
    public String toString() {
        return "UserInfo [user=" + user + ", profile=" + profile + "]";
    }

}
