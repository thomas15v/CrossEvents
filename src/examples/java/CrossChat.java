import com.google.inject.Inject;
import com.thomas15v.crossevents.api.CrossEventService;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import java.util.concurrent.TimeoutException;

@Plugin(id="CrossChat", name="CrossChat", version="0.1", dependencies="required-after:CrossEvents;")
public class CrossChat
{
    private CrossEventService service;
    private Game game;


    @Inject
    private Logger logger;

    @Subscribe
    public void onEnable(ServerStartedEvent event)
    {
        this.game = event.getGame();
        this.service = this.game.getServiceManager().provide(CrossEventService.class).get();
    }

    public class CrossChatEvent
            extends AbstractEvent
    {
        private Text message;

        public CrossChatEvent(Text message)
        {
            this.message = message;
        }

        public Text getMessage() {
            return message;
        }
    }

    @Subscribe
    public void PlayerChatEvent(CrossChatEvent event)
    {
        this.game.getServer().getBroadcastSink().sendMessage(event.getMessage());
    }

    @Subscribe
    public void PlayerChatEvent(PlayerChatEvent event)
    {
        try {
            this.service.callEvent(new CrossChatEvent(event.getNewMessage()));
        } catch (TimeoutException e) {
            logger.info("Failed to pass event to server!");
        }
    }
}
