package cn.noryea.motionblur.config;

import eu.midnightdust.lib.config.MidnightConfig;

public class MotionBlurConfig extends MidnightConfig {

    @Entry(min = 0, max = 100)
    public static int motionBlurAmount = 50;

}
