package com.thomas15v.crossevents.network.packet.packets;

import com.google.gson.Gson;
import com.thomas15v.crossevents.network.packet.PacketHandler;
import org.spongepowered.api.event.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by thomas15v on 4/06/15.
 */
public class EventPacket extends Packet {
    private Event event;
    private UUID eventId;
    private int hop = 0;

    public EventPacket(){}

    public EventPacket(UUID sender, Event event){
        super(sender);
        this.event = event;
        eventId = UUID.randomUUID();
    }

    public Event getEvent() {
        return event;
    }

    @Override
    public void read(BufferedReader in) throws IOException {
        try {
            hop = Integer.parseInt(in.readLine());
            Class<? extends Event> clazz = (Class<? extends Event>) Class.forName(in.readLine());
            event = gson.fromJson(in.readLine(), clazz);
            eventId = UUID.fromString(in.readLine());

        } catch (Exception e) {
            e.printStackTrace();
        }
        super.read(in);
    }

    @Override
    public void write(BufferedWriter out) throws IOException {
        writeln(out, String.valueOf(hop));
        writeln(out, event.getClass().getName());
        writeln(out, gson.toJson(event));
        writeln(out, eventId.toString());
        super.write(out);
    }

    @Override
    public void handle(PacketHandler handler) throws Exception {
        handler.handle(this);
    }

    public int getHop() {
        return hop;
    }

    public void hop(){
        hop++;
    }

    public UUID getEventId() {
        return eventId;
    }
}
