package com.backend.golvia.app.services.post;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
public class Pagination {
    private int currentPage;
    private int perPage;
    private int totalPages;
    private int totalItems;

    public Pagination(int currentPage, int perPage, int totalPages, int totalItems) {
        this.currentPage = currentPage;
        this.perPage = perPage;
        this.totalPages = totalPages;
        this.totalItems = totalItems;
    }
}
