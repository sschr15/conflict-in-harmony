package sschr15.cirback;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sschr15.cirback.interfaces.IKeyBinding;

@Mod(
        modid = Ref.MOD_ID,
        name = Ref.NAME,
        version = Ref.VERSION
)
public class ConflictInHarmonyBackport {
    public static final Logger LOGGER = LogManager.getLogger(Ref.MOD_ID);

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        // This basically crashes the game if the mixin didn't apply :)
        KeyBinding keyBinding = Minecraft.getMinecraft().gameSettings.keyBindAttack;
        if (!(keyBinding instanceof IKeyBinding)) throw new RuntimeException("Mixin class failed to correctly apply!");
        LOGGER.info("Conflict In Harmony loaded!");
    }
}
