package me.danvb10.mossify;

import net.fabricmc.api.ClientModInitializer;

import static me.danvb10.mossify.Mossify.LOGGER;

public class MossifyClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        LOGGER.info("onInitializeClient");
    }
}