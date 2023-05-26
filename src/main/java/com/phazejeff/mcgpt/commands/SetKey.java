package com.phazejeff.mcgpt.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.phazejeff.mcgpt.data.Key;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * Represents a command for setting the API key.
 * 
 * @author Marcus Beckerman
 * @version 1.0
 */
public class SetKey extends Command {

    /**
     * Constructs a new SetKey command.
     * Sets the name to "setkey" and initializes the args with a single "key" argument.
     */
    public SetKey() {
        setName("setkey");
        String[] args = {"key"};
        setArgs(args);
    }

    /**
     * Executes the SetKey command.
     *
     * @param context The command execution context.
     * @return The result of executing the command.
     */
    public int executes(CommandContext<ServerCommandSource> context) {
        String key = StringArgumentType.getString(context, getArgs()[0]);
        Key.write(key);

        Text message = Text.of("Open AI key set. Use /build to get started.");
        ((ServerCommandSource) context.getSource()).sendMessage(message);
        return 1;
    }

    /**
     * Returns a string representation of the SetKey command.
     *
     * @return A string representation of the SetKey command.
     */
    public String toString() {
        String key = Key.read();
        if (key == null) {
            return "Command " + getName() + " has no key set.";
        }
        return "Command " + getName() + " has the key " + key;
    }

    /**
     * Checks if this SetKey command is equal to another object.
     *
     * @param another The object to compare with.
     * @return true if the SetKey command is equal to the other object, false otherwise.
     */
    public boolean equals(Object another) {
        if (
            this.getClass() == another.getClass() &&
            ((SetKey) another).getName() == getName() &&
            ((SetKey) another).getArgs() == getArgs()
        ) {
            return true;
        }
        return false;
    }
}
