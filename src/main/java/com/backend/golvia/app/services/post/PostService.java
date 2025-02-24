package com.backend.golvia.app.services.post;

import com.backend.golvia.app.dtos.activity_log.StatsDto;
import com.backend.golvia.app.entities.*;
import com.backend.golvia.app.exceptions.NotFoundException;
import com.backend.golvia.app.exceptions.UnauthorizedException;
import com.backend.golvia.app.models.request.CommentRequest;
import com.backend.golvia.app.models.request.EditComment;
import com.backend.golvia.app.models.request.Media;
import com.backend.golvia.app.models.request.PostRequest;
import com.backend.golvia.app.models.response.PostResponse;
import com.backend.golvia.app.models.response.ResponseDto;
import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.cloudinary.CloudinaryResponseRepository;
import com.backend.golvia.app.repositories.post.CommentRepository;
import com.backend.golvia.app.repositories.post.CreativeRepository;
import com.backend.golvia.app.repositories.post.LikeRepository;
//import com.backend.golvia.app.repositories.post.MediaRepository;
import com.backend.golvia.app.repositories.post.PostRepository;
import com.backend.golvia.app.repositories.auth.UserRepository;
import com.backend.golvia.app.repositories.profiles.AssetRepository;
import com.backend.golvia.app.services.activities.ActivityLogService;
import com.backend.golvia.app.services.activities.ActivityStatsService;
import com.backend.golvia.app.services.interactions.ChallengeService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
public class PostService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final CreativeRepository creativeRepository;
    private final AssetRepository assetRepository;
    private final ActivityStatsService activityStatsService;
    private final JwtUtil jwtUtil;
    private final Cloudinary cloudinary;
    private final CloudinaryResponseRepository cloudinaryResponseRepository;
    private final ChallengeService challengeService;
    private final UserActivityRepository userActivityRepository;

    public PostService(UserRepository userRepository, PostRepository postRepository, LikeRepository likeRepository, CommentRepository commentRepository, CreativeRepository creativeRepository, AssetRepository assetRepository, ActivityStatsService activityStatsService, JwtUtil jwtUtil, Cloudinary cloudinary, CloudinaryResponseRepository cloudinaryResponseRepository, ChallengeService challengeService, UserActivityRepository userActivityRepository, UserActivityRepository userActivityRepository1) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.creativeRepository = creativeRepository;
        this.assetRepository = assetRepository;
        this.activityStatsService = activityStatsService;
        this.cloudinary = cloudinary;
        this.cloudinaryResponseRepository = cloudinaryResponseRepository;
        this.challengeService = challengeService;
        this.userActivityRepository = userActivityRepository1;
        System.out.println("PostService initialized");
        this.jwtUtil = jwtUtil;
    }


    @Transactional
    public ResponseDto<Object> createPost(PostRequest postRequest, String email) throws Exception {

        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Create the post entity
        Post post = new Post();
        post.setContent(postRequest.getContent());
        post.setChallengeId(postRequest.getChallengeId());
        post.setUser(user);

        // Save the post to generate its ID
        post = postRepository.save(post);

        if(postRequest.getChallengeId() != null){
            challengeService.updateChallengeEntryPost(email, Long.valueOf(postRequest.getChallengeId()), post.getId());
        }

        // Save the media associated with the post
        if (postRequest.getMedia() != null) {
            List<Media> mediaList = postRequest.getMedia().stream()
                    .map(mediaRequest -> new Media(mediaRequest.getType(), mediaRequest.getLink(), mediaRequest.getPublicId())) // Create Media objects
                    .collect(Collectors.toList());

            post.setMediaUrls(mediaList); // Set media list on the post
        }

        // Save the updated post with media
        postRepository.save(post);
        //Create UserActivity entry for the post creation
        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
        userActivity.setActivityType("Post Created");
        userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp

        // Save the UserActivity in the user_activity table
        userActivityRepository.save(userActivity);


        // Create the response object with media URLs, title, and content
        PostResponse postResponse = PostResponse.builder()
                .message("Successfully posted.")
                .content(post.getContent())
                .mediaUrls(post.getMediaUrls()) // Now returns both type and link for each media
                .dateCreated(post.getDateCreated())
                .build();

        // Return wrapped response with status and timestamp
        return ResponseDto.builder()
                .status(201)
                .message("Successfully posted.")
                .data(postResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ResponseDto<Object> likeOrUnlikePost(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        Optional<Like> existingLike = likeRepository.findByPostIdAndUserId(postId, user.getId());

        String message;
        if (existingLike.isPresent()) {
            likeRepository.delete(existingLike.get());
            message = "Successfully unliked";
            //Create UserActivity entry for the like creation
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
            userActivity.setActivityType("Unliked");
            userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
            // Save the UserActivity in the user_activity table
            userActivityRepository.save(userActivity);
        } else {
            Like like = new Like();
            like.setPost(post);
            like.setUser(user);
            likeRepository.save(like);
            //Create UserActivity entry for the like creation
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
            userActivity.setActivityType("Liked");
            userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
            // Save the UserActivity in the user_activity table
            userActivityRepository.save(userActivity);
            message = "Successfully liked";
        }

        activityStatsService.registerPostImpressions(email);

        return ResponseDto.builder()
                .status(201)
                .message(message)
                .data(null)  // Setting data to null as per your format
                .timestamp(LocalDateTime.now())
                .build();
    }
    public ResponseDto<Object> markCreativeOrUncreative(Long postId, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        Optional<Creative> exCreative = creativeRepository.findByPostIdAndUserId(postId, user.getId());

        String message;
        if (exCreative.isPresent()) {
            creativeRepository.delete(exCreative.get());
            message = "Unmarked as creative";
            //Create UserActivity entry for the like creation
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
            userActivity.setActivityType("Unmarked as creative");
            userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
            // Save the UserActivity in the user_activity table
            userActivityRepository.save(userActivity);
        } else {
            Creative creative = new Creative();
            creative.setPost(post);
            creative.setUser(user);
            creativeRepository.save(creative);
            //Create UserActivity entry for the like creation
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
            userActivity.setActivityType("Marked as Creative");
            userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
            // Save the UserActivity in the user_activity table
            userActivityRepository.save(userActivity);
            message = "Marked as Creative";
        }

        activityStatsService.registerPostImpressions(email);

        return ResponseDto.builder()
                .status(201)
                .message(message)
                .data(null)  // Setting data to null as per your format
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ResponseDto<Object> commentOnPost(String email, CommentRequest commentRequest) {
        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        Post post = postRepository.findById(commentRequest.getPostId())
                .orElseThrow(() -> new NotFoundException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(commentRequest.getContent());
        // comment.setParentContentId(commentRequest.getParentContentId());
        comment.setDateCreated(LocalDateTime.now());
        comment.setPost(post);
        comment.setUser(user);

        Comment savedComment = commentRepository.save(comment);

        //Create UserActivity entry for the comment creation
        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(user.getEmail()); // or use user.getId() if you want to store the user ID
        userActivity.setActivityType("Comment Created");
        userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
        // Save the UserActivity in the user_activity table
        userActivityRepository.save(userActivity);

        // Create a CommentDto to hold response data
        CommentRequest savedCommentDto = new CommentRequest();
        savedCommentDto.setContent(savedComment.getContent());
        // savedCommentDto.setParentContentId(savedComment.getParentContentId());
        savedCommentDto.setPostId(post.getId());

        activityStatsService.registerPostImpressions(email);


        // Wrap the CommentDto in a ResponseDto and return
        return ResponseDto.builder()
                .status(201)
                .message("Comment added successfully")
                .data(savedCommentDto)
                .timestamp(LocalDateTime.now())
                .build();
    }

    //perfect
    @Transactional
    public ResponseDto<Object> deletePostById(Long postId, String email) {
        // Find the post by ID
        Post post = postRepository.findPostWithUserById(postId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Post not found."));

        if (post.getUser() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Post user not found.");
        }
        System.out.println("Post user email: " + post.getUser().getEmail());

        // Print the email to verify it's being passed correctly
        System.out.println("Email passed to the service method: " + email);
        //        // Check if the post belongs to the user making the request
        if (!post.getUser().getEmail().toLowerCase().equals(email.toLowerCase())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this post.");
        }

        // Print the email of the user associated with the post for debugging
        System.out.println("Post user email: " + post.getUser().getEmail());

        try {
            // Iterate over media files associated with the post and attempt to delete them
            for (Media media : post.getMediaUrls()) {
                try {
                    deleteMediaFromCloudinary(media);
                } catch (Exception e) {
                    // Log the error and continue with post deletion
                    System.err.println("Error deleting media from Cloudinary: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            challengeService.deleteChallengeEntryPost(email, postId);

            // Delete the post from the database
            postRepository.delete(post);

            // Create UserActivity entry for the deleted post
            UserActivity userActivity = new UserActivity();
            userActivity.setUserIdentifier(post.getUser().getEmail()); // Or use user ID
            userActivity.setActivityType("Post Deleted");
            userActivity.setTimestamp(LocalDateTime.now());
            userActivityRepository.save(userActivity);

            // Return success response
            return new ResponseDto<>(
                    HttpStatus.OK.value(),
                    "Post and associated media successfully deleted.",
                    null,
                    LocalDateTime.now()
            );

        } catch (Exception e) {
            // Log detailed error information
            System.err.println("Error occurred during post deletion: " + e.getMessage());
            e.printStackTrace();

            // Throw a response status exception with detailed error message
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "An error occurred while deleting the post or media: " + e.getMessage()
            );
        }
    }


    // Method to handle media deletion from Cloudinary
    public void deleteMediaFromCloudinary(Media media) {
        String publicId = media.getPublicId();

        try {
            // Check if the publicId exists in Cloudinary
            Map<String, Object> resourceResponse = cloudinary.api().resource(publicId, ObjectUtils.emptyMap());
            if (resourceResponse == null || !resourceResponse.containsKey("public_id")) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Media with public ID " + publicId + " not found in Cloudinary.");
            }

            // Proceed with media deletion
            Map<String, Object> destroyParams = ObjectUtils.asMap("invalidate", true);
            Map<String, Object> destroyResult = cloudinary.uploader().destroy(publicId, destroyParams);

            // Log Cloudinary response
            logCloudinaryResponse(publicId, destroyResult);

            // Determine the result or reason
            String result = (String) destroyResult.getOrDefault("result", "unknown_error");
            String reason = (String) destroyResult.getOrDefault("error", "Deletion successful");

            // Save the response to the database
            CloudinaryResponse cloudinaryResponse = new CloudinaryResponse();
            cloudinaryResponse.setPublicId(publicId);
            cloudinaryResponse.setResult(result);
            cloudinaryResponse.setTimestamp(LocalDateTime.now());
            if (!"ok".equals(result)) {
                cloudinaryResponse.setResult(reason);
            }

            cloudinaryResponseRepository.save(cloudinaryResponse);

            // Check if deletion was unsuccessful
            if (!"ok".equals(result)) {
                throw new RuntimeException("Failed to delete media from Cloudinary: " + reason);
            }

        } catch (com.cloudinary.api.exceptions.NotFound e) {
            // Handle the case where media is not found in Cloudinary
            String detail = "Media with public ID " + publicId + " not found in Cloudinary.";
            handleErrorAndSaveToDB(publicId, detail, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            // Handle other exceptions
            String detail = "Error deleting media with public ID " + publicId + ": " + e.getMessage();
            handleErrorAndSaveToDB(publicId, detail, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void handleErrorAndSaveToDB(String publicId, String reason, HttpStatus status) {
        CloudinaryResponse cloudinaryResponse = new CloudinaryResponse();
        cloudinaryResponse.setPublicId(publicId);
        cloudinaryResponse.setResult(reason);
        cloudinaryResponse.setTimestamp(LocalDateTime.now());
        cloudinaryResponseRepository.save(cloudinaryResponse);

        // Throw an exception with detailed message
        throw new ResponseStatusException(status, reason);
    }


    private void logCloudinaryResponse(String publicId, Map<String, Object> destroyResult) {
        // Log the result to console or your logging framework
        System.out.println("Cloudinary response for publicId: " + publicId);
        System.out.println("Result: " + destroyResult.get("result"));
    }

    @Transactional
    public ResponseDto<Object> editPost(Long postId, PostRequest postRequest, String email) {
        // Find the post by ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // Verify that the user is the author of the post
        if (!post.getUser().getEmail().equals(email)) {
            throw new UnauthorizedException("You are not authorized to edit this post");
        }

        // Update the post's details if the values are provided
        if (postRequest.getContent() != null && !postRequest.getContent().isEmpty()) {
            post.setContent(postRequest.getContent());
        }

        // Update media URLs and types if provided
        if (postRequest.getMedia() != null) {
            List<Media> mediaList = postRequest.getMedia().stream()
                    .map(mediaRequest -> new Media(mediaRequest.getType(), mediaRequest.getLink(), mediaRequest.getPublicId())) // Create Media objects
                    .collect(Collectors.toList());

            post.setMediaUrls(mediaList); // Set the updated media list
        }

        // Save the updated post
        post = postRepository.save(post);

        //Create UserActivity entry for the edit post creation
        UserActivity userActivity = new UserActivity();
        userActivity.setUserIdentifier(post.getUser().getEmail()); // or use user.getId() if you want to store the user ID
        userActivity.setActivityType("Post Edited");
        userActivity.setTimestamp(LocalDateTime.now()); // Set current timestamp
        // Save the UserActivity in the user_activity table
        userActivityRepository.save(userActivity);

        // Create the response object with updated details
        PostResponse postResponse = PostResponse.builder()
                .message("Post updated successfully.")
                .content(post.getContent())
                .mediaUrls(post.getMediaUrls()) // Includes both type and link
                .dateCreated(post.getDateCreated())
                .lastUpdated(LocalDateTime.now()) // Update the lastUpdated timestamp
                .build();

        // Return wrapped response
        return ResponseDto.builder()
                .status(200)
                .message("Post updated successfully.")
                .data(postResponse)
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    public ResponseDto<Object> editComment(Long postId, Long commentId, String email, EditComment editComment) {
        // Find user by email
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new NotFoundException("User not found");
        }

        // Find the post by ID
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException("Post not found"));

        // Find the comment by ID within the post
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));

        // Ensure the comment belongs to the post
        if (!comment.getPost().getId().equals(post.getId())) {
            throw new NotFoundException("Comment does not belong to the specified post");
        }

        // Check if the user is the author of the comment
        if (!comment.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not authorized to edit this comment");
        }

        // Update the comment content
        comment.setContent(editComment.getContent());
        // Update the date of modification (optional)
        comment.setDateModified(LocalDateTime.now());

        // Save the updated comment
        Comment updatedComment = commentRepository.save(comment);

        // Create a CommentDto to hold response data
        CommentRequest updatedCommentDto = new CommentRequest();
        updatedCommentDto.setContent(updatedComment.getContent());
        updatedCommentDto.setPostId(updatedComment.getPost().getId());

        // Wrap the updated CommentDto in a ResponseDto and return
        return ResponseDto.builder()
                .status(200)
                .message("Comment updated successfully")
                .data(updatedCommentDto)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public ResponseDto<Object> getPostsByUser(String email, int page, int size) {
        try {
            // Find the user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new NotFoundException("User not found");
            }

            // Fetch posts by the user
            List<Post> userPosts = postRepository.findByUserId(user.getId());
            // Check if there are no posts
            if (userPosts == null || userPosts.isEmpty()) {
                return ResponseDto.builder()
                        .status(200)
                        .message("No available post!")
                        .data(null)
                        .timestamp(LocalDateTime.now())
                        .build();
            }
            // Reverse the posts to achieve LIFO (most recent first)
            Collections.reverse(userPosts);
            // Fetch the profile picture URL from the Asset entity
            String profilePictureUrl = getProfilePictureUrl(user);
            // Map posts to the required response format
            List<Map<String, Object>> postResponses = userPosts.stream()
                    .map(post -> {
                        // Fetch like status for the current user and post
                        boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

                        boolean creativeStatus = creativeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

                        // Fetch comments for the post
                        List<Map<String, Object>> comments = commentRepository.findByPostId(post.getId()).stream()
                                .map(comment -> {
                                    Map<String, Object> commentMap = new HashMap<>();
                                    commentMap.put("commentId", comment.getId());
                                    commentMap.put("comment", comment.getContent());
                                    commentMap.put("commentedBy", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                                    commentMap.put("dateTime", comment.getDateCreated().toString());
                                    // Use the utility method to get avatar and email
                                    Map<String, Object> userDetails = getUserAvatarAndEmail(comment.getUser());
                                    commentMap.put("user", userDetails);
                                    return commentMap;
                                })
                                .collect(Collectors.toList());

                        // Map media URLs (type and link) to Media objects
                        List<Map<String, String>> mediaDetails = post.getMediaUrls().stream()
                                .map(media -> {
                                    Map<String, String> mediaMap = new HashMap<>();
                                    mediaMap.put("type", media.getType());
                                    mediaMap.put("link", media.getLink());
                                    return mediaMap;
                                })
                                .collect(Collectors.toList());

                        // Filter and map user details for the post
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("firstName", post.getUser().getFirstName());
                        userDetails.put("lastName", post.getUser().getLastName());
                        userDetails.put("email", post.getUser().getEmail());
                        userDetails.put("profileType", post.getUser().getProfileType());
                        userDetails.put("sportType", post.getUser().getSportType());
                        userDetails.put("teamName", post.getUser().getTeamName());
                        userDetails.put("avatar", profilePictureUrl);

                        // Build post details dynamically
                        Map<String, Object> postDetails = new HashMap<>();
                        postDetails.put("id", post.getId());
                        postDetails.put("user", userDetails);
                        postDetails.put("isLiked", likeStatus);
                        postDetails.put("isCreative", creativeStatus);
                        postDetails.put("challengeId", post.getChallengeId());
                        postDetails.put("content", post.getContent());
                        postDetails.put("mediaUrls", mediaDetails);
                                                postDetails.put("likes", likeRepository.countByPostId(post.getId()));
                        postDetails.put("creatives", creativeRepository.countByPostId(post.getId()));

                        postDetails.put("commentCount", commentRepository.countByPostId(post.getId()));
                        postDetails.put("comments", comments); // Add comments to the post
                        postDetails.put("postedAt", post.getDateCreated().toString()); // Ensure proper formatting

                        return postDetails;
                    })
                    .collect(Collectors.toList());

            // Apply pagination logic
            int totalItems = postResponses.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int start = Math.min((page - 1) * size, totalItems);
            int end = Math.min(start + size, totalItems);
            List<Map<String, Object>> paginatedPosts = postResponses.subList(start, end);

            // Build the response DTO with pagination
            return ResponseDto.builder()
                    .status(200)
                    .message("Successfully retrieved posts!")
                    .data(paginatedPosts)
                    .timestamp(LocalDateTime.now())
                    .pagination(new Pagination(page, size, totalPages, totalItems))
                    .build();

        } catch (NotFoundException e) {
            return ResponseDto.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            return ResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected error occurred: " + e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    public ResponseEntity<ResponseDto<Object>> getPostById(Long postId, String email) {
        try {
            // Find the user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new NotFoundException("User not found");
            }
            // Find the post by ID and throw NotFoundException if not found
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new NotFoundException("Post not found"));
            // Fetch like status for the current user and post
            boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

            boolean creativeStatus = creativeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();
            // Fetch comments in one call and map them
            List<Map<String, Object>> comments = commentRepository.findByPostId(post.getId()).stream()
                    .map(comment -> {
                        Map<String, Object> commentMap = new HashMap<>();
                        commentMap.put("postId", post.getId());
                        commentMap.put("commentId", comment.getId());
                        commentMap.put("comment", comment.getContent());
                        commentMap.put("commentedBy", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                        commentMap.put("dateTime", comment.getDateCreated().toString());
                        // Use the utility method to get avatar and email
                        Map<String, Object> userDetail = getUserAvatarAndEmail(comment.getUser());
                        commentMap.put("user", userDetail);
                        return commentMap;
                    })
                    .collect(Collectors.toList());

            // Reverse the comments to achieve LIFO (most recent first)
            Collections.reverse(comments);
            // Prepare the PostResponse with the expected structure
            PostResponse postResponse = PostResponse.builder()
                    .content(post.getContent())
                    .id(postId)
                    .isLiked(likeStatus)
                    .isCreative(creativeStatus)
                    .mediaUrls(Optional.ofNullable(post.getMediaUrls())
                            .map(urls -> urls.stream()
                                    .map(media -> new Media(media.getType(), media.getLink(), media.getPublicId())) // Assuming media object has a 'type' and 'link'
                                    .collect(Collectors.toList()))
                            .orElse(Collections.emptyList()))  // If no media URLs, return an empty list
                    .likeCount(likeRepository.countByPostId(post.getId()))
                    .commentCount(commentRepository.countByPostId(post.getId())) // Set comment count
                    .comments(comments)
                    .challengeId(post.getChallengeId())
                    .user(mapUserEntity(post.getUser())) // Directly map user details
                    .dateCreated(LocalDateTime.parse(post.getDateCreated().toString()))
                    .build();

            // Build and return the success response
            ResponseDto<Object> responseDto = ResponseDto.builder()
                    .status(HttpStatus.OK.value())
                    .message("Successfully retrieved!")
                    .data(postResponse)
                    .timestamp(LocalDateTime.now())
                    .build();

            return ResponseEntity.ok(responseDto);
        } catch (NotFoundException e) {
            ResponseDto<Object> errorResponse = ResponseDto.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        } catch (Exception e) {
            ResponseDto<Object> errorResponse = ResponseDto.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("An unexpected error occurred: " + e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    public ResponseDto<Object> deleteCommentById(Long commentId, String email) {
        try {
            // Find the comment by ID
            Comment comment = commentRepository.findById(commentId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comment not found."));
            // Check if the comment belongs to the user making the request
            if (!comment.getUser().getEmail().equals(email)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to delete this comment.");
            }
            // Delete the comment
            commentRepository.delete(comment);
            // Build and return the response
            return new ResponseDto<>(
                    HttpStatus.OK.value(),
                    "Comment successfully deleted.",
                    null, // Data is null as it's just a deletion message
                    LocalDateTime.now()
            );
        } catch (ResponseStatusException e) {
            return new ResponseDto<>(
                    e.getStatusCode().value(),
                    e.getReason(),
                    null,
                    LocalDateTime.now()
            );
        } catch (Exception e) {
            return new ResponseDto<>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    "An unexpected error occurred: " + e.getMessage(),
                    null,
                    LocalDateTime.now()
            );
        }
    }

    //perfect
    public List<Map<String, Object>> getAllCommentsByPostId(Long postId) {
        try {
            // Fetch comments for the given postId
            List<Comment> comments = commentRepository.findByPostId(postId);
            // Reverse the comments to achieve LIFO (most recent first)
            Collections.reverse(comments);
            // Map comments to the required response format
            return comments.stream()
                    .map(comment -> {
                        Map<String, Object> commentMap = new HashMap<>();
                        commentMap.put("commentId", comment.getId());
                        commentMap.put("postId", postId);
                        commentMap.put("comment", comment.getContent());
                        commentMap.put("commentedBy", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                        commentMap.put("dateTime", comment.getDateCreated().toString());
                        // Use the utility method to get avatar and email
                        Map<String, Object> userDetails = getUserAvatarAndEmail(comment.getUser());
                        commentMap.put("user", userDetails);

                        return commentMap;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseDto<Object> getAllPosts(int page, int size, String email) {
        try {
            // Find the user by email
            User user = userRepository.findByEmail(email);
            if (user == null) {
                throw new NotFoundException("User not found");
            }
            // Fetch all posts from the database
            List<Post> allPosts = postRepository.findAll();
            // Check if posts list is null or empty
            if (allPosts.isEmpty()) {
                throw new NotFoundException("No posts available");
            }
            // Reverse the posts to achieve LIFO (most recent first)
            Collections.reverse(allPosts);

            // Map posts to the required response format
            List<Map<String, Object>> postResponses = allPosts.stream()
                    .filter(post -> post.getUser() != null) // Exclude posts with null users
                    .map(post -> {
                        // Fetch like status for the current user and post
                        boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

                        boolean creativeStatus = creativeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

                        // Fetch comments for the post
                        List<Map<String, Object>> comments = commentRepository.findByPostId(post.getId()).stream()
                                .map(comment -> {
                                    Map<String, Object> commentMap = new HashMap<>();
                                    commentMap.put("commentId", comment.getId());
                                    commentMap.put("comment", comment.getContent());
                                    commentMap.put("commentedBy", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                                    commentMap.put("dateTime", comment.getDateCreated().toString());
                                    // Use the utility method to get avatar and email
                                    Map<String, Object> userDetails = getUserAvatarAndEmail(comment.getUser());
                                    commentMap.put("user", userDetails);
                                    return commentMap;
                                })
                                .collect(Collectors.toList());

                        // Map media URLs (type and link) to Media objects
                        List<Map<String, String>> mediaDetails = post.getMediaUrls().stream()
                                .map(media -> {
                                    Map<String, String> mediaMap = new HashMap<>();
                                    mediaMap.put("type", media.getType());
                                    mediaMap.put("link", media.getLink());
                                    mediaMap.put("publicId", media.getPublicId());
                                    return mediaMap;
                                })
                                .collect(Collectors.toList());

                        // Filter and map user details for the post
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("firstName", post.getUser().getFirstName());
                        userDetails.put("lastName", post.getUser().getLastName());
                        userDetails.put("email", post.getUser().getEmail());
                        userDetails.put("profileType", post.getUser().getProfileType());
                        userDetails.put("sportType", post.getUser().getSportType());
                        userDetails.put("teamName", post.getUser().getTeamName());
                        userDetails.put("avatar", getProfilePictureUrl(post.getUser())); // Fetch avatar dynamically

                        // Build post details dynamically
                        Map<String, Object> postDetails = new HashMap<>();
                        postDetails.put("id", post.getId());
                        postDetails.put("user", userDetails);
                        postDetails.put("challengeId", post.getChallengeId());
                        postDetails.put("isLiked", likeStatus);
                        postDetails.put("isCreative", creativeStatus);
                        postDetails.put("content", post.getContent());
                        postDetails.put("mediaUrls", mediaDetails);
                                                postDetails.put("likes", likeRepository.countByPostId(post.getId()));
                        postDetails.put("creatives", creativeRepository.countByPostId(post.getId()));

                        postDetails.put("commentCount", commentRepository.countByPostId(post.getId()));
                        postDetails.put("comments", comments); // Add comments to the post
                        postDetails.put("postedAt", post.getDateCreated().toString()); // Ensure proper formatting

                        return postDetails;
                    })
                    .collect(Collectors.toList());

            // Apply pagination logic
            int totalItems = postResponses.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int start = Math.min((page - 1) * size, totalItems);
            int end = Math.min(start + size, totalItems);
            List<Map<String, Object>> paginatedPosts = postResponses.subList(start, end);

            // Build the response DTO with pagination
            return ResponseDto.builder()
                    .status(200)
                    .message("Successfully retrieved posts!")
                    .data(paginatedPosts)
                    .timestamp(LocalDateTime.now())
                    .pagination(new Pagination(page, size, totalPages, totalItems))
                    .build();
        } catch (NotFoundException e) {
            // Handle user not found exception
            return ResponseDto.builder()
                    .status(404)
                    .message(e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            // Handle other unexpected exceptions
            return ResponseDto.builder()
                    .status(500)
                    .message("An unexpected error occurred: " + e.getMessage())
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    public ResponseDto<Object> getPostsByUser(User user, int page, int size) {
        try {
            // Fetch posts by the user
            List<Post> userPosts = postRepository.findByUserId(user.getId());
            // Reverse the posts for LIFO
            Collections.reverse(userPosts);
            // Fetch the profile picture URL from the Asset entity
            String profilePictureUrl = getProfilePictureUrl(user);
            // Map posts to response format
            List<Map<String, Object>> postResponses = userPosts.stream()
                    .map(post -> {
                        // Check like status for the post
                        boolean likeStatus = likeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

                        boolean creativeStatus = creativeRepository.findByPostIdAndUserId(post.getId(), user.getId()).isPresent();

                        // Fetch comments for the post
                        List<Map<String, Object>> comments = commentRepository.findByPostId(post.getId()).stream()
                                .map(comment -> {
                                    Map<String, Object> commentMap = new HashMap<>();
                                    commentMap.put("commentId", comment.getId());
                                    commentMap.put("comment", comment.getContent());
                                    commentMap.put("commentedBy", comment.getUser().getFirstName() + " " + comment.getUser().getLastName());
                                    commentMap.put("dateTime", comment.getDateCreated().toString());
                                    Map<String, Object> userDetails = getUserAvatarAndEmail(comment.getUser());
                                    commentMap.put("user", userDetails);
                                    return commentMap;
                                })
                                .collect(Collectors.toList());

                        // Map media URLs
                        List<Map<String, String>> mediaDetails = post.getMediaUrls().stream()
                                .map(media -> {
                                    Map<String, String> mediaMap = new HashMap<>();
                                    mediaMap.put("type", media.getType());
                                    mediaMap.put("link", media.getLink());
                                    mediaMap.put("publicId", media.getPublicId());
                                    return mediaMap;
                                })
                                .collect(Collectors.toList());

                        // User details for the post
                        Map<String, Object> userDetails = new HashMap<>();
                        userDetails.put("firstName", post.getUser().getFirstName());
                        userDetails.put("lastName", post.getUser().getLastName());
                        userDetails.put("email", post.getUser().getEmail());
                        userDetails.put("profileType", post.getUser().getProfileType());
                        userDetails.put("sportType", post.getUser().getSportType());
                        userDetails.put("teamName", post.getUser().getTeamName());
                        userDetails.put("avatar", profilePictureUrl);

                        // Build post details
                        Map<String, Object> postDetails = new HashMap<>();
                        postDetails.put("id", post.getId());
                        postDetails.put("user", userDetails);
                        postDetails.put("isLiked", likeStatus);
                        postDetails.put("isCreative", creativeStatus);
                        postDetails.put("challengeId", post.getChallengeId());
                        postDetails.put("content", post.getContent());
                        postDetails.put("mediaUrls", mediaDetails);
                                                postDetails.put("likes", likeRepository.countByPostId(post.getId()));
                        postDetails.put("creatives", creativeRepository.countByPostId(post.getId()));

                        postDetails.put("commentCount", commentRepository.countByPostId(post.getId()));
                        postDetails.put("comments", comments);
                        postDetails.put("postedAt", post.getDateCreated().toString());

                        return postDetails;
                    })
                    .collect(Collectors.toList());

            // Apply pagination logic
            int totalItems = postResponses.size();
            int totalPages = (int) Math.ceil((double) totalItems / size);
            int start = Math.min((page - 1) * size, totalItems);
            int end = Math.min(start + size, totalItems);
            List<Map<String, Object>> paginatedPosts = postResponses.subList(start, end);

            // Build the response DTO
            return ResponseDto.builder()
                    .status(200)
                    .message("Successfully retrieved posts!")
                    .data(paginatedPosts)
                    .timestamp(LocalDateTime.now())
                    .pagination(new Pagination(page, size, totalPages, totalItems))
                    .build();
        } catch (Exception e) {
            // Handle unexpected exceptions
            return ResponseDto.builder()
                    .status(500)
                    .message("An unexpected error occurred: " + e.getMessage())
                    .data(null)
                    .timestamp(LocalDateTime.now())
                    .build();
        }
    }

    public Map<String, Object> getUserAvatarAndEmail(User user) {
        Map<String, Object> userDetails = new HashMap<>();
        if (user != null) {
            // Add email to the map
            userDetails.put("email", user.getEmail());

            // Add avatar (profile picture URL) to the map
            String avatar = null;
            if (user.getId() != null) {
                Optional<Asset> asset = assetRepository.findByUserId(user.getId());
                if (asset.isPresent()) {
                    avatar = asset.get().getProfilePictureUrl(); // If no profile picture, this will be null
                }
            }
            userDetails.put("avatar", avatar);
        }
        return userDetails;
    }

    public String getProfilePictureUrl(User user) {
        if (user != null && user.getId() != null) {
            Optional<Asset> asset = assetRepository.findByUserId(user.getId()); // Assuming you have a repository to fetch Asset by user ID
            if (asset.isPresent()) {
                return asset.get().getProfilePictureUrl(); // Return the profile picture URL, or null if not set
            } else {
                // Log if no asset is found
                System.out.println("No profile picture found for user ID: " + user.getId());
                return null; // Return null if no asset found
            }
        }
        return null; // Return null if user is null or user ID is not set
    }

    // Helper method to map user details to a Map
    private Map<String, Object> mapUserEntity(User user) {
        Map<String, Object> userEntity = new HashMap<>();
        userEntity.put("teamName", user.getTeamName());
        userEntity.put("firstName", user.getFirstName());
        userEntity.put("lastName", user.getLastName());
        userEntity.put("profileType", user.getProfileType());
        userEntity.put("sportType", user.getSportType());
        userEntity.put("email", user.getEmail());
        // Fetch the profile picture URL from the Asset entity
        String profilePictureUrl = getProfilePictureUrl(user);
        userEntity.put("avatar", profilePictureUrl);
        return userEntity;
    }

}