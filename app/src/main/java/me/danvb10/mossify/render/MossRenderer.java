package me.danvb10.mossify.render;

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import org.joml.Quaternionf;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class MossRenderer {

    public static void render(WorldRenderContext context) {
        MinecraftClient client = MinecraftClient.getInstance();

        if (client.player == null) return;
        if (client.world == null) return;

        BlockPos cameraPosition = client.player.getBlockPos();

        float size = 0.25f;


        //drawCube(context, new BlockPos(0, 100, 0), 0.15f);
        //drawCube(context, new BlockPos(5, 100, 0), 0.25f);
        drawCube(context, new BlockPos(10, 100, 0), 0.50f);

        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    //BlockPos position = cameraPosition.add(x, y+2, z-4);
                    //BlockPos position = new BlockPos(0, 100, 0);
                    //BlockState state = client.world.getBlockState(position);
                    //drawCube(context, position, size);
                }
            }
        }
    }

    public static void drawCube(WorldRenderContext context, BlockPos position, float size) {

        // Start
        MatrixStack matrixStack = context.matrixStack();
        if (matrixStack == null) return;
        matrixStack.push();

        // Move to world space
        matrixStack.translate(
                position.getX() - context.camera().getPos().x,
                position.getY() - context.camera().getPos().y,
                position.getZ() - context.camera().getPos().z
        );

        // Get matrix
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();

        // Get VertexConsumerProvider
        VertexConsumerProvider consumers = context.consumers();
        if (consumers == null) return;

        // Render textures
        VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayer.getSolid());
        //Identifier stoneTexture = Identifier.tryParse("textures/blocks/stone_stone.png");
        //VertexConsumer vertexConsumer = consumers.getBuffer(RenderLayer.getEntitySolid(stoneTexture));

        // Define UV texture coordinates for stone -- TODO: Remove hardcode and add dynamic detection
        float uMin = 0f, vMin = 0f, uMax = 1f, vMax = 1f;

        /* *********** Draw quads to create cube *********** */

        // South-facing
        drawQuad(matrixStack, vertexConsumer, matrix, size, uMin, vMin, uMax, vMax,
                new Quaternionf(), new Vector3f(0, 0, 1));

        // North-facing
        drawQuad(matrixStack, vertexConsumer, matrix, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateY((float) Math.toRadians(180)), new Vector3f(0, 0, -1));

        // East-facing
        drawQuad(matrixStack, vertexConsumer, matrix, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateY((float) Math.toRadians(90)), new Vector3f(1, 0, 0));

        // West-facing
        drawQuad(matrixStack, vertexConsumer, matrix, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateY((float) Math.toRadians(-90)), new Vector3f(-1, 0, 0));

        // Down-facing
        drawQuad(matrixStack, vertexConsumer, matrix, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateX((float) Math.toRadians(90)), new Vector3f(0, -1, 0));

        // Up-facing
        drawQuad(matrixStack, vertexConsumer, matrix, size, uMin, vMin, uMax, vMax,
                new Quaternionf().rotateX((float) Math.toRadians(-90)), new Vector3f(0, 1, 0));

        // End
        matrixStack.pop();
    }

    private static void drawQuad(MatrixStack matrixStack, VertexConsumer vertexConsumer, Matrix4f matrix, float size,
                                 float uMin, float vMin, float uMax, float vMax, Quaternionf rotation, Vector3f translation) {
        // Start
        matrixStack.push();

        // Apply translation
        matrixStack.translate(translation.x * size, translation.y * size, translation.z * size);

        // Apply rotation
        matrixStack.multiply(rotation);

        // Get transformed matrix
        Matrix4f transformedMatrix = matrixStack.peek().getPositionMatrix();

        // Draw quad
        vertexConsumer.vertex(transformedMatrix, -size, -size, 0).texture(uMin, vMin).color(255, 255, 255, 255).light(0xF000F0).normal(0, 0, 1);
        vertexConsumer.vertex(transformedMatrix, size, -size, 0).texture(uMax, vMin).color(255, 255, 255, 255).light(0xF000F0).normal(0, 0, 1);
        vertexConsumer.vertex(transformedMatrix, size, size, 0).texture(uMax, vMax).color(255, 255, 255, 255).light(0xF000F0).normal(0, 0, 1);
        vertexConsumer.vertex(transformedMatrix, -size, size, 0).texture(uMin, vMax).color(255, 255, 255, 255).light(0xF000F0).normal(0, 0, 1);

        // Stop
        matrixStack.pop();
    }
}