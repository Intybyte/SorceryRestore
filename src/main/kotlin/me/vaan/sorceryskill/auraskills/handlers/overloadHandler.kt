package me.vaan.sorceryskill.auraskills.handlers

import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.event.mana.ManaAbilityActivateEvent
import me.vaan.sorceryskill.utils.Utils
import me.vaan.sorceryskill.utils.failsChecks
import org.bukkit.entity.Player

fun overloadHandler(ability: CustomAbility, player: Player, args: Array<out Any>) {
    if (ability.failsChecks(player)) return

    val event = args[0] as ManaAbilityActivateEvent
    val value = Utils.getSkillValue(player, ability) / 100.0

    if (Math.random() > value) return
    event.manaUsed = 0.0
    player.sendMessage("OVERLOAD - No mana used")
}