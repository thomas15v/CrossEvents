package com.thomas15v.crossevents.network.client;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.api.CrossEventService;
import com.thomas15v.crossevents.api.Returnable;
import com.thomas15v.crossevents.api.Server;
import com.thomas15v.crossevents.formatting.*;
import com.thomas15v.crossevents.formatting.Formatter;
import com.thomas15v.crossevents.network.ICrossConnectable;
import com.thomas15v.crossevents.network.packet.PacketHandler;
import com.thomas15v.crossevents.network.packet.packets.*;
import com.thomas15v.crossevents.network.packet.PacketManager;
import com.thomas15v.crossevents.network.packet.PacketConnection;
import com.thomas15v.crossevents.util.ConnectionInfo;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.service.event.EventManager;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * Created by thomas15v on 4/06/15.
 */
public class NodeClient extends PacketHandler implements Runnable, ICrossConnectable, CrossEventService {

    private PacketManager packetManager;
    private PacketConnection connection;
    private volatile Map<UUID, Event> callbackEvents = new HashMap<UUID, Event>();
    private Thread thread;
    private boolean running = true;
    private boolean connected;
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();
    private EventManager eventManager;
    private Map<UUID, Server> onlineServers = new HashMap<UUID, Server>();
    private GsonBaker gsonBaker;
    private ConnectionInfo ci;

    public NodeClient(ConnectionInfo connectionInfo, Game game){
        this.ci = connectionInfo;
        this.eventManager = game.getEventManager();
        this.gsonBaker = new GsonBaker(game.getServer());
    }

    public void connect() throws IOException {
        if (this.connection != null) {
            this.connection.close();
            connected = false;
        }
        Socket socket = new Socket();
        socket.connect(new InetSocketAddress(ci.getHostname(), ci.getPort()));
        this.packetManager = new PacketManager();
        this.connection = new PacketConnection(socket, packetManager, gsonBaker);
        this.connection.writePacket(new LoginPacket(ci.getUuid(), ci.getPwd(), ci.getServername()));
    }

    @Override
    public void run() {
        while (running){
            try {
                Optional<Packet> packet = connection.readPacket();
                if (packet.isPresent())
                    packet.get().handle(this);
            }
            catch (Exception e) {
                logger.info("Lost Connection with Server");
                reconnect(true);
            }
        }
    }

    @Override
    public void handle(EventPacket packet) {
        if (packet.getEvent().isPresent())
            if (packet.getSender().equals(ci.getUuid()))
            {
                callbackEvents.put(packet.getEventId(), packet.getEvent().get());
            }else {
                Event event = packet.getEvent().get();
                logger.debug(event.toString());
                eventManager.post(event);
                if (event instanceof Returnable || event instanceof Cancellable)
                    writePacket(packet);
            }
    }

    @Override
    public void handle(ServerInformationPacket packet) throws Exception {
        if (!connected) {
            logger.info("Succesfully Connected to Server!");
            connected = true;
        }
        if (packet.getStatus() == ServerInformationPacket.Status.OFFLINE)
            onlineServers.remove(packet.getUniqueServerId());
        else
            onlineServers.put(packet.getUniqueServerId(), new CrossServer(this, packet.getUniqueServerId(), packet.getServerName()));
    }

    public void reconnect(boolean wait) {
        if (wait) {
            try {
                connected = false;
                Thread.sleep(1800);
                logger.info("Reconnecting in 30 sec");
                connect();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else {
            stop();
            try {
                connect();
            } catch (IOException e) {
                e.printStackTrace();
            }
            start();
        }
    }

    @Override
    public void handle(LogoutPacket packet) throws Exception {
        if (packet.getMessage().equals("DUPLICATEDID")) {
            CrossEventsPlugin.getInstance().getConfig().resetServerId();
            ci.setUuid(CrossEventsPlugin.getInstance().getConfig().getServerId());
            reconnect(false);
        }
        else
            disconect("Kicked: " + packet.getMessage());
    }

    public void disconect(String message){
        logger.info(message);
        running = false;
    }

    @Override
    public void writePacket(Packet packet) {
        try {
            connection.writePacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T extends Event> T callEvent(T event) {
        return callEvent(event, null);
    }

    @Override
    public <T extends Event> T callEvent(T event, UUID target) {
        if (event instanceof Returnable || event instanceof Cancellable) {
            EventPacket packet = new EventPacket(ci.getUuid(), event, target, true);
            writePacket(packet);
            int ticks = onlineServers.size() * 3;
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
            writePacket(new EventPacket(ci.getUuid(), event, target, false));
            return event;
        }
    }

    @Override
    public Optional<Server> getServer(UUID uuid) {
        return Optional.fromNullable(onlineServers.get(uuid));
    }

    @Override
    public String getServerName() {
        return ci.getServername();
    }

    @Override
    public void registerFormatter(Type type, Formatter formatter) {
        gsonBaker.registerFormatter(type, formatter);
    }

    public void start(){
        this.thread = new Thread(this);
        thread.start();
    }

    public void stop(){
        running = false;
        thread.interrupt();
        connected = false;
        writePacket(new LogoutPacket(ci.getUuid(), "SHUTDOWN" ));
        try {
            Thread.sleep(10);
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return connected;
    }
}
