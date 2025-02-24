package com.backend.golvia.app.models.request;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Embeddable
public class Media {
    private String type;
    private String link;

    private String publicId;

    public Media(String type, String link, String publicId) {
        this.type = type;
        this.link = link;
        this.publicId = publicId;
    }
}
