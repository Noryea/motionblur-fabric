package cn.noryea.motionblur;

import cn.noryea.motionblur.config.MotionBlurConfig;
import com.google.gson.JsonSyntaxException;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import ladysnake.satin.api.managed.uniform.Uniform1f;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;

public class MotionBlurMod implements ClientModInitializer {

    public static String ID = "motionblur";
    private float currentBlur;

    private final ManagedShaderEffect motionblur = ShaderEffectManager.getInstance().manage(new Identifier(ID, "shaders/post/motion_blur.json"),
            shader -> shader.setUniformValue("BlendFactor", getBlur()));

    @Override
    public void onInitializeClient() {
        MotionBlurConfig.init("motionblur", MotionBlurConfig.class);

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
  		    dispatcher.register(
                ClientCommandManager.literal("motionblur")
                    .then(ClientCommandManager.literal("amount")
                        .then(ClientCommandManager.argument("percent", IntegerArgumentType.integer(1, 100))
                            .executes(context -> changeAmount(context.getSource(), IntegerArgumentType.getInteger(context, "percent")))))
                    .then(ClientCommandManager.literal("toggle")
                            .executes(context -> toggle(context.getSource())))
            );
        });

        ShaderEffectRenderCallback.EVENT.register((deltaTick) -> {
            if (MotionBlurConfig.enable) {
                if(currentBlur!=getBlur()){
                    motionblur.setUniformValue("BlendFactor", getBlur());
                    currentBlur=getBlur();
                }
                motionblur.render(deltaTick);
            }
        });
    }

    private static int changeAmount(FabricClientCommandSource src, int amount) {
        MotionBlurConfig.motionBlurAmount = amount;

        src.sendFeedback(Text.literal("Motion blur Amount: " + amount + "%"));
        return amount;

    }

    private static int toggle(FabricClientCommandSource src) {
        MotionBlurConfig.enable = !MotionBlurConfig.enable;

        if (MotionBlurConfig.enable) {
            src.sendFeedback(Text.literal("Motion blur: On"));
        } else {
            src.sendFeedback(Text.literal("Motion blur: Off"));
        }
        return 1;
    }

    public float getBlur() {
        return Math.min(MotionBlurConfig.motionBlurAmount, 99)/100F;
    }

}
