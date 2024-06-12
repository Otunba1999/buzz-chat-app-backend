package com.otunba.services;

import com.otunba.dtos.ChatDto;
import com.otunba.enums.ChatType;
import com.otunba.enums.Status;
import com.otunba.exceptions.ApiException;
import com.otunba.models.AppUser;
import com.otunba.models.Chat;
import com.otunba.models.Message;
import com.otunba.repository.IAppUserRepository;
import com.otunba.repository.IChatRepository;
import com.otunba.repository.IMessageRepository;
import com.otunba.utils.ModelMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.otunba.services.AppUserService.getAuthenticatedUsername;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);
    private final IChatRepository IChatRepository;
    private  final IAppUserRepository userRepository;
    private final IMessageRepository IMessageRepository;
    private final ModelMapper mapper;
    public Optional<String> getChatRoomId(
            String senderId,
            String recipientId,
            boolean createNewRoomIfNotExists
    ) {
        return IChatRepository
                .findBySenderIdAndRecipientId(senderId, recipientId)
                .map(Chat::getChatId)
                .or(() -> {
                    if(createNewRoomIfNotExists) {
                        var chatId = createChatId(senderId, recipientId);
                        return Optional.of(chatId);
                    }

                    return  Optional.empty();
                });
    }

    public List<ChatDto> getUsersChat(){
        var user = userRepository.findByUsername(getAuthenticatedUsername())
                .orElseThrow(() -> new ApiException("Invalid user"));

        List<Chat> bySenderId = IChatRepository.findBySenderId(user.getId());
        List<ChatDto> chatDtos = new ArrayList<>();

        for(var chat : bySenderId) {
            AppUser recipient = userRepository.findById(chat.getRecipientId()).orElseThrow();
            List<Message> messages = IMessageRepository.findByChatId(chat.getChatId());
            List<Message> unReadMessages = messages.stream().filter(mes -> mes.getStatus() == Status.DELIVERED).toList();
            if(!messages.isEmpty()){
                Message message = messages.get(messages.size() - 1);
                ChatDto chatDto = ChatDto.builder()
                        .user(mapper.mapToUserDto(recipient))
                        .lastMessage(message)
                        .chatType(chat.getChatType())
                        .unReadMessages(unReadMessages.size())
                        .build();
                chatDtos.add(chatDto);
            }
        }
        return chatDtos;
    }

    private String createChatId(String senderId, String recipientId) {
        var chatId = String.format("%s_%s", senderId, recipientId);

        Chat senderRecipient = Chat
                .builder()
                .chatId(chatId)
                .senderId(senderId)
                .recipientId(recipientId)
                .chatType(ChatType.PRIVATE)
                .build();

        Chat recipientSender = Chat
                .builder()
                .chatId(chatId)
                .senderId(recipientId)
                .recipientId(senderId)
                .chatType(ChatType.PRIVATE)
                .build();

        IChatRepository.save(senderRecipient);
        IChatRepository.save(recipientSender);

        return chatId;
    }
}
