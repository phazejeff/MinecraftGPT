package com.phazejeff.mcgpt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;

public class MinecraftGPT implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("minecraftgpt");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Starting McGPT!");

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("build")
			.requires(source -> source.isExecutedByPlayer())
				.then(argument("prompt", StringArgumentType.greedyString())
					.executes(context -> {
						ServerCommandSource source = context.getSource();
						String prompt = StringArgumentType.getString(context, "prompt");

						BlockPos blockPos = Build.getTargettedBlock(source);
						JsonObject build = OpenAI.prompt(prompt); // TODO put this in a seperate thread so it doesn't freeze mc

						ServerWorld world = source.getWorld();

						Build.build(build, blockPos.getX(), blockPos.getY(), blockPos.getZ(), world);
						return 1;
					})
			)
		));
	}
}