package blacklistmod.blacklistmod;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import net.minecraft.network.NetworkManager;
@Plugin(
        id = "blacklistmod",
        name = "BlacklistMod",
        authors = {
                "MahmutKocas"
        }
)
public class BlacklistMod {

    @Inject
    private Logger logger;

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }
}
