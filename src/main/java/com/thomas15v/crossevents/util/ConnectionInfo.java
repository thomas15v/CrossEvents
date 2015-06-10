package com.thomas15v.crossevents.util;

import java.util.UUID;

/**
 * Created by thomas15v on 10/06/15.
 */
public class ConnectionInfo {

    private final String hostname;
    private final int port;
    private final String pwd;
    private UUID uuid;
    private final String servername;

    public ConnectionInfo (String hostname, int port, String pwd, UUID uuid, String servername){
        this.hostname = hostname;
        this.port = port;
        this.pwd = pwd;
        this.uuid = uuid;
        this.servername = servername;
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getPwd() {
        return pwd;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getServername() {
        return servername;
    }
}
