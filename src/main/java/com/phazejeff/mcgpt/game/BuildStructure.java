package com.phazejeff.mcgpt.game;

import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.phazejeff.mcgpt.exceptions.InvalidBlockException;
import com.phazejeff.mcgpt.openai.Chat;

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

/**
 * A utility class for building structures in Minecraft based on a specific JSON.
 * 
 * @author Marcus Beckerman
 * @version 1.0
 */
public class BuildStructure {

    /**
     * Builds a structure defined by the provided JSON object at the specified coordinates in the world.
     *
     * @param build   The JSON object defining the structure.
     * @param startX  The starting X coordinate.
     * @param startY  The starting Y coordinate.
     * @param startZ  The starting Z coordinate.
     * @param world   The server world where the structure should be built.
     * @throws InvalidBlockException If an invalid block type is encountered.
     */
    public static void build(JsonObject build, int startX, int startY, int startZ, ServerWorld world) throws InvalidBlockException {
        List<JsonElement> blocks = build.get("blocks").getAsJsonArray().asList();

        // iterates through every block in the list
        for (JsonElement b : blocks) {
            // grabs block location and type
            JsonObject block = b.getAsJsonObject();
            int blockX = block.get("x").getAsInt();
            int blockY = block.get("y").getAsInt();
            int blockZ = block.get("z").getAsInt();
            String blockType = block.get("type").getAsString();

            boolean fill = false;
            // Check if this is a fill or just placing 1 block
            try {
                fill = block.get("fill").getAsBoolean();
            } catch (NullPointerException e) {
                fill = false;
            }

            // if it is a fill, get the remaining parameters and get the dimensions of the filled area
            if (fill) {
                int endBlockX = block.get("endX").getAsInt();
                int endBlockY = block.get("endY").getAsInt();
                int endBlockZ = block.get("endZ").getAsInt();

                int lengthX = Math.abs(blockX - endBlockX);
                int lengthY = Math.abs(blockY - endBlockY);
                int lengthZ = Math.abs(blockZ - endBlockZ);

                // fill the area
                fillArea(
                    startX + blockX, startY + blockY, startZ + blockZ, 
                    lengthX, lengthY, lengthZ, 
                    blockType, world
                );
            } else {
                // place the block
                placeBlock(
                    startX + blockX, startY + blockY, startZ + blockZ, 
                    blockType, world
                );
            } 
        }
    }

    /**
     * Fills an area with blocks starting from the specified coordinates and with the given dimensions.
     *
     * @param startX     The starting X coordinate.
     * @param startY     The starting Y coordinate.
     * @param startZ     The starting Z coordinate.
     * @param lengthX    The length of the area along the X-axis.
     * @param lengthY    The length of the area along the Y-axis.
     * @param lengthZ    The length of the area along the Z-axis.
     * @param blockType  The type of block to fill the area with.
     * @param world      The server world where the blocks should be placed.
     * @throws InvalidBlockException If an invalid block type is encountered.
     */
    private static void fillArea(
        int startX, int startY, int startZ, 
        int lengthX, int lengthY, int lengthZ, 
        String blockType, ServerWorld world
    ) throws InvalidBlockException {
        // Place each block one at a time, going along the x, then y, then z
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

    /**
     * Places a block of the specified type at the given coordinates in the world.
     *
     * @param x          The X coordinate.
     * @param y          The Y coordinate.
     * @param z          The Z coordinate.
     * @param blockType  The type of block to place.
     * @param world      The server world where the block should be placed.
     * @throws InvalidBlockException If an invalid block type is encountered.
     */
    private static void placeBlock(int x, int y, int z, String blockType, ServerWorld world) throws InvalidBlockException {
        // get the block position in Minecraft and place the block
        BlockPos pos = new BlockPos(x, y, z);
        BlockState blockState = getBlockState(blockType);
        world.setBlockState(pos, blockState);
    }

    /**
     * Retrieves the block state of the specified block type.
     *
     * @param blockType  The type of block.
     * @return The block state.
     * @throws InvalidBlockException If an invalid block type is encountered.
     */
    private static BlockState getBlockState(String blockType) throws InvalidBlockException {
        Identifier id = Identifier.tryParse(blockType);
        Block blockMC = Registries.BLOCK.get(id);

        // If the block doesn't exist, throw custom exception
        if (blockMC == null) {
            System.out.println("Couldn't find block for " + blockType);
            throw new InvalidBlockException(blockType);
        }
        BlockState blockState = blockMC.getDefaultState();

        return blockState;
    }

    /**
     * Retrieves the targeted block based on the player's line of sight in the server command source.
     *
     * @param source The server command source.
     * @return The position of the targeted block.
     */
    public static BlockPos getTargettedBlock(ServerCommandSource source) {
        // Create a raycast from the player's eyes
        HitResult blockHit = source.getPlayer().getCameraEntity().raycast(20.0D, 0.0f, true);

        // Get the block the raycast hits
        if (blockHit.getType() != HitResult.Type.BLOCK) {
            source.sendError(Text.literal("Must be looking at a block!"));
            return new BlockPos(0, 0, 0);
        }

        BlockPos blockPos = ((BlockHitResult) blockHit).getBlockPos();
        return blockPos;
    }

    /**
     * Creates a build item with the specified messages and zero location.
     *
     * @param messages      The list of messages to be included in the build item.
     * @param zeroLocation  The zero location (starting point) of the build item.
     * @return The created build item.
     */
    public static BuildItem makeBuildItem(List<String> messages, BlockPos zeroLocation) {
        Pos pos = new Pos(zeroLocation.getX(), zeroLocation.getY(), zeroLocation.getZ());
        Chat chat = new Chat(pos, messages);

        // Get the build item from registry
        Identifier id = Identifier.of("mcgpt", "build");
        BuildItem buildItem = (BuildItem) Registries.ITEM.get(id);
        
        buildItem.setChat(chat);
        
        return buildItem;
    }
}
