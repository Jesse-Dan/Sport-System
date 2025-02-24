package com.backend.golvia.app.services.dashboard;

import com.backend.golvia.app.repositories.activities.UserActivityRepository;
import com.backend.golvia.app.repositories.post.LikeRepository;
import com.backend.golvia.app.repositories.post.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;


@Service
public class DashboardService {

    private final UserActivityRepository userActivityRepository;

    private final PostRepository postRepository;

    private final LikeRepository likeRepository;

    @Autowired
    public DashboardService(UserActivityRepository userActivityRepository, PostRepository postRepository, LikeRepository likeRepository) {
        this.userActivityRepository = userActivityRepository;
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
    }

    public Map<String, Object> getDailyActiveUsers() {
        // Define the start and end of the current day
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Get the daily active users from the user_activities table
        long dailyUsersCount = userActivityRepository.countDailyActiveUsers(startOfDay, endOfDay);
        String formattedCount = String.format("%,d", dailyUsersCount);

        long newUsersToday = userActivityRepository.countNewUsersToday(startOfDay, endOfDay);
        String formattedNewUsers = String.format("+ %,d today", newUsersToday);

        // Prepare the response
        Map<String, Object> data = new HashMap<>();
        data.put("status", 200);
        data.put("message", "Successfully fetched daily users.");
        data.put("data", Map.of(
                "title", "Daily Active Users",
                "value", formattedCount,
                "current", formattedNewUsers
        ));
        data.put("timestamp", LocalDateTime.now());

        return data;
    }

    public Map<String, Object> getWeeklyActiveUsers() {
        // Define the start and end of the current week (Monday to Sunday)
        LocalDateTime startOfWeek = LocalDate.now().with(DayOfWeek.MONDAY).atStartOfDay();
        LocalDateTime endOfWeek = startOfWeek.plusDays(7);

        // Get the weekly active users from the user_activities table
        long weeklyUsersCount = userActivityRepository.countWeeklyActiveUsers(startOfWeek, endOfWeek);
        String formattedWeeklyCount = String.format("%,d", weeklyUsersCount);

        long newUsersThisWeek = userActivityRepository.countNewUsersThisWeek(startOfWeek, endOfWeek);
        String formattedNewUsersThisWeek = String.format("+ %,d this week", newUsersThisWeek);

        // Prepare the response
        Map<String, Object> data = new HashMap<>();
        data.put("status", 200);
        data.put("message", "Successfully fetched weekly users.");
        data.put("data", Map.of(
                "title", "Weekly Active Users",
                "value", formattedWeeklyCount,
                "current", formattedNewUsersThisWeek
        ));
        data.put("timestamp", LocalDateTime.now());

        return data;
    }

    public Map<String, Object> getMonthlyActiveUsers() {
        // Define the start and end of the current month
        LocalDateTime startOfMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay();
        LocalDateTime endOfMonth = startOfMonth.plusMonths(1);

        // Get the monthly active users from the user_activities table
        long monthlyUsersCount = userActivityRepository.countMonthlyActiveUsers(startOfMonth, endOfMonth);
        String formattedMonthlyCount = String.format("%,d", monthlyUsersCount);

        long newUsersThisMonth = userActivityRepository.countNewUsersThisMonth(startOfMonth, endOfMonth);
        String formattedNewUsersThisMonth = String.format("+ %,d this month", newUsersThisMonth);

        // Prepare the response
        Map<String, Object> data = new HashMap<>();
        data.put("status", 200);
        data.put("message", "Successfully fetched monthly users.");
        data.put("data", Map.of(
                "title", "Monthly Active Users",
                "value", formattedMonthlyCount,
                "current", formattedNewUsersThisMonth
        ));
        data.put("timestamp", LocalDateTime.now());

        return data;
    }

    public Map<String, Object> getTotalUploadedVideos() {
        // Define the start and end of the current day
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Get the total video count from the media_urls table
        long totalVideos = postRepository.countTotalVideos(); // Query for total count

        // Get the count of videos uploaded today
        long videosUploadedToday = postRepository.countVideosUploadedToday(startOfDay, endOfDay);

        // Format the response
        String formattedCount = String.format("%,d", totalVideos);
        String formattedNewVideos = String.format("+ %,d today", videosUploadedToday);

        // Prepare the response payload
        Map<String, Object> data = new HashMap<>();
        data.put("status", 200);
        data.put("message", "Successfully fetched total uploaded videos.");
        data.put("data", Map.of(
                "title", "Total uploaded videos",
                "value", formattedCount,
                "current", formattedNewVideos
        ));
        data.put("timestamp", LocalDateTime.now());

        return data;
    }

    public Map<String, Object> getTotalLikes() {
        // Define the start and end of the current day
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1);

        // Get the total number of likes
        long totalLikes = likeRepository.countTotalLikes(); // Query for total likes

        // Get the number of likes received today
        long likesReceivedToday = likeRepository.countLikesReceivedToday(startOfDay, endOfDay);

        // Format the response
        String formattedCount = String.format("%,d", totalLikes);
        String formattedNewLikes = String.format("+ %,d today", likesReceivedToday);

        // Prepare the response payload
        Map<String, Object> data = new HashMap<>();
        data.put("status", 200);
        data.put("message", "Successfully fetched total likes.");
        data.put("data", Map.of(
                "title", "Total likes",
                "value", formattedCount,
                "current", formattedNewLikes
        ));
        data.put("timestamp", LocalDateTime.now());

        return data;
    }

}
