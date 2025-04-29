package me.arcator.onfimBungee

import java.util.concurrent.TimeUnit
import me.arcator.onfimBungee.commands.ToggleCommand
import me.arcator.onfimBungee.commands.ToggleImage
import me.arcator.onfimBungee.listener.ChatListener
import me.arcator.onfimBungee.listener.JoinListener
import me.arcator.onfimBungee.listener.QuitListener
import me.arcator.onfimBungee.listener.SwitchListener
import me.arcator.onfimBungee.sender.ChatSender
import me.arcator.onfimLib.SCTPIn
import me.arcator.onfimLib.UDPIn
import me.arcator.onfimLib.format.SerializedEvent
import me.arcator.onfimLib.format.PlayerMoveInterface
import me.arcator.onfimLib.out.Dispatcher
import me.arcator.onfimLib.utils.Unpacker
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.plugin.Plugin

@Suppress("unused")
class OnfimPlugin : Plugin() {
    private val cs = ChatSender(this, proxy.scheduler)


    private val unpacker = Unpacker(cs, logger::info)
    private val uListener = UDPIn(unpacker::read)
    private val sListener = SCTPIn(unpacker::read)
    private val ds: Dispatcher =
        Dispatcher(
            { text ->
                // Debug logger.info(text)
            },
            uListener::port,
            sListener::port,
        )

    init {
        unpacker.setOnHeartbeat(ds::getHeartbeat)
    }


    override fun onEnable() {
        ProxyServer.getInstance().pluginManager.registerCommand(this, ToggleCommand())
        ProxyServer.getInstance().pluginManager.registerCommand(this, ToggleImage())
        ProxyServer.getInstance().scheduler.runAsync(this, sListener)
        ProxyServer.getInstance().scheduler.runAsync(this, uListener)
        ProxyServer.getInstance().scheduler.schedule(
            this,
            {
                ds.pingAll()
            },
            10, 30, TimeUnit.SECONDS,
        )

        val sender = { evt: SerializedEvent ->
            proxy.scheduler.runAsync(this) { ds.broadcast(evt) }
            // Relay to self
            if (evt is PlayerMoveInterface) cs.say(evt)
        }

        proxy.pluginManager.registerListener(this, ChatListener(sender))
        proxy.pluginManager.registerListener(this, JoinListener(this, sender))
        proxy.pluginManager.registerListener(this, SwitchListener(sender))
        proxy.pluginManager.registerListener(this, QuitListener(sender))
    }

    override fun onDisable() {
        logger.info("Disabling plugin.")
        ds.disable()
        proxy.pluginManager.unregisterListeners(this)
    }
}
