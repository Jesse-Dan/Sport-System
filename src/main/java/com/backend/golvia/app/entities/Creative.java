package com.backend.golvia.app.entities;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "creative_tbl", uniqueConstraints = {@UniqueConstraint(columnNames = {"post_id", "user_id"})})
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Creative {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime dateCreated;

//    @ManyToOne
//    @JoinColumn(name = "comment_id")
//    @JsonBackReference("like-comment")
//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference("creative-post")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("creative-user")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User user;
}
