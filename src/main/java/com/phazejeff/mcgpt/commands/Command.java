package com.phazejeff.mcgpt.commands;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;

public abstract class Command {
    private String name;
    private String[] args;

    protected void setName(String name) {
        this.name = name;
    }

    protected void setArgs(String[] args) {
        this.args = args;
    }

    public String getName() {
        return name;
    }

    public String[] getArgs() {
        return args;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Command " + getName());
        if (getArgs().length != 0) {
            sb.append(" args: ");
            for (String arg : getArgs()) {
                sb.append(arg + ", ");
            }

            // Removes extra space and comma at the end
            sb.deleteCharAt(sb.length());
            sb.deleteCharAt(sb.length());
        }

        return sb.toString();
    };

    public abstract boolean equals(Object another);

    public abstract int executes(CommandContext<ServerCommandSource> context);
}
