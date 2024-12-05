package phoupraw.mcmod.trifleclient.jade;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import phoupraw.mcmod.trifleclient.constant.TCIDs;
import phoupraw.mcmod.trifleclient.misc.MiningDelay;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

import java.text.DecimalFormat;

//FIXME 在neoforge上不显示
public class MiningSpeedComponentProvider implements IBlockComponentProvider {
    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        if (!(config.get(TCIDs.MINING_DELAY)/* && ClientConfigs.getOrCreate(TConfigs.CLIENT).get(TConfigs.MINING_DELAY))*/)) return;
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        float progress = accessor.getBlockState().calcBlockBreakingDelta(player, MinecraftClient.getInstance().world, accessor.getPosition());
        float ticks = 1 / progress;
        int len = MinecraftClient.getInstance().textRenderer.fontHeight;
        tooltip.add(new SpriteElement(MinecraftClient.getInstance().getStatusEffectSpriteManager().getSprite(StatusEffects.HASTE), len, len));
        MutableText text = Text.literal(DecimalFormat.getInstance().format(ticks));
        if (MiningDelay.isOn()) {
            text.formatted(Formatting.UNDERLINE);
        } else if (!(ticks <= 5 || player.isCreative())) {
            text.formatted(Formatting.YELLOW);
        } else {
            text.formatted(Formatting.RED).append(Text.literal("000").formatted(Formatting.OBFUSCATED));
        }
        tooltip.append(text);
    }
    @Override
    public Identifier getUid() {
        return TCIDs.MINING_DELAY;
    }
}
