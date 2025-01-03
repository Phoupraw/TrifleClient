package phoupraw.mcmod.trifleclient.jade;

import net.minecraft.block.Block;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class TrifleClientWailaPlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(new MiningSpeedComponentProvider(), Block.class);
    }
}
