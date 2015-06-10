package com.thomas15v.crossevents.api.events;

import com.thomas15v.crossevents.api.Server;

/**
 * Gets called when a Server goes down.
 *
 * Note that the server object here can't send events anymore.
 *
 */
public class ServerDownEvent extends ServerEvent {
    public ServerDownEvent(Server server) {
        super(server);
    }
}
