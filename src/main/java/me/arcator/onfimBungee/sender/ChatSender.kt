package me.arcator.onfimBungee.sender

import java.util.UUID
import me.arcator.onfimLib.format.Chat
import me.arcator.onfimLib.format.ImageEvt
import me.arcator.onfimLib.format.PlayerMoveInterface
import me.arcator.onfimLib.interfaces.ChatSenderInterface
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.scheduler.TaskScheduler


val noRelayPlayers = mutableSetOf<UUID>()
val noImagePlayers = mutableSetOf<UUID>()

private fun broadcastPlayers(): List<ProxiedPlayer> {
    return ProxyServer.getInstance().players
        .filter { player -> !noRelayPlayers.contains(player.uniqueId) }
}

class ChatSender(private val plugin: Plugin, private val scheduler: TaskScheduler) :
    ChatSenderInterface {
    private val adv = BungeeAudiences.create(plugin)
    private fun schedule(run: Runnable) {
        if (ProxyServer.getInstance().players.isNotEmpty()) {
            scheduler.runAsync(plugin, run)
        }
    }

    override fun say(evt: Chat) {
        if (!evt.shouldRelay()) return

        schedule {
            val text = evt.getChatMessage()
            broadcastPlayers()
                // Avoid duplicates for different bungees to same server
                .filter { player -> player.server == null || (player.server.info.name != evt.server.name) }
                .forEach { player ->
                    adv.player(player).sendMessage { text }
                }
        }
    }

    override fun say(evt: ImageEvt) {
        schedule {
            for (comp in evt.getLines()) {
                // Print line by line
                broadcastPlayers()
                    .filter { player -> !noImagePlayers.contains(player.uniqueId) }
                    .forEach { player ->
                        adv.player(player).sendMessage { comp }
                    }
            }
        }
    }

    override fun say(evt: PlayerMoveInterface) {
        schedule {
            val text = evt.getComponent()
            broadcastPlayers().forEach { player ->
                adv.player(player).sendMessage { text }
            }
        }
    }
}
