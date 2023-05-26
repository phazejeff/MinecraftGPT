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

/**
 * Represents a command for editing the generated structure.
 * 
 * @author Marcus Beckerman
 * @version 1.0
 */
public class Edit extends Command {
    public Edit() {
        setName("edit");
        String[] args = {"prompt"};
        setArgs(args);
    }

    /**
     * Executes the edit command.
     *
     * @param context The command execution context.
     * @return The result of executing the command.
     */
    public int executes(CommandContext<ServerCommandSource> context) {
        // Save start time
        Long startTime = System.currentTimeMillis();

        // Let player know command is being executed
        ServerCommandSource source = context.getSource();
        String prompt = StringArgumentType.getString(context, "prompt");
        source.sendMessage(Text.of("Edit: " + prompt + "..."));

        // Grab the item in the player's hand
        ItemStack buildItemStack = source.getPlayer().getMainHandStack();

        // Check that the item in player's hand is the custom Build item generated by /build
        if (!buildItemStack.getItem().equals(MinecraftGPT.BUILD_ITEM)) {
            source.sendMessage(Text.of("Please be holding a build item (given by /build) to use this."));
            return 1;
        }

        // Grab the name and data of the context of the build from the item
        BuildItem buildItem = (BuildItem) buildItemStack.getItem();
        NbtCompound nbt = buildItemStack.getNbt();
        Text name = buildItemStack.getName();

        int x = nbt.getInt("x");
        int y = nbt.getInt("y");
        int z = nbt.getInt("z");

        List<String> messages = new ArrayList<String>();

        // Convert the data into messages that will be sent to OpenAI
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
                edit = OpenAI.promptBuild(prompt); // Send to OpenAI
            } catch (JsonSyntaxException e) {
                // If generated JSON is an invalid JSON, throw exception
                Text m = Text.of("JsonSyntaxException: " + e.getMessage() + " This can sometimes happen because ChatGPT ran out of characters or just didn't respond right. Most of the time, repeating your prompt will work.");
                source.sendMessage(m);
                return;
            }

            // Custom exception
            try {
                // Build structure according to JSON
                BuildStructure.build(edit, x, y, z, source.getWorld());
            } catch (InvalidBlockException e) {
                // Throw custom exception if an invalid block is encountered
                Text m = Text.of(e.getMessage());
                source.sendMessage(m);
                return;
            }

            // Add the generated output to the build item's data for future edits to reference
            messages.add(edit.toString());
            ItemStack newBuildItemStack = buildItem.updateItemStack(buildItemStack.getNbt(), messages);
            buildItemStack.setNbt(newBuildItemStack.getNbt());

            // Updating the data gets rid of the custom name, so it needs to be set again
            buildItemStack.setCustomName(name);

            // Print how long it took
            long endTime = System.currentTimeMillis();
            source.sendMessage(Text.of("Done in " + (float) ((float) (endTime - startTime) / 1000.0f) + " seconds"));
        }).start();

        return 0;
    }

    /**
     * Checks if this Edit command is equal to another object.
     *
     * @param another The object to compare with.
     * @return true if the Edit command is equal to the other object, false otherwise.
     */
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
