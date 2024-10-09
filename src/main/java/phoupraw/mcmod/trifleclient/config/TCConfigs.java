package phoupraw.mcmod.trifleclient.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TCConfigs {
    public static final TCConfigs A = new TCConfigs();
    protected TCConfigs() {
    }
    private boolean speedSpeed;
    private boolean normalSpeed;
    private boolean noUsingItemSlow;
    private boolean autoAttacker;
    private boolean blockFinder;
    private boolean miningDelay;
}
