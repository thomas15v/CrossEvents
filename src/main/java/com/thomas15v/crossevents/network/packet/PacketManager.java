package com.thomas15v.crossevents.network.packet;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.network.packet.packets.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by thomas15v on 4/06/15.
 */
public class PacketManager {

    private List<Constructor<? extends Packet>> packetList = new ArrayList<Constructor<? extends Packet>>();

    public PacketManager(){
        registerPacket(LoginPacket.class);
        registerPacket(EventPacket.class);
        registerPacket(LoginPacket.class);
        registerPacket(LogoutPacket.class);
        registerPacket(ServerInformationPacket.class);
    }

    public void registerPacket(Class packet){
        try {
            packetList.add(packet.getConstructor());
        } catch (NoSuchMethodException e) {
            throw new NoSuchElementException("A packet needs to have an empty constructor!");
        }
    }

    public Optional<Packet> getPacketFor(int id){
        Constructor<? extends Packet> constructor = packetList.get(id);
        if (constructor == null || id == -1)
            return Optional.absent();
        else
            try {
                return Optional.of(constructor.newInstance());
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        return Optional.absent();
    }

    public int getIdForPacket(Packet packet){
        try {
            return packetList.indexOf(packet.getClass().getConstructor());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return -1;
    }

}
