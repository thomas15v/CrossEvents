package com.thomas15v.crossevents.api;

import org.spongepowered.api.event.Event;

/**
 * Created by thomas15v on 4/06/15.
 */
public interface CrossEventService {

    /**
     *
     * @param event The event you want to pass to other servers.
     * @return In case the event implements a {@link com.thomas15v.crossevents.api.Returnable} or {@link org.spongepowered.api.event.Cancellable}.
     * It will return the changed event. Otherwise it will return the same event you already passed.
     */
    <T extends Event> T callEvent(T event);
}
