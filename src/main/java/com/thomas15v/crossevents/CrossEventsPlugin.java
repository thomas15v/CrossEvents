package com.thomas15v.crossevents;

import com.google.inject.Inject;
import com.thomas15v.crossevents.api.CrossEventService;
import com.thomas15v.crossevents.config.Config;
import com.thomas15v.crossevents.examples.CrossChat;
import com.thomas15v.crossevents.network.ICrossConnectable;
import com.thomas15v.crossevents.network.client.NodeClient;
import com.thomas15v.crossevents.network.server.NodeServer;
import com.thomas15v.crossevents.network.server.Server;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.AbstractEvent;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerChatEvent;
import org.spongepowered.api.event.state.ServerStartedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.ProviderExistsException;
import org.spongepowered.api.service.config.ConfigDir;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.event.callback.CallbackList;

import java.io.File;
import java.io.IOException;

/**
 * Created by thomas15v on 4/06/15.
 */
@Plugin(id = "CrossEvents", name = "CrossEvents", version = "0.1")
public class CrossEventsPlugin {

    private static CrossEventsPlugin plugin;

    public static CrossEventsPlugin getInstance(){
        return plugin;
    }

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDir;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private File defaultConfig;

    private NodeServer server;
    private NodeClient client;
    private Config config;

    //todo: remove this test
    private Game game;

    @Subscribe
    public void onEnable(ServerStartedEvent event){
        this.game = event.getGame();
        this.plugin = this;
        if (!configDir.exists())
            configDir.mkdirs();
        try {
            this.config = new Config(defaultConfig);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (config.isServer())
            try {
                server = new NodeServer(config.getHostName(), config.getPort(), config.getPwd());
                logger.info("CrossEventsServer started on " + config.getHostName() + ":" + config.getPort());
                server.start();
            } catch (IOException e) {
                logger.error("CrossEventsServer failed to start on " + config.getHostName() + ":" + config.getPort());
            }

        try {
            this.client = new NodeClient(config.getHostName(), config.getPort(), config.getPwd(), config.getServerId(), event.getGame().getEventManager());
            logger.info("CrossEventsClient connecting to " + config.getHostName() + ":" + config.getPort());
            this.client.start();
        } catch (IOException e) {
            logger.error("CrossEventsClient failed to connect to " + config.getHostName() + ":" + config.getPort());
        }

        try {
            event.getGame().getServiceManager().setProvider(this, CrossEventService.class, client);
        } catch (ProviderExistsException e) {
            logger.error("Ehm, Seems somebody already took the exact classname of my service.... . Does this make any sense ? Go check your plugins if their aren't 2 versions of this plugin... .");
        }
    }

    public Logger getLogger() {
        return logger;
    }

    public Config getConfig() {
        return config;
    }
}
