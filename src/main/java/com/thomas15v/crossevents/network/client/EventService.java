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

/**
 * Created by thomas15v on 10/06/15.
 */
public class EventService implements CrossEventService {

    private NodeClient nodeClient;
    private Game game;
    private volatile Map<UUID, Event> callbackEvents = new HashMap<UUID, Event>();
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();
    private EventManager eventManager;

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
        if (event instanceof Returnable || event instanceof Cancellable) {
            EventPacket packet = new EventPacket(nodeClient.getConnectionInfo().getUuid(), event, target, true);
            nodeClient.writePacket(packet);
            int ticks = nodeClient.getOnlineServers().size() * 3;
            while (!callbackEvents.containsKey(packet.getEventId()))
                try {
                    logger.debug("Waiting");
                    Thread.sleep(1);
                    ticks--;
                    if (ticks == 0) {
                        logger.warn("Return event took to long, returning normal event!");
                        return event;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            Event returnevent = callbackEvents.get(packet.getEventId());
            callbackEvents.remove(packet.getEventId());
            return (T) returnevent;
        }
        else {
            nodeClient.writePacket(new EventPacket(nodeClient.getConnectionInfo().getUuid(), event, target, false));
            return event;
        }
    }

    public void postPacket(EventPacket packet) {
        if (packet.getSender().equals(nodeClient.getConnectionInfo().getUuid()))
        {
            callbackEvents.put(packet.getEventId(), packet.getEvent().get());
        }else {
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
