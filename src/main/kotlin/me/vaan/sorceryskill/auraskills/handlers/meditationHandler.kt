package me.vaan.sorceryskill.auraskills.handlers

import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.utils.Utils
import org.bukkit.entity.Player

fun meditationHandler(ability: CustomAbility, player: Player, args: Array<out Any>) {
    val event = args[0] as ManaRegenerateEvent

    if (!player.isSneaking) return

    val multiplier = 1.0 + Utils.getSkillValue(player, ability)
    SorceryRestore.debug("meditationHandler called - multiplier : $multiplier")
    event.amount *= multiplier
}