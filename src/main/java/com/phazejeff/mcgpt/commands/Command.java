package com.phazejeff.mcgpt.commands;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

/**
 * Represents an abstract Minecraft command.
 * 
 * @author phazejeff
 * @version 1.0
 */
public abstract class Command {
    private String name;
    private String[] args;

    /**
     * Sets the name of the command.
     *
     * @param name The name of the command.
     */
    protected void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the arguments of the command.
     *
     * @param args The arguments of the command.
     */
    protected void setArgs(String[] args) {
        this.args = args;
    }

    /**
     * Retrieves the name of the command.
     *
     * @return The name of the command.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the arguments of the command.
     *
     * @return The arguments of the command.
     */
    public String[] getArgs() {
        return args;
    }

    /**
     * Returns a string representation of the command.
     *
     * @return A string representation of the command.
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Command ").append(getName());
        if (getArgs().length != 0) {
            sb.append(" args: ");
            for (String arg : getArgs()) {
                sb.append(arg).append(", ");
            }

            // Removes extra space and comma at the end
            sb.deleteCharAt(sb.length() - 1);
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString();
    };

    /**
     * Checks if this command is equal to another object.
     *
     * @param another The object to compare with.
     * @return true if the command is equal to the other object, false otherwise.
     */
    public abstract boolean equals(Object another);

    /**
     * Executes the command.
     *
     * @param context The command execution context.
     * @return The result of executing the command.
     */
    public abstract int executes(CommandContext<ServerCommandSource> context);
}
