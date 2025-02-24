package com.backend.golvia.app.repositories.cloudinary;

import com.backend.golvia.app.entities.CloudinaryResponse;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CloudinaryResponseRepository extends JpaRepository<CloudinaryResponse, Long> {

}
