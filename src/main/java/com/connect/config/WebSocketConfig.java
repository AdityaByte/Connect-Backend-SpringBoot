package com.connect.config;

import com.connect.service.JwtTokenService;
import com.connect.security.StompPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.security.Principal;

@Configuration
@EnableWebSocketMessageBroker
@Slf4j
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${frontend.origin}")
    private String FRONTEND_URL;

    private final JwtTokenService service;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins(FRONTEND_URL)
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                log.info("STOMP Command: {}", accessor.getCommand()); // Log the command

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    log.info("Processing CONNECT command...");
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        String username = service.extractUsername(token);
                        log.info("Extracted Username from token: {}", username); // Log extracted username

                        if (username != null) {
                            Principal userPrincipal = new StompPrincipal(username);
                            accessor.setUser(userPrincipal);
                            log.info("Principal set for user: {}", username);
                        } else {
                            log.error("Username could not be extracted from token. Principal NOT SET.");
                        }
                    } else {
                        log.error("Authorization header is missing or does not start with 'Bearer '. Principal NOT SET.");
                    }
                } else {
                    // For other commands (like SEND), check if principal is already present
                    if (accessor.getUser() != null) {
                        log.info("Principal already present for command {} : {}", accessor.getCommand(), accessor.getUser().getName());
                    } else {
                        log.error("No principal found for command: {}", accessor.getCommand());
                    }
                }
                return message;
            }
        });
    }
}