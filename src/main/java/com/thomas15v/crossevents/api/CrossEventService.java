package com.thomas15v.crossevents.api;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.formatting.Formatter;
import org.spongepowered.api.event.Event;

import java.lang.reflect.Type;
import java.util.UUID;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by thomas15v on 4/06/15.
 */
public interface CrossEventService {

    /**
     *
     * @param event The event you want to pass to other servers.
     * @return In case the event implements a {@link com.thomas15v.crossevents.api.Returnable} or {@link org.spongepowered.api.event.Cancellable}. It will return the changed event. Otherwise it will return the same event you already passed.
     * @throws TimeoutException When the the plugin isn't connected to a server or when a server disconnects during the event call.
     */
    <T extends Event> T callEvent(T event) throws TimeoutException;

    /**
     *
     * @param event The event you want to pass.
     * @param target The uniqueId of the target server you want to send the event to.
     * @return In case the event implements a {@link com.thomas15v.crossevents.api.Returnable} or {@link org.spongepowered.api.event.Cancellable}. It will return the changed event. Otherwise it will return the same event you already passed.
     * @throws TimeoutException When the the plugin isn't connected to a server or when a server disconnects during the event call.
     */
    <T extends Event> T callEvent(T event, UUID target) throws TimeoutException;

    /**
     *
     * @param event The event you want to pass.
     * @param target The uniqueId of the target server you want to send the event to.
     * @return
     */
    <T extends Event> Future<T> callEventAsync(T event, UUID target);

    /**
     *
     * @param event The event you want to pass.
     * @return
     */
    <T extends Event> Future<T> callEventAsync(T event);

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

    /***
     *
     * Use this to add extra serialization. See Gson documentation.
     *
     * @param formatter
     * @param type The type for the formatter
     */
    void registerFormatter(Type type, Formatter formatter);


}
