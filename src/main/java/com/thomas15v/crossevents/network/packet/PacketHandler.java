package com.thomas15v.crossevents.network.packet;

import com.thomas15v.crossevents.network.packet.packets.EventPacket;
import com.thomas15v.crossevents.network.packet.packets.LogoutPacket;
import com.thomas15v.crossevents.network.packet.packets.Packet;

public class PacketHandler {

    public void handle(EventPacket packet) throws Exception {
        handle((Packet) packet);
    }

    public void handle(LogoutPacket packet) throws Exception {
        handle((Packet) packet);
    }

    public void handle(Packet packet) throws Exception {
        throw new Exception("This wasn't supposed to happen!");
    }

}
