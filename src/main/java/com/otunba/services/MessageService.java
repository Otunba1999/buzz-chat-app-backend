package com.otunba.services;

import com.otunba.exceptions.ApiException;
import com.otunba.models.Message;
import com.otunba.repository.IMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.otunba.enums.Status.READ;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final IMessageRepository repository;
    private final ChatService chatService;

    public Message save(Message chatMessage) {
        var chatId = chatService
                .getChatRoomId(chatMessage.getSenderId(), chatMessage.getRecipientId(), true)
                .orElseThrow(() -> new ApiException("Chat id not found")); // You can create your own dedicated exception
        chatMessage.setChatId(chatId);
        return repository.save(chatMessage);

    }

    public List<Message> findChatMessages(String senderId, String recipientId) {
        var chatId = chatService.getChatRoomId(senderId, recipientId, false);
        return chatId.map(repository::findByChatId).orElse(new ArrayList<>());
    }

    public List<Message> read(String ids) {
        String[] mesIds = ids.split(",");
        long[]  longIds = new long[mesIds.length];
        for (int i = 0; i < mesIds.length; i++) {
            longIds[i] = Long.parseLong(mesIds[i]);
        }
        List<Long> iterableId = Arrays.stream(longIds).boxed().toList();
        List<Message> allById = repository.findAllById(iterableId);
        for(var mes : allById){
            mes.setStatus(READ);
            repository.save(mes);
        }
        return repository.findAllById(iterableId);
    }
}
