package me.danvb10.mossify.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.texture.Sprite;
import net.minecraft.util.math.BlockPos;

import static me.danvb10.mossify.MossifyClient.*;
import static me.danvb10.mossify.render.PrimitiveRenderer.drawCube;

public class MossRenderer {

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;
        if (client.world == null) return;

        PLAYER_POSITION = client.player.getBlockPos();

        for (int x = -10; x <= 10; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -10; z <= 10; z++) {

                    BlockPos thisBlockPosition = PLAYER_POSITION.add(x, y, z);
                    BlockPos underBlockPosition = PLAYER_POSITION.add(x, y - 1, z);

                    BlockState thisBlockState = client.world.getBlockState(thisBlockPosition);
                    BlockState underBlockState = client.world.getBlockState(underBlockPosition);

                    Block thisBlock = thisBlockState.getBlock();
                    Block underBlock = underBlockState.getBlock();

                    if (!ALLOWED_TEXTURES.contains(underBlock)) continue;
                    if (!ALLOWED_ENVIRONMENTS.contains(thisBlock)) continue;

                    int r = 255;
                    int g = 255;
                    int b = 255;
                    int a = 255;

                    Sprite sprite = client.getBlockRenderManager().getModel(underBlockState).getParticleSprite();

                    if (underBlock == Blocks.GRASS_BLOCK) {
                        // Get top of grass sprite
                        sprite = client.getBlockRenderManager().getModel(underBlockState).getQuads(underBlockState, net.minecraft.util.math.Direction.UP, client.world.random).getFirst().getSprite();
                        // Get biome color for grass
                        int color = MinecraftClient.getInstance().getBlockColors().getColor(underBlockState, client.world, underBlockPosition, 0);
                        // Extract RGB values from color integer
                        r = (color >> 16) & 0xFF;
                        g = (color >> 8) & 0xFF;
                        b = color & 0xFF;
                    }

                    // Size CANNOT be greater than 16f
                    float size = 3f;
                    int positionOffsetX = 0,
                        positionOffsetY = 0,
                        positionOffsetZ = 0;

                    drawCube(context, thisBlockPosition, positionOffsetX,positionOffsetY,positionOffsetZ, size, sprite, r, g, b, a);

                }
            }
        }
    }
}