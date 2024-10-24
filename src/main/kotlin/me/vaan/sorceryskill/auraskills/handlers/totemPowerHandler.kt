package me.vaan.sorceryskill.auraskills.handlers

import dev.aurelium.auraskills.api.ability.CustomAbility
import dev.aurelium.auraskills.api.stat.StatModifier
import dev.aurelium.auraskills.api.stat.Stats
import me.vaan.sorceryskill.utils.Utils
import me.vaan.sorceryskill.utils.failsChecks
import me.vaan.sorceryskill.utils.getSkillPlayer
import org.bukkit.Material
import org.bukkit.entity.Player

private const val MODIFIER_NAME = "AbilityModifier-TotemPower"

fun totemPowerHandler(ability: CustomAbility, player: Player, args: Array<out Any>) {
    if (ability.failsChecks(player)) return

    val value = Utils.getSkillValue(player, ability) / 100.0
    val skillUser = player.getSkillPlayer() ?: return

    if (player.inventory.itemInOffHand.type != Material.TOTEM_OF_UNDYING) {
        val strengthModifier = StatModifier(MODIFIER_NAME, Stats.STRENGTH, value)
        val wisdomModifier = StatModifier(MODIFIER_NAME, Stats.WISDOM, value)

        skillUser.addStatModifier(strengthModifier)
        skillUser.addStatModifier(wisdomModifier)
    } else {
        skillUser.removeStatModifier(MODIFIER_NAME)
    }
}