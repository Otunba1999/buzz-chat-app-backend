package com.otunba.dtos;

import com.otunba.enums.ChatType;
import com.otunba.models.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class ChatDto {
    private AppUserDto user;
    private Message lastMessage;
    private ChatType chatType;
    private int unReadMessages;
}
