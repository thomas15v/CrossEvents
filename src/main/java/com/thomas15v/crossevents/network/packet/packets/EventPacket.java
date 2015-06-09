package com.thomas15v.crossevents.network.packet.packets;

import com.google.common.base.Optional;
import com.thomas15v.crossevents.CrossEventsPlugin;
import com.thomas15v.crossevents.network.packet.PacketHandler;
import org.spongepowered.api.event.Event;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.UUID;

/**
 *
 * Packet Layout
 *
 * Target server
 * HOP number
 * event class name
 * eventdata
 * eventid
 *
 */
public class EventPacket extends Packet {
    private Optional<Event> event;
    private String eventData;
    private String eventClass;
    private UUID eventId;
    private int hop = 0;
    private Optional<UUID> target;

    public EventPacket(){}

    public EventPacket(UUID sender, Event event){
        super(sender);
        this.event = Optional.of(event);
        eventId = UUID.randomUUID();
        target = Optional.absent();
    }

    public EventPacket(UUID sender, Event event, UUID target){
        this(sender, event);
        this.target = Optional.fromNullable(target);
    }

    public Optional<Event> getEvent() {
        return event;
    }

    @Override
    public void read(BufferedReader in) throws IOException {
        String target = in.readLine();
        if (target.equals("ALL"))
            this.target = Optional.absent();
        else
            this.target = Optional.of(UUID.fromString(target));
        hop = Integer.parseInt(in.readLine());
        event = parseEvent(in.readLine(), in.readLine());
        eventId = UUID.fromString(in.readLine());
        super.read(in);
    }

    private Optional<Event> parseEvent(String eventClass, String eventData){
        this.eventData = eventData;
        this.eventClass = eventClass;
        try {
            return Optional.of(gson.fromJson(eventData,(Class<? extends Event>) Class.forName(eventClass)));
        } catch (ClassNotFoundException e) {
            CrossEventsPlugin.getInstance().getLogger().debug("Could not find event class " + eventClass);
            return Optional.absent();
        }
    }

    @Override
    public void write(BufferedWriter out) throws IOException {
        if (target.isPresent())
            writeln(out, target.get().toString());
        else
            writeln(out, "ALL");
        writeln(out, String.valueOf(hop));
        if (event.isPresent()) {
            writeln(out, event.get().getClass().getName());
            writeln(out, gson.toJson(event.get()));
        }
        else {
            writeln(out, eventClass);
            writeln(out, eventData);
        }
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

    public Optional<UUID> getTarget() {
        return target;
    }
}
