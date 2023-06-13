package com.phazejeff.mcgpt.commands;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Represents a command for toggling GPT-4 usage.
 * 
 * @author phazejeff
 * @version 1.0
 */
public class Gpt4 extends Command {
    private static boolean useGpt4 = false;

    /**
     * Constructs a new Gpt4 command.
     * Sets the name to "gpt4".
     */
    public Gpt4() {
        setName("gpt4");
    }

    /**
     * Retrieves the current toggle state of GPT-4 usage.
     *
     * @return true if GPT-4 is enabled, false otherwise.
     */
    public static boolean getToggle() {
        return useGpt4;
    }

    /**
     * Toggles the usage of GPT-4.
     * If GPT-4 is currently enabled, it will be disabled, and vice versa.
     */
    public static void toggle() {
        useGpt4 = !useGpt4;
    }

    /**
     * Executes the Gpt4 command.
     *
     * @param context The command execution context.
     * @return The result of executing the command.
     */
    public int executes(CommandContext<ServerCommandSource> context) {
        toggle();
        Text message = Text.of("GPT4 now set to " + getToggle());
        ((ServerCommandSource) context.getSource()).sendMessage(message);
        return 0;
    }

    /**
     * Returns a string representation of the Gpt4 command.
     *
     * @return A string representation of the Gpt4 command.
     */
    public String toString() {
        return "Command " + getName() + " which currently has GPT4 set as " + getToggle();
    }

    /**
     * Checks if this Gpt4 command is equal to another object.
     *
     * @param another The object to compare with.
     * @return true if the Gpt4 command is equal to the other object, false otherwise.
     */
    public boolean equals(Object another) {
        if (
            this.getClass() == another.getClass() &&
            ((Gpt4) another).getName() == getName() &&
            ((Gpt4) another).getArgs() == getArgs()
        ) {
            return true;
        }
        return false;
    }
}
