package com.phazejeff.mcgpt.exceptions;

/**
 * Exception thrown when an invalid Minecraft block is encountered.
 * 
 * @author phazejeff
 * @version 1.0
 */
public class InvalidBlockException extends Exception {
    /**
     * Constructs a new InvalidBlockException with the specified block type.
     *
     * @param blockType The invalid block type.
     */
    public InvalidBlockException(String blocktype) {
        super(
            "InvalidBlockException: " + blocktype + " is not a valid Minecraft block." + 
            "This can sometimes happen because ChatGPT doesn't know what is and isn't a valid Minecraft block." + 
            "It can help to give an example of a valid minecraft block" + 
            "that would replace the invalid one in your prompt."
        );
    }
}
