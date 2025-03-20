package me.danvb10.mossify;

import me.danvb10.mossify.render.MossRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

import static me.danvb10.mossify.Commands.registerCommands;

public class MossifyClient implements ClientModInitializer {

    // Mod ID
    public static final String MOD_ID = "mossify";

    // Logger
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // Minecraft Client
    public static MinecraftClient CLIENT;

    // Player position
    public static BlockPos PLAYER_POSITION;

    // Enable/disable entire mod
    public static boolean isEnabled = true;

    // Effect radius
    public static int renderDistance = 20;

    public static final Set<Block> ALLOWED_TEXTURES = Set.of(
            Blocks.GRASS_BLOCK,
            Blocks.DIRT,
            Blocks.STONE,
            Blocks.GRAVEL,
            Blocks.GRANITE,
            Blocks.ANDESITE,
            Blocks.DIORITE,
            Blocks.SAND,
            Blocks.SANDSTONE,
            Blocks.RED_SANDSTONE,
            Blocks.TUFF,
            Blocks.DEEPSLATE,
            Blocks.COAL_ORE,
            Blocks.COPPER_ORE,
            Blocks.DEEPSLATE_COPPER_ORE,
            Blocks.DEEPSLATE_COAL_ORE,
            Blocks.DEEPSLATE_DIAMOND_ORE,
            Blocks.SNOW_BLOCK,
            Blocks.POWDER_SNOW,
            Blocks.ICE,
            Blocks.BLUE_ICE,
            Blocks.FROSTED_ICE,
            Blocks.PACKED_ICE

    );

    public static final Set<Block> ALLOWED_ENVIRONMENTS = Set.of(
            Blocks.AIR,
            Blocks.CAVE_AIR,
            Blocks.VOID_AIR,
            Blocks.SHORT_GRASS,
            Blocks.TALL_GRASS,
            Blocks.CORNFLOWER,
            Blocks.WATER,
            Blocks.LAVA,
            Blocks.SNOW,
            Blocks.GLOW_LICHEN
    );

    @Override
    public void onInitializeClient() {
        LOGGER.info("onInitializeClient");

        CLIENT = MinecraftClient.getInstance();

        registerCommands();

        // Register the MossRenderer to run
        WorldRenderEvents.AFTER_ENTITIES.register(MossRenderer::renderEntrypoint);

    }
}