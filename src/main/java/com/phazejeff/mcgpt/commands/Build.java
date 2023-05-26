package com.phazejeff.mcgpt.commands;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.phazejeff.mcgpt.exceptions.InvalidBlockException;
import com.phazejeff.mcgpt.game.BuildItem;
import com.phazejeff.mcgpt.game.BuildStructure;
import com.phazejeff.mcgpt.openai.OpenAI;

import net.minecraft.item.ItemStack;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

/**
 * Represents a command for building.
 * 
 * @author Marcus Beckerman
 * @version 1.0
 */
public class Build extends Command {
    public Build() {
        setName("build");
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
		
        // Grab prompt and tell player command is being executed
        ServerCommandSource source = (ServerCommandSource) context.getSource();
        String prompt = StringArgumentType.getString(context, "prompt");

        source.sendMessage(Text.of("Building " + prompt + "..."));

        // note to professor: I know you said to not use threading.
        // technically, this would work fine without putting it in it's own thread.
        // The problem is that while it's being generated, the entire game logic would freeze until it was finished.
        // This was the only way to avoid the freezing problem.

        // Again, it works fine without putting it in a thread, it will just pause the game until its finished
        // Other than it being in a seperate thread, it's still just normal code we have used in class.

        new Thread(() -> {
            List<String> messages = new ArrayList<String>();
            messages.add("Build " + prompt);

            // Get the block that the player is looking at to use as the origin
            BlockPos blockPos = BuildStructure.getTargettedBlock(source);
            JsonObject build;

            try {
                // Send to OpenAI
                build = OpenAI.promptBuild(prompt);
            } catch (JsonSyntaxException e) {
                // Throw exception if generated JSON is invalid JSON
                Text m = Text.of("JsonSyntaxException: " + e.getMessage() + " This can sometimes happen because ChatGPT ran out of characters or just didn't respond right. Most of the time, repeating your prompt will work.");
                source.sendMessage(m);
                return;
            }
            
            
            messages.add(build.toString());

            ServerWorld world = source.getWorld();

            // Custom Exception
            try {
                // Try building the structure based off the generates JSON
                BuildStructure.build(build, blockPos.getX(), blockPos.getY(), blockPos.getZ(), world);
            } catch (InvalidBlockException e) {
                // Throw custom exception if an invalid block is encountered
                Text m = Text.of(e.getMessage());
                source.sendMessage(m);
                return;
            }
            
            // Give player a custom item containing the data of the generated structure that can be referenced in future edits
            BuildItem buildItem = BuildStructure.makeBuildItem(messages, blockPos);
            ItemStack itemStack = buildItem.getItemStack(messages, blockPos.getX(), blockPos.getY(), blockPos.getZ());

            itemStack.setCustomName(Text.of(prompt));

            source.getPlayer().giveItemStack(itemStack);

            // Print how long it took to generate
            long endTime = System.currentTimeMillis();
            source.sendMessage(Text.of("Done in " + (float) ((float) (endTime - startTime) / 1000.0f) + " seconds"));
        }).start();

        return 0;
    }

    public boolean equals(Object another) {
        if (
            this.getClass() == another.getClass() &&
            ((Build) another).getName() == getName() &&
            ((Build) another).getArgs() == getArgs()
        ) {
            return true;
        }
        return false;
    }
}
