package com.thomas15v.crossevents.network.server;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.network.packet.PacketConnection;
import com.thomas15v.crossevents.network.packet.packets.ServerInformationPacket;
import com.thomas15v.crossevents.network.packet.packets.LoginPacket;
import com.thomas15v.crossevents.network.packet.PacketManager;
import com.thomas15v.crossevents.network.packet.packets.LogoutPacket;
import com.thomas15v.crossevents.network.packet.packets.Packet;
import org.slf4j.Logger;
import org.spongepowered.api.event.Event;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by thomas15v on 4/06/15.
 */
public class NodeServer implements Runnable {

    private final String host;
    private final int port;
    private ServerSocket serversocket;
    private PacketManager packetManager;
    private volatile List<Server> serverList = new ArrayList<Server>();
    private String pwd;
    private UUID uuid = UUID.fromString("00000000-00000000-00000000-00000000-00000000");
    private Thread thread;
    private volatile boolean run;
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();

    public static void main(String[] args) {
        try {
            new NodeServer("localhost", 8080, "").run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        stop();
        run = true;
        this.thread = new Thread(this);
        thread.start();
    }

    public void stop(){
        run = false;
    }

    public NodeServer(String host, int port, String pwd) throws IOException {
        this.host = host;
        this.port = port;
        this.pwd = pwd;
        this.packetManager = new PacketManager();
        this.serversocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        while (run) {
            try {
                Socket incom = serversocket.accept();
                PacketConnection connection = new PacketConnection(incom, packetManager);
                LoginPacket loginPacket = (LoginPacket) connection.readPacket().get();
                if (loginPacket.getPwd().equals(pwd)) {
                    if (!getServer(loginPacket.getSender()).isPresent()){
                        Server server = new Server(loginPacket.getSender(), loginPacket.getName(), connection, this);
                        serverList.add(server);
                        logger.info(loginPacket.getSender() + " logged in!");
                        updateServers(ServerInformationPacket.Status.ONLINE, server);
                    }else
                        connection.writePacket(new LogoutPacket(uuid, "DUPLICATEDID"));
                }
                else {
                    connection.writePacket(new LogoutPacket(uuid, "Wrong password!"));
                    System.out.println("kicked " + loginPacket.getSender() + " : Wrong password!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void updateServers(ServerInformationPacket.Status status, Server... servers){
        for (Server server : servers)
                writePacket(new ServerInformationPacket(uuid, server, status));

    }

    public void removeServer(Server server){
        serverList.remove(server);
    }

    public Optional<Server> getServer(int i){
        if (serverList.size() > i)
            return Optional.fromNullable(serverList.get(i));
        else
            return Optional.absent();
    }

    public Optional<Server> getServer(UUID uuid) {
        for (Server server : serverList)
            if (server.getUniqueId().equals(uuid))
                return Optional.of(server);
        return Optional.absent();
    }


    public void writePacket(Packet packet) {
        for (Server server: serverList)
            if (!server.getUniqueId().equals(packet.getSender()))
                server.writePacket(packet);
    }
}
