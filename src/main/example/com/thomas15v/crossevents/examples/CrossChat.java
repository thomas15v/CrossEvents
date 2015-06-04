package com.thomas15v.crossevents.examples;

import com.thomas15v.crossevents.api.CrossEventService;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;
import org.spongepowered.api.text.Texts;

/**
 * Created by thomas15v on 4/06/15.
 */
public class CrossChat {

    private final CrossEventService service;
    private Game game;

    public CrossChat(Game game, Object plugin){
        this.game = game;
        game.getEventManager().register(plugin, this);
        this.service = game.getServiceManager().provide(CrossEventService.class).get();
    }


    public class CrossChatEvent extends AbstractEvent {
        private String message;

        public CrossChatEvent(String message){
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    @Subscribe
    public void PlayerChatEvent(CrossChatEvent event){
        game.getServer().getBroadcastSink().sendMessage(Texts.parseJson(event.getMessage()));
    }

    @Subscribe
    public void PlayerChatEvent(PlayerChatEvent event){
        service.callEvent(new CrossChatEvent(Texts.toJson(event.getNewMessage())));
    }
}
