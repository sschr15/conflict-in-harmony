package sschr15.cihback.mixin;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyBindingMap;
import net.minecraftforge.client.settings.KeyModifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sschr15.cihback.interfaces.IKeyBinding;

import java.util.Map;

/**
 * Mixin to change the keybinding storage method, allowing multiple
 * actions to be assigned to the same key
 * @author sschr15
 */
@Mixin(value = KeyBinding.class)
public abstract class KeyBindingMixin implements IKeyBinding {
    @Shadow @Final private static Map<String, KeyBinding> KEYBIND_ARRAY;
    @Shadow @Final private static KeyBindingMap HASH;
    @Shadow private int keyCode;
    @Shadow(remap = false) private KeyModifier keyModifier;
    @Shadow private int pressTime;
    @Shadow private boolean pressed;
    /**
     * Multimap supporting multiple keybindings. Casting to `IKeyBinding` to be able to
     * apply `this` to the thing
     */
    private static final Multimap<Integer, IKeyBinding> keybindingMap = HashMultimap.create();

    @Inject(method = "onTick", at = @At("HEAD"), cancellable = true)
    private static void onTick(int keyCode, CallbackInfo ci) {
        ci.cancel();
        if (keyCode != 0) {
            keybindingMap.get(keyCode).forEach(key -> key.increasePressTime());
        }
    }

    @Inject(method = "setKeyBindState", at = @At("HEAD"), cancellable = true)
    private static void onSetState(int keyCode, boolean pressed, CallbackInfo ci) {
        ci.cancel();
        if (keyCode != 0) {
            keybindingMap.get(keyCode).forEach(key -> key.press(pressed));
        }
    }

    @Inject(method = "resetKeyBindingArrayAndHash", at = @At("HEAD"), cancellable = true)
    private static void onReset(CallbackInfo ci) {
        ci.cancel();
        keybindingMap.clear();
        KEYBIND_ARRAY.forEach((s, keyBinding) -> {
            keybindingMap.put(keyBinding.getKeyCode(), (IKeyBinding) keyBinding);
        });
    }

    @Inject(method = "<init>(Ljava/lang/String;ILjava/lang/String;)V", at = @At("RETURN"))
    private void constructor(String description, int keyCode, String category, CallbackInfo ci) {
        HASH.clearMap();
        keybindingMap.put(keyCode, this);
    }

    // Forge-specific code is below

    @Inject(method = "<init>(Ljava/lang/String;Lnet/minecraftforge/client/settings/IKeyConflictContext;Lnet/minecraftforge/client/settings/KeyModifier;ILjava/lang/String;)V", at = @At("RETURN"), remap = false)
    private void constructor(String description, IKeyConflictContext ctx, KeyModifier modifier, int keyCode, String category, CallbackInfo ci) {
        this.constructor(description, keyCode, category, ci);
    }

    @Inject(method = "setKeyModifierAndCode", at = @At("HEAD"), cancellable = true, remap = false)
    private void onSetModifier(KeyModifier modifier, int keyCode, CallbackInfo ci) {
        ci.cancel();
        if (modifier.matches(keyCode)) modifier = KeyModifier.NONE;
        keybindingMap.remove(this.keyCode, this);
        this.keyCode = keyCode;
        this.keyModifier = modifier;
        keybindingMap.put(this.keyCode, this);
    }

    // Interface methods to access private fields

    @Override
    public void increasePressTime() {
        this.pressTime++;
    }

    @Override
    public void press(boolean state) {
        this.pressed = state;
    }
}
