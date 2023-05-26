package com.phazejeff.mcgpt.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.phazejeff.mcgpt.data.Key;

import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class SetKey extends Command {

    public SetKey() {
        setName("setkey");
        String[] args = {"key"};
        setArgs(args);
    }

    public int executes(CommandContext<ServerCommandSource> context) {
        String key = StringArgumentType.getString(context, getArgs()[0]);
        Key.write(key);

        Text message = Text.of("Open AI key set. Use /build to get started.");
        ((ServerCommandSource) context.getSource()).sendMessage(message);
        return 1;
    }

    public String toString() {
        String key = Key.read();
        if (key == null) {
            return "Command " + getName() + " has no key set.";
        }
        return "Command " + getName() + " has the key " + key;
    }

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
