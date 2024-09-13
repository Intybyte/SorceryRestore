package me.vaan.sorceryskill.auraskills.listeners

import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent
import me.vaan.sorceryskill.auraskills.SorceryAbilities
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object ManaRegenListener : Listener {

    @EventHandler
    fun onRegen(event: ManaRegenerateEvent) {
        SorceryAbilities.MEDITATION.callHandler(event.player, event)
    }
}