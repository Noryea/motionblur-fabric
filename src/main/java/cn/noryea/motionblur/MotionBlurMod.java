package cn.noryea.motionblur;

import cn.noryea.motionblur.config.MotionBlurConfig;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import ladysnake.satin.api.event.ShaderEffectRenderCallback;
import ladysnake.satin.api.managed.ManagedShaderEffect;
import ladysnake.satin.api.managed.ShaderEffectManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
                    .then(ClientCommandManager.argument("percent", IntegerArgumentType.integer(0, 100))
                        .executes(context -> changeAmount(context.getSource(), IntegerArgumentType.getInteger(context, "percent"))))
            );
        });

        ShaderEffectRenderCallback.EVENT.register((deltaTick) -> {
            if (MotionBlurConfig.motionBlurAmount != 0) {
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

        src.sendFeedback(Text.literal("Motion Blur: " + amount + "%"));
        return amount;

    }

    public float getBlur() {
        return Math.min(MotionBlurConfig.motionBlurAmount, 99)/100F;
    }

}
