package com.gtaintro;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;

public final class IntroAnimation {
    /** Duração em ticks (20 ticks = 1 segundo). */
    public static final int DURATION = 120;
    /** Altura inicial da câmera acima do jogador, em blocos. */
    public static final double START_HEIGHT = 180.0;
    /** Quantos graus a câmera gira durante a descida. */
    public static final float SPIN_DEGREES = 270f;

    private static boolean active;
    private static int ticks;
    private static boolean hudWasHidden;

    private IntroAnimation() {}

    public static void start(Minecraft client) {
        active = true;
        ticks = 0;
        hudWasHidden = client.options.hideGui;
        client.options.hideGui = true;
    }

    public static void stop(Minecraft client) {
        if (!active) return;
        active = false;
        client.options.hideGui = hudWasHidden;
    }

    public static boolean isActive() {
        return active;
    }

    public static void tick(Minecraft client) {
        if (!active) return;
        if (client.player == null) {
            stop(client);
            return;
        }
        if (client.isPaused()) return;
        if (++ticks >= DURATION) stop(client);
    }

    /** Progresso bruto de 0 a 1, suavizado com o partialTick. */
    public static float progress(float partialTick) {
        return Mth.clamp((ticks + partialTick) / DURATION, 0f, 1f);
    }

    /** Ease in-out cúbico: começa devagar, acelera, e pousa suave. */
    public static float ease(float t) {
        return t < 0.5f ? 4f * t * t * t : 1f - (float) Math.pow(-2f * t + 2f, 3) / 2f;
    }
}
