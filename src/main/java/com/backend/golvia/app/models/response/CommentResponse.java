package com.backend.golvia.app.models.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CommentResponse {

        private Long commentId;
        private Long postId;
        private String content;
        private Long parentContentId;
        private String fullName;
        private LocalDateTime dateCreated;
}
