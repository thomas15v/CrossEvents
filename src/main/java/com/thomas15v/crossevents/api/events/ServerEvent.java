package com.thomas15v.crossevents.api.events;

import com.thomas15v.crossevents.api.Server;
import org.spongepowered.api.event.AbstractEvent;

/**
 * Created by thomas15v on 10/06/15.
 */
public class ServerEvent extends AbstractEvent {

    private Server server;

    public ServerEvent(Server server){
        this.server = server;
    }

    /**
     *
     * @return The server Object
     */
    public Server getServer() {
        return server;
    }
}
