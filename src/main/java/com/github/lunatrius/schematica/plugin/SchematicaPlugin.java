package com.github.lunatrius.schematica.plugin;

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class SchematicaPlugin extends Plugin implements Listener {
    private static final String CHANNEL = "schematica";
    private static final String PERM_PRINTER = "schematica.printer";
    private static final String PERM_SAVE = "schematica.save";
    private static final String PERM_LOAD = "schematica.load";

    @Override
    public void onLoad() {
    }

    @Override
    public void onEnable() {
        getProxy().getPluginManager().registerListener(this, this);
        getProxy().registerChannel(CHANNEL);
    }


    @Override
    public void onDisable() {
        getProxy().unregisterChannel(CHANNEL);
        getProxy().getPluginManager().unregisterListener(this);
    }

    @EventHandler
    public void onPostLogin(final PostLoginEvent event) {
        final ProxiedPlayer player = event.getPlayer();
        final byte[] payload = getPayload(player);
        if (payload != null) {
            player.sendData(CHANNEL, payload);
        }
    }

    private byte[] getPayload(ProxiedPlayer player) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        try {
            dataOutputStream.writeByte(0);
            dataOutputStream.writeBoolean(player.hasPermission(PERM_PRINTER));
            dataOutputStream.writeBoolean(player.hasPermission(PERM_SAVE));
            dataOutputStream.writeBoolean(player.hasPermission(PERM_LOAD));

            return byteArrayOutputStream.toByteArray();
        } catch (final IOException ioe) {
            getLogger().throwing(SchematicaPlugin.class.getName(), "getPayload", ioe);
        }

        return null;
    }
}
