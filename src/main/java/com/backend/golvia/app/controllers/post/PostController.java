
package com.backend.golvia.app.controllers.post;

import com.backend.golvia.app.entities.User;
import com.backend.golvia.app.exceptions.BadRequestException;
import com.backend.golvia.app.exceptions.CustomException;
import com.backend.golvia.app.models.request.CommentRequest;
import com.backend.golvia.app.models.request.EditComment;
import com.backend.golvia.app.models.request.PostRequest;
import com.backend.golvia.app.models.response.ResponseDto;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.services.post.PostService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;


@CrossOrigin(origins = "*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {

    private final PostService postService ;
    private final JwtUtil jwtUtil ;
    private final UserRepository userRepository;

        //Create a post
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto<Object>> createPost(
            @RequestBody PostRequest postRequest,
            HttpServletRequest request) {

        try {
            // Extract email from the token
            String email = extractEmailFromToken(request);
            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            // Call the service to create the post
            ResponseDto<Object> postResponse = postService.createPost(postRequest, email);

            return ResponseEntity.ok(postResponse);

        } catch (BadRequestException e) {
            // Handle BadRequestException and return a 400 response with a meaningful message
            ResponseDto<Object> errorResponse = new ResponseDto<>(
                    HttpStatus.BAD_REQUEST.value(),  // Status code 400
                    "Content must not be empty",     // Error message
                    null                            // No data for error
            );
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            // Catch any other unexpected exceptions and return a 500 response
            ResponseDto<Object> errorResponse = new ResponseDto<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    e.getMessage(),
                    null
            );
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    // Like or unlike a post
    @PostMapping("/{postId}/like")
    public ResponseEntity<ResponseDto<Object>> likeOrUnlikePost(@PathVariable Long postId, HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseDto<Object> response = postService.likeOrUnlikePost(postId, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Comment on a post
    @PostMapping("/comment")
    public ResponseEntity<ResponseDto<Object>> commentOnPost(@RequestBody CommentRequest commentRequest, HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseDto<Object> response = postService.commentOnPost(email, commentRequest);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto<Object>> deletePost(@PathVariable Long id, HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new ResponseDto<>(HttpStatus.UNAUTHORIZED.value(), "Unauthorized access", null, LocalDateTime.now())
            );
        }

        ResponseDto<Object> response = postService.deletePostById(id, email);
        return ResponseEntity.ok(response);
    }

    // Edit a post
    @PutMapping(value = "/{postId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseDto<Object>> editPost(
            @PathVariable Long postId,
            @RequestBody PostRequest postRequest,
            HttpServletRequest request) {

        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseDto<Object> postResponse = postService.editPost(postId, postRequest, email);
        return ResponseEntity.ok(postResponse);
    }


    // Edit a comment
    @PutMapping("/{post_id}/comments/{comment_id}")
    public ResponseEntity<ResponseDto<Object>> editComment(@PathVariable Long post_id,
                                                           @PathVariable Long comment_id,
                                                           @RequestBody EditComment editComment,
                                                           HttpServletRequest request) {

        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        ResponseDto<Object> response = postService.editComment(post_id, comment_id, email, editComment);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/user-post")
    public ResponseEntity<ResponseDto<Object>> getPostsByUser(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

        String email = extractEmailFromToken(request);
        if (email == null) {
            System.out.println("Unauthorized access detected: Invalid or missing token"); // Debug line
            throw new Exception("Unauthorized access: Invalid or missing token");
        }

        ResponseDto<Object> posts = postService.getPostsByUser(email, page, size);
        return ResponseEntity.ok(posts);
    }



    // Get a post by ID
    @GetMapping("/{postId}")
    public ResponseEntity<ResponseDto<Object>> getPostById(@PathVariable Long postId, HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseDto<Object> response = postService.getPostById(postId, email).getBody();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Delete a comment
    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ResponseDto<Object>> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        ResponseDto<Object> response = postService.deleteCommentById(id, email);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // Get all comments by post ID
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<ResponseDto<Object>> getAllCommentsByPostId(@PathVariable Long postId, HttpServletRequest request) {
        String email = extractEmailFromToken(request);
        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        List<Map<String, Object>> comments = postService.getAllCommentsByPostId(postId);

        ResponseDto<Object> response = new ResponseDto<>(
                HttpStatus.OK.value(),
                "Comments retrieved successfully.",
                comments,
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/posts")
    public ResponseEntity<ResponseDto<Object>> getAllPosts(
            HttpServletRequest request,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) throws Exception {

        // Extract the email from the token
        String email = extractEmailFromToken(request);

        // Check if the email is null, meaning the token is invalid or missing
        if (email == null) {
            System.out.println("Unauthorized access detected: Invalid or missing token"); // Debug line
            throw new Exception("Unauthorized access: Invalid or missing token");
        }

        // Call the service method to fetch all posts
        ResponseDto<Object> posts = postService.getAllPosts(page, size, email);

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/other-feeds")
    public ResponseEntity<ResponseDto> getPostsByEmail(
            @RequestParam("email") String email,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {

        try {
            // Fetch the user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new Exception("User with email " + email + " not found");
            }

            // Call the service to get posts
            ResponseDto response = postService.getPostsByUser(user, page, size);
            return ResponseEntity.ok(response);

        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    ResponseDto.builder()
                            .status(HttpStatus.NOT_FOUND.value())
                            .message(e.getMessage())
                            .timestamp(LocalDateTime.now())
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private String extractEmailFromToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        String token = authHeader.substring(7);
        return jwtUtil.extractUsername(token);
    }


    @PostMapping("{postId}/creative")
    public ResponseEntity<?> markCreativeOrUncreative(@PathVariable Long postId, HttpServletRequest request) {
        try{

            String email = extractEmailFromToken(request);

            if (email == null) {
                throw new Exception("Unauthorized access: Invalid or missing token");
            }

            ResponseDto<Object> response =  postService.markCreativeOrUncreative(postId, email);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
           return  ResponseHelper.error(e.getMessage(), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}
