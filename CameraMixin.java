package com.gtaintro.mixin;

import com.gtaintro.IntroAnimation;
import net.minecraft.client.Camera;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Camera.class)
public abstract class CameraMixin {

    @Shadow protected abstract void setPosition(double x, double y, double z);
    @Shadow protected abstract void setRotation(float yRot, float xRot);

    // ATENÇÃO: se o build falhar aqui, a assinatura de Camera#setup mudou nesta
    // versão. Abra Camera.java no IDE e ajuste os parâmetros do método abaixo.
    @Inject(method = "setup", at = @At("TAIL"))
    private void gtaintro$setup(BlockGetter level, Entity entity, boolean detached,
                                boolean mirror, float partialTick, CallbackInfo ci) {
        if (!IntroAnimation.isActive() || entity == null) return;

        float t = IntroAnimation.ease(IntroAnimation.progress(partialTick));
        float inv = 1f - t;

        // Posição: parte de bem alto e desce até os olhos do jogador
        Vec3 eye = entity.getEyePosition(partialTick);
        this.setPosition(eye.x, eye.y + IntroAnimation.START_HEIGHT * inv, eye.z);

        // Rotação: olha pra baixo (90°) e vai endireitando enquanto gira
        float yRot = entity.getViewYRot(partialTick) + IntroAnimation.SPIN_DEGREES * inv;
        float xRot = Mth.lerp(t, 90f, entity.getViewXRot(partialTick));
        this.setRotation(yRot, xRot);
    }

    /** Mostra o modelo do jogador durante a descida (terceira pessoa forçada). */
    @Inject(method = "isDetached", at = @At("HEAD"), cancellable = true)
    private void gtaintro$detached(CallbackInfoReturnable<Boolean> cir) {
        if (IntroAnimation.isActive() && IntroAnimation.progress(0f) < 0.85f) {
            cir.setReturnValue(true);
        }
    }
}
