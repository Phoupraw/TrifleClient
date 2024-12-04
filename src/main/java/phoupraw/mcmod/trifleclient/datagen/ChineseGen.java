package phoupraw.mcmod.trifleclient.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.registry.RegistryWrapper;
import phoupraw.mcmod.trifleclient.TrifleClient;
import phoupraw.mcmod.trifleclient.config.TCConfigs;
import phoupraw.mcmod.trifleclient.config.TCYACL;
import phoupraw.mcmod.trifleclient.config.YACLDataGen;
import phoupraw.mcmod.trifleclient.constant.TCIDs;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;

import static phoupraw.mcmod.trifleclient.TrifleClient.ID;

final class ChineseGen extends FabricLanguageProvider {
    ChineseGen(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, "zh_cn", registryLookup);
    }
    @Override
    public void generateTranslations(RegistryWrapper.WrapperLookup registryLookup, TranslationBuilder b) {
        String modName = "琐物·客户端";
        b.add(TrifleClient.NAME_KEY, modName);
        b.add("modmenu.summaryTranslation." + ID, "一个随便写的客户端辅助模组");
        b.add("modmenu.descriptionTranslation." + ID, """
          提供一些客户端辅助功能：
          - 用于搜索方块的指令：/trifleclient find block [block]
          - 挖掘延迟控制
          """);
        b.add(TCIDs.MINING_DELAY.toTranslationKey("key"), "挖掘延迟");
        b.add("config.jade.plugin_" + TCIDs.MINING_DELAY.toTranslationKey(), "挖掘速度与延迟");
        b.add(TCIDs.MINING_SAME.toTranslationKey("key"), "连续挖掘方块须相同");
        for (Field field : TCConfigs.toFields(TCConfigs.class)) {
            YACLDataGen annotation = field.getAnnotation(YACLDataGen.class);
            if (annotation != null) {
                if (!annotation.name().isEmpty()) {
                    b.add("yacl3.config." + TCYACL.CONFIG_ID + "." + field.getName(), annotation.name());
                }
                if (!annotation.desc().isEmpty()) {
                    b.add("yacl3.config." + TCYACL.CONFIG_ID + "." + field.getName() + ".desc", annotation.name());
                }
            }
        }
    }
}
