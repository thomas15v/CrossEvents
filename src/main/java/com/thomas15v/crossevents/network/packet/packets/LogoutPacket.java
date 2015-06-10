package com.thomas15v.crossevents.network.packet.packets;

import com.google.gson.Gson;
import com.thomas15v.crossevents.network.packet.PacketHandler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by thomas15v on 4/06/15.
 */
public class LogoutPacket extends Packet {

    private String message;

    public LogoutPacket(){}

    public LogoutPacket(UUID uuid, String message){
        super(uuid);
        this.message = message;
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }

    @Override
    public void write(BufferedWriter out, Gson gson) throws IOException {
        writeln(out, message);
        super.write(out, gson);
    }

    @Override
    public void read(BufferedReader in, Gson gson) throws IOException {
        message = in.readLine();
        super.read(in, gson);
    }

    public String getMessage() {
        return message;
    }
}
