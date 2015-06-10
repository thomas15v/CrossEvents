package com.thomas15v.crossevents.network.packet;

import com.google.common.base.Optional;
import com.google.gson.Gson;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.formatting.GsonBaker;
import com.thomas15v.crossevents.network.packet.PacketManager;
import com.thomas15v.crossevents.network.packet.packets.Packet;
import org.slf4j.Logger;

import java.io.*;
import java.net.Socket;

public class PacketConnection {

    private Socket socket;
    private PacketManager packetManager;
    private GsonBaker gsonBaker;
    private BufferedWriter out;
    private BufferedReader in;
    private Logger logger = CrossEventsPlugin.getInstance().getLogger();


    public PacketConnection(Socket socket, PacketManager packetManager, GsonBaker gsonBaker) throws IOException {
        this.socket = socket;
        this.packetManager = packetManager;
        this.gsonBaker = gsonBaker;
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }


    public synchronized void writePacket(Packet packet) throws IOException {
        out.write(String.valueOf(packetManager.getIdForPacket(packet)) + "\n");
        packet.write(out, gsonBaker.getGson());
        out.flush();
        logger.debug("write " + packet);
    }

    public Optional<Packet> readPacket() throws IOException {
        Optional<Packet> packet = packetManager.getPacketFor(Integer.parseInt(in.readLine()));
        if (packet.isPresent())
            packet.get().read(in, gsonBaker.getGson());
        logger.debug("read " + packet.get());
        return packet;
    }

    public synchronized void close(){
        try {
            socket.close();
        } catch (IOException e) {
        }
    }
}
