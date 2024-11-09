package phoupraw.mcmod.trifleclient.util;

import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.ToolMaterial;

import java.util.Map;

public final class PublicAxeItem extends AxeItem {
    public static final Map<Block, Block> STRIPPED_BLOCKS = AxeItem.STRIPPED_BLOCKS;
    private PublicAxeItem(ToolMaterial toolMaterial, Settings settings) {
        super(toolMaterial, settings);
    }
}
