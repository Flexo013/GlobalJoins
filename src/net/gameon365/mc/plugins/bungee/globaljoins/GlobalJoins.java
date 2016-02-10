package net.gameon365.mc.plugins.bungee.globaljoins;

import net.craftminecraft.bungee.bungeeyaml.pluginapi.ConfigurablePlugin;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GlobalJoins extends ConfigurablePlugin implements Listener {
    protected String loginString;
    protected String logoutString;
    
    @Override
    public void onEnable()
    {
        this.loginString = this.getConfig().getString( "strings.login", "&e%s joined the network." );
        this.logoutString = this.getConfig().getString( "strings.logout", "&e%s left the network." );
        
        this.getProxy().getPluginManager().registerListener( this, this );
    }
    
    @EventHandler
    public void onPostLoginEvent( PostLoginEvent e )
    {
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.loginString, e.getPlayer().getName() ) ) );
    }
    
    @EventHandler
    public void onPlayerDisconnectEvent( PlayerDisconnectEvent e )
    {
        this.getProxy().broadcast( ChatColor.translateAlternateColorCodes( '&', String.format( this.logoutString, e.getPlayer().getName() ) ) );
    }
}
