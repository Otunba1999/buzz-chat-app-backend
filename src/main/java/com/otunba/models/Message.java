package com.otunba.models;

import com.otunba.enums.MessageType;
import com.otunba.enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Date;

@Entity(name = "messages")
@Data
@SuperBuilder
@NoArgsConstructor
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String chatId;
    private String senderId;
    private String recipientId;
    private String content;
    private Date timestamp;
    @Enumerated(EnumType.STRING)
    private MessageType messageType;
    private Status status;
}
