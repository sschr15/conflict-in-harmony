package sschr15.cihback.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.IntHashMap;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sschr15.cihback.interfaces.IKeyBinding;

import java.util.Collection;
import java.util.List;

/**
 * Mixin to change the keybinding storage method, allowing multiple
 * actions to be assigned to the same key
 * @author sschr15
 */
@Mixin(value = KeyBinding.class, remap = false)
public abstract class KeyBindingMixin implements IKeyBinding {
    // These values are obfuscated here because I can't figure out how to tell Mixin what mappings are being used
    @Shadow private int field_151474_i;                     // pressTime
    @Shadow private boolean field_74513_e;                  // pressed
    @Shadow private static @Final List field_74516_a;       // keybindArray
    @Shadow private static @Final IntHashMap field_74514_b; // hash
    /**
     * Multimap supporting multiple keybindings. Casting to `IKeyBinding` to be able to
     * apply `this` to the thing
     */
    private static final Multimap<Integer, IKeyBinding> keybindingMap = HashMultimap.create();

    // My method names are the deobf versions of the actual methods

    // Soft @Overwrite: return after my code is run
    @Inject(method = "func_74507_a", at = @At("HEAD"), cancellable = true)
    private static void onTick(int keyCode, CallbackInfo ci) {
        // Telling it to return, so if I return early this still fires
        ci.cancel();
        if (keyCode != 0) {
            Collection<IKeyBinding> keyBindings = keybindingMap.get(keyCode);
            if (keyBindings == null) return;
            for (IKeyBinding keyBinding : keyBindings) {
                keyBinding.increasePressTime();
            }
        }
    }

    // Same thing as above
    @Inject(method = "func_74510_a", at = @At("HEAD"), cancellable = true)
    private static void setKeyBindState(int keyCode, boolean pressed, CallbackInfo ci) {
        ci.cancel();
        if (keyCode != 0) {
            Collection<IKeyBinding> keyBindings = keybindingMap.get(keyCode);
            if (keyBindings == null) return;
            for (IKeyBinding keyBinding : keyBindings) {
                keyBinding.press(pressed);
            }
        }
    }

    // Get all the keybinds and re-add them to the (now cleared) map
    @Inject(method = "func_74508_b", at = @At("HEAD"), cancellable = true)
    private static void resetKeyBindingArrayAndHash(CallbackInfo ci) {
        keybindingMap.clear();
        for (Object o : field_74516_a) {
            KeyBinding keyBinding = (KeyBinding) o;
            keybindingMap.put(keyBinding.getKeyCode(), (IKeyBinding) keyBinding);
        }
        ci.cancel();
    }

    // constructor: add the key to my map and clear the existing map
    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    private void onInit(String desc, int keyCode, String category, CallbackInfo ci) {
        keybindingMap.put(keyCode, this);
        field_74514_b.clearMap();
    }

    // two methods to make IDEs happy
    public void increasePressTime() {
        this.field_151474_i += field_151474_i;
    }

    public void press(boolean state) {
        this.field_74513_e = state;
    }
}
