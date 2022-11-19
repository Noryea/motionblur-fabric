package cn.noryea.motionblur;

import cn.noryea.motionblur.config.MotionBlurConfig;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.resource.Resource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class MotionBlurMod implements ClientModInitializer {

    public static Logger LOGGER = LoggerFactory.getLogger("MotionBlurMod");

    private static final MotionBlur motionBlur = new MotionBlur();

    public static HashMap<Identifier, Resource> runtimeResources = new HashMap<>();

    @Override
    public void onInitializeClient() {
        MotionBlurConfig.init("motionblur", MotionBlurConfig.class);
        getCurrentMotionBlur().init();

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

    public static MotionBlur getCurrentMotionBlur() {
        return motionBlur;
    }
}
