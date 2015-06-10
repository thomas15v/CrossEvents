package com.thomas15v.crossevents.api.events;


import com.thomas15v.crossevents.api.Server;

/**
 *
 * Gets called when a server connections the server and is ready for handling packets.
 *
 */
public class ServerUpEvent extends ServerEvent {
    public ServerUpEvent(Server server) {
        super(server);
    }
}
