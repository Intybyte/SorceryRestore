package me.vaan.sorceryskill.auraskills.listeners

import dev.aurelium.auraskills.api.event.skill.XpGainEvent
import me.vaan.sorceryskill.auraskills.SorceryAbilities
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object XpEarnListener : Listener {
    @EventHandler
    fun onEarnXp(event: XpGainEvent) {
        SorceryAbilities.ARCANE_KNOWLEDGE.callHandler(event.player, event)
    }
}