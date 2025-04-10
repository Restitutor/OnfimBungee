package me.arcator.onfimBungee.commands

import me.arcator.onfimBungee.sender.noImagePlayers
import me.arcator.onfimBungee.sender.noRelayPlayers
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.plugin.Command

class ToggleCommand : Command("togglerelay") {
    override fun execute(player: CommandSender, args: Array<String>) {
        if (player !is ProxiedPlayer) return

        val component: TextComponent

        if (noRelayPlayers.toggle(player.uniqueId)) {
            component = TextComponent("Disabled inbound chat relay.")
            component.color = ChatColor.RED
        } else {
            component = TextComponent("Enabled inbound chat relay.")
            component.color = ChatColor.GREEN
        }
        player.sendMessage(component)
    }
}

class ToggleImage : Command("toggleimage") {
    override fun execute(player: CommandSender, args: Array<String>) {
        if (player !is ProxiedPlayer) return

        val component: TextComponent

        if (noImagePlayers.toggle(player.uniqueId)) {
            component = TextComponent("Disabled inbound image relay.")
            component.color = ChatColor.RED
        } else {
            component = TextComponent("Enabled inbound image relay.")
            component.color = ChatColor.GREEN
        }
        player.sendMessage(component)
    }
}
