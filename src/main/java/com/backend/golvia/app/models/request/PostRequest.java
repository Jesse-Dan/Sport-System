package com.backend.golvia.app.models.request;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostRequest {

    private String content;
    private List<Media> media; // List to hold media type and link
    private Integer challengeId;
}

