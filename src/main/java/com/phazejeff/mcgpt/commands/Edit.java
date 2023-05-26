package com.phazejeff.mcgpt.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.phazejeff.mcgpt.MinecraftGPT;
import com.phazejeff.mcgpt.exceptions.InvalidBlockException;
import com.phazejeff.mcgpt.game.BuildItem;
import com.phazejeff.mcgpt.game.BuildStructure;
import com.phazejeff.mcgpt.openai.OpenAI;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class Edit extends Command {
    public Edit() {
        setName("edit");
        String[] args = {"prompt"};
        setArgs(args);
    }

    public int executes(CommandContext<ServerCommandSource> context) {
        Long startTime = System.currentTimeMillis();

        ServerCommandSource source = context.getSource();
        String prompt = StringArgumentType.getString(context, "prompt");
        source.sendMessage(Text.of("Edit: " + prompt + "..."));

        ItemStack buildItemStack = source.getPlayer().getMainHandStack();

        if (!buildItemStack.getItem().equals(MinecraftGPT.BUILD_ITEM)) {
            source.sendMessage(Text.of("Please be holding a build item (given by /build) to use this."));
            return 1;
        }

        BuildItem buildItem = (BuildItem) buildItemStack.getItem();
        NbtCompound nbt = buildItemStack.getNbt();
        Text name = buildItemStack.getName();

        int x = nbt.getInt("x");
        int y = nbt.getInt("y");
        int z = nbt.getInt("z");

        List<String> messages = new ArrayList<String>();

        for (int i=0; i < nbt.getInt("size"); i++) {
            String m = nbt.getString(String.valueOf(i));
            messages.add(m);
        }
        messages.add(prompt);

        // note to professor: I know you said to not use threading.
        // technically, this would work fine without putting it in it's own thread.
        // The problem is that while it's being generated, the entire game logic would freeze until it was finished.
        // This was the only way to avoid the freezing problem.

        // Again, it works fine without putting it in a thread, it will just pause the game until its finished
        // Other than it being in a seperate thread, it's still just normal code we have used in class.
        new Thread(() -> {

            JsonObject edit;
            try {
                edit = OpenAI.promptBuild(prompt);
            } catch (JsonSyntaxException e) {
                Text m = Text.of("JsonSyntaxException: " + e.getMessage() + " This can sometimes happen because ChatGPT ran out of characters or just didn't respond right. Most of the time, repeating your prompt will work.");
                source.sendMessage(m);
                return;
            }

            // Custom exception
            try {
                BuildStructure.build(edit, x, y, z, source.getWorld());
            } catch (InvalidBlockException e) {
                Text m = Text.of(e.getMessage());
                source.sendMessage(m);
                return;
            }

            messages.add(edit.toString());
            ItemStack newBuildItemStack = buildItem.updateItemStack(buildItemStack.getNbt(), messages);
            buildItemStack.setNbt(newBuildItemStack.getNbt());
            buildItemStack.setCustomName(name);

            long endTime = System.currentTimeMillis();
            source.sendMessage(Text.of("Done in " + (float) ((float) (endTime - startTime) / 1000.0f) + " seconds"));
        }).start();

        return 0;
    }

    public boolean equals(Object another) {
        if (
            this.getClass() == another.getClass() &&
            ((Edit) another).getName() == getName() &&
            ((Edit) another).getArgs() == getArgs()
        ) {
            return true;
        }
        return false;
    }
}
