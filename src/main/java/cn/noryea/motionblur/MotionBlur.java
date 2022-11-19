package cn.noryea.motionblur;

import cn.noryea.motionblur.config.MotionBlurConfig;
import cn.noryea.motionblur.mixin.AccessorShaderEffect;
import com.google.gson.JsonSyntaxException;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.GlUniform;
import net.minecraft.client.gl.ShaderEffect;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.apache.commons.io.IOUtils;

import java.io.IOException;

public class MotionBlur {
    public ShaderEffect shader;
    private final MinecraftClient client = MinecraftClient.getInstance();
    private float currentBlur;
    private int lastWidth;
    private int lastHeight;

    private final Identifier shaderLocation = new Identifier("minecraft:shaders/post/motion_blur.json");

    private static float getBlur() {
        return MotionBlurConfig.motionBlurAmount/100F;
    }

    public void init() {
        MotionBlurMod.runtimeResources.put(shaderLocation, new MotionBlurShader());
    }

    public void onUpdate() {
        if( (shader == null ||
                MinecraftClient.getInstance().getWindow().getWidth()!=lastWidth ||
                MinecraftClient.getInstance().getWindow().getHeight()!=lastHeight ) &&
                MinecraftClient.getInstance().getWindow().getWidth() > 0 &&
                MinecraftClient.getInstance().getWindow().getHeight() > 0) {
            currentBlur=getBlur();
            try {
                shader = new ShaderEffect(client.getTextureManager(),
                        client.getResourceManager(), client.getFramebuffer(),
                        shaderLocation);
                shader.setupDimensions(MinecraftClient.getInstance().getWindow().getWidth(), MinecraftClient.getInstance().getWindow().getHeight());
            } catch (JsonSyntaxException | IOException e) {
                MotionBlurMod.LOGGER.error("Could not load motion blur", e);
            }
        }
        if(currentBlur!=getBlur()){
            ((AccessorShaderEffect)shader).getPasses().forEach(shader -> {
                GlUniform blendFactor = shader.getProgram().getUniformByName("BlendFactor");
                if(blendFactor!=null){
                    blendFactor.set(getBlur());
                }
            });
            currentBlur=getBlur();
        }

        lastWidth = MinecraftClient.getInstance().getWindow().getWidth();
        lastHeight = MinecraftClient.getInstance().getWindow().getHeight();
    }

    public class MotionBlurShader extends Resource {

        public MotionBlurShader() {
            super("", ()-> IOUtils.toInputStream(String.format("{" +
                    "    \"targets\": [" +
                    "        \"swap\"," +
                    "        \"previous\"" +
                    "    ]," +
                    "    \"passes\": [" +
                    "        {" +
                    "            \"name\": \"motion_blur\"," +
                    "            \"intarget\": \"minecraft:main\"," +
                    "            \"outtarget\": \"swap\"," +
                    "            \"auxtargets\": [" +
                    "                {" +
                    "                    \"name\": \"PrevSampler\"," +
                    "                    \"id\": \"previous\"" +
                    "                }" +
                    "            ]," +
                    "            \"uniforms\": [" +
                    "                {" +
                    "                    \"name\": \"BlendFactor\"," +
                    "                    \"values\": [ %s ]" +
                    "                }" +
                    "            ]" +
                    "        }," +
                    "        {" +
                    "            \"name\": \"blit\"," +
                    "            \"intarget\": \"swap\"," +
                    "            \"outtarget\": \"previous\"" +
                    "        }," +
                    "        {" +
                    "            \"name\": \"blit\"," +
                    "            \"intarget\": \"swap\"," +
                    "            \"outtarget\": \"minecraft:main\"" +
                    "        }" +
                    "    ]" +
                    "}", getBlur()) , "utf-8"));
        }
    }
}
