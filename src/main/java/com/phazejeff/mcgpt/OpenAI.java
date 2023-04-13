package com.phazejeff.mcgpt;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;

public class OpenAI {
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
    + "Since this will be parsed by a program, do NOT add any text outside of the JSON."
    ;
    private static final String OPENAI_KEY = System.getenv("OPENAI_KEY");
    private static final String MODEL = "gpt-3.5-turbo";
    // private static final String MODEL = "gpt-4";


    public static JsonObject promptBuild(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("system", SYSTEM_MESSAGE));
        messages.add(new ChatMessage("user", "build " + prompt));

        JsonObject resultJson = getResponse(messages);
        return resultJson;
    }

    public static JsonObject promptEdit(List<String> messages) {
        List<ChatMessage> chatMessages = new ArrayList<>();
        chatMessages.add(new ChatMessage("system", SYSTEM_MESSAGE));

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
    

    private static JsonObject getResponse(List<ChatMessage> messages) {
        OpenAiService service = new OpenAiService(OPENAI_KEY, Duration.ofSeconds(5000));

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
            .messages(messages)
            .model(MODEL)
            .build();

        ChatCompletionResult chatCompletion = service.createChatCompletion(completionRequest);

        String result = chatCompletion.getChoices().get(0).getMessage().getContent();
        System.out.println(result);
        JsonObject resultJson = JsonParser.parseString(result).getAsJsonObject();
        return resultJson;
        // List<String> allResults = new ArrayList<>();
        // String fullBuildString = "";

        // boolean running = true;
        // while (running) {
        //     allResults.add(result);
        //     System.out.println(result);
        //     int resultStart = result.indexOf("<START>") + 8;
        //     if (fullBuildString.equals("")) {
        //         fullBuildString += result.substring(resultStart, result.length());
        //     } else {
        //         int lastCommaLocation = fullBuildString.lastIndexOf("},") + 2;
        //         int firstOpenBracketLocation = result.indexOf("{");
        //         fullBuildString = fullBuildString.substring(0, lastCommaLocation) + result.substring(firstOpenBracketLocation, result.length());
        //     }
            
            

        //     if (fullBuildString.contains("[END]")) {
        //         fullBuildString = fullBuildString.substring(0, fullBuildString.indexOf("[END]"));
        //         running = false;
        //     } else {
        //         // TODO combine all chatgpt messages into one json output

        //         messages = new ArrayList<>();
        //         messages.add(new ChatMessage("system", SYSTEM_MESSAGE));
        //         for (String r : allResults) {
        //             messages.add(new ChatMessage("assistant", r));
        //             messages.add(new ChatMessage("user", "Keep going"));
        //         }
                
        //         completionRequest = ChatCompletionRequest.builder()
        //             .messages(messages)
        //             .model(MODEL)
        //             .build();

        //         result = chatCompletion.getChoices().get(0).getMessage().getContent();
        //     }
        // }

        // System.out.println("FULL: " + fullBuildString);
        // JsonObject resultJson = JsonParser.parseString(fullBuildString).getAsJsonObject();

        // return resultJson;
    }

}
