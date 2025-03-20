package me.danvb10.mossify;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.text.Text;

public class Commands {

    public static void registerCommands() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(
                    ClientCommandManager.literal("mossify")
                            .executes(context -> { // Default command: show version
                                context.getSource().sendFeedback(Text.literal("Mossify 1.0.0"));
                                return Command.SINGLE_SUCCESS;
                            })
                            .then(ClientCommandManager.literal("toggle") // Subcommand: toggle
                                    .executes(context -> {
                                        MossifyClient.isEnabled = !MossifyClient.isEnabled;
                                        context.getSource().sendFeedback(Text.literal("Mossify toggled: " + (MossifyClient.isEnabled ? "ON" : "OFF")));
                                        return Command.SINGLE_SUCCESS;
                                    }))
                            .then(ClientCommandManager.literal("radius") // Subcommand: radius <number>
                                    .then(ClientCommandManager.argument("value", IntegerArgumentType.integer(1, 200))
                                            .executes(context -> {
                                                int newRadius = IntegerArgumentType.getInteger(context, "value");
                                                MossifyClient.renderDistance = newRadius;
                                                context.getSource().sendFeedback(Text.literal("Mossify render distance set to: " + newRadius));
                                                return Command.SINGLE_SUCCESS;
                                            })))
            );
        });
    }
}