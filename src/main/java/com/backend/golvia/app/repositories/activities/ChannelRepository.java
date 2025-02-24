package com.backend.golvia.app.repositories.activities;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.backend.golvia.app.entities.Channel;

@Repository
public interface ChannelRepository extends JpaRepository<Channel, Long> {
}
