package com.backend.golvia.app.dtos;

import lombok.Data;

import java.util.List;

@Data
public class CustomEmailRequest {
    private String to;
    private String subject;
    private String htmlContent;
    private Boolean allUsers;
    private Boolean specifiedUsers;
    private List<String> emails;
}