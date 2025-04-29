package me.arcator.onfimBungee.listener

import me.arcator.onfimLib.format.Chat
import me.arcator.onfimLib.format.ChatUser
import me.arcator.onfimLib.format.EventLocation
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ChatEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.event.EventHandler
import net.md_5.bungee.event.EventPriority

class ChatListener(private val sendEvt: (Chat) -> Unit) : Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    fun postChatEvent(event: ChatEvent) {
        val msg: String = Chat.fromMessage(event.message)
        if (msg.isEmpty()) return

        val player = event.sender as ProxiedPlayer

        sendEvt(
            Chat(
                plaintext = msg,
                ChatUser(player.displayName, uuid = player.uniqueId),
                server = EventLocation(name=if (player.server == null) "Unknown" else player.server.info.name),
            ),
        )
    }
}
