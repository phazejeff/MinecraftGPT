package com.phazejeff.mcgpt.commands;

import com.mojang.brigadier.context.CommandContext;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Gpt4 extends Command {
    private static boolean useGpt4 = false;

    public Gpt4() {
        setName("gpt4");
    }

    public static boolean getToggle() {
        return useGpt4;
    }

    public static void toggle() {
        useGpt4 = !useGpt4;
    }

    public int executes(CommandContext<ServerCommandSource> context) {
        toggle();
        Text message = Text.of("GPT4 now set to " + getToggle());
        ((ServerCommandSource) context.getSource()).sendMessage(message);
        return 0;
    }

    public String toString() {
        return "Command " + getName() + " which current has GPT4 set as " + getToggle();
    }

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
