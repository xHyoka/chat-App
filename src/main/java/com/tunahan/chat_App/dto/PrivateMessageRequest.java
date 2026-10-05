package com.tunahan.chat_App.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PrivateMessageRequest {

    private String recipient;
    private String content;
}