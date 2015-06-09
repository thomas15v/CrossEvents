package com.thomas15v.crossevents.api;

import com.google.common.base.Optional;
import org.spongepowered.api.event.Event;

import java.util.UUID;

/**
 * Created by thomas15v on 4/06/15.
 */
public interface CrossEventService {

    /**
     *
     * @param event The event you want to pass to other servers.
     * @return In case the event implements a {@link com.thomas15v.crossevents.api.Returnable} or {@link org.spongepowered.api.event.Cancellable}. It will return the changed event. Otherwise it will return the same event you already passed.
     */
    <T extends Event> T callEvent(T event);

    /**
     *
     * @param event The event you want to pass.
     * @param target The uniqueId of the target server you want to send the event to.
     * @return
     */
    <T extends Event> T callEvent(T event, UUID target);


    /***
     *
     * @param uuid Unique ID of the server
     * @return A server instance
     */
    Optional<Server> getServer(UUID uuid);


    /***
     *
     * @return The name of the server
     */
    String getServerName();


}
