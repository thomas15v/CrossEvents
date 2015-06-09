package com.thomas15v.crossevents.config;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import java.io.File;
import java.io.IOException;

public abstract class GenericConfig {

    private final int version;
    protected ConfigurationNode root;
    private ConfigurationLoader<CommentedConfigurationNode> configManager;

    private final String VERSION = "version";

    public GenericConfig(File configFile, int version) throws IOException {
        this.version = version;
        this.configManager = HoconConfigurationLoader.builder().setFile(configFile).build();
        if (!configFile.exists()) {
            configFile.createNewFile();
            this.reload();
            generateDefaults();
        }
        this.reload();
        checkVersion();
    }

    private void checkVersion() throws IOException {
        int configversion = getRoot().getNode(VERSION).getInt();
        if (configversion < this.version){
            getRoot().getNode(VERSION).setValue(this.version);
            migrate(configversion);
            save();
        }
    }

    protected void migrate(int oldversion){

    }

    protected void generateDefaults(){
        try {
            getRoot().getNode(VERSION).setValue(version);
            save();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ConfigurationNode getRoot() {
        return root;
    }

    public void reload() throws IOException {
        this.root = configManager.load();
    }

    public void save() throws IOException {
        configManager.save(root);
    }
}