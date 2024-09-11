package phoupraw.mcmod.trifleclient.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.trifleclient.TrifleClient;

import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.trifleclient.TrifleClient.ID;

final class ChineseGen extends FabricLanguageProvider {
    ChineseGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "快速下楼梯";
        b.add(TrifleClient.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "下楼梯不再磕头");
        b.add("modmenu.descriptionTranslation." + ID, """
          走下楼梯时尝试立刻落到地面。 
          当玩家悬空时，如果到下方方块的距离小于等于玩家的可上台阶高度，则瞬间下落到方块上。
          """);
    }
}
