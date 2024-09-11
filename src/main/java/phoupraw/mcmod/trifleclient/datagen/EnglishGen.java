package phoupraw.mcmod.trifleclient.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.trifleclient.TrifleClient;

import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.trifleclient.TrifleClient.ID;

final class EnglishGen extends FabricLanguageProvider {
    EnglishGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "Fast Go Downstairs";
        b.add(TrifleClient.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "No more banging your head when go downstairs");
        b.add("modmenu.descriptionTranslation." + ID, """
          Immediately try down to ground after go out of stairs. 
          """);
    }
}
