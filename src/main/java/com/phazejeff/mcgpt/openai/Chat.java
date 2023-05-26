package com.phazejeff.mcgpt.openai;

import java.util.List;

import com.google.gson.Gson;
import com.phazejeff.mcgpt.game.Pos;

/**
 * Represents an OpenAI chat, with context of the position in Minecraft
 * 
 * @author Marcus Beckerman
 * @version 1.0
 */
public class Chat {
    
    private Pos pos;
    private List<String> messages;

    /**
     * Constructs a new `Chat` object with the specified position and messages.
     *
     * @param pos The position associated with the chat.
     * @param messages The list of messages in the chat.
     */
    public Chat(Pos pos, List<String> messages) {
        this.pos = pos;
        this.messages = messages;
    }

    /**
     * Retrieves the position associated with the chat.
     *
     * @return The position of the chat.
     */
    public Pos getPos() {
        return pos;
    }

    /**
     * Retrieves the list of messages in the chat.
     *
     * @return The list of messages.
     */
    public List<String> getMessages() {
        return messages;
    }

    /**
     * Appends a new message to the list of messages in the chat.
     *
     * @param message The message to append.
     */
    public void appendMessage(String message) {
        messages.add(message);
    }

    /**
     * Converts the `Chat` object to its JSON representation.
     *
     * @return The JSON string representing the chat.
     */
    public String toJson() {
        Gson gson = new Gson();
        String chatJson = gson.toJson(this);

        return chatJson;
    }
}
