package com.backend.golvia.app.channels.aspects;

import java.util.Optional;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.backend.golvia.app.channels.LogChannel;
import com.backend.golvia.app.entities.Channel;
import com.backend.golvia.app.services.activities.ChannelService;

import jakarta.servlet.http.HttpServletRequest;

@Aspect
@Component
public class LogChannelAspect {

    @Autowired
    private ChannelService channelService;

    @Autowired
    private HttpServletRequest request;

    @Before("@annotation(logChannel)")
    public void logChannelData(JoinPoint joinPoint, LogChannel logChannel) {
        Channel channel = new Channel();

        String source = Optional.ofNullable(request.getHeader("X-Channel")).orElse("UNKNOWN");
        String email = Optional.ofNullable(request.getHeader("X-Email")).orElse("unknown@example.com");
        String description = Optional.ofNullable(request.getHeader("X-Description")).orElse(null);
        String ipAddress = request.getRemoteAddr();

        channel.setSource(source);
        channel.setEmail(email);
        channel.setDescription(description);
        channel.setIpAddress(ipAddress);
        channel.setRequestInfo(request.getMethod() + " " + request.getRequestURI());

        channelService.saveChannel(channel);
    }
}
