package com.phazejeff.mcgpt;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;

public class Build {
    public static void build(JsonObject build, int startX, int startY, int startZ, ServerWorld world) {
        List<JsonElement> blocks = build.get("blocks").getAsJsonArray().asList();

        for (JsonElement b : blocks) {
            JsonObject block = b.getAsJsonObject();
            int blockX = block.get("x").getAsInt();
            int blockY = block.get("y").getAsInt();
            int blockZ = block.get("z").getAsInt();
            String blockType = block.get("type").getAsString();
            Boolean fill = block.get("fill").getAsBoolean();

            if (fill) {
                int endBlockX = block.get("endX").getAsInt();
                int endBlockY = block.get("endY").getAsInt();
                int endBlockZ = block.get("endZ").getAsInt();

                int lengthX = Math.abs(blockX - endBlockX);
                int lengthY = Math.abs(blockY - endBlockY);
                int lengthZ = Math.abs(blockZ - endBlockZ);

                fillArea(
                    startX + blockX, startY + blockY, startZ + blockZ, 
                    lengthX, lengthY, lengthZ, 
                    blockType, world
                );
            } else {
                placeBlock(
                    startX + blockX, startY + blockY, startZ + blockZ, 
                    blockType, world
                );
            } 
        }
    }

    private static void fillArea(
        int startX, int startY, int startZ, 
        int lengthX, int lengthY, int lengthZ, 
        String blockType, ServerWorld world
    ) {
        for (int x=0; x <= lengthX; x++) {
            for (int y=0; y <= lengthY; y++) {
                for (int z=0; z <= lengthZ; z++) {
                    placeBlock(
                        startX + x, startY + y, startZ + z, 
                        blockType, world
                    );
                }
            }
        }
    }

    private static void placeBlock(int x, int y, int z, String blockType, ServerWorld world) {
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = getBlockState(blockType);
        world.setBlockState(pos, blockState);
    }

    private static BlockState getBlockState(String blockType) {
        Identifier id = Identifier.tryParse(blockType);
        Block blockMC = Registries.BLOCK.get(id);
        if (blockMC == null) {
            System.out.println("Couldn't find block for " + blockType);
        }
        BlockState blockState = blockMC.getDefaultState();

        return blockState;
    }

    public static BlockPos getTargettedBlock(ServerCommandSource source) {
        HitResult blockHit = source.getPlayer().getCameraEntity().raycast(20.0D, 0.0f, true);
        if (blockHit.getType() != HitResult.Type.BLOCK) {
            source.sendError(Text.literal("Must be looking at a block!"));
            return new BlockPos(0, 0, 0);
        }

        BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();
        return blockPos;
    }
}
