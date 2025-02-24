package com.backend.golvia.app.utilities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponsePagination<T> {
    private int status;
    private String message;
    private LocalDateTime timestamp;
    private List<T> data;
    private PaginationMetadata pagination;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaginationMetadata {
        private int currentPage;
        private int perPage;
        private int totalPages;
        private int totalItems;
    }

    public static <T> ResponseEntity<ResponsePagination<T>> successWithPagination(
            List<T> items,
            int currentPage,
            int perPage,
            int totalItems,
            String message
    ) {
        PaginationMetadata pagination = calculatePagination(currentPage, perPage, totalItems);

        // Paginate the items
        List<T> paginatedItems = paginate(items, currentPage, perPage);

        ResponsePagination<T> response = new ResponsePagination<>(
                HttpStatus.OK.value(),
                message,
                LocalDateTime.now(),
                paginatedItems,
                pagination
        );

        return ResponseEntity.ok(response);
    }

    // Calculate pagination metadata
    private static PaginationMetadata calculatePagination(int currentPage, int perPage, int totalItems) {
        int totalPages = (int) Math.ceil((double) totalItems / perPage);
        return new PaginationMetadata(currentPage, perPage, totalPages, totalItems);
    }

    // Paginate the list dynamically
    public static <T> List<T> paginate(List<T> items, int page, int size) {
        int start = (page - 1) * size;
        int end = Math.min(start + size, items.size());
        return items.subList(start, end);
    }

    public static ResponseEntity<Map<String, Object>> error(String message, HttpStatus statusCode) {
        return ResponseHelper.error(message, statusCode);
    }
}
