package com.thomas15v.crossevents.network.client;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.api.CrossEventService;
import com.thomas15v.crossevents.api.Returnable;
import com.thomas15v.crossevents.network.ICrossConnectable;
import com.thomas15v.crossevents.network.packet.PacketHandler;
import com.thomas15v.crossevents.network.packet.packets.EventPacket;
import com.thomas15v.crossevents.network.packet.packets.LoginPacket;
import com.thomas15v.crossevents.network.packet.PacketManager;
import com.thomas15v.crossevents.network.packet.packets.LogoutPacket;
import com.thomas15v.crossevents.network.packet.packets.Packet;
import com.thomas15v.crossevents.network.server.PacketConnection;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Cancellable;
import org.spongepowered.api.event.Event;
import org.spongepowered.api.service.event.EventManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.*;

/**
 * Created by thomas15v on 4/06/15.
 */
public class NodeClient extends PacketHandler implements Runnable, ICrossConnectable, CrossEventService {

    private final String hostname;
    private final int port;
    private Socket socket;
    private PacketManager packetManager;
    private PacketConnection connection;
    private UUID uuid;
    private volatile Map<UUID, Event> callbackEvents = new HashMap<UUID, Event>();
    private Thread thread;
    private String pwd;
    private boolean connected = true;
    private boolean run;
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();
    private EventManager eventManager;

    public static void main(String[] args) throws IOException {
        NodeClient client = new NodeClient("localhost", 3947, "123456", UUID.randomUUID(), null);
    }

    public NodeClient(String hostname, int port , String pwd, UUID uuid, EventManager eventManager) throws IOException {
        this.eventManager = eventManager;
        this.hostname = hostname;
        this.port = port;
        this.pwd = pwd;
        this.uuid = uuid;
        connect();
    }

    public void connect() throws IOException {
        this.socket = new Socket();
        this.socket.connect(new InetSocketAddress(hostname, port));
        this.packetManager = new PacketManager();
        this.connection = new PacketConnection(socket, packetManager);
        this.connection.writePacket(new LoginPacket(uuid, pwd));
    }

    @Override
    public void run() {
        while (connected){
            try {
                Optional<Packet> packet = connection.readPacket();
                if (packet.isPresent())
                    packet.get().handle(this);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }

    @Override
    public void handle(EventPacket packet) {
        if (packet.getSender().equals(uuid))
        {
            callbackEvents.put(packet.getEventId(), packet.getEvent());
        }else {
            Event event = packet.getEvent();
            logger.debug(event.toString());
            //todo: remove this for sure
            if (eventManager != null)
                eventManager.post(event);
            if (event instanceof Returnable || event instanceof Cancellable)
                writePacket(packet);
        }
    }

    public void reconnect() throws IOException {
        stop();
        connect();
        start();
    }

    @Override
    public void handle(LogoutPacket packet) throws Exception {
        //Hey I know how anoying it can be, So I added an autoreset :). You love me right ? Love goes above pretty code!!!!! PRAISE LORD GABEN
        if (packet.getMessage().equals("DUPLICATEDID")) {
            CrossEventsPlugin.getInstance().getConfig().resetServerId();
            this.uuid = CrossEventsPlugin.getInstance().getConfig().getServerId();
            reconnect();
        }
        else
            disconect("Kicked: " + packet.getMessage());
    }

    public void disconect(String message){
        logger.info(message);
        connected = false;
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
        if (event instanceof Returnable || event instanceof Cancellable) {
            EventPacket packet = new EventPacket(uuid, event);
            writePacket(packet);
            int ticks = 10;
            while (!callbackEvents.containsKey(packet.getEventId()))
                try {
                    logger.debug("Waiting");
                    Thread.sleep(100);
                    ticks--;
                    if (ticks == 0) {
                        logger.warn("Return event took to long, returning normal event!");
                        return event;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            Event returnevent = callbackEvents.get(packet.getEventId());
            callbackEvents.remove(packet.getEventId());
            return (T) returnevent;
        }
        else {
            writePacket(new EventPacket(uuid, event));
            return event;
        }

    }

    public void start(){
        this.thread = new Thread(this);
        thread.start();
    }

    public void stop(){
        run = false;
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
