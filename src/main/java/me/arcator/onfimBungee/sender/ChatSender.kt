package me.arcator.onfimBungee.sender

import me.arcator.onfimLib.format.Chat
import me.arcator.onfimLib.format.ImageEvt
import me.arcator.onfimLib.format.PrintableGeneric
import me.arcator.onfimLib.interfaces.ChatSenderInterface
import me.arcator.onfimLib.structs.ToggleSet
import net.kyori.adventure.platform.bungeecord.BungeeAudiences
import net.kyori.adventure.text.Component
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.scheduler.TaskScheduler

private fun broadcastPlayers(): List<ProxiedPlayer> {
    return ProxyServer.getInstance().players
        .filter { player -> !noRelayPlayers.contains(player.uniqueId) }
}

val noRelayPlayers = ToggleSet()
val noImagePlayers = ToggleSet()

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
                .filter { player -> player.server == null || (player.server.info.name != evt.server) }
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

    override fun say(evt: PrintableGeneric) {
        schedule {
            val text = Component.text(evt.printString, evt.colour)
            broadcastPlayers().forEach { player ->
                adv.player(player).sendMessage { text }
            }
        }
    }
}
