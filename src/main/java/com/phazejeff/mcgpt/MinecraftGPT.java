package com.phazejeff.mcgpt;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import static net.minecraft.server.command.CommandManager.*;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.StringArgumentType;

public class MinecraftGPT implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("minecraftgpt");

	public static final Item BUILD_ITEM = new BuildItem(new FabricItemSettings());

	public static String openai_key;

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Starting McGPT!");

		Registry.register(Registries.ITEM, new Identifier("mcgpt", "build"), BUILD_ITEM);
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("setkey")
			.then(argument("key", StringArgumentType.greedyString())
				.executes(context -> {
					openai_key = StringArgumentType.getString(context, "key");

					context.getSource().sendMessage(Text.of("Open AI key set. Use /build to get started."));
					return 1;
				})
			)

		));

		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("build")
			.requires(source -> source.isExecutedByPlayer())
			.then(argument("prompt", StringArgumentType.greedyString())
				.executes(context -> {
					if (openai_key.isEmpty()) {
						context.getSource().sendMessage(Text.of("Please set your openai key with /setkey"));
					}

					try {
					Long startTime = System.currentTimeMillis();
					
					ServerCommandSource source = context.getSource();
					String prompt = StringArgumentType.getString(context, "prompt");

					source.sendMessage(Text.of("Building " + prompt + "..."));

					List<String> messages = new ArrayList<String>();
					messages.add("Build " + prompt);

					BlockPos blockPos = Build.getTargettedBlock(source);
					JsonObject build = OpenAI.promptBuild(prompt); // TODO put this in a seperate thread so it doesn't freeze mc
					
					messages.add(build.toString());

					ServerWorld world = source.getWorld();

					Build.build(build, blockPos.getX(), blockPos.getY(), blockPos.getZ(), world);
					
					
					BuildItem buildItem = Build.makeBuildItem(messages, blockPos);
					ItemStack itemStack = buildItem.getItemStack(messages, blockPos.getX(), blockPos.getY(), blockPos.getZ());

					itemStack.setCustomName(Text.of(prompt));

					source.getPlayer().giveItemStack(itemStack);

					long endTime = System.currentTimeMillis();
					source.sendMessage(Text.of("Done in " + (float) ((endTime - startTime) / 1000.0f) + " seconds"));
					} catch (Exception e) {
						e.printStackTrace();
						context.getSource().sendMessage(Text.of(e.toString()));
					}
					
					return 1;
				})
			)
		));
		
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> dispatcher.register(
			literal("edit")
			.requires(source -> source.isExecutedByPlayer() )
			.then(argument("prompt", StringArgumentType.greedyString())
				.executes(context -> {
					if (openai_key.isEmpty()) {
						context.getSource().sendMessage(Text.of("Please set your openai key with /setkey"));
					}
					
					try {
					Long startTime = System.currentTimeMillis();

					ServerCommandSource source = context.getSource();
					String prompt = StringArgumentType.getString(context, "prompt");
					source.sendMessage(Text.of("Edit: " + prompt + "..."));

					ItemStack buildItemStack = source.getPlayer().getMainHandStack();
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


					JsonObject edit = OpenAI.promptEdit(messages);
					Build.build(edit, x, y, z, source.getWorld());

					messages.add(edit.toString());
					ItemStack newBuildItemStack = buildItem.updateItemStack(buildItemStack.getNbt(), messages);
					buildItemStack.setNbt(newBuildItemStack.getNbt());
					buildItemStack.setCustomName(name);

					long endTime = System.currentTimeMillis();
					source.sendMessage(Text.of("Done in " + (float) ((endTime - startTime) / 1000) + " seconds"));
					} catch (Exception e) {
						e.printStackTrace();
						context.getSource().sendMessage(Text.of(e.toString()));
					}
					return 1;
				})
			)
		));

	}
}