package io.github.betterclient.chatstack;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChatStackVersionMixinPlugin implements IMixinConfigPlugin {
    @Override
    public void onLoad(String mixinPackage) {}

    @Override
    public String getRefMapperConfig() { return null; }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String version = mixinClassName.substring(
                mixinClassName.indexOf("v") + 1,
                mixinClassName.lastIndexOf(".")
        );
        String vv = version;
        if (version.length() == 3) {
            vv = version.charAt(0) + "" + version.charAt(1) + "." + version.charAt(2);
        }
        String versionWithDots = "1." + vv;
        String minecraftVer = FabricLoader.getInstance().getModContainer("minecraft").orElseThrow(NullPointerException::new).getMetadata().getVersion().getFriendlyString();
        //System.out.println("Expected " + minecraftVer + " but found: " + versionWithDots);
        //"Expected 1.21.3 but found: 1.21.2"

        if ("1.16.3-combat.8.c".equals(minecraftVer) && version.equals("cts")) {
            //i aint typing allat
            return true;
        }

        return versionWithDots.equals(minecraftVer);
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) { }

    @Override
    public List<String> getMixins() { return new ArrayList<>(); }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) { }
}
