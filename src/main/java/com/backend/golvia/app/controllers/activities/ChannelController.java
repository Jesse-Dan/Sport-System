package com.backend.golvia.app.controllers.activities;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.entities.Channel;
import com.backend.golvia.app.services.activities.ChannelService;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/channels")
public class ChannelController {

    @Autowired
    private ChannelService channelService;

    @LogChannel
    @PostMapping
    public ResponseEntity<Channel> createChannel(@RequestBody Channel channel) {
        Channel savedChannel = channelService.saveChannel(channel);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedChannel);
    }

    @LogChannel
    @GetMapping
    public ResponseEntity<List<Channel>> getAllChannels() {
        List<Channel> channels = channelService.getAllChannels();
        return ResponseEntity.ok(channels);
    }
}
