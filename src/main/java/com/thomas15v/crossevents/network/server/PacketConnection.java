package com.thomas15v.crossevents.network.server;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.network.packet.PacketManager;
import com.thomas15v.crossevents.network.packet.packets.Packet;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;

public class PacketConnection {

    private Socket socket;
    private PacketManager packetManager;
    private BufferedWriter out;
    private BufferedReader in;
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();


    public PacketConnection(Socket socket, PacketManager packetManager) throws IOException {
        this.socket = socket;
        this.packetManager = packetManager;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public void writePacket(Packet packet) throws IOException {
        logger.debug("write " + packet);
        out.write(String.valueOf(packetManager.getIdForPacket(packet)) + "\n");
        packet.write(out);
        out.flush();
    }

    public Optional<Packet> readPacket() throws IOException {
        Optional<Packet> packet = packetManager.getPacketFor(Integer.parseInt(in.readLine()));
        if (packet.isPresent())
            packet.get().read(in);
       logger.debug("read " + packet.get());
        return packet;

    }

}
