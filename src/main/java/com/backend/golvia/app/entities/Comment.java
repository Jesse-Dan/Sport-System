package com.backend.golvia.app.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comment_tbl")
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @CreationTimestamp
    private LocalDateTime dateCreated;

    @CreationTimestamp
    private LocalDateTime dateModified;

    private String content;

//    private Long parentContentId;


    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference("comment-post")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference("comment-user")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private User user;

//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonManagedReference
//    private List<Like> likes;

}
