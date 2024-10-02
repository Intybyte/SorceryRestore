package me.vaan.sorceryskill.auraskills.handlers

import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.event.skill.XpGainEvent
import me.vaan.sorceryskill.utils.Utils
import org.bukkit.entity.Player

fun arcaneKnowledgeHandler(ability: CustomAbility, player: Player, args: Array<out Any>) {
    if (!ability.isEnabled) return

    val multiplier = 1.0 + (Utils.getSkillValue(player, ability) / 100.0)
    val event = args[0] as XpGainEvent

    event.amount *= multiplier
}