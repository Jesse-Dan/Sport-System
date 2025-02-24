package com.backend.golvia.app.dtos.challenges;

import com.backend.golvia.app.enums.MediaType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class CreateChallengePayloadDto {
   private String title;
   private String description;
   private List<String> sponsors;
   private MediaType mediaType;
   private LocalDateTime startDate;
   private LocalDateTime endDate;
}
