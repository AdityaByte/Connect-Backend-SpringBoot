package com.connect.controller;

import com.connect.dto.ChatUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebSocketController {
    // The client make the websocket connection sends the token to the server before the request we need
    // to validate it with the help of the interceptors ok.
    // ok just testing out the things here we have a handler just a handler which recieves the message
    // about the client username and email ok.
    // and we opens up a websocket connection with him.

    @MessageMapping("/firstmsg")
    @SendTo("/topic/first")
    public ResponseEntity<String> handleFirstConn(ChatUserDTO chatUser) {
        System.out.println(chatUser.toString());
        System.out.println(chatUser.getUsername());
        System.out.println(chatUser.getRole());
        System.out.println(chatUser.getStatus());
        return ResponseEntity.ok("Websocket connection established..");
    }
}