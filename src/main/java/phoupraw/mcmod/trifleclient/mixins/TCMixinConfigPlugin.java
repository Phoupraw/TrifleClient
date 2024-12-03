package phoupraw.mcmod.trifleclient.mixins;

import fun.rtos.modchecker.ModChecker;
import net.fabricmc.loader.api.FabricLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import phoupraw.mcmod.trifleclient.TrifleClient;

import java.util.List;
import java.util.Set;

public final class TCMixinConfigPlugin implements IMixinConfigPlugin {
    @ApiStatus.Internal
    public static final Logger LOGGER = LogManager.getLogger();
    @Override
    public void onLoad(String mixinPackage) {
    
    }
    @Override
    public @Nullable String getRefMapperConfig() {
        return null;
    }
    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.startsWith("phoupraw.mcmod." + TrifleClient.ID + ".mixin." + ModChecker.MOD_ID)) {
            return FabricLoader.getInstance().isModLoaded(ModChecker.MOD_ID);
        }
        if (mixinClassName.startsWith("phoupraw.mcmod." + TrifleClient.ID + ".mixin.minecraft.elytra")) {
            return !FabricLoader.getInstance().isModLoaded("neoforge");
        }
        return true;
    }
    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    
    }
    @Override
    public @Nullable List<String> getMixins() {
        return null;
    }
    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    
    }
    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    
    }
}
