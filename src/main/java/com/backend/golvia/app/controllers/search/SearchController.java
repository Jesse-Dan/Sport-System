package com.backend.golvia.app.controllers.search;


import com.backend.golvia.app.dtos.interactions.GetUsersSingleUserDto;
import com.backend.golvia.app.services.search.SearchService;
import com.backend.golvia.app.utilities.JwtUtil;
import com.backend.golvia.app.utilities.ResponseHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/search")
public class SearchController {

    @Autowired
    private JwtUtil jwtUtil;


    @Autowired
    private SearchService searchService;

    @GetMapping
    public ResponseEntity<?> search(
            @RequestParam String query,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        try{
            String token = JwtUtil.extractTokenFromHeader(authorizationHeader);
            String email = jwtUtil.extractUsername(token);

            List<GetUsersSingleUserDto> searchResponse = searchService.search(query);
            return ResponseHelper.success(searchResponse, "Search Results", HttpStatus.OK);
        } catch (Exception e) {
            return ResponseHelper.unprocessableEntity(e.getMessage());
        }

    }
}
