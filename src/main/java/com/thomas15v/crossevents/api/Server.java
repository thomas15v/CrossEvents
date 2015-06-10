package com.thomas15v.crossevents.api;

import org.spongepowered.api.event.Event;

import java.util.UUID;
import java.util.concurrent.TimeoutException;

/**
 * Created by thomas15v on 6/06/15.
 */
public interface Server {

    /**
     *
     * @param event The event you want to pass to this server.
     * @return In case the event implements a {@link com.thomas15v.crossevents.api.Returnable} or {@link org.spongepowered.api.event.Cancellable}.
     * It will return the changed event. Otherwise it will return the same event you already passed.
     * @throws TimeoutException When the server can't be reached.
     */

    <T extends Event> T callEvent(T event) throws TimeoutException;

    /**
     *
     * @return The Unique ID of the server
     */
    public UUID getUUID();

    /**
     *
     * @return A nicer name for the server. This name isn't unique but can be used to display servers easier.
     */
    public String getName();

}
