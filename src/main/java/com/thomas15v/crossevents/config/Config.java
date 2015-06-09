package com.thomas15v.crossevents.config;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by thomas15v on 4/06/15.
 */
public class Config extends GenericConfig {

    private final String ISSERVER = "IsServer";
    private final String PWD = "password";
    private final String HOSTNAME = "HostName";
    private final String PORT = "Port";
    private final String SERVERID = "ServerId";
    private final String SERVERNAME = "ServerName";

    public Config(File configFile) throws IOException {
        super(configFile, 2);
    }

    @Override
    protected void migrate(int oldversion) {
        switch (oldversion){
            case 1:
                getRoot().getNode(SERVERNAME).setValue("Server");
                break;
        }
    }

    @Override
    protected void generateDefaults() {
        getRoot().getNode(ISSERVER).setValue(false);
        getRoot().getNode(PWD).setValue(UUID.randomUUID().toString());
        getRoot().getNode(HOSTNAME).setValue("localhost");
        getRoot().getNode(PORT).setValue(3947);
        getRoot().getNode(SERVERID).setValue(UUID.randomUUID().toString());
        getRoot().getNode(SERVERNAME).setValue("Server");
        super.generateDefaults();
    }

    public boolean isServer(){
        return getRoot().getNode(ISSERVER).getBoolean();
    }

    public String getPwd(){
        return getRoot().getNode(PWD).getString();
    }

    public String getHostName(){
        return getRoot().getNode(HOSTNAME).getString();
    }

    public int getPort(){
        return getRoot().getNode(PORT).getInt();
    }

    public UUID getServerId(){
        return UUID.fromString(getRoot().getNode(SERVERID).getString());
    }

    public String getServerName(){
        return getRoot().getNode(SERVERNAME).getString();
    }

    public void resetServerId(){
        getRoot().getNode(SERVERID).setValue(UUID.randomUUID().toString());
        try {
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
