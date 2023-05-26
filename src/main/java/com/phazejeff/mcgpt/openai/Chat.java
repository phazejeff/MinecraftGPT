package com.phazejeff.mcgpt.openai;

import java.util.List;

import com.google.gson.Gson;
import com.phazejeff.mcgpt.game.Pos;

public class Chat {
    
    private Pos pos;
    private List<String> messages;

    public Chat(Pos pos, List<String> messages) {
        this.pos = pos;
        this.messages = messages;
    }

    public Pos getPos() {
        return pos;
    }

    public List<String> getMessages() {
        return messages;
    }

    public void appendMessage(String message) {
        messages.add(message);
    }

    public String toJson() {
        Gson gson = new Gson();
        String chatJson = gson.toJson(this);

        return chatJson;
    }
}
