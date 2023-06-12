package cn.noryea.motionblur.config;

import com.mojang.datafixers.util.Pair;
import java.util.ArrayList;
import java.util.List;
public class MotionBlurConfigProvider implements SimpleConfig.DefaultConfig {
    private String configContents = "";
    private final List<Pair> configsList = new ArrayList<>();

    public List<Pair> getConfigsList() {
        return configsList;
    }

    public void
    addKeyValuePair(Pair<String, ?> keyValuePair, String comment) {
        configsList.add(keyValuePair);
        configContents += keyValuePair.getFirst() + "=" + keyValuePair.getSecond();
    }

    @Override public String get(String namespace) {
        return configContents;
    }
}
