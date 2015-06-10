package com.thomas15v.crossevents.network.client;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.api.CrossEventService;
import com.thomas15v.crossevents.api.Returnable;
import com.thomas15v.crossevents.api.Server;
import com.thomas15v.crossevents.formatting.Formatter;
import com.thomas15v.crossevents.network.packet.packets.EventPacket;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.service.event.EventManager;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Created by thomas15v on 10/06/15.
 */
public class EventService implements CrossEventService {

    private NodeClient nodeClient;
    private Game game;
    private volatile Map<UUID, BlockingQueue<Event>> callbackEvents = new HashMap<UUID, BlockingQueue<Event>>();
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();
    private EventManager eventManager;
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    public EventService(NodeClient nodeClient, Game game){
        this.nodeClient = nodeClient;
        this.game = game;
        this.eventManager = game.getEventManager();
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return callEvent(event, null);
    }

    @Override
    public <T extends Event> T callEvent(T event, UUID target) {
        try {
            return callEventAsync(event, target).get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return event;
    }

    @Override
    public <T extends Event> Future<T> callEventAsync(final T event, final UUID target) {
        if (event instanceof Returnable || event instanceof Cancellable) {
            return pool.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    EventPacket packet = new EventPacket(nodeClient.getConnectionInfo().getUuid(), event, target, true);
                    nodeClient.writePacket(packet);
                    final BlockingQueue<T> packetBlockingQueue = new SynchronousQueue<T>();
                    callbackEvents.put(packet.getEventId(), (BlockingQueue<Event>) packetBlockingQueue);
                    return packetBlockingQueue.take();
                }
            });
        }
        else {
            return pool.submit(new Callable<T>() {
                @Override
                public T call() throws Exception {
                    nodeClient.writePacket(new EventPacket(nodeClient.getConnectionInfo().getUuid(), event, target, false));
                    return event;
                }
            });
        }
    }

    @Override
    public <T extends Event> Future<T> callEventAsync(T event) {
        return callEventAsync(event, null);
    }

    public void postPacket(EventPacket packet) {
        if (packet.getSender().equals(nodeClient.getConnectionInfo().getUuid())) {
            try {
                callbackEvents.get(packet.getEventId()).put(packet.getEvent().get());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            Event event = packet.getEvent().get();
            logger.debug(event.toString());
            eventManager.post(event);
            if (packet.isReturnable())
                nodeClient.writePacket(packet);
        }
    }

    @Override
    public Optional<Server> getServer(UUID uuid) {
        return Optional.fromNullable(nodeClient.getOnlineServers().get(uuid));
    }

    @Override
    public String getServerName() {
        return nodeClient.getConnectionInfo().getServername();
    }

    public void registerFormatter(Type type, Formatter formatter) {
        nodeClient.getGsonBaker().registerFormatter(type, formatter);
    }

}
