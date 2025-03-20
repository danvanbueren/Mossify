package me.danvb10.mossify.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class PrimitiveRenderer {

    public static void drawCube(
            WorldRenderContext context,
            BlockPos position,
            int positionOffsetX, int positionOffsetY, int positionOffsetZ,
            float size,
            Sprite sprite,
            int r, int g, int b, int a
    ) {

        MinecraftClient client = MinecraftClient.getInstance();

        if (client.world == null) return;

        size /= 32.0f; //size is scaled such as 1 unit is 1/16th of a block. This means a size of 16 is a full standard block.

        float scaledPositionOffsetX = positionOffsetX / 16.0f;
        float scaledPositionOffsetY = positionOffsetY / 16.0f;
        float scaledPositionOffsetZ = positionOffsetZ / 16.0f;

        // Start
        MatrixStack matrixStack = context.matrixStack();
        if (matrixStack == null) return;
        matrixStack.push();

        // Move to world space
        matrixStack.translate(
                position.getX() - context.camera().getPos().x + size + scaledPositionOffsetX,
                position.getY() - context.camera().getPos().y + size + scaledPositionOffsetY,
                position.getZ() - context.camera().getPos().z + size + scaledPositionOffsetZ
        );

        // Get VertexConsumerProvider
        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        // Define UV texture coords dynamically
        float uMin = sprite.getMinU(),
                vMin = sprite.getMinV(),
                uMax = sprite.getMaxU(),
                vMax = sprite.getMaxV();

        // Adjust UV scaling based on size
        float texScale = size * 2;
        uMax = uMin + (uMax - uMin) * texScale;
        vMax = vMin + (vMax - vMin) * texScale;

        /*

        Strange... trying to offset the texture to match the base texture, while avoiding spillage into other sprites, but not working

        uMin += positionOffsetX % (size + positionOffsetX);
        vMin += positionOffsetY % (size + positionOffsetY);
        uMax += positionOffsetX % (size + positionOffsetX);
        vMax += positionOffsetY % (size + positionOffsetY);
        */

        // Render textures
        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayer.getCutout());

        // Get light values for 8 unique cube corners
        int light000 = WorldRenderer.getLightmapCoordinates(client.world, position.add(-1, -1, -1)); // Bottom-left-back
        int light100 = WorldRenderer.getLightmapCoordinates(client.world, position.add(1, -1, -1));  // Bottom-right-back
        int light110 = WorldRenderer.getLightmapCoordinates(client.world, position.add(1, 1, -1));   // Top-right-back
        int light010 = WorldRenderer.getLightmapCoordinates(client.world, position.add(-1, 1, -1));  // Top-left-back
        int light001 = WorldRenderer.getLightmapCoordinates(client.world, position.add(-1, -1, 1));  // Bottom-left-front
        int light101 = WorldRenderer.getLightmapCoordinates(client.world, position.add(1, -1, 1));   // Bottom-right-front
        int light111 = WorldRenderer.getLightmapCoordinates(client.world, position.add(1, 1, 1));    // Top-right-front
        int light011 = WorldRenderer.getLightmapCoordinates(client.world, position.add(-1, 1, 1));   // Top-left-front

        /* *********** Draw quads to create cube *********** */
        // South face (Z+)
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax,
                new Quaternionf(),
                new Vector3f(0, 0, 1),
                positionOffsetX, positionOffsetY, positionOffsetZ,
                light001, light101, light111, light011,
                r, g, b, a, 0, 0, 1);

        // North face (Z-)
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateY((float) Math.toRadians(180)),
                new Vector3f(0, 0, -1),
                positionOffsetX, positionOffsetY, positionOffsetZ,
                light000, light100, light110, light010,
                r, g, b, a, 0, 0, -1);

        // East face (X+)
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateY((float) Math.toRadians(90)),
                new Vector3f(1, 0, 0),
                positionOffsetX, positionOffsetY, positionOffsetZ,
                light100, light101, light111, light110,
                r, g, b, a, 1, 0, 0);

        // West face (X-)
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateY((float) Math.toRadians(-90)),
                new Vector3f(-1, 0, 0),
                positionOffsetX, positionOffsetY, positionOffsetZ,
                light000, light001, light011, light010,
                r, g, b, a, -1, 0, 0);

        // Bottom face (Y-)
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateX((float) Math.toRadians(90)),
                new Vector3f(0, -1, 0),
                positionOffsetX, positionOffsetY, positionOffsetZ,
                light000, light100, light101, light001,
                r, g, b, a, 0, -1, 0);

        // Top face (Y+)
        drawQuad(matrixStack, vertexConsumer, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateX((float) Math.toRadians(-90)),
                new Vector3f(0, 1, 0),
                positionOffsetX, positionOffsetY, positionOffsetZ,
                light010, light110, light111, light011,
                r, g, b, a, 0, 1, 0);

        // End
        matrixStack.pop();
    }

    private static void drawQuad(
            MatrixStack matrixStack,
            VertexConsumer vertexConsumer,
            float size, //total size
            float uMin, float vMin, float uMax, float vMax, //texture coords
            Quaternionf rotation, //rotate
            Vector3f translation, //translate
            int positionOffsetX, int positionOffsetY, int positionOffsetZ,
            int l_00, int l_10, int l_11, int l_01, //light
            int r, int g, int b, int a, //color
            int n_x, int n_y, int n_z //normal vector
    ) {
        // Start
        matrixStack.push();

        // Apply translation
        matrixStack.translate(translation.x * size, translation.y * size, translation.z * size);

        // Apply rotation
        matrixStack.multiply(rotation);

        // Get transformed matrix
        Matrix4f transformedMatrix = matrixStack.peek().getPositionMatrix();

        // Draw quad
        vertexConsumer.vertex(transformedMatrix, -size, -size, 0).texture(uMin, vMin).color(r, g, b, a).light(l_00).normal(n_x, n_y, n_z);
        vertexConsumer.vertex(transformedMatrix, size, -size, 0).texture(uMax, vMin).color(r, g, b, a).light(l_10).normal(n_x, n_y, n_z);
        vertexConsumer.vertex(transformedMatrix, size, size, 0).texture(uMax, vMax).color(r, g, b, a).light(l_11).normal(n_x, n_y, n_z);
        vertexConsumer.vertex(transformedMatrix, -size, size, 0).texture(uMin, vMax).color(r, g, b, a).light(l_01).normal(n_x, n_y, n_z);

        // Stop
        matrixStack.pop();
    }
}