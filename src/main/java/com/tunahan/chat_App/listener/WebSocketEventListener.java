package com.tunahan.chat_App.listener;


import com.tunahan.chat_App.model.Message;
import com.tunahan.chat_App.model.MessageType;
import com.tunahan.chat_App.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class WebSocketEventListener {
    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserService onlineUserService;
    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event){

        String sessionId = event.getSessionId();
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String room = (String) headerAccessor.getSessionAttributes().get("room");


        onlineUserService.removeUser(
                room,
                sessionId
        );


        messagingTemplate.convertAndSend(
                "/topic/room/" + room + "/users",
                onlineUserService.getUsers(room)
        );

        if (username != null){
            Message leaveMessage = Message.builder()
                    .sender(username)
                    .content(username + " sohbotten ayrıldı")
                    .type(MessageType.LEAVE)
                    .timestamp(LocalDateTime.now())
                    .build();

            messagingTemplate.convertAndSend("/topic/room/" + room,leaveMessage);
        }
    }
}
