package phoupraw.mcmod.trifleclient.constant;

import net.minecraft.util.Identifier;
import org.jetbrains.annotations.ApiStatus;
import phoupraw.mcmod.trifleclient.TrifleClient;

@ApiStatus.NonExtendable
public interface TCIDs {
    Identifier MINING_DELAY = of("mining_delay");
    Identifier BEFORE = of("before");
    Identifier MINING_SAME = of("mining_same");
    static Identifier of(String path) {
        return Identifier.of(TrifleClient.ID, path);
    }
}
