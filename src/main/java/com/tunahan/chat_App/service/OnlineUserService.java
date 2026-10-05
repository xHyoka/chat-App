package com.tunahan.chat_App.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OnlineUserService {

    private final Map<String,Map<String,String>> usersByRoom = new ConcurrentHashMap<>();

    public void addUser(String room,String sessionId,String username){
        usersByRoom.computeIfAbsent(room,key -> new ConcurrentHashMap<>())
                .put(sessionId,username);
    }

    public void removeUser(String room,String sessionId){
        Map<String,String> users = usersByRoom.get(room);
        if (users != null){
            users.remove(sessionId);
        }
    }

    public List<String> getUsers(String room){
        Map<String,String> users = usersByRoom.get(room);
        if (users == null){
            return new ArrayList<>();
        }
        return new ArrayList<>(users.values());
    }
}
