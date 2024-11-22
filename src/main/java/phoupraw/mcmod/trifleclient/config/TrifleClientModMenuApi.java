package phoupraw.mcmod.trifleclient.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;

public final class TrifleClientModMenuApi implements ModMenuApi {
    private static Screen create(Screen parent) {
        return !FabricLoader.getInstance().isModLoaded(TCYACL.MOD_ID) ? null : TCYACL.createScreen(parent);
    }
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return TrifleClientModMenuApi::create;
    }
}
