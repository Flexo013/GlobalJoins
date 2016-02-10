package net.gameon365.mc.plugins.bungee.globaljoins;

import java.util.HashSet;
import java.util.Set;
import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GlobalJoins extends ConfigurablePlugin implements Listener {

    private String loginString;
    private String logoutString;
    private Boolean delayedCheck;
    private Integer delayedSeconds;
    private Set<ProxiedPlayer> onlinePlayers = new HashSet<>();

    @Override
    public void onEnable() {
        this.loginString = this.getConfig().getString("strings.login", "&e%s joined the network.");
        this.logoutString = this.getConfig().getString("strings.logout", "&e%s left the network.");
        this.delayedCheck = this.getConfig().getBoolean("delayedCheck.enabled", false);
        this.delayedSeconds = this.getConfig().getInt("delayedCheck.seconds", 3);

        if (this.delayedCheck) {
            this.getProxy().getLogger().info("You have delayedCheck enabled.  Please note, this is an experimental feature and may reduce performance and/or stability.");
        } else if (this.delayedCheck && (this.delayedSeconds == 0)) {
            this.getProxy().getLogger().warning("You have delayedCheck enabled, but set the delay to 0 seconds.  This will likely act the same as having delayedCheck disabled, though performance and/or stability may be decreased.");
        } else if (this.delayedCheck && (this.delayedSeconds < 0)) {
            this.getProxy().getLogger().severe("You have delayedCheck enabled, but set the delay to a negative value, which is not acceptable for this option.  Please set the delay to a positive number of seconds.  Because of this, delayedCheck has been disabled for now.");
            this.delayedCheck = false;
        }

        this.getProxy().getPluginManager().registerListener(this, this);
    }

    @EventHandler
    public void onPostLoginEvent(PostLoginEvent e) {
        if (!this.delayedCheck) {
            this.getProxy().broadcast(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', String.format(this.loginString, e.getPlayer().getName()))));
        } else {
            this.getProxy().getScheduler().schedule(this, new onPostLoginEventTask(e.getPlayer().getName(), this), this.delayedSeconds, TimeUnit.SECONDS);
        }
    }

    public class onPostLoginEventTask implements Runnable {

        private String playerName;
        private GlobalJoins plugin;

        public onPostLoginEventTask(String playerName, GlobalJoins plugin) {
            this.playerName = playerName;
            this.plugin = plugin;
        }

        @Override
        public void run() {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(playerName);
            if (player != null) {
                plugin.getProxy().broadcast(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', String.format(plugin.getLoginString(), playerName))));
                plugin.getOnlinePlayers().add(player);
            }
        }
    }

    @EventHandler
    public void onPlayerDisconnectEvent(PlayerDisconnectEvent e) {
        if (!this.delayedCheck) {
            this.getProxy().broadcast(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', String.format(this.logoutString, e.getPlayer().getName()))));
        } else {
            this.getProxy().getScheduler().schedule(this, new onPlayerDisconnectEvent(e.getPlayer(), this), 0, TimeUnit.SECONDS);
        }
    }

    public class onPlayerDisconnectEvent implements Runnable {

        private ProxiedPlayer player;
        private GlobalJoins plugin;

        public onPlayerDisconnectEvent(ProxiedPlayer player, GlobalJoins plugin) {
            this.player = player;
            this.plugin = plugin;
        }

        @Override
        public void run() {
            if (plugin.getOnlinePlayers().contains(player)) {
                plugin.getProxy().broadcast(TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', String.format(plugin.getLogoutString(), player.getName()))));
                plugin.getOnlinePlayers().remove(player);
            }
        }
    }

    public String getLoginString() {
        return loginString;
    }

    public String getLogoutString() {
        return logoutString;
    }

    public Set<ProxiedPlayer> getOnlinePlayers() {
        return onlinePlayers;
    }

}
