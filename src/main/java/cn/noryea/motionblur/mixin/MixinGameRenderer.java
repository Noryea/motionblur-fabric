package cn.noryea.motionblur.mixin;

import cn.noryea.motionblur.MotionBlur;
import cn.noryea.motionblur.MotionBlurMod;
import cn.noryea.motionblur.config.MotionBlurConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public abstract class MixinGameRenderer {

    @Final @Shadow private MinecraftClient client;

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gl/Framebuffer;beginWrite(Z)V"))
    public void worldMotionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci) {
        motionBlur(tickDelta, startTime, tick, null);
    }

    @Inject(method = "render", at = @At("TAIL"))
    public void motionBlur(float tickDelta, long startTime, boolean tick, CallbackInfo ci){
        if(ci != null) {
            return;
        }

        this.client.getProfiler().push("Motion Blur");

        if(MotionBlurConfig.enable) {
            MotionBlur blur = MotionBlurMod.getCurrentMotionBlur();
            blur.onUpdate();
            blur.shader.render(tickDelta);
        }

        this.client.getProfiler().pop();
    }

}
