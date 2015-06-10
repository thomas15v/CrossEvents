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
public class LoginPacket extends Packet {

    private String pwd;
    private String name;

    public LoginPacket(){}

    public LoginPacket(UUID uuid, String pwd, String name){
        super(uuid);
        this.pwd = pwd;
        this.name = name;
    }

    @Override
    public void read(BufferedReader in, Gson gson) throws IOException {
        this.pwd = in.readLine();
        this.name = in.readLine();
        super.read(in, gson);
    }

    @Override
    public void write(BufferedWriter out, Gson gson) throws IOException {
        writeln(out, pwd);
        writeln(out, name);
        super.write(out, gson);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public String getPwd() {
        return pwd;
    }

    public String getName() {
        return name;
    }
}
