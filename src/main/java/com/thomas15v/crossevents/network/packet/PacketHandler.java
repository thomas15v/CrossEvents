package com.thomas15v.crossevents.network.packet;

import com.thomas15v.crossevents.network.packet.packets.*;

public abstract class PacketHandler {

    public void handle(EventPacket packet) throws Exception {
        handle((Packet) packet);
    }

    public void handle(LoginPacket packet) throws Exception {
        handle((Packet) packet);
    }

    public void handle(LogoutPacket packet) throws Exception {
        handle((Packet) packet);
    }

    public void handle(ServerInformationPacket packet) throws Exception {
        handle((Packet) packet);
    }

    public void handle(Packet packet) throws Exception {
        throw new Exception("This wasn't supposed to happen!");
    }

}
