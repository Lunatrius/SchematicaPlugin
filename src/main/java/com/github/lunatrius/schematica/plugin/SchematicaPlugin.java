package com.github.lunatrius.schematica.plugin;

import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class SchematicaPlugin extends Plugin implements Listener {
    private static final String CONFIG_FILE_NAME = "config.yml";
    private File configFile = null;
    private boolean isPrinterEnabled = true;
    private boolean isSaveEnabled = true;
    private boolean isLoadEnabled = true;

    @Override
    public void onLoad() {
        this.configFile = new File(getDataFolder(), CONFIG_FILE_NAME);
    }

    @Override
    public void onEnable() {
        copyDefaultConfiguration();
        loadConfiguration();

        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel("schematica");
    }


    private void copyDefaultConfiguration() {
        final File dataFolder = this.configFile.getParentFile();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }

        if (!this.configFile.exists()) {
            try (final InputStream in = getResourceAsStream(CONFIG_FILE_NAME)) {
                Files.copy(in, this.configFile.toPath());
            } catch (final IOException ioe) {
                getLogger().throwing(SchematicaPlugin.class.getName(), "copyDefaultConfig", ioe);
            }
        }
    }

    private void loadConfiguration() {
        try {
            final Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.configFile);

            this.isPrinterEnabled = configuration.getBoolean("capability.printer", true);
            this.isSaveEnabled = configuration.getBoolean("capability.save", true);
            this.isLoadEnabled = configuration.getBoolean("capability.load", true);
        } catch (final IOException ioe) {
            getLogger().throwing(SchematicaPlugin.class.getName(), "loadConfiguration", ioe);
        }
    }

    @Override
    public void onDisable() {
        getProxy().unregisterChannel("schematica");
        getProxy().getPluginManager().unregisterListener(this);
    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        event.getPlayer().sendData("schematica", getPayload());
    }

    private byte[] getPayload() {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeByte(0);
            dataOutputStream.writeBoolean(this.isPrinterEnabled);
            dataOutputStream.writeBoolean(this.isSaveEnabled);
            dataOutputStream.writeBoolean(this.isLoadEnabled);

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException ioe) {
            getLogger().throwing(SchematicaPlugin.class.getName(), "getPayload", ioe);
        }

        return null;
    }
}
