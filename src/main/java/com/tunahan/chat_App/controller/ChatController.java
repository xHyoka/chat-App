package com.tunahan.chat_App.controller;
import com.tunahan.chat_App.dto.PrivateMessageRequest;
import com.tunahan.chat_App.dto.ChangeRoomRequest;
import com.tunahan.chat_App.dto.MessageRequest;
import com.tunahan.chat_App.model.Message;
import com.tunahan.chat_App.model.MessageType;
import com.tunahan.chat_App.service.MessageService;
import com.tunahan.chat_App.service.OnlineUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.broker.AbstractBrokerMessageHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatController {
    private final MessageService service;
    private final SimpMessagingTemplate messagingTemplate;
    private final OnlineUserService onlineUserService;

    @MessageMapping("/chat")
    public Message sendMessage(MessageRequest request){
        Message savedMessage = service.saveMessage(request);

        messagingTemplate.convertAndSend(
                "/topic/room/" + request.getRoom(),
                savedMessage
        );
        return savedMessage;
    }

    @GetMapping("/messages")
    @ResponseBody
    public List<Message> getMessages(){
        return service.getAllMessage();
    }

    @MessageMapping("/chat.addUser")
    public Message addUser(
            MessageRequest request,
            SimpMessageHeaderAccessor headerAccessor
    ) {

        headerAccessor.getSessionAttributes()
                .put("username", request.getSender());

        headerAccessor.getSessionAttributes()
                .put("room", request.getRoom());

        Message joinMessage = Message.builder()
                .sender(request.getSender())
                .content(request.getSender() + " odaya katıldı")
                .room(request.getRoom())
                .type(MessageType.JOIN)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/room/" + request.getRoom(),
                joinMessage
        );


        String username = request.getSender();
        String room = request.getRoom();
        String sessionId = headerAccessor.getSessionId();
        onlineUserService.addUser(room,sessionId,username);


        messagingTemplate.convertAndSend(
                "/topic/room/" + room + "/users",
                onlineUserService.getUsers(room)
        );

        return joinMessage;
    }

    @GetMapping("/api/online-users/{room}")
    @ResponseBody
    public List<String> getOnlineUsers(@PathVariable String room) {
        return onlineUserService.getUsers(room);
    }

    @MessageMapping("/chat.changeRoom")
    public void changeRoom(ChangeRoomRequest request,SimpMessageHeaderAccessor headerAccessor){
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        String room = (String) headerAccessor.getSessionAttributes().get("room");

        String newRoom = request.getRoom();
        String sessionId = headerAccessor.getSessionId();

        onlineUserService.removeUser(room,sessionId);

        messagingTemplate.convertAndSend(
                "/topic/room/" + room + "/users",
                onlineUserService.getUsers(room)
        );

        onlineUserService.addUser(
                newRoom,
                sessionId,
                username
        );

        messagingTemplate.convertAndSend(
                "/topic/room/" + newRoom + "/users",
                onlineUserService.getUsers(newRoom)
        );

        headerAccessor
                .getSessionAttributes()
                .put("room", newRoom);

        if (username == null || newRoom == null){
            return;
        }
        if (newRoom.equals(room)) {
            return;
        }

        if (room != null){
            Message leaveMessage = Message.builder()
                    .sender(username)
                    .content(username + "odadan ayrıldı")
                    .room(room)
                    .type(MessageType.LEAVE)
                    .timestamp(LocalDateTime.now())
                    .build();

            messagingTemplate.convertAndSend("/topic/room" + room,leaveMessage);
        }

        headerAccessor.getSessionAttributes()
                .put("room",newRoom);

        Message joinMessage = Message.builder()
                .sender(username)
                .content(username + "odaya katıldı")
                .room(newRoom)
                .type(MessageType.JOIN)
                .timestamp(LocalDateTime.now())
                .build();

        messagingTemplate.convertAndSend("/topic/room" + newRoom,joinMessage);
    }

    @GetMapping("/api/messages/{room}")
    @ResponseBody
    public List<Message> getMessages(@PathVariable String room) {
        return service.getMessagesByRoom(room);
    }
}
