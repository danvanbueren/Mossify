package me.danvb10.mossify.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Quaternionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import static me.danvb10.mossify.MossifyClient.*;

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

                    if (ALLOWED_TEXTURES.contains(underBlock)) {
                        if (ALLOWED_ENVIRONMENTS.contains(thisBlock)) {
                            int r = 255;
                            int g = 255;
                            int b = 255;
                            int a = 255;

                            Sprite sprite = client.getBlockRenderManager().getModel(underBlockState).getParticleSprite();

                            if (underBlock == Blocks.GRASS_BLOCK) {
                                // Get top of grass sprite
                                sprite = client.getBlockRenderManager()
                                        .getModel(underBlockState)
                                        .getQuads(underBlockState, net.minecraft.util.math.Direction.UP, client.world.random)
                                        .getFirst()
                                        .getSprite();

                                // Get biome color for grass
                                int color = MinecraftClient.getInstance().getBlockColors().getColor(underBlockState, client.world, underBlockPosition, 0);

                                // Extract RGB values from color integer
                                r = (color >> 16) & 0xFF;
                                g = (color >> 8) & 0xFF;
                                b = color & 0xFF;
                            }


                            float size = 8f;

                            int light = WorldRenderer.getLightmapCoordinates(client.world, thisBlockPosition);

                            drawCube(context, thisBlockPosition, size, sprite, light, r, g, b, a);
                        }
                    }

                }
            }
        }
    }

    public static void drawCube(WorldRenderContext context, BlockPos position, float size, Sprite sprite, int light, int r, int g, int b, int a) {

        size /= 32.0f;

        // Start
        MatrixStack matrixStack = context.matrixStack();
        if (matrixStack == null) return;
        matrixStack.push();

        // Move to world space
        matrixStack.translate(
                position.getX() - context.camera().getPos().x + size,
                position.getY() - context.camera().getPos().y + size,
                position.getZ() - context.camera().getPos().z + size
        );

        // Get VertexConsumerProvider
        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        // Define UV texture coords dynamically
        float uMin = sprite.getMinU(), vMin = sprite.getMinV(), uMax = sprite.getMaxU(), vMax = sprite.getMaxV();

        // Render textures
        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayer.getCutout());

        /* *********** Draw quads to create cube *********** */
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax, new Quaternionf(), new Vector3f(0, 0, 1), light, r, g, b, a); // South-facing
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax, new Quaternionf().rotateY((float) Math.toRadians(180)), new Vector3f(0, 0, -1), light, r, g, b, a); // North-facing
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax, new Quaternionf().rotateY((float) Math.toRadians(90)), new Vector3f(1, 0, 0), light, r, g, b, a); // East-facing
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax, new Quaternionf().rotateY((float) Math.toRadians(-90)), new Vector3f(-1, 0, 0), light, r, g, b, a); // West-facing
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax, new Quaternionf().rotateX((float) Math.toRadians(90)), new Vector3f(0, -1, 0), light, r, g, b, a); // Down-facing
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax, new Quaternionf().rotateX((float) Math.toRadians(-90)), new Vector3f(0, 1, 0), light, r, g, b, a); // Up-facing

        // End
        matrixStack.pop();
    }

    private static void drawQuad(MatrixStack matrixStack, VertexConsumer vertexConsumer, float size, float uMin, float vMin, float uMax, float vMax, Quaternionf rotation, Vector3f translation, int light, int r, int g, int b, int a) {
        // Start
        matrixStack.push();

        // Apply translation
        matrixStack.translate(translation.x * size, translation.y * size, translation.z * size);

        // Apply rotation
        matrixStack.multiply(rotation);

        // Get transformed matrix
        Matrix4f transformedMatrix = matrixStack.peek().getPositionMatrix();

        // Draw quad
        vertexConsumer.vertex(transformedMatrix, -size, -size, 0).texture(uMin, vMin).color(r, g, b, a).light(light).normal(0, 0, 1);
        vertexConsumer.vertex(transformedMatrix, size, -size, 0).texture(uMax, vMin).color(r, g, b, a).light(light).normal(0, 0, 1);
        vertexConsumer.vertex(transformedMatrix, size, size, 0).texture(uMax, vMax).color(r, g, b, a).light(light).normal(0, 0, 1);
        vertexConsumer.vertex(transformedMatrix, -size, size, 0).texture(uMin, vMax).color(r, g, b, a).light(light).normal(0, 0, 1);

        // Stop
        matrixStack.pop();
    }
}