package cn.noryea.motionblur.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class MotionBlurConfig extends MidnightConfig {

    @Entry public static boolean enable = true;

    @Entry(min = 1, max = 100)
    public static int motionBlurAmount = 50;

}
