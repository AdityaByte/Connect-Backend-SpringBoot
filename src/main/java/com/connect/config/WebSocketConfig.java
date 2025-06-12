package com.connect.config;

import com.connect.service.JwtTokenService;
import com.connect.security.StompPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
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
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private JwtTokenService service;

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
                .setAllowedOrigins("http://127.0.0.1:5500", "http://localhost:5500", "http://localhost:5173", "http://127.0.0.1:5173")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                System.out.println("STOMP Command: " + accessor.getCommand()); // Log the command

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    System.out.println("Processing CONNECT command...");
                    String authHeader = accessor.getFirstNativeHeader("Authorization");
                    System.out.println("Authorization Header: " + authHeader); // Log the header

                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        String token = authHeader.substring(7);
                        System.out.println("Extracted Token: " + token); // Log the token

                        String username = service.extractUsername(token);
                        System.out.println("Extracted Username from token: " + username); // Log extracted username

                        if (username != null) {
                            Principal userPrincipal = new StompPrincipal(username);
                            accessor.setUser(userPrincipal);
                            System.out.println("Principal set for user: " + username);
                        } else {
                            System.out.println("Username could not be extracted from token. Principal NOT SET.");
                        }
                    } else {
                        System.out.println("Authorization header is missing or does not start with 'Bearer '. Principal NOT SET.");
                    }
                } else {
                    // For other commands (like SEND), check if principal is already present
                    if (accessor.getUser() != null) {
                        System.out.println("Principal already present for command " + accessor.getCommand() + ": " + accessor.getUser().getName());
                    } else {
                        System.out.println("No principal found for command: " + accessor.getCommand());
                    }
                }
                return message;
            }
        });
    }
}