package com.phazejeff.mcgpt.exceptions;

public class InvalidBlockException extends Exception {
    public InvalidBlockException(String blocktype) {
        super("InvalidBlockException: " + blocktype + " is not a valid Minecraft block. This can sometimes happen because ChatGPT doesn't know what is and isn't a valid Minecraft block. It can help to give an example of a valid minecraft block that would replace the invalid one in your prompt.");
    }
}
