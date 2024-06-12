package com.otunba.controllers;

import com.otunba.dtos.ChatDto;
import com.otunba.models.Message;
import com.otunba.services.ChatService;
import com.otunba.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/buzz/auth")
@RequiredArgsConstructor
public class ChatController {

    private static final Logger log = LoggerFactory.getLogger(ChatController.class);
    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService chatMessageService;
    private final ChatService chatService;


    @GetMapping("/chats")
    public ResponseEntity<List<ChatDto>> getUserChats(){
        return ResponseEntity.ok(chatService.getUsersChat());
    }

    @GetMapping("/messages/{senderId}/{recipientId}")
    public ResponseEntity<List<Message>> findChatMessages(@PathVariable("senderId") String senderId,
                                                          @PathVariable("recipientId") String recipientId) {
        return ResponseEntity
                .ok(chatMessageService.findChatMessages(senderId, recipientId));
    }
//    @GetMapping("/users/{username}")
//    public ResponseEntity<List<AppUser>> searchUsers(@PathVariable("username") String username){
//        return ResponseEntity.ok()
//    }
}
