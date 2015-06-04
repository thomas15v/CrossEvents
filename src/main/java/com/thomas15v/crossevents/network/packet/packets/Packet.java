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
public abstract class Packet {

    protected static final Gson gson = new Gson();
    private UUID sender;

    public Packet(){}

    public Packet(UUID sender){
        this.sender = sender;
    }

    public void read(BufferedReader in) throws IOException{
        this.sender = UUID.fromString(in.readLine());
    }

    public void write(BufferedWriter out) throws IOException{
        writeln(out, sender.toString());
    }

    protected void writeln(BufferedWriter out, String string) throws IOException {
        out.write(string + "\n");
    }

    abstract public void handle(PacketHandler handler) throws Exception;

    public UUID getSender() {
        return sender;
    }

    @Override
    public String toString() {
        return gson.toJson(this);
    }
}
