package com.backend.golvia.app.models.request;

import com.backend.golvia.app.enums.ProfileType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequest {

       private String firstName;
       private String lastName;
       private String email;
       private String country;
       private ProfileType profileType;
       private String password;
       private LocalDateTime dateCreated;
       private LocalDateTime dateUpdated;

}
