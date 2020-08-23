package sschr15.cihback.mixin;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.Mixins;

import java.util.Map;

/**
 * Mixin requires an initialization thing, so this Coremod
 * launches Mixin and notifies it of where my mixins are.
 */
public class MixinCoremod implements IFMLLoadingPlugin {
    public MixinCoremod() {
        MixinBootstrap.init();
        Mixins.addConfiguration("cihback.mixins.json");
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return null;
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {

    }

    public String getAccessTransformerClass() {
        return null;
    }
}
