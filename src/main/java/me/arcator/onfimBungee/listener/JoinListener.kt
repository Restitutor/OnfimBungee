package me.arcator.onfimBungee.listener

import java.util.concurrent.TimeUnit
import me.arcator.onfimLib.format.GenericChat
import me.arcator.onfimLib.format.JoinQuit
import net.md_5.bungee.api.event.PostLoginEvent
import net.md_5.bungee.api.plugin.Listener
import net.md_5.bungee.api.plugin.Plugin
import net.md_5.bungee.api.scheduler.TaskScheduler
import net.md_5.bungee.event.EventHandler

class JoinListener(private val plugin: Plugin, private val sendEvt: (GenericChat) -> Unit) :
    Listener {
    @EventHandler
    fun onPostLogin(event: PostLoginEvent) {
        val scheduler: TaskScheduler = plugin.proxy.scheduler
        scheduler.schedule(
            plugin,
            object : Runnable {
                override fun run() {
                    val player = event.player
                    if (!player.isConnected) return

                    val server = if (player.server == null) {
                        // Try again
                        scheduler.schedule(plugin, this, 1, TimeUnit.SECONDS)
                        return
                    } else player.server.info.name
                    sendEvt(JoinQuit(name = player.displayName, server = server, type = "Join"))
                }
            },
            1, TimeUnit.SECONDS,
        )
    }
}
