package com.backend.golvia.app.models.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class EmailDetails {
    private String fullName;

    private String recipient;

    private String messageBody;

    private String subject;

    private String attachment;

    private String link;
}