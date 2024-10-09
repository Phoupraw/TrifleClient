package phoupraw.mcmod.trifleclient.config;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TCConfigs {
    public static final TCConfigs A = new TCConfigs();
    protected TCConfigs() {
    }
    private boolean speedSpeed = true;
    private boolean normalSpeed = true;
    private boolean noUsingItemSlow = true;
    private boolean autoAttacker = true;
    private boolean blockFinder = true;
    private boolean miningDelay = true;
    private boolean autoCrit = true;
}
