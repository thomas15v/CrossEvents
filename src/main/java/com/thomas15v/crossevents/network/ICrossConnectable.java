package com.thomas15v.crossevents.network;

import com.thomas15v.crossevents.network.packet.packets.Packet;

/**
 * Created by thomas15v on 4/06/15.
 */
public interface ICrossConnectable {

    public void writePacket(Packet packet);

}
