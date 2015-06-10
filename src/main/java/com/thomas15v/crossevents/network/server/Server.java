package com.thomas15v.crossevents.network.server;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.network.ICrossConnectable;
import com.thomas15v.crossevents.network.packet.PacketConnection;
import com.thomas15v.crossevents.network.packet.PacketHandler;
import com.thomas15v.crossevents.network.packet.packets.EventPacket;
import com.thomas15v.crossevents.network.packet.packets.LogoutPacket;
import com.thomas15v.crossevents.network.packet.packets.Packet;
import com.thomas15v.crossevents.network.packet.packets.ServerInformationPacket;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Created by thomas15v on 4/06/15.
 */
public class Server extends PacketHandler implements Runnable, ICrossConnectable {

    private UUID uuid;
    private String name;
    private PacketConnection connection;
    private NodeServer nodeserver;
    private Thread thread;
    private boolean connected = true;
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();

    public Server(UUID uuid, String name,  PacketConnection connection, NodeServer server) {
        this.uuid = uuid;
        this.name = name;
        this.connection = connection;
        this.nodeserver = server;
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
        while (connected){
            try {
                Optional<Packet> packet = connection.readPacket();
                if (packet.isPresent()) {
                    packet.get().handle(this);
                }
            }
            catch (Exception e) {
                if (e instanceof NumberFormatException)
                    disconect("Server " + uuid + " Has Disconnected!");
                else {
                    disconect("Server " + uuid + " Has Disconnected!: " + e.toString());
                    e.printStackTrace();
                }
            }
        }
    }

    public void disconect(String message){
        logger.info(message);
        connected = false;
        thread.interrupt();
        connection.close();
        nodeserver.removeServer(this);
        nodeserver.updateServers(ServerInformationPacket.Status.OFFLINE, this);
    }

    @Override
    public void handle(EventPacket packet) {
        if (packet.isReturnable()){
            Optional<Server> server;
            if (packet.getTarget().isPresent() && packet.getHop() == 0)
                server = nodeserver.getServer(packet.getTarget().get());
            else
                server = nodeserver.getServer(packet.getHop());
            packet.hop();
            if (server.isPresent()){
                if (!server.get().getUniqueId().equals(getUniqueId())){
                    server.get().writePacket(packet);
                }else {
                    handle(packet);
                }
            }
            else nodeserver.getServer(packet.getSender()).get().writePacket(packet);
        }
        else if (packet.getTarget().isPresent()){
            Optional<Server> server = nodeserver.getServer(packet.getTarget().get());
            if (server.isPresent())
                server.get().writePacket(packet);
        }
        else nodeserver.writePacket(packet);
    }

    @Override
    public void handle(LogoutPacket packet) throws Exception {
        thread.interrupt();
        connection.close();
        disconect(uuid + " Has disconnected!");
    }

    @Override
    public void writePacket(Packet packet) {
        try {
            connection.writePacket(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public String getName() {
        return name;
    }
}
