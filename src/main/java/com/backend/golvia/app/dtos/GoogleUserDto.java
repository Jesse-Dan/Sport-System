package com.backend.golvia.app.dtos;


import lombok.Data;

@Data
public class GoogleUserDto {
    private String iss;
    private String sub;
    private String email;
    private boolean email_verified;
    private String name;
    private String picture;
    private String given_name;
    private String family_name;
    private String locale;
}