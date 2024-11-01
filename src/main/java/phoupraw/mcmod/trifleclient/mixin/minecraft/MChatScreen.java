package phoupraw.mcmod.trifleclient.mixin.minecraft;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.text.Style;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.mixins.minecraft.MMChatScreen;

@Environment(EnvType.CLIENT)
@Mixin(ChatScreen.class)
abstract class MChatScreen {
    @Shadow
    @Nullable
    protected abstract Style getTextStyleAt(double x, double y);
    @WrapOperation(method = "mouseClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screen/Screen;mouseClicked(DDI)Z"))
    private boolean rightClick(ChatScreen instance, double mouseX, double mouseY, int button, Operation<Boolean> original) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_RIGHT && TCConfigs.A.isRightClickOpenFolder()) {
            if (MMChatScreen.rightClick(getTextStyleAt(mouseX, mouseY))) {
                return true;
            }
        }
        return original.call(instance, mouseX, mouseY, button);
    }
}
