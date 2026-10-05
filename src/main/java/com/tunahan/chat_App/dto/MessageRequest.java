package com.tunahan.chat_App.dto;

import com.tunahan.chat_App.model.MessageType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequest {
    private String sender;
    private String content;
    private String room;


    @Enumerated(EnumType.STRING)
    private MessageType type;
}
