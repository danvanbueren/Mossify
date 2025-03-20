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

    public static void renderEntrypoint(WorldRenderContext context) {
        if (!isEnabled) return;

        long startTime = System.currentTimeMillis();
        if (!isSafeToRender()) return;
        renderLoop(context);
        long endTime = System.currentTimeMillis();
        // System.out.println("Render time: " + (endTime - startTime) + "ms -- Average: x?ms");
    }

    public static boolean isSafeToRender() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;
        if (client.world == null) return false;
        return true;
    }

    public static void renderLoop(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();
        PLAYER_POSITION = client.player.getBlockPos();
/*
        // FRUSTUM CULLING START
        // Get camera
        Camera camera = context.camera();
        Matrix4f projectionMatrix = context.projectionMatrix();

        // Create a new view matrix
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();

        // Apply camera rotation first (correcting direction)
        Quaternionf rotation = camera.getRotation();
        viewMatrix.rotate(rotation.conjugate());

        // Apply translation (move the world opposite to the camera's position)
        viewMatrix.translate(
                (float) -camera.getPos().x,
                (float) -camera.getPos().y,
                (float) -camera.getPos().z
        );

        // Setup frustum
        Frustum frustum = new Frustum(projectionMatrix, viewMatrix);
        frustum.setPosition(camera.getPos().x, camera.getPos().y, camera.getPos().z);
        // FRUSTUM CULLING END
*/
        int hologramsRendered = 0;
        for (int x = -renderDistance; x <= renderDistance; x++) {
            for (int y = -renderDistance/5; y <= renderDistance/5; y++) {
                for (int z = -renderDistance; z <= renderDistance; z++) {
                    BlockPos thisBlockPosition = PLAYER_POSITION.add(x, y, z);
                    BlockPos underBlockPosition = PLAYER_POSITION.add(x, y - 1, z);

                    /*
                    // Frustum Culling START
                    Box blockBox = new Box(
                            (double) thisBlockPosition.getX(), (double) thisBlockPosition.getY(), (double) thisBlockPosition.getZ(),
                            (double) thisBlockPosition.getX() + 1.0, (double) thisBlockPosition.getY() + 1.0, (double) thisBlockPosition.getZ() + 1.0
                    );

                    if (!frustum.isVisible(blockBox)) continue;

                    // Frustum Culling END
                    */

                    assert client.world != null;

                    BlockState thisBlockState = client.world.getBlockState(thisBlockPosition);
                    BlockState underBlockState = client.world.getBlockState(underBlockPosition);
                    Block thisBlock = thisBlockState.getBlock();
                    Block underBlock = underBlockState.getBlock();
                    if (!ALLOWED_TEXTURES.contains(underBlock)) continue;
                    if (!ALLOWED_ENVIRONMENTS.contains(thisBlock)) continue;
                    hologramsRendered++;

                    int r = 255;
                    int g = 255;
                    int b = 255;
                    int a = 255;

                    Sprite sprite = client.getBlockRenderManager().getModel(underBlockState).getParticleSprite();

                    // Manage sprite/color for Grass blocks
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

        System.out.println("Holograms rendered this cycle: " + hologramsRendered);
    }
}