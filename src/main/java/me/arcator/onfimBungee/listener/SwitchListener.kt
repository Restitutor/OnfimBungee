package me.arcator.onfimBungee.listener

import me.arcator.onfimLib.format.SerializedEvent
import me.arcator.onfimLib.format.SJoin
import me.arcator.onfimLib.format.SQuit
import me.arcator.onfimLib.format.makeSwitch
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ServerSwitchEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler

class SwitchListener(private val sendEvt: (SerializedEvent) -> Unit) : Listener {
    @EventHandler
    fun postServerSwitchEvent(event: ServerSwitchEvent) {
        if (event.from == null) return
        val fromServer: String = event.from.name
        val player: ProxiedPlayer = event.player
        val server: String = player.server.info.name

        // We don't know why this case happens
        if (fromServer == server) return
        val name: String = player.displayName
        sendEvt(SQuit(name, fromServer))
        sendEvt(makeSwitch(username = name, serverName = server, fromServer = fromServer))
        sendEvt(SJoin(name, server))
    }
}
