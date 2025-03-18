package me.danvb10.mossify;

import me.danvb10.mossify.render.MossRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;

import static me.danvb10.mossify.Mossify.LOGGER;

public class MossifyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LOGGER.info("onInitializeClient");

        // Register the MossRenderer to run after entity rendering
        WorldRenderEvents.BEFORE_DEBUG_RENDER.register(MossRenderer::render);

    }
}