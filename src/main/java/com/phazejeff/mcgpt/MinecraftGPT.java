package com.phazejeff.mcgpt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import static net.minecraft.server.command.CommandManager.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.phazejeff.mcgpt.commands.Build;
import com.phazejeff.mcgpt.commands.Edit;
import com.phazejeff.mcgpt.commands.Gpt4;
import com.phazejeff.mcgpt.commands.SetKey;
import com.phazejeff.mcgpt.data.Key;
import com.phazejeff.mcgpt.game.BuildItem;

/**
 * <h1>MinecraftGPT</h1>
 * <p>A Minecraft Fabric mod that allows ChatGPT to build structures in Minecraft</p>
 * <p><b>Algorithm: </b></p>
 * <ul>
 * 	<li>/setkey [key]
 * 		<ul>
 * 			<li>Writes the [key] to a file in the config folder</li>
 * 		</ul>
 * 	</li>
 * 
 * 	<li>/gpt4
 * 		<ul>
 * 			<li>Simple toggle that switches between GPT4 and GPT3.5. Default is GPT3.5 (off)</li>
 * 		</ul>
 * 	</li>
 * 
 * 	<li>/build [prompt]
 * 		<ol>
 * 			<li>Checks if key is set via /setkey.</li>
 * 			<li>Sends the prompt to OpenAI API, with hardcoded System Message in {@code com.phazejeff.mcgpt.openai.OpenAI} to generate the proper output
	* 			<ul>
	* 				<li>This outputs a JSON with a list of the blocks. For example: </li>
	* 				<li><code>{\"blocks\": [{\"type\": \"minecraft:oak_planks\", \"x\": 0, \"y\": 0, \"z\": 0, \"fill\": false}]}</code></li>
	* 				<li>This would place 1 oak plank at the spot the command runner was looking</li>
	* 				<li>If "fill" is true, then there would be extra parameters endX, endY, endZ indicating the area that should be filled</li>
	* 			</ul>
 * 			</li>
 * 			<li>Takes output and trims it to get the start and end of the JSON</li>
 * 			<li>Iterates through each block in order, placing blocks reletive to where the player was looking at the time of the command execution</li>
 * 			<li>It will respect the "fill" attribute accordingly, and throw an error if the generated output JSON has a problem</li>
 * 			<li>After completion, it will give an item to the player which is named with the prompt, and contains data with the previous inputs/outputs</li>
 * 		</ol>
 * 	</li>
 * 
 * 	<li>/edit [prompt]
 * 		<ol>
 * 			<li>Checks if key is set via /setkey.</li>
 * 			<li>Checks if player is holding an item given by /build</li>
 * 			<li>Sends the prompt to OpenAI with the context of the previous inputs/outputs, held in the item</li>
 * 			<li>Performs same technique as in /build command when output is given</li>
 * 			<li>Edits item's data by appending the new edits inputs/outputs</li>
 * 		</ol>
 * 	</li>
 * </ul>
 * 
 * <p><b>Items included:<b> Text File I/O (in data.Key), Abstract class (commands.Command), exception class (exceptions.InvalidBlockException),
 * Inheritance (Everything inside of commands) and Polymorphism (some of the equals and toString methods in the commands)
 * 
 * @author phazejeff
 * @version 1.0
 */
public class MinecraftGPT implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("minecraftgpt");

	public static final Item BUILD_ITEM = new BuildItem(new FabricItemSettings());

	// This is the "main" method that gets called
	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Starting McGPT!");

		// Adds the custom item given by /build to the Minecraft registry
		Registry.register(Registries.ITEM, new Identifier("mcgpt", "build"), BUILD_ITEM);

		// note to professor: I don't think we have covered lambda expressions,
		// but this is the only way to add a command into Minecraft.

		// GPT4 toggle
		Gpt4 gpt4Toggle = new Gpt4();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(gpt4Toggle.getName())
			// executes code located in Gpt4.executes()
			.executes(context -> gpt4Toggle.executes(context))
		));
		
		// setkey command
		SetKey setKey = new SetKey();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(setKey.getName())
			.then(argument(setKey.getArgs()[0], StringArgumentType.greedyString()) // gets argument, which will be stored in the context
				//executes code located at SetKey.executes()
				.executes(context -> setKey.executes(context))
			)
		));

		// build command
		Build build = new Build();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(build.getName())
			.requires(source -> source.isExecutedByPlayer()) // Command must be executed by a player and not the server
			.then(argument(build.getArgs()[0], StringArgumentType.greedyString()) // Grabs prompt argument, stored in context
				.executes(context -> {
					// Check if key has been set by /setkey
					if (Key.read() == null) {
						Text message = Text.of("Please set your openai key with /setkey");
						context.getSource().sendMessage(message);
						return 0;
					}

					// runs Build.executes()
					build.executes(context);
					
					return 1;
				})
			)
		));
		
		// edit command
		Edit edit = new Edit();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(edit.getName())
			.requires(source -> source.isExecutedByPlayer()) // Command must be run by player and not by server
			.then(argument(edit.getArgs()[0], StringArgumentType.greedyString()) // Grabs prompt argument and is stored in the context
				.executes(context -> {
					// Checks if key is set with /setkey
					if (Key.read() == null) {
						Text message = Text.of("Please set your openai key with /setkey");
						context.getSource().sendMessage(message);
						return 0;
					}

					// executes code in Edit.executes()
					edit.executes(context);
					return 1;
				})
			)
		));
	}
}