package com.backend.golvia.app.services.activities;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.backend.golvia.app.entities.Channel;
import com.backend.golvia.app.repositories.activities.ChannelRepository;

@Service
public class ChannelService {

    @Autowired
    private ChannelRepository channelRepository;

    public Channel saveChannel(Channel channel) {
        return channelRepository.save(channel);
    }

    public List<Channel> getAllChannels() {
        return channelRepository.findAll();
    }
}
