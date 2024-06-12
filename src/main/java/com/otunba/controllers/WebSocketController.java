package com.otunba.controllers;

import com.otunba.dtos.AppUserDto;
import com.otunba.dtos.AuthRequest;
import com.otunba.models.ChatNotification;
import com.otunba.models.Message;
import com.otunba.services.AppUserService;
import com.otunba.services.ChatService;
import com.otunba.services.MessageService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class WebSocketController {
    private static final Logger log = LoggerFactory.getLogger(WebSocketController.class);
    private final AppUserService userService;
    private final SimpMessagingTemplate template;
    private final ChatService chatService;
    private final MessageService chatMessageService;


    @MessageMapping("/user.addUser")
    @SendToUser("/queue/reply")
    public AppUserDto addUser(
            @Payload AuthRequest request
    ) {
       var user = userService.login(request);
       template.convertAndSend("/topic/public", user);
        return user;
    }

    @MessageMapping("/user.disconnectUser")
    @SendTo("/user/public")
    public AppUserDto disconnectUser (
            @Payload AuthRequest request
    ) {
        return userService.disconnect(request);

    }

    @MessageMapping("/chat")
    public void processMessage(@Payload Message chatMessage) {
        log.info("processMessage: {}", chatMessage);
        Message savedMsg = chatMessageService.save(chatMessage);
        template.convertAndSendToUser(
                chatMessage.getRecipientId(), "/queue/messages",
               savedMsg);
    }
    @MessageMapping("/read")
    public void readMessage(@Payload String ids){
       var readMessages =  chatMessageService.read(ids);
       String recipientId = readMessages.get(readMessages.size() - 1).getSenderId();
       template.convertAndSendToUser(recipientId, "/queue/read", readMessages);
    }



}
