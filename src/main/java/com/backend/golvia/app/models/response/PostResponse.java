package com.backend.golvia.app.models.response;

import com.backend.golvia.app.entities.Asset;
import com.backend.golvia.app.models.request.Media;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostResponse {
    private Long id;

    private String message;

    private Map<String, Object> user; // Ensure it's a Map

    private String email;

    private String avatar;

    private Boolean isLiked;

    private Boolean isCreative;

    private Integer challengeId;

    private String title;

    private String content;

    private List<Media> mediaUrls; // Include media URLs in response

    private LocalDateTime dateCreated;

    private LocalDateTime lastUpdated;

    private int likeCount;   // For number of likes

    private int commentCount; // For number of comments

    private List<Map<String, Object>> comments; // For number of comments
}
