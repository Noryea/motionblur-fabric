package cn.noryea.motionblur.mixin;

import cn.noryea.motionblur.MotionBlurMod;
import net.minecraft.resource.ReloadableResourceManagerImpl;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ReloadableResourceManagerImpl.class)
public class MixinReloadableResourceManagerImpl {

    @Inject(method = "getResource", at = @At("HEAD"), cancellable = true)
    public void getResource(Identifier identifier, CallbackInfoReturnable<Optional<Resource>> cir){
        if(MotionBlurMod.runtimeResources.get(identifier) != null){
            cir.setReturnValue(Optional.of(MotionBlurMod.runtimeResources.get(identifier)));
        }
    }
}
