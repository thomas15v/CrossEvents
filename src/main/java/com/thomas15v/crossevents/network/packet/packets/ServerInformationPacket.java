package com.thomas15v.crossevents.network.packet.packets;

import com.thomas15v.crossevents.network.packet.PacketHandler;
import com.thomas15v.crossevents.network.server.Server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by thomas15v on 6/06/15.
 */
public class ServerInformationPacket extends Packet {

    public enum Status{
        ONLINE,
        OFFLINE
    }

    private String serverName;
    private UUID serverUUID;
    private Status status;


    public ServerInformationPacket(){}

    public ServerInformationPacket(UUID sender, Server server, Status status){
        super(sender);
        this.serverName = server.getName();
        this.serverUUID = server.getUniqueId();
        this.status = status;
    }


    @Override
    public void write(BufferedWriter out) throws IOException {
        writeln(out, serverName);
        writeln(out, serverUUID.toString());
        writeln(out, status.name());
        super.write(out);
    }

    @Override
    public void read(BufferedReader in) throws IOException {
        serverName = in.readLine();
        serverUUID = UUID.fromString(in.readLine());
        status = Status.valueOf(in.readLine());
        super.read(in);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public UUID getUniqueServerId(){
        return serverUUID;
    }

    public String getServerName() {
        return serverName;
    }

    public Status getStatus() {
        return status;
    }
}
