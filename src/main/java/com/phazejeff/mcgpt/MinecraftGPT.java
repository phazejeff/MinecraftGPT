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

public class MinecraftGPT implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("minecraftgpt");

	public static final Item BUILD_ITEM = new BuildItem(new FabricItemSettings());

	public static String openai_key;
	public static boolean gpt4 = false;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Starting McGPT!");

		Registry.register(Registries.ITEM, new Identifier("mcgpt", "build"), BUILD_ITEM);

		// note to professor: I don't think we have covered lambda expressions,
		// but this is the only way to add a command into Minecraft.

		// GPT4 toggle
		Gpt4 gpt4Toggle = new Gpt4();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(gpt4Toggle.getName())
			.executes(context -> gpt4Toggle.executes(context))
		));
		
		// setkey command
		SetKey setKey = new SetKey();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(setKey.getName())
			.then(argument(setKey.getArgs()[0], StringArgumentType.greedyString())
				.executes(context -> setKey.executes(context))
			)
		));

		// build command
		Build build = new Build();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(build.getName())
			.requires(source -> source.isExecutedByPlayer())
			.then(argument(build.getArgs()[0], StringArgumentType.greedyString())
				.executes(context -> {
					if (Key.read() == null) {
						Text message = Text.of("Please set your openai key with /setkey");
						context.getSource().sendMessage(message);
						return 0;
					}

					build.executes(context);
					
					return 1;
				})
			)
		));
		
		// edit command
		Edit edit = new Edit();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal(edit.getName())
			.requires(source -> source.isExecutedByPlayer() )
			.then(argument(edit.getArgs()[0], StringArgumentType.greedyString())
				.executes(context -> {
					if (Key.read() == null) {
						Text message = Text.of("Please set your openai key with /setkey");
						context.getSource().sendMessage(message);
						return 0;
					}

					edit.executes(context);
					return 1;
				})
			)
		));
	}
}