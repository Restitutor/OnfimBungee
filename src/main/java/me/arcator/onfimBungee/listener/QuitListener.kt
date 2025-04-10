package me.arcator.onfimBungee.listener

import me.arcator.onfimLib.format.GenericChat
import me.arcator.onfimLib.format.JoinQuit
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PlayerDisconnectEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class QuitListener(private val sendEvt: (GenericChat) -> Unit) : Listener {
    @EventHandler
    fun onPlayerDisconnectEvent(event: PlayerDisconnectEvent) {
        val player: ProxiedPlayer = event.player
        if (player.server == null) return
        val server: String = player.server.info.name
        sendEvt(JoinQuit(name = player.displayName, server = server, type = "Quit"))
    }
}