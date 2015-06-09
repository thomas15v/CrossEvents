package com.thomas15v.crossevents.network.client;

import com.thomas15v.crossevents.api.Server;
import org.spongepowered.api.event.Event;

import java.util.UUID;

/**
 * Created by thomas15v on 6/06/15.
 */
public class CrossServer implements Server {

    private NodeClient nodeClient;
    private UUID uuid;
    private String serverName;

    public CrossServer(NodeClient nodeClient, UUID uuid, String serverName){
        this.nodeClient = nodeClient;
        this.uuid = uuid;
        this.serverName = serverName;
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        nodeClient.callEvent(event);
        return event;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public String getName() {
        return serverName;
    }
}