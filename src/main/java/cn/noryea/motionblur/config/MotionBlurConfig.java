package cn.noryea.motionblur.config;

import cn.noryea.motionblur.MotionBlurMod;
import com.mojang.datafixers.util.Pair;

public class MotionBlurConfig {

    public static SimpleConfig CONFIG;
    private static MotionBlurConfigProvider provider;

    public static int MOTIONBLUR_AMOUNT;  //是动态模糊量

    public static void registerConfigs(int amount) {
        provider = new MotionBlurConfigProvider();

        provider.addKeyValuePair(new Pair<>("motionblur.amount", amount), "int");
        CONFIG = SimpleConfig.of(MotionBlurMod.ID).provider(provider).request();

        syncConfigs();
    }

    private static void syncConfigs() {
        MOTIONBLUR_AMOUNT = CONFIG.getOrDefault("motionblur.amount", 50);

        //System.out.println(provider.getConfigsList().size() + " have been set.");
    }

    public static void update(int value) {
        CONFIG.delete();
        registerConfigs(value);
    }

}
