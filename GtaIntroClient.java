package com.gtaintro;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

public class GtaIntroClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Ao entrar no mundo, dispara a animação
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
                client.execute(() -> IntroAnimation.start(client)));

        // Ao sair, garante que a HUD volta ao normal
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) ->
                client.execute(() -> IntroAnimation.stop(client)));

        ClientTickEvents.END_CLIENT_TICK.register(IntroAnimation::tick);
    }
}
