package com.otunba.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ChatNotification {
    private long id;
    private String senderId;
    private String recipientId;
    private String content;
}
