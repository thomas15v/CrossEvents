package com.thomas15v.crossevents.formatting;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.thomas15v.crossevents.formatting.Formatters.TextFormatter;
import com.thomas15v.crossevents.formatting.Formatters.UserFormatter;
import org.spongepowered.api.Server;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by thomas15v on 9/06/15.
 */
public class GsonBaker {

    private List<FormatterEntry> formatterEntryList = new ArrayList<FormatterEntry>();
    private Server server;
    private Gson gson;

    public GsonBaker(){
        bakeGson();
    }

    public GsonBaker(Server server){
        this();
        this.server = server;
        registerFormatter(Text.class, new TextFormatter());
    }

    private class FormatterEntry{
        private Type type;
        private Formatter formatter;

        public FormatterEntry(Type type, Formatter formatter){
            this.type = type;
            this.formatter = formatter;
        }
    }

    public void registerFormatter(Type type, Formatter formatter){
        formatterEntryList.add(new FormatterEntry(type, formatter));
        bakeGson();
    }

    private void bakeGson(){
        GsonBuilder builder = new GsonBuilder();
        for (FormatterEntry entry : formatterEntryList)
           builder = builder.registerTypeAdapter(entry.type, entry.formatter);
        gson = builder.create();
    }

    public Gson getGson() {
        return gson;
    }
}
