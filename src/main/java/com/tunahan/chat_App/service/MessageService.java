package com.tunahan.chat_App.service;

import com.tunahan.chat_App.dto.MessageRequest;
import com.tunahan.chat_App.model.Message;
import com.tunahan.chat_App.model.MessageType;
import com.tunahan.chat_App.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageService {
    private final MessageRepository messageRepository;

    public Message saveMessage(MessageRequest request){
        Message message = new Message().builder()
                .sender(request.getSender())
                .content(request.getContent())
                .room(request.getRoom())
                .type(request.getType() != null ? request.getType() : MessageType.CHAT)
                .timestamp(LocalDateTime.now())
                .build();
        return messageRepository.save(message);
    }

    public List<Message> getAllMessage(){
        return messageRepository.findAll();
    }

    public List<Message> getMessagesByRoom(String room) {
        return messageRepository.findByRoomOrderByTimestampAsc(room);
    }
}
