package me.vaan.sorceryskill.auraskills.handlers

import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.event.mana.ManaRegenerateEvent
import me.vaan.sorceryskill.SorceryRestore
import me.vaan.sorceryskill.utils.Utils
import me.vaan.sorceryskill.utils.failsChecks
import org.bukkit.entity.Player

fun meditationHandler(ability: CustomAbility, player: Player, args: Array<out Any>) {
    if (!player.isSneaking) return
    if (ability.failsChecks(player)) return

    val event = args[0] as ManaRegenerateEvent

    val multiplier = 1.0 + Utils.getSkillValue(player, ability) / 100.0
    SorceryRestore.debug("meditationHandler called - multiplier : $multiplier")
    event.amount *= multiplier
}