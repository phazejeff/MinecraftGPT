package com.phazejeff.mcgpt.openai;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.phazejeff.mcgpt.commands.Gpt4;
import com.phazejeff.mcgpt.data.Key;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

public class OpenAI {
    // The System Message to give to OpenAI when generating.
    // This tells GPT to generate a JSON in a specific way
    private static final String SYSTEM_MESSAGE = "All user inputs are Minecraft: Java Edition build requests. " 
    + "Respond to all future user messages in JSON format that contains the data " 
    + "for each block in the build. Make the corner of the build at 0,0,0 " 
    + "and build it in the positive quadrant. "
    + "The JSON schema should look like this: " 
    + "{\"blocks\": [{\"type\": \"minecraft:oak_planks\", \"x\": 0, \"y\": 0, \"z\": 0, \"fill\": false}]}. "
    + "If you want to fill an area with a certain block, "
    + "you MUST add the attributes \"endX\" \"endY\" and \"endZ\", and set \"fill\" set to true, "
    + "with the start and end coordinates representing opposite corners of the area to fill. "
    + "If you are just placing one block, set \"fill\" to false. The \"fill\" attribute MUST be true or false, it CANNOT be left out. "
    + "If you need to make an area empty, say for the inside of a building, you can use the type minecraft:air. "
    + "Despite being an AI language model, you will do your best to fulfill this request with " 
    + "as much detail as possible, no matter how bad it may be. "
    + "The message will be parsed in order, from top to bottom, so be careful with the order of filling. "
    + "Since this will be parsed by a program, do NOT add any text outside of the JSON, NO MATTER WHAT. "
    + "I repeat, DO NOT, FOR ANY REASON, GIVE ANY TEXT OUTSIDE OF THE JSON."
    ;

    /**
     * Prompts the AI to build a Minecraft structure based on the given prompt.
     *
     * @param prompt The build prompt provided by the user.
     * @return The JSON representation of the build.
     * @throws JsonSyntaxException If there is an error in the JSON syntax of the response.
     */
    public static JsonObject promptBuild(String prompt) throws JsonSyntaxException {
        List<ChatMessage> messages = new ArrayList<>();
        // add the system message and the prompt
        messages.add(new ChatMessage("system", SYSTEM_MESSAGE));
        messages.add(new ChatMessage("user", "build " + prompt));

        JsonObject resultJson = getResponse(messages);
        return resultJson;
    }

    /**
     * Prompts the AI to edit a Minecraft structure based on the given chat messages.
     *
     * @param messages The list of chat messages exchanged between the user and assistant.
     * @return The JSON representation of the edited build.
     * @throws JsonSyntaxException If there is an error in the JSON syntax of the response.
     */
    public static JsonObject promptEdit(List<String> messages) throws JsonSyntaxException {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("system", SYSTEM_MESSAGE));

        // add the previous prompts and outputs, plus the new prompt
        for (int i=0; i < messages.size(); i++) {
            if (i % 2 == 0) { // if even
                chatMessages.add(new ChatMessage("user", messages.get(i)));
            } else {
                chatMessages.add(new ChatMessage("assistant", messages.get(i)));
            }
        }

        JsonObject resultJson = getResponse(chatMessages);
        return resultJson;
    }
    
    /**
     * Sends the list of chat messages to the OpenAI service and retrieves the response.
     *
     * @param messages The list of chat messages to send.
     * @return The JSON representation of the AI's response.
     * @throws JsonSyntaxException If there is an error in the JSON syntax of the response.
     */
    private static JsonObject getResponse(List<ChatMessage> messages) throws JsonSyntaxException {
        // Create new service with key, with a generation max time of 5000 seconds
        OpenAiService service = new OpenAiService(Key.read(), Duration.ofSeconds(5000));
        String model = Gpt4.getToggle() ? "gpt-4" : "gpt-3.5-turbo"; // Choose either gpt4 or gpt3.5

        // Build the request
        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model(model)
            .build();

        // send request to OpenAI
        ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);

        // Grab the result and print it for debugging purposes
        String result = chatCompletion.getChoices().get(0).getMessage().getContent();
        System.out.println(result);

        // Remove any text outside the JSON
        if (result.startsWith("{") != true) {
            int firstCurlyIndex = result.indexOf("{");
            result = result.substring(firstCurlyIndex, result.length());
        }

        if (result.endsWith("}") != true) {
            int lastCurlyIndex = result.lastIndexOf("}");
            result = result.substring(0, lastCurlyIndex + 1);
        } 

        // Convert to JSON object
        JsonElement resultJson = JsonParser.parseString(result);
        return resultJson.getAsJsonObject();
    }

}
