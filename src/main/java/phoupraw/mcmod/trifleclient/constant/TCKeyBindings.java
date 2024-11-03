package phoupraw.mcmod.trifleclient.constant;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.jetbrains.annotations.ApiStatus;
import org.lwjgl.glfw.GLFW;
import phoupraw.mcmod.trifleclient.TrifleClient;

@ApiStatus.NonExtendable
public interface TCKeyBindings {
    KeyBinding MINING_DELAY = KeyBindingHelper.registerKeyBinding(new KeyBinding(TCIDs.MINING_DELAY.toTranslationKey("key"), InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, TrifleClient.NAME_KEY));
    KeyBinding SPEED = KeyBindingHelper.registerKeyBinding(new KeyBinding("attribute.name.generic.movement_speed", GLFW.GLFW_KEY_G, TrifleClient.NAME_KEY));
    KeyBinding MINING_SAME = KeyBindingHelper.registerKeyBinding(new KeyBinding(TCIDs.MINING_SAME.toTranslationKey("key"), GLFW.GLFW_KEY_GRAVE_ACCENT, TrifleClient.NAME_KEY));
}
