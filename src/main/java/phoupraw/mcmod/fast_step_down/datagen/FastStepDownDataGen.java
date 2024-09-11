package phoupraw.mcmod.fast_step_down.datagen;

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public final class FastStepDownDataGen implements DataGeneratorEntrypoint {
    @Override
    public void onInitializeDataGenerator(FabricDataGenerator g) {
        var pack = g.createPack();
        //server
        //pack.addProvider(BlockLootGen::new);
        //pack.addProvider(RecipeGen::new);
        //pack.addProvider(BlockTagGen::new);
        //pack.addProvider(ItemTagGen::new);
        //pack.addProvider(EnchTagGen::new);
        //pack.addProvider(EnchGen::new);
        //client
        pack.addProvider(ChineseGen::new);
        pack.addProvider(EnglishGen::new);
        //pack.addProvider(ModelGen::new);
        
        ////override
        //var fishing = g.createBuiltinResourcePack(TIDs.LEVELED_FISHING_TREASURE);
        ////server
        //fishing.addProvider(FishingLootGen2::new);
    }
}
